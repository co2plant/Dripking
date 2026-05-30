package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.PlanRequestDTO;
import kr.co.inntavern.dripking.dto.response.PlanResponseDTO;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.CityRepository;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plan-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
class PlanServiceTest {

    @Autowired
    private PlanService planService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Test
    void customPlacePlanStoresUserEnteredSnapshot() {
        User user = saveUser("custom-place-user@example.com", "custom-place-user");
        Trip trip = saveTrip(user);
        PlanRequestDTO requestDTO = basePlanRequest();
        requestDTO.setItemType(ItemType.CUSTOM_PLACE);
        requestDTO.setName("Evening bar stop");
        requestDTO.setCustomPlaceName("Local tasting bar");
        requestDTO.setCustomPlaceAddress("12 Custom Street");

        PlanResponseDTO responseDTO = planService.createPlan(trip.getId(), requestDTO, user.getId());

        assertThat(responseDTO.getTripId()).isEqualTo(trip.getId());
        assertThat(responseDTO.getItemType()).isEqualTo(ItemType.CUSTOM_PLACE);
        assertThat(responseDTO.getTargetId()).isNull();
        assertThat(responseDTO.getCustomPlaceName()).isEqualTo("Local tasting bar");
        assertThat(responseDTO.getCustomPlaceAddress()).isEqualTo("12 Custom Street");
        assertThat(responseDTO.getSnapshotName()).isEqualTo("Local tasting bar");
        assertThat(responseDTO.getSnapshotAddress()).isEqualTo("12 Custom Street");
        assertThat(responseDTO.getSnapshotLatitude()).isNull();
        assertThat(responseDTO.getSnapshotLongitude()).isNull();
    }

    @Test
    void catalogBackedDestinationSnapshotDoesNotChangeWhenSourceChanges() {
        User user = saveUser("destination-plan-user@example.com", "destination-plan-user");
        Trip trip = saveTrip(user);
        Destination destination = saveDestination("Original destination", 34.1d, 135.2d);
        PlanRequestDTO createRequestDTO = basePlanRequest();
        createRequestDTO.setItemType(ItemType.DESTINATION);
        createRequestDTO.setTargetId(destination.getId());

        PlanResponseDTO createdPlan = planService.createPlan(trip.getId(), createRequestDTO, user.getId());

        destination.setName("Changed destination");
        destination.setLatitude(35.3d);
        destination.setLongitude(136.4d);
        destinationRepository.save(destination);

        PlanRequestDTO updateRequestDTO = basePlanRequest();
        updateRequestDTO.setItemType(ItemType.DESTINATION);
        updateRequestDTO.setTargetId(destination.getId());
        updateRequestDTO.setName("Updated itinerary title");
        updateRequestDTO.setStartTime(LocalTime.of(10, 0));
        updateRequestDTO.setEndTime(LocalTime.of(11, 0));

        PlanResponseDTO updatedPlan = planService.updatePlan(trip.getId(), createdPlan.getId(), updateRequestDTO, user.getId());

        assertThat(updatedPlan.getName()).isEqualTo("Updated itinerary title");
        assertThat(updatedPlan.getSnapshotName()).isEqualTo("Original destination");
        assertThat(updatedPlan.getSnapshotAddress()).isEqualTo("Test City, Test Country");
        assertThat(updatedPlan.getSnapshotLatitude()).isEqualTo(34.1d);
        assertThat(updatedPlan.getSnapshotLongitude()).isEqualTo(135.2d);
    }

    @Test
    void getPlansByTripUsesSavedSortOrder() {
        User user = saveUser("sorted-plan-user@example.com", "sorted-plan-user");
        Trip trip = saveTrip(user);
        createPlanWithSortOrder(trip, user, "Third", 2);
        createPlanWithSortOrder(trip, user, "First", 0);
        createPlanWithSortOrder(trip, user, "Second", 1);

        List<PlanResponseDTO> plans = planService.getPlansByTripId(trip.getId(), user.getId());

        assertThat(plans)
                .extracting(PlanResponseDTO::getName)
                .containsExactly("First", "Second", "Third");
        assertThat(plans)
                .extracting(PlanResponseDTO::getSortOrder)
                .containsExactly(0, 1, 2);
    }

    @Test
    void planReadUpdateAndDeleteRejectOtherUsers() {
        User owner = saveUser("plan-owner@example.com", "plan-owner");
        User other = saveUser("plan-other@example.com", "plan-other");
        Trip trip = saveTrip(owner);
        PlanRequestDTO createRequestDTO = basePlanRequest();
        createRequestDTO.setItemType(ItemType.CUSTOM_PLACE);
        createRequestDTO.setCustomPlaceName("Owner stop");
        createRequestDTO.setCustomPlaceAddress("Owner address");
        PlanResponseDTO createdPlan = planService.createPlan(trip.getId(), createRequestDTO, owner.getId());

        PlanRequestDTO updateRequestDTO = basePlanRequest();
        updateRequestDTO.setName("Other update");

        assertThatThrownBy(() -> planService.getPlansByTripId(trip.getId(), other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");
        assertThatThrownBy(() -> planService.updatePlan(createdPlan.getId(), updateRequestDTO, other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");
        assertThatThrownBy(() -> planService.updatePlan(trip.getId(), createdPlan.getId(), updateRequestDTO, other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");
        assertThatThrownBy(() -> planService.deletePlanById(trip.getId(), createdPlan.getId(), other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");

        assertThat(planService.getPlansByTripId(trip.getId(), owner.getId()))
                .extracting(PlanResponseDTO::getId)
                .containsExactly(createdPlan.getId());
    }

    private PlanResponseDTO createPlanWithSortOrder(Trip trip, User user, String name, Integer sortOrder) {
        PlanRequestDTO requestDTO = basePlanRequest();
        requestDTO.setItemType(ItemType.CUSTOM_PLACE);
        requestDTO.setName(name);
        requestDTO.setCustomPlaceName(name);
        requestDTO.setCustomPlaceAddress(name + " address");
        requestDTO.setSortOrder(sortOrder);
        return planService.createPlan(trip.getId(), requestDTO, user.getId());
    }

    private PlanRequestDTO basePlanRequest() {
        PlanRequestDTO requestDTO = new PlanRequestDTO();
        requestDTO.setName("Plan item");
        requestDTO.setDescription("Plan description");
        requestDTO.setPlanDate(LocalDate.of(2026, 6, 1));
        requestDTO.setStartTime(LocalTime.of(9, 0));
        requestDTO.setEndTime(LocalTime.of(10, 0));
        return requestDTO;
    }

    private User saveUser(String email, String nickname) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setPassword("encoded-password");
        user.setLocked(false);
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    private Trip saveTrip(User user) {
        Trip trip = Trip.builder()
                .name("Test trip")
                .description("Test trip")
                .user(user)
                .startDate(new Date())
                .endDate(new Date())
                .build();
        return tripRepository.save(trip);
    }

    private Destination saveDestination(String name, Double latitude, Double longitude) {
        Country country = countryRepository.save(Country.builder()
                .name("Test Country")
                .description("Test country")
                .build());
        City city = cityRepository.save(City.builder()
                .name("Test City")
                .description("Test city")
                .country(country)
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name("Test Category")
                .description("Test category")
                .build());

        Destination destination = Destination.builder()
                .name(name)
                .description("Destination description")
                .city(city)
                .category(category)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return destinationRepository.save(destination);
    }
}
