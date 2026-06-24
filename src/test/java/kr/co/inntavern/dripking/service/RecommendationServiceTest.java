package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.RecommendationResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Category;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private AlcoholRepository alcoholRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private DistilleryRepository distilleryRepository;

    @Mock
    private UserTasteProfileRepository userTasteProfileRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(
                alcoholRepository,
                destinationRepository,
                distilleryRepository,
                userTasteProfileRepository,
                wishlistItemRepository
        );
    }

    @Test
    void guestRecommendationsUseQueryFiltersAndFlavorTags() {
        Category whisky = category(10L);
        Alcohol peatyWhisky = alcohol(100L, "Peaty bottle", "smoky peaty finish", 4.5f, whisky);
        Destination whiskyDestination = destination(200L, "Whisky town", "distillery district", 4.0f, whisky);

        when(alcoholRepository.findRecommendationCandidates(eq(10L), eq(1L), any(Pageable.class)))
                .thenReturn(List.of(peatyWhisky));
        when(destinationRepository.findRecommendationCandidates(eq(10L), eq(1L), any(Pageable.class)))
                .thenReturn(List.of(whiskyDestination));
        when(distilleryRepository.findRecommendationCandidates(eq(10L), eq(1L), any(Pageable.class)))
                .thenReturn(List.of());

        RecommendationResponseDTO responseDTO = recommendationService.getRecommendations(null, 10L, "peaty", 1L, 5);

        assertThat(responseDTO.getItems()).hasSize(2);
        assertThat(responseDTO.getItems().getFirst().getItemType()).isEqualTo(ItemType.ALCOHOL);
        assertThat(responseDTO.getItems().getFirst().getTargetId()).isEqualTo(100L);
        assertThat(responseDTO.getItems().getFirst().getScore()).isGreaterThan(responseDTO.getItems().get(1).getScore());
        verify(userTasteProfileRepository, never()).findByUserId(any());
        verify(wishlistItemRepository, never()).findAllByUserIdOrderByCreatedAtAscIdAsc(any());
    }

    @Test
    void loggedInRecommendationsUseTasteProfileAndWishlistBoost() {
        Category sake = category(20L);
        UserTasteProfile profile = new UserTasteProfile();
        profile.setUserId(1L);
        profile.setCategories(List.of(sake.getId()));
        profile.setFlavorTags(List.of("fresh"));

        Alcohol freshSake = alcohol(101L, "Fresh sake", "fresh rice aroma", 4.0f, sake);
        Destination savedDestination = destination(201L, "Saved destination", "quiet city", 3.0f, null);
        WishlistItem wishlistItem = wishlistItem(ItemType.DESTINATION, savedDestination.getId());

        when(userTasteProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(wishlistItemRepository.findAllByUserIdOrderByCreatedAtAscIdAsc(1L)).thenReturn(List.of(wishlistItem));
        when(alcoholRepository.findRecommendationCandidates(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(List.of(freshSake));
        when(destinationRepository.findRecommendationCandidates(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(List.of(savedDestination));
        when(distilleryRepository.findRecommendationCandidates(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(List.of());

        RecommendationResponseDTO responseDTO = recommendationService.getRecommendations(1L, null, null, null, 10);

        assertThat(responseDTO.getItems()).extracting(RecommendationResponseDTO.ItemDTO::getTargetId)
                .containsExactly(101L, 201L);
        assertThat(responseDTO.getItems().getFirst().getScore()).isGreaterThan(responseDTO.getItems().get(1).getScore());
    }

    @Test
    void limitIsClampedForResponseAndCandidateLoad() {
        when(alcoholRepository.findRecommendationCandidates(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(List.of());
        when(destinationRepository.findRecommendationCandidates(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(List.of());
        when(distilleryRepository.findRecommendationCandidates(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(List.of());

        recommendationService.getRecommendations(null, null, null, null, 500);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(alcoholRepository).findRecommendationCandidates(eq(null), eq(null), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(150);
    }

    private Category category(Long id) {
        return Category.builder()
                .id(id)
                .name("category-%d".formatted(id))
                .description("category")
                .build();
    }

    private Alcohol alcohol(Long id, String name, String description, float rating, Category category) {
        Alcohol alcohol = new Alcohol();
        alcohol.setId(id);
        alcohol.setName(name);
        alcohol.setDescription(description);
        alcohol.setRating(rating);
        alcohol.setCategory(category);
        return alcohol;
    }

    private Destination destination(Long id, String name, String description, float rating, Category category) {
        Destination destination = new Destination();
        destination.setId(id);
        destination.setName(name);
        destination.setDescription(description);
        destination.setRating(rating);
        destination.setCategory(category);
        return destination;
    }

    private WishlistItem wishlistItem(ItemType itemType, Long targetId) {
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setItemType(itemType);
        wishlistItem.setTargetId(targetId);
        return wishlistItem;
    }
}
