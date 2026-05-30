package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.InteractionEventRequestDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.DashboardActivityResponseDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.PopularItemResponseDTO;
import kr.co.inntavern.dripking.model.*;
import kr.co.inntavern.dripking.model.enumType.InteractionEventType;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.*;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.security.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InteractionEventService {
    private final InteractionEventRepository interactionEventRepository;
    private final UserRepository userRepository;
    private final AlcoholRepository alcoholRepository;
    private final DestinationRepository destinationRepository;
    private final DistilleryRepository distilleryRepository;
    private final PopularityScoringPolicy scoringPolicy;

    public InteractionEventService(InteractionEventRepository interactionEventRepository,
                                   UserRepository userRepository,
                                   AlcoholRepository alcoholRepository,
                                   DestinationRepository destinationRepository,
                                   DistilleryRepository distilleryRepository,
                                   PopularityScoringPolicy scoringPolicy) {
        this.interactionEventRepository = interactionEventRepository;
        this.userRepository = userRepository;
        this.alcoholRepository = alcoholRepository;
        this.destinationRepository = destinationRepository;
        this.distilleryRepository = distilleryRepository;
        this.scoringPolicy = scoringPolicy;
    }

    @Transactional
    public void recordEvent(InteractionEventRequestDTO requestDTO, CustomUserDetails customUserDetails) {
        validateRequest(requestDTO);

        User user = null;
        if (customUserDetails != null && customUserDetails.getId() != null) {
            user = userRepository.findById(customUserDetails.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            if (isAdmin(user)) {
                return;
            }
        }

        InteractionEvent event = new InteractionEvent();
        event.setUser(user);
        event.setAnonymousKey(normalizeAnonymousKey(requestDTO.getAnonymousKey()));
        event.setItemType(requestDTO.getItemType());
        event.setTargetId(requestDTO.getTargetId());
        event.setEventType(requestDTO.getEventType());
        interactionEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<PopularItemResponseDTO> getPopularItems(ItemType itemType, String window, int limit) {
        validatePopularItemType(itemType);
        List<InteractionEvent> events = loadEvents(itemType, window);
        Map<Long, ScoreBucket> buckets = scoreEvents(events);

        return buckets.entrySet().stream()
                .sorted((a, b) -> {
                    int scoreComparison = Double.compare(b.getValue().score, a.getValue().score);
                    if (scoreComparison != 0) {
                        return scoreComparison;
                    }
                    return Long.compare(a.getKey(), b.getKey());
                })
                .limit(Math.max(1, limit))
                .map(entry -> mapPopularItem(itemType, entry.getKey(), entry.getValue()))
                .flatMap(Optional::stream)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DashboardActivityResponseDTO> getRecentActivities() {
        return interactionEventRepository.findTop10ByOrderByOccurredAtDesc().stream()
                .filter(event -> !isAdmin(event.getUser()))
                .map(this::mapActivity)
                .toList();
    }

    private void validateRequest(InteractionEventRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("이벤트 요청이 필요합니다.");
        }
        validatePopularItemType(requestDTO.getItemType());
        if (requestDTO.getTargetId() == null) {
            throw new IllegalArgumentException("targetId가 필요합니다.");
        }
        if (requestDTO.getEventType() == null) {
            throw new IllegalArgumentException("eventType이 필요합니다.");
        }
        validateTargetExists(requestDTO.getItemType(), requestDTO.getTargetId());
    }

    private void validatePopularItemType(ItemType itemType) {
        if (itemType != ItemType.ALCOHOL && itemType != ItemType.DESTINATION && itemType != ItemType.DISTILLERY) {
            throw new IllegalArgumentException("지원하지 않는 itemType입니다.");
        }
    }

    private void validateTargetExists(ItemType itemType, Long targetId) {
        boolean exists = switch (itemType) {
            case ALCOHOL -> alcoholRepository.existsById(targetId);
            case DESTINATION -> destinationRepository.existsById(targetId);
            case DISTILLERY -> distilleryRepository.existsById(targetId);
            default -> false;
        };
        if (!exists) {
            throw new IllegalArgumentException("대상 아이템이 존재하지 않습니다.");
        }
    }

    private List<InteractionEvent> loadEvents(ItemType itemType, String window) {
        LocalDateTime windowStart = windowStart(window);
        if (windowStart == null) {
            return interactionEventRepository.findAllByItemTypeOrderByOccurredAtDesc(itemType);
        }
        return interactionEventRepository.findAllByItemTypeAndOccurredAtGreaterThanEqualOrderByOccurredAtDesc(itemType, windowStart);
    }

    private LocalDateTime windowStart(String window) {
        String normalizedWindow = window == null ? "7d" : window.trim().toLowerCase(Locale.ROOT);
        return switch (normalizedWindow) {
            case "7d", "7", "week" -> LocalDateTime.now().minusDays(7);
            case "30d", "30", "month" -> LocalDateTime.now().minusDays(30);
            case "all", "all-time", "alltime" -> null;
            default -> LocalDateTime.now().minusDays(7);
        };
    }

    private Map<Long, ScoreBucket> scoreEvents(List<InteractionEvent> events) {
        Map<Long, ScoreBucket> buckets = new HashMap<>();
        Set<String> countedActorEventKeys = new HashSet<>();

        for (InteractionEvent event : events) {
            if (isAdmin(event.getUser())) {
                continue;
            }
            String dedupeKey = actorKey(event) + ":" + event.getItemType() + ":" + event.getTargetId() + ":" + event.getEventType();
            if (!countedActorEventKeys.add(dedupeKey)) {
                continue;
            }

            ScoreBucket bucket = buckets.computeIfAbsent(event.getTargetId(), ignored -> new ScoreBucket());
            bucket.score += scoringPolicy.weightOf(event.getEventType());
            bucket.eventCount += 1;
        }

        return buckets;
    }

    private String actorKey(InteractionEvent event) {
        if (event.getUser() != null && event.getUser().getId() != null) {
            return "user:" + event.getUser().getId();
        }
        if (event.getAnonymousKey() != null && !event.getAnonymousKey().isBlank()) {
            return "anonymous:" + event.getAnonymousKey();
        }
        return "event:" + event.getId();
    }

    private Optional<PopularItemResponseDTO> mapPopularItem(ItemType itemType, Long targetId, ScoreBucket bucket) {
        return switch (itemType) {
            case ALCOHOL -> alcoholRepository.findById(targetId)
                    .map(alcohol -> PopularItemResponseDTO.builder()
                            .itemType(itemType)
                            .targetId(targetId)
                            .name(alcohol.getName())
                            .description(alcohol.getDescription())
                            .imgUrl(alcohol.getImgUrl())
                            .rating(alcohol.getRating())
                            .score(bucket.score)
                            .eventCount(bucket.eventCount)
                            .build());
            case DESTINATION -> destinationRepository.findById(targetId)
                    .map(destination -> PopularItemResponseDTO.builder()
                            .itemType(itemType)
                            .targetId(targetId)
                            .name(destination.getName())
                            .description(destination.getDescription())
                            .imgUrl(destination.getImgUrl())
                            .rating(destination.getRating())
                            .score(bucket.score)
                            .eventCount(bucket.eventCount)
                            .build());
            case DISTILLERY -> distilleryRepository.findById(targetId)
                    .map(distillery -> PopularItemResponseDTO.builder()
                            .itemType(itemType)
                            .targetId(targetId)
                            .name(distillery.getName())
                            .description(distillery.getDescription())
                            .imgUrl(distillery.getImgUrl())
                            .rating(distillery.getRating())
                            .score(bucket.score)
                            .eventCount(bucket.eventCount)
                            .build());
            default -> Optional.empty();
        };
    }

    private DashboardActivityResponseDTO mapActivity(InteractionEvent event) {
        String itemName = resolveItemName(event.getItemType(), event.getTargetId());
        return DashboardActivityResponseDTO.builder()
                .activityType(event.getEventType().name())
                .title(activityTitle(event.getEventType()))
                .description(itemName + " (" + event.getItemType().name() + ")")
                .occurredAt(event.getOccurredAt())
                .build();
    }

    private String resolveItemName(ItemType itemType, Long targetId) {
        return switch (itemType) {
            case ALCOHOL -> alcoholRepository.findById(targetId).map(Alcohol::getName).orElse("삭제된 술");
            case DESTINATION -> destinationRepository.findById(targetId).map(Destination::getName).orElse("삭제된 여행지");
            case DISTILLERY -> distilleryRepository.findById(targetId).map(Distillery::getName).orElse("삭제된 양조장");
            default -> "지원하지 않는 항목";
        };
    }

    private String activityTitle(InteractionEventType eventType) {
        return switch (eventType) {
            case LIST_CARD_CLICK -> "목록 카드 클릭";
            case DETAIL_VIEW -> "상세 페이지 조회";
            case WISHLIST_ADD -> "위시리스트 추가";
            case TRIP_PLAN_ADD -> "여행 계획 추가";
        };
    }

    private boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        return user.getRoles().stream()
                .anyMatch(authority -> authority.getName() == UserRole.ADMIN);
    }

    private String normalizeAnonymousKey(String anonymousKey) {
        if (anonymousKey == null || anonymousKey.isBlank()) {
            return null;
        }
        return anonymousKey.length() > 80 ? anonymousKey.substring(0, 80) : anonymousKey;
    }

    private static class ScoreBucket {
        private double score;
        private int eventCount;
    }
}
