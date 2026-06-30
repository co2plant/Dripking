package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.CourseGenerateRequestDTO;
import kr.co.inntavern.dripking.dto.response.CourseGenerateResponseDTO;
import kr.co.inntavern.dripking.dto.response.RecommendationResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.WishlistItem;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseGenerationServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private AlcoholRepository alcoholRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private DistilleryRepository distilleryRepository;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private CreditService creditService;

    @Mock
    private CourseGenerationGateService courseGenerationGateService;

    private CourseGenerationService courseGenerationService;

    @BeforeEach
    void setUp() {
        lenient().when(courseGenerationGateService.beforeGenerate(any(), any(), any()))
                .thenReturn(CourseGenerationGateService.GateContext.authenticated(BigDecimal.ZERO));
        courseGenerationService = new CourseGenerationService(
                countryRepository,
                wishlistItemRepository,
                alcoholRepository,
                destinationRepository,
                distilleryRepository,
                recommendationService,
                creditService,
                courseGenerationGateService
        );
    }

    @Test
    void generateBuildsDraftDaysFromWishlistAndRecommendations() {
        CourseGenerateRequestDTO requestDTO = request();
        requestDTO.setWishlistItemIds(List.of(5L));
        Country japan = country(1L, "일본");
        User user = user(10L);
        WishlistItem wishlistItem = wishlistItem(5L, user, ItemType.DESTINATION, 200L);
        Destination savedDestination = destination(200L, "저장한 여행지", "위시리스트 여행지", 4.1f);
        RecommendationResponseDTO recommendations = recommendations(
                recommendation(ItemType.DESTINATION, 200L, "중복 여행지", 0.9),
                recommendation(ItemType.ALCOHOL, 100L, "추천 술", 0.8)
        );

        when(countryRepository.findByName("일본")).thenReturn(japan);
        when(wishlistItemRepository.findAllById(List.of(5L))).thenReturn(List.of(wishlistItem));
        when(destinationRepository.findById(200L)).thenReturn(Optional.of(savedDestination));
        when(recommendationService.getRecommendations(10L, 20L, "fresh,peaty", 1L, 6))
                .thenReturn(recommendations);
        when(creditService.chargeForCourseGeneration(eq(10L), anyString(), anyString(), eq(BigDecimal.ZERO)))
                .thenReturn(new CreditService.GenerationCreditResult(10, 40));

        CourseGenerateResponseDTO responseDTO = courseGenerationService.generate(10L, requestDTO);

        assertThat(responseDTO.getCourseId()).startsWith("draft_");
        assertThat(responseDTO.getGenerationMode()).isEqualTo("RECOMMENDATION_DRAFT");
        assertThat(responseDTO.getDurationDays()).isEqualTo(2);
        assertThat(responseDTO.getSourceItemCount()).isEqualTo(2);
        assertThat(responseDTO.getCreditCharged()).isEqualTo(10);
        assertThat(responseDTO.getRemainingCredit()).isEqualTo(40);
        assertThat(responseDTO.isCacheHit()).isFalse();
        assertThat(responseDTO.getDays()).hasSize(2);
        assertThat(responseDTO.getDays().get(0).getPlans()).hasSize(1);
        assertThat(responseDTO.getDays().get(1).getPlans()).hasSize(1);
        assertThat(responseDTO.getDays().get(0).getPlans().getFirst().getSource()).isEqualTo("WISHLIST");
        assertThat(responseDTO.getDays().get(0).getPlans().getFirst().getName()).isEqualTo("저장한 여행지");
        assertThat(responseDTO.getDays().get(0).getPlans().getFirst().getTime()).isEqualTo("09:00");
        assertThat(responseDTO.getDays().get(0).getPlans().getFirst().getTravelMinutesFromPrev()).isZero();
        assertThat(responseDTO.getDays().get(1).getPlans().getFirst().getItemType()).isEqualTo(ItemType.ALCOHOL);
        verify(creditService).chargeForCourseGeneration(eq(10L), anyString(), anyString(), eq(BigDecimal.ZERO));
    }

    @Test
    void generateRejectsDateRangeOverSevenDays() {
        CourseGenerateRequestDTO requestDTO = request();
        requestDTO.setEndDate(LocalDate.of(2026, 7, 10));

        assertThatThrownBy(() -> courseGenerationService.generate(null, requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("7일");
    }

    @Test
    void generatePassesTasteAndCountryToRecommendationService() {
        CourseGenerateRequestDTO requestDTO = request();
        Country japan = country(1L, "일본");
        when(countryRepository.findByName("일본")).thenReturn(japan);
        when(recommendationService.getRecommendations(null, 20L, "fresh,peaty", 1L, 6))
                .thenReturn(recommendations());

        courseGenerationService.generate(null, requestDTO);

        verify(recommendationService).getRecommendations(null, 20L, "fresh,peaty", 1L, 6);
    }

    @Test
    void generateGuestRecordsTrialStatusWithoutChargingCredit() {
        CourseGenerateRequestDTO requestDTO = request();
        requestDTO.setAnonId("anon-1");
        requestDTO.setCaptchaToken("dev-captcha");
        Country japan = country(1L, "일본");
        CourseGenerationGateService.GateContext gateContext =
                new CourseGenerationGateService.GateContext("anon-1", BigDecimal.ZERO, 2, 1);
        when(courseGenerationGateService.beforeGenerate(null, "127.0.0.1", requestDTO)).thenReturn(gateContext);
        when(courseGenerationGateService.recordGuestGeneration(eq(gateContext), anyString(), anyString()))
                .thenReturn(new CourseGenerationGateService.GuestTrialStatus(2, 2, 0));
        when(countryRepository.findByName("일본")).thenReturn(japan);
        when(recommendationService.getRecommendations(null, 20L, "fresh,peaty", 1L, 6))
                .thenReturn(recommendations(recommendation(ItemType.ALCOHOL, 100L, "추천 술", 0.8)));

        CourseGenerateResponseDTO responseDTO = courseGenerationService.generate(null, "127.0.0.1", requestDTO);

        assertThat(responseDTO.getCreditCharged()).isZero();
        assertThat(responseDTO.getRemainingCredit()).isNull();
        assertThat(responseDTO.getGuestTrialLimit()).isEqualTo(2);
        assertThat(responseDTO.getGuestTrialUsed()).isEqualTo(2);
        assertThat(responseDTO.getGuestTrialRemaining()).isZero();
        verify(courseGenerationGateService).recordGuestGeneration(eq(gateContext), anyString(), anyString());
        verify(creditService, never()).chargeForCourseGeneration(any(), anyString(), anyString(), any());
    }

    private CourseGenerateRequestDTO request() {
        CourseGenerateRequestDTO requestDTO = new CourseGenerateRequestDTO();
        requestDTO.setStartDate(LocalDate.of(2026, 7, 1));
        requestDTO.setEndDate(LocalDate.of(2026, 7, 2));
        requestDTO.setCountryName("일본");
        CourseGenerateRequestDTO.TasteDTO tasteDTO = new CourseGenerateRequestDTO.TasteDTO();
        tasteDTO.setCategories(List.of(20L));
        tasteDTO.setFlavorTags(List.of("fresh", "peaty"));
        requestDTO.setTaste(tasteDTO);
        return requestDTO;
    }

    private Country country(Long id, String name) {
        return Country.builder()
                .id(id)
                .name(name)
                .description(name)
                .build();
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user-%d@example.com".formatted(id));
        return user;
    }

    private WishlistItem wishlistItem(Long id, User user, ItemType itemType, Long targetId) {
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setId(id);
        wishlistItem.setUser(user);
        wishlistItem.setItemType(itemType);
        wishlistItem.setTargetId(targetId);
        return wishlistItem;
    }

    private Destination destination(Long id, String name, String description, float rating) {
        Destination destination = new Destination();
        destination.setId(id);
        destination.setName(name);
        destination.setDescription(description);
        destination.setRating(rating);
        return destination;
    }

    private RecommendationResponseDTO recommendations(RecommendationResponseDTO.ItemDTO... items) {
        RecommendationResponseDTO responseDTO = new RecommendationResponseDTO();
        responseDTO.setItems(List.of(items));
        return responseDTO;
    }

    private RecommendationResponseDTO.ItemDTO recommendation(ItemType itemType, Long targetId, String name, double score) {
        RecommendationResponseDTO.ItemDTO itemDTO = new RecommendationResponseDTO.ItemDTO();
        itemDTO.setItemType(itemType);
        itemDTO.setTargetId(targetId);
        itemDTO.setName(name);
        itemDTO.setDescription(name + " description");
        itemDTO.setRating(4.0f);
        itemDTO.setScore(score);
        return itemDTO;
    }
}
