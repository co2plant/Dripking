package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.RecommendationResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.model.UserTasteProfile;
import kr.co.inntavern.dripking.model.WishlistItem;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.UserTasteProfileRepository;
import kr.co.inntavern.dripking.repository.WishlistItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class RecommendationService {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final AlcoholRepository alcoholRepository;
    private final DestinationRepository destinationRepository;
    private final DistilleryRepository distilleryRepository;
    private final UserTasteProfileRepository userTasteProfileRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public RecommendationService(AlcoholRepository alcoholRepository,
                                 DestinationRepository destinationRepository,
                                 DistilleryRepository distilleryRepository,
                                 UserTasteProfileRepository userTasteProfileRepository,
                                 WishlistItemRepository wishlistItemRepository) {
        this.alcoholRepository = alcoholRepository;
        this.destinationRepository = destinationRepository;
        this.distilleryRepository = distilleryRepository;
        this.userTasteProfileRepository = userTasteProfileRepository;
        this.wishlistItemRepository = wishlistItemRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationResponseDTO getRecommendations(Long userId,
                                                        Long categoryId,
                                                        String flavorTags,
                                                        Long countryId,
                                                        int limit) {
        int normalizedLimit = normalizeLimit(limit);
        RecommendationContext context = buildContext(userId, categoryId, flavorTags);
        Pageable candidatePage = candidatePage(normalizedLimit);
        Set<String> wishlistKeys = wishlistKeys(userId);

        List<ScoredCandidate> candidates = new ArrayList<>();
        alcoholRepository.findRecommendationCandidates(categoryId, countryId, candidatePage).stream()
                .map(this::candidate)
                .map(candidate -> score(candidate, context, wishlistKeys))
                .forEach(candidates::add);
        destinationRepository.findRecommendationCandidates(categoryId, countryId, candidatePage).stream()
                .map(this::candidate)
                .map(candidate -> score(candidate, context, wishlistKeys))
                .forEach(candidates::add);
        distilleryRepository.findRecommendationCandidates(categoryId, countryId, candidatePage).stream()
                .map(this::candidate)
                .map(candidate -> score(candidate, context, wishlistKeys))
                .forEach(candidates::add);

        RecommendationResponseDTO responseDTO = new RecommendationResponseDTO();
        responseDTO.setItems(candidates.stream()
                .sorted(Comparator
                        .comparingDouble(ScoredCandidate::score).reversed()
                        .thenComparing(candidate -> candidate.candidate().name(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(candidate -> candidate.candidate().targetId()))
                .limit(normalizedLimit)
                .map(this::mapItem)
                .toList());
        return responseDTO;
    }

    private RecommendationContext buildContext(Long userId, Long categoryId, String flavorTags) {
        Set<Long> categories = new LinkedHashSet<>();
        if (categoryId != null) {
            categories.add(categoryId);
        }

        Set<String> normalizedFlavorTags = new LinkedHashSet<>(parseFlavorTags(flavorTags));
        if (userId != null) {
            userTasteProfileRepository.findByUserId(userId).ifPresent(profile -> {
                categories.addAll(profile.getCategories());
                normalizedFlavorTags.addAll(normalizeFlavorTags(profile));
            });
        }

        return new RecommendationContext(categories, List.copyOf(normalizedFlavorTags));
    }

    private List<String> normalizeFlavorTags(UserTasteProfile profile) {
        if (profile.getFlavorTags() == null) {
            return List.of();
        }
        return profile.getFlavorTags().stream()
                .map(this::normalizeToken)
                .filter(token -> !token.isBlank())
                .distinct()
                .toList();
    }

    private List<String> parseFlavorTags(String flavorTags) {
        if (flavorTags == null || flavorTags.isBlank()) {
            return List.of();
        }
        List<String> normalized = new ArrayList<>();
        for (String token : flavorTags.split(",")) {
            String normalizedToken = normalizeToken(token);
            if (!normalizedToken.isBlank()) {
                normalized.add(normalizedToken);
            }
        }
        return normalized;
    }

    private String normalizeToken(String token) {
        return token == null ? "" : token.trim().toLowerCase(Locale.ROOT);
    }

    private Set<String> wishlistKeys(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        Set<String> keys = new LinkedHashSet<>();
        for (WishlistItem wishlistItem : wishlistItemRepository.findAllByUserIdOrderByCreatedAtAscIdAsc(userId)) {
            keys.add(key(wishlistItem.getItemType(), wishlistItem.getTargetId()));
        }
        return keys;
    }

    private Pageable candidatePage(int limit) {
        int candidateLimit = Math.min(MAX_LIMIT * 3, Math.max(30, limit * 3));
        return PageRequest.of(0, candidateLimit, Sort.by(Sort.Direction.DESC, "rating").and(Sort.by("id")));
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private ScoredCandidate score(Candidate candidate,
                                  RecommendationContext context,
                                  Set<String> wishlistKeys) {
        double score = 0.20 + Math.min(Math.max(candidate.rating(), 0.0f), 5.0f) / 5.0 * 0.20;

        if (candidate.categoryId() != null && context.categories().contains(candidate.categoryId())) {
            score += 0.35;
        }
        if (wishlistKeys.contains(key(candidate.itemType(), candidate.targetId()))) {
            score += 0.30;
        }
        score += Math.min(0.30, matchingFlavorCount(candidate, context.flavorTags()) * 0.10);

        return new ScoredCandidate(candidate, score);
    }

    private int matchingFlavorCount(Candidate candidate, List<String> flavorTags) {
        if (flavorTags.isEmpty()) {
            return 0;
        }
        String text = (candidate.name() + " " + nullToEmpty(candidate.description())).toLowerCase(Locale.ROOT);
        int count = 0;
        for (String flavorTag : flavorTags) {
            if (text.contains(flavorTag)) {
                count += 1;
            }
        }
        return count;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Candidate candidate(Alcohol alcohol) {
        Long categoryId = alcohol.getCategory() == null ? null : alcohol.getCategory().getId();
        return new Candidate(
                ItemType.ALCOHOL,
                alcohol.getId(),
                alcohol.getName(),
                alcohol.getDescription(),
                alcohol.getImgUrl(),
                alcohol.getRating(),
                categoryId
        );
    }

    private Candidate candidate(Destination destination) {
        Long categoryId = destination.getCategory() == null ? null : destination.getCategory().getId();
        return new Candidate(
                ItemType.DESTINATION,
                destination.getId(),
                destination.getName(),
                destination.getDescription(),
                destination.getImgUrl(),
                destination.getRating(),
                categoryId
        );
    }

    private Candidate candidate(Distillery distillery) {
        Long categoryId = null;
        if (distillery.getDestination() != null && distillery.getDestination().getCategory() != null) {
            categoryId = distillery.getDestination().getCategory().getId();
        }
        return new Candidate(
                ItemType.DISTILLERY,
                distillery.getId(),
                distillery.getName(),
                distillery.getDescription(),
                distillery.getImgUrl(),
                distillery.getRating(),
                categoryId
        );
    }

    private RecommendationResponseDTO.ItemDTO mapItem(ScoredCandidate scoredCandidate) {
        Candidate candidate = scoredCandidate.candidate();
        RecommendationResponseDTO.ItemDTO itemDTO = new RecommendationResponseDTO.ItemDTO();
        itemDTO.setItemType(candidate.itemType());
        itemDTO.setTargetId(candidate.targetId());
        itemDTO.setName(candidate.name());
        itemDTO.setDescription(candidate.description());
        itemDTO.setImgUrl(candidate.imgUrl());
        itemDTO.setRating(candidate.rating());
        itemDTO.setScore(roundScore(scoredCandidate.score()));
        return itemDTO;
    }

    private double roundScore(double score) {
        return Math.round(score * 1000.0) / 1000.0;
    }

    private String key(ItemType itemType, Long targetId) {
        return itemType + ":" + targetId;
    }

    private record RecommendationContext(Set<Long> categories, List<String> flavorTags) {
    }

    private record Candidate(ItemType itemType,
                             Long targetId,
                             String name,
                             String description,
                             String imgUrl,
                             float rating,
                             Long categoryId) {
    }

    private record ScoredCandidate(Candidate candidate, double score) {
    }
}
