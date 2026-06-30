package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.CourseGenerateRequestDTO;
import kr.co.inntavern.dripking.dto.response.CourseGenerateResponseDTO;
import kr.co.inntavern.dripking.dto.response.RecommendationResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.model.WishlistItem;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

@Service
public class CourseGenerationService {
    private static final int MAX_COURSE_DAYS = 7;
    private static final int MAX_PLANS_PER_DAY = 3;
    private static final int FIRST_PLAN_HOUR = 9;
    private static final int PLAN_INTERVAL_HOURS = 2;
    private static final String GENERATION_MODE = "RECOMMENDATION_DRAFT";

    private final CountryRepository countryRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final AlcoholRepository alcoholRepository;
    private final DestinationRepository destinationRepository;
    private final DistilleryRepository distilleryRepository;
    private final RecommendationService recommendationService;
    private final CreditService creditService;
    private final CourseGenerationGateService courseGenerationGateService;

    public CourseGenerationService(CountryRepository countryRepository,
                                   WishlistItemRepository wishlistItemRepository,
                                   AlcoholRepository alcoholRepository,
                                   DestinationRepository destinationRepository,
                                   DistilleryRepository distilleryRepository,
                                   RecommendationService recommendationService,
                                   CreditService creditService,
                                   CourseGenerationGateService courseGenerationGateService) {
        this.countryRepository = countryRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.alcoholRepository = alcoholRepository;
        this.destinationRepository = destinationRepository;
        this.distilleryRepository = distilleryRepository;
        this.recommendationService = recommendationService;
        this.creditService = creditService;
        this.courseGenerationGateService = courseGenerationGateService;
    }

    @Transactional
    public CourseGenerateResponseDTO generate(Long userId, CourseGenerateRequestDTO requestDTO) {
        return generate(userId, null, requestDTO);
    }

    @Transactional
    public CourseGenerateResponseDTO generate(Long userId, String clientIp, CourseGenerateRequestDTO requestDTO) {
        validateRequest(requestDTO);
        CourseGenerationGateService.GateContext gateContext = courseGenerationGateService.beforeGenerate(userId, clientIp, requestDTO);
        int durationDays = durationDays(requestDTO);
        Country country = resolveCountry(requestDTO.getCountryName());

        List<CourseItem> sourceItems = mergeSourceItems(userId, requestDTO, country.getId(), durationDays);
        String inputHash = buildCourseInputHash(requestDTO, country.getId(), sourceItems);
        String courseId = "draft_" + inputHash;
        CreditService.GenerationCreditResult creditResult = null;
        CourseGenerationGateService.GuestTrialStatus guestTrialStatus = null;
        if (userId == null) {
            guestTrialStatus = courseGenerationGateService.recordGuestGeneration(gateContext, courseId, inputHash);
        } else {
            creditResult = creditService.chargeForCourseGeneration(userId, courseId, inputHash, gateContext.estimatedCost());
        }

        CourseGenerateResponseDTO responseDTO = new CourseGenerateResponseDTO();
        responseDTO.setCourseId(courseId);
        responseDTO.setGenerationMode(GENERATION_MODE);
        responseDTO.setStartDate(requestDTO.getStartDate());
        responseDTO.setEndDate(requestDTO.getEndDate());
        responseDTO.setCountryName(country.getName());
        responseDTO.setRegionHint(trimToNull(requestDTO.getRegionHint()));
        responseDTO.setDurationDays(durationDays);
        responseDTO.setSourceItemCount(sourceItems.size());
        responseDTO.setCreditCharged(creditResult == null ? 0 : creditResult.creditCharged());
        responseDTO.setRemainingCredit(creditResult == null ? null : creditResult.remainingCredit());
        if (guestTrialStatus != null) {
            responseDTO.setGuestTrialLimit(guestTrialStatus.trialLimit());
            responseDTO.setGuestTrialUsed(guestTrialStatus.usedCount());
            responseDTO.setGuestTrialRemaining(guestTrialStatus.remainingCount());
        }
        responseDTO.setCacheHit(false);
        responseDTO.setDays(buildDays(requestDTO, durationDays, sourceItems));
        return responseDTO;
    }

    private void validateRequest(CourseGenerateRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("코스 생성 요청이 필요합니다.");
        }
        if (requestDTO.getStartDate() == null || requestDTO.getEndDate() == null) {
            throw new IllegalArgumentException("여행 날짜를 입력해주세요.");
        }
        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new IllegalArgumentException("출발일이 도착일보다 늦을 수 없습니다.");
        }
        int durationDays = durationDays(requestDTO);
        if (durationDays < 1 || durationDays > MAX_COURSE_DAYS) {
            throw new IllegalArgumentException("코스 기간은 1일부터 %d일까지 선택할 수 있습니다.".formatted(MAX_COURSE_DAYS));
        }
        if (trimToNull(requestDTO.getCountryName()) == null) {
            throw new IllegalArgumentException("국가를 선택해주세요.");
        }
        if (selectedSignalCount(requestDTO) == 0) {
            throw new IllegalArgumentException("취향 또는 위시리스트 항목을 하나 이상 선택해주세요.");
        }
    }

    private int durationDays(CourseGenerateRequestDTO requestDTO) {
        return Math.toIntExact(ChronoUnit.DAYS.between(requestDTO.getStartDate(), requestDTO.getEndDate()) + 1);
    }

    private int selectedSignalCount(CourseGenerateRequestDTO requestDTO) {
        return normalizedCategoryIds(requestDTO).size()
                + normalizedFlavorTags(requestDTO).size()
                + normalizedWishlistItemIds(requestDTO).size();
    }

    private Country resolveCountry(String countryName) {
        Country country = countryRepository.findByName(countryName.trim());
        if (country == null) {
            throw new IllegalArgumentException("해당 이름의 국가가 존재하지 않습니다.");
        }
        return country;
    }

    private List<CourseItem> mergeSourceItems(Long userId,
                                              CourseGenerateRequestDTO requestDTO,
                                              Long countryId,
                                              int durationDays) {
        Map<String, CourseItem> itemsByKey = new LinkedHashMap<>();
        selectedWishlistItems(userId, requestDTO).forEach(item -> itemsByKey.putIfAbsent(item.key(), item));

        RecommendationResponseDTO recommendations = recommendationService.getRecommendations(
                userId,
                firstCategoryId(requestDTO),
                String.join(",", normalizedFlavorTags(requestDTO)),
                countryId,
                durationDays * MAX_PLANS_PER_DAY
        );

        for (RecommendationResponseDTO.ItemDTO itemDTO : recommendations.getItems()) {
            CourseItem item = CourseItem.fromRecommendation(itemDTO);
            itemsByKey.putIfAbsent(item.key(), item);
        }

        return itemsByKey.values().stream()
                .limit((long) durationDays * MAX_PLANS_PER_DAY)
                .toList();
    }

    private List<CourseItem> selectedWishlistItems(Long userId, CourseGenerateRequestDTO requestDTO) {
        List<Long> wishlistItemIds = normalizedWishlistItemIds(requestDTO);
        if (wishlistItemIds.isEmpty()) {
            return List.of();
        }
        if (userId == null) {
            throw new IllegalArgumentException("위시리스트 항목은 로그인 후 선택할 수 있습니다.");
        }

        Map<Long, WishlistItem> itemsById = new LinkedHashMap<>();
        wishlistItemRepository.findAllById(wishlistItemIds).stream()
                .filter(item -> item.getUser() != null && Objects.equals(item.getUser().getId(), userId))
                .sorted(Comparator.comparingInt(item -> wishlistItemIds.indexOf(item.getId())))
                .forEach(item -> itemsById.put(item.getId(), item));

        if (itemsById.size() != wishlistItemIds.size()) {
            throw new IllegalArgumentException("선택한 위시리스트 항목을 찾을 수 없습니다.");
        }

        return itemsById.values().stream()
                .map(this::mapWishlistItem)
                .toList();
    }

    private CourseItem mapWishlistItem(WishlistItem wishlistItem) {
        return switch (wishlistItem.getItemType()) {
            case ALCOHOL -> alcoholRepository.findById(wishlistItem.getTargetId())
                    .map(alcohol -> CourseItem.fromAlcohol(alcohol, "WISHLIST"))
                    .orElseThrow(() -> new IllegalArgumentException("위시리스트의 술 정보를 찾을 수 없습니다."));
            case DESTINATION -> destinationRepository.findById(wishlistItem.getTargetId())
                    .map(destination -> CourseItem.fromDestination(destination, "WISHLIST"))
                    .orElseThrow(() -> new IllegalArgumentException("위시리스트의 여행지 정보를 찾을 수 없습니다."));
            case DISTILLERY -> distilleryRepository.findById(wishlistItem.getTargetId())
                    .map(distillery -> CourseItem.fromDistillery(distillery, "WISHLIST"))
                    .orElseThrow(() -> new IllegalArgumentException("위시리스트의 양조장 정보를 찾을 수 없습니다."));
            default -> throw new IllegalArgumentException("코스 생성에 사용할 수 없는 위시리스트 항목입니다.");
        };
    }

    private List<CourseGenerateResponseDTO.DayDTO> buildDays(CourseGenerateRequestDTO requestDTO,
                                                             int durationDays,
                                                             List<CourseItem> sourceItems) {
        List<CourseGenerateResponseDTO.DayDTO> days = new ArrayList<>();
        for (int dayIndex = 0; dayIndex < durationDays; dayIndex += 1) {
            CourseGenerateResponseDTO.DayDTO dayDTO = new CourseGenerateResponseDTO.DayDTO();
            dayDTO.setDay(dayIndex + 1);
            dayDTO.setDate(requestDTO.getStartDate().plusDays(dayIndex));
            days.add(dayDTO);
        }

        int itemsPerDay = Math.max(1, Math.min(MAX_PLANS_PER_DAY, (int) Math.ceil(sourceItems.size() / (double) durationDays)));
        for (int index = 0; index < sourceItems.size(); index += 1) {
            int dayIndex = Math.min(durationDays - 1, index / itemsPerDay);
            CourseGenerateResponseDTO.DayDTO dayDTO = days.get(dayIndex);
            dayDTO.getPlans().add(sourceItems.get(index).toPlanDTO(dayDTO.getPlans().size() + 1));
        }

        return days;
    }

    private Long firstCategoryId(CourseGenerateRequestDTO requestDTO) {
        List<Long> categoryIds = normalizedCategoryIds(requestDTO);
        return categoryIds.isEmpty() ? null : categoryIds.getFirst();
    }

    private List<Long> normalizedCategoryIds(CourseGenerateRequestDTO requestDTO) {
        if (requestDTO.getTaste() == null || requestDTO.getTaste().getCategories() == null) {
            return List.of();
        }
        return requestDTO.getTaste().getCategories().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<String> normalizedFlavorTags(CourseGenerateRequestDTO requestDTO) {
        if (requestDTO.getTaste() == null || requestDTO.getTaste().getFlavorTags() == null) {
            return List.of();
        }
        Set<String> tags = new LinkedHashSet<>();
        for (String flavorTag : requestDTO.getTaste().getFlavorTags()) {
            String normalized = flavorTag == null ? "" : flavorTag.trim().toLowerCase(Locale.ROOT);
            if (!normalized.isBlank()) {
                tags.add(normalized);
            }
        }
        return List.copyOf(tags);
    }

    private List<Long> normalizedWishlistItemIds(CourseGenerateRequestDTO requestDTO) {
        if (requestDTO.getWishlistItemIds() == null) {
            return List.of();
        }
        return requestDTO.getWishlistItemIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String buildCourseInputHash(CourseGenerateRequestDTO requestDTO, Long countryId, List<CourseItem> sourceItems) {
        StringJoiner sourceKeyJoiner = new StringJoiner(",");
        for (CourseItem sourceItem : sourceItems) {
            sourceKeyJoiner.add(sourceItem.key());
        }

        String rawId = String.join("|",
                requestDTO.getStartDate().toString(),
                requestDTO.getEndDate().toString(),
                String.valueOf(countryId),
                String.valueOf(trimToNull(requestDTO.getRegionHint())),
                String.join(",", normalizedCategoryIds(requestDTO).stream().map(String::valueOf).toList()),
                String.join(",", normalizedFlavorTags(requestDTO)),
                String.join(",", normalizedWishlistItemIds(requestDTO).stream().map(String::valueOf).toList()),
                sourceKeyJoiner.toString()
        );
        return UUID.nameUUIDFromBytes(rawId.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static String planTime(int order) {
        int hour = Math.min(21, FIRST_PLAN_HOUR + Math.max(0, order - 1) * PLAN_INTERVAL_HOURS);
        return "%02d:00".formatted(hour);
    }

    private record CourseItem(ItemType itemType,
                              Long targetId,
                              String name,
                              String description,
                              String imgUrl,
                              float rating,
                              double score,
                              String source) {

        private static CourseItem fromRecommendation(RecommendationResponseDTO.ItemDTO itemDTO) {
            return new CourseItem(
                    itemDTO.getItemType(),
                    itemDTO.getTargetId(),
                    itemDTO.getName(),
                    itemDTO.getDescription(),
                    itemDTO.getImgUrl(),
                    itemDTO.getRating(),
                    itemDTO.getScore(),
                    "RECOMMENDATION"
            );
        }

        private static CourseItem fromAlcohol(Alcohol alcohol, String source) {
            return new CourseItem(
                    ItemType.ALCOHOL,
                    alcohol.getId(),
                    alcohol.getName(),
                    alcohol.getDescription(),
                    alcohol.getImgUrl(),
                    alcohol.getRating(),
                    1.0,
                    source
            );
        }

        private static CourseItem fromDestination(Destination destination, String source) {
            return new CourseItem(
                    ItemType.DESTINATION,
                    destination.getId(),
                    destination.getName(),
                    destination.getDescription(),
                    destination.getImgUrl(),
                    destination.getRating(),
                    1.0,
                    source
            );
        }

        private static CourseItem fromDistillery(Distillery distillery, String source) {
            return new CourseItem(
                    ItemType.DISTILLERY,
                    distillery.getId(),
                    distillery.getName(),
                    distillery.getDescription(),
                    distillery.getImgUrl(),
                    distillery.getRating(),
                    1.0,
                    source
            );
        }

        private CourseGenerateResponseDTO.PlanDTO toPlanDTO(int order) {
            CourseGenerateResponseDTO.PlanDTO planDTO = new CourseGenerateResponseDTO.PlanDTO();
            planDTO.setOrder(order);
            planDTO.setItemType(itemType);
            planDTO.setTargetId(targetId);
            planDTO.setName(name);
            planDTO.setDescription(description);
            planDTO.setImgUrl(imgUrl);
            planDTO.setRating(rating);
            planDTO.setScore(score);
            planDTO.setSource(source);
            planDTO.setTime(planTime(order));
            planDTO.setTravelMinutesFromPrev(order == 1 ? 0 : null);
            return planDTO;
        }

        private String key() {
            return itemType + ":" + targetId;
        }
    }
}
