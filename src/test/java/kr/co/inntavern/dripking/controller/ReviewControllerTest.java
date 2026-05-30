package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:review-controller-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Test
    void invalidReviewRatingReturnsBadRequestResponse() throws Exception {
        User user = saveUser();
        Destination destination = saveDestination();
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true
        );

        String requestBody = """
                {
                  "itemType": "DESTINATION",
                  "targetId": %d,
                  "rating": 6,
                  "contents": "Invalid rating"
                }
                """.formatted(destination.getId());

        mockMvc.perform(post("/api/reviews")
                        .with(user(userDetails))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("평점은 0점에서 5점 사이여야 합니다."));
    }

    private User saveUser() {
        User user = new User();
        user.setEmail("review-controller@example.com");
        user.setNickname("review-controller");
        user.setPassword("encoded-password");
        user.setLocked(false);
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    private Destination saveDestination() {
        Destination destination = Destination.builder()
                .name("Review controller destination")
                .description("Destination description")
                .build();
        return destinationRepository.save(destination);
    }
}
