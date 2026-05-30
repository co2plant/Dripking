package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.InteractionEventRequestDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.PopularItemResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.InteractionEvent;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.InteractionEventType;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.InteractionEventRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.security.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionEventServiceTest {

    @Mock
    private InteractionEventRepository interactionEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlcoholRepository alcoholRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private DistilleryRepository distilleryRepository;

    private InteractionEventService interactionEventService;

    @BeforeEach
    void setUp() {
        interactionEventService = new InteractionEventService(
                interactionEventRepository,
                userRepository,
                alcoholRepository,
                destinationRepository,
                distilleryRepository,
                new PopularityScoringPolicy()
        );
    }

    @Test
    void popularItemsDeduplicateRepeatedSameUserEventType() {
        User user = user(1L, UserRole.USER);
        Alcohol alcohol = alcohol(10L, "Popular whisky");

        when(interactionEventRepository.findAllByItemTypeOrderByOccurredAtDesc(ItemType.ALCOHOL))
                .thenReturn(List.of(
                        event(1L, user, 10L, InteractionEventType.DETAIL_VIEW),
                        event(2L, user, 10L, InteractionEventType.DETAIL_VIEW),
                        event(3L, user, 10L, InteractionEventType.WISHLIST_ADD)
                ));
        when(alcoholRepository.findById(10L)).thenReturn(Optional.of(alcohol));

        List<PopularItemResponseDTO> popularItems = interactionEventService.getPopularItems(ItemType.ALCOHOL, "all", 5);

        assertThat(popularItems).hasSize(1);
        assertThat(popularItems.getFirst().getTargetId()).isEqualTo(10L);
        assertThat(popularItems.getFirst().getScore()).isEqualTo(8.0);
        assertThat(popularItems.getFirst().getEventCount()).isEqualTo(2);
    }

    @Test
    void popularItemsExcludeAdminEventsAtQueryTime() {
        User admin = user(1L, UserRole.ADMIN);
        User member = user(2L, UserRole.USER);
        Alcohol alcohol = alcohol(10L, "Member whisky");

        when(interactionEventRepository.findAllByItemTypeOrderByOccurredAtDesc(ItemType.ALCOHOL))
                .thenReturn(List.of(
                        event(1L, admin, 20L, InteractionEventType.TRIP_PLAN_ADD),
                        event(2L, member, 10L, InteractionEventType.LIST_CARD_CLICK)
                ));
        when(alcoholRepository.findById(10L)).thenReturn(Optional.of(alcohol));

        List<PopularItemResponseDTO> popularItems = interactionEventService.getPopularItems(ItemType.ALCOHOL, "all", 5);

        assertThat(popularItems).hasSize(1);
        assertThat(popularItems.getFirst().getTargetId()).isEqualTo(10L);
        assertThat(popularItems.getFirst().getScore()).isEqualTo(1.0);
    }

    @Test
    void recordEventSkipsAdminInteractions() {
        User admin = user(1L, UserRole.ADMIN);
        InteractionEventRequestDTO requestDTO = new InteractionEventRequestDTO();
        requestDTO.setItemType(ItemType.ALCOHOL);
        requestDTO.setTargetId(10L);
        requestDTO.setEventType(InteractionEventType.WISHLIST_ADD);

        when(alcoholRepository.existsById(10L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        interactionEventService.recordEvent(requestDTO, userDetails(admin, "ROLE_ADMIN"));

        verify(interactionEventRepository, never()).save(org.mockito.ArgumentMatchers.any(InteractionEvent.class));
    }

    @Test
    void recentActivitiesExcludeAdminEventsAndResolveTargetNames() {
        User admin = user(1L, UserRole.ADMIN);
        User member = user(2L, UserRole.USER);
        Alcohol alcohol = alcohol(10L, "Member whisky");

        when(interactionEventRepository.findTop10ByOrderByOccurredAtDesc())
                .thenReturn(List.of(
                        event(1L, admin, 20L, InteractionEventType.TRIP_PLAN_ADD),
                        event(2L, member, 10L, InteractionEventType.DETAIL_VIEW)
                ));
        when(alcoholRepository.findById(10L)).thenReturn(Optional.of(alcohol));

        var activities = interactionEventService.getRecentActivities();

        assertThat(activities).hasSize(1);
        assertThat(activities.getFirst().getActivityType()).isEqualTo("DETAIL_VIEW");
        assertThat(activities.getFirst().getTitle()).isEqualTo("상세 페이지 조회");
        assertThat(activities.getFirst().getDescription()).isEqualTo("Member whisky (ALCOHOL)");
        assertThat(activities.getFirst().getOccurredAt()).isEqualTo(LocalDateTime.of(2026, 5, 30, 3, 0));
    }

    private InteractionEvent event(Long id, User user, Long targetId, InteractionEventType eventType) {
        InteractionEvent event = new InteractionEvent();
        event.setId(id);
        event.setUser(user);
        event.setItemType(ItemType.ALCOHOL);
        event.setTargetId(targetId);
        event.setEventType(eventType);
        event.setOccurredAt(LocalDateTime.of(2026, 5, 30, 3, 0));
        return event;
    }

    private User user(Long id, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setEmail("user-%d@example.com".formatted(id));
        user.setNickname("user-%d".formatted(id));
        user.setPassword("encoded-password");
        user.getRoles().add(Authority.builder().name(role).build());
        return user;
    }

    private Alcohol alcohol(Long id, String name) {
        Alcohol alcohol = new Alcohol();
        alcohol.setId(id);
        alcohol.setName(name);
        alcohol.setDescription("description");
        return alcohol;
    }

    private CustomUserDetails userDetails(User user, String authority) {
        return new CustomUserDetails(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(authority)),
                true
        );
    }
}
