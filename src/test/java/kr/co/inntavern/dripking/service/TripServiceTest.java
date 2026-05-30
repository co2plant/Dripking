package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.TripRequestDTO;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:trip-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
class TripServiceTest {

    @Autowired
    private TripService tripService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void tripReadUpdateAndDeleteRejectOtherUsers() {
        User owner = saveUser("trip-owner@example.com", "trip-owner");
        User other = saveUser("trip-other@example.com", "trip-other");
        Country country = saveCountry("일본");
        Trip trip = saveTrip(owner, country);
        TripRequestDTO updateRequestDTO = tripRequest(country);
        updateRequestDTO.setName("Other update");

        assertThatThrownBy(() -> tripService.getTripById(trip.getId(), other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");
        assertThatThrownBy(() -> tripService.updateTrip(trip.getId(), updateRequestDTO, other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");
        assertThatThrownBy(() -> tripService.deleteTripById(trip.getId(), other.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("권한");

        assertThat(tripRepository.existsById(trip.getId())).isTrue();
    }

    @Test
    void tripCreateUsesAuthenticatedUserId() {
        User authenticatedUser = saveUser("trip-authenticated@example.com", "trip-authenticated");
        Country country = saveCountry("한국");

        Long tripId = tripService.createTrip(tripRequest(country), authenticatedUser.getId()).getId();

        Trip trip = tripRepository.findById(tripId).orElseThrow();
        assertThat(trip.getUser().getId()).isEqualTo(authenticatedUser.getId());
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

    private Country saveCountry(String name) {
        Country country = Country.builder()
                .name(name)
                .description(name + " description")
                .build();
        return countryRepository.save(country);
    }

    private Trip saveTrip(User user, Country country) {
        Trip trip = Trip.builder()
                .name("Test trip")
                .description("Test trip")
                .user(user)
                .country(country)
                .startDate(new Date())
                .endDate(new Date())
                .build();
        return tripRepository.save(trip);
    }

    private TripRequestDTO tripRequest(Country country) {
        TripRequestDTO requestDTO = new TripRequestDTO();
        requestDTO.setName("Trip request");
        requestDTO.setDescription("Trip request");
        requestDTO.setStartDate(new Date());
        requestDTO.setEndDate(new Date());
        requestDTO.setCountryId(country.getId());
        return requestDTO;
    }
}
