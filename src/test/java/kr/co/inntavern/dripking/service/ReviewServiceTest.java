package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.response.ReviewResponseDTO;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:review-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewModerationService reviewModerationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Test
    void reviewCreateUpdateAndDeleteRecalculateDestinationRating() {
        User firstUser = saveUser("rating-first@example.com", "rating-first");
        User secondUser = saveUser("rating-second@example.com", "rating-second");
        Destination destination = saveDestination("Rating destination");

        ReviewResponseDTO firstReview = reviewService.createReview(
                firstUser.getId(),
                reviewRequest(destination.getId(), (byte) 5)
        );
        assertDestinationRating(destination.getId(), 5.0f);

        ReviewResponseDTO secondReview = reviewService.createReview(
                secondUser.getId(),
                reviewRequest(destination.getId(), (byte) 3)
        );
        assertDestinationRating(destination.getId(), 4.0f);

        reviewService.updateReview(firstUser.getId(), firstReview.getId(), reviewRequest(destination.getId(), (byte) 1));
        assertDestinationRating(destination.getId(), 2.0f);

        reviewService.deleteReview(secondUser.getId(), secondReview.getId());
        assertDestinationRating(destination.getId(), 1.0f);

        reviewService.deleteReview(firstUser.getId(), firstReview.getId());
        assertDestinationRating(destination.getId(), 0.0f);
    }

    @Test
    void reviewModerationHideAndDeleteRecalculateDestinationRating() {
        User firstUser = saveUser("moderation-rating-first@example.com", "moderation-rating-first");
        User secondUser = saveUser("moderation-rating-second@example.com", "moderation-rating-second");
        Destination destination = saveDestination("Moderation rating destination");

        ReviewResponseDTO firstReview = reviewService.createReview(
                firstUser.getId(),
                reviewRequest(destination.getId(), (byte) 5)
        );
        ReviewResponseDTO secondReview = reviewService.createReview(
                secondUser.getId(),
                reviewRequest(destination.getId(), (byte) 1)
        );
        assertDestinationRating(destination.getId(), 3.0f);

        reviewModerationService.hideReview(firstReview.getId());
        assertDestinationRating(destination.getId(), 1.0f);

        reviewModerationService.deleteReview(secondReview.getId());
        assertDestinationRating(destination.getId(), 0.0f);
    }

    @Test
    void reviewCreateAndUpdateRejectRatingOutsideAllowedRange() {
        User user = saveUser("rating-validation@example.com", "rating-validation");
        Destination destination = saveDestination("Rating validation destination");

        assertThatThrownBy(() -> reviewService.createReview(user.getId(), reviewRequest(destination.getId(), (byte) 6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("평점은 0점에서 5점 사이여야 합니다.");
        assertDestinationRating(destination.getId(), 0.0f);

        ReviewResponseDTO review = reviewService.createReview(user.getId(), reviewRequest(destination.getId(), (byte) 4));

        assertThatThrownBy(() -> reviewService.updateReview(user.getId(), review.getId(), reviewRequest(destination.getId(), (byte) -1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("평점은 0점에서 5점 사이여야 합니다.");
        assertDestinationRating(destination.getId(), 4.0f);
    }

    @Test
    void reviewCreateRejectsXssContents() {
        User user = saveUser("xss-review@example.com", "xss-review");
        Destination destination = saveDestination("XSS review destination");
        ReviewRequestDTO requestDTO = reviewRequest(destination.getId(), (byte) 4);
        requestDTO.setContents("<img src=x onerror=alert(1)>");

        assertThatThrownBy(() -> reviewService.createReview(user.getId(), requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XSS 위험 패턴");
        assertDestinationRating(destination.getId(), 0.0f);
    }

    @Test
    void reviewUpdateAllowsSqlLikePlainTextContents() {
        User user = saveUser("sql-review@example.com", "sql-review");
        Destination destination = saveDestination("SQL review destination");
        ReviewResponseDTO review = reviewService.createReview(user.getId(), reviewRequest(destination.getId(), (byte) 4));
        ReviewRequestDTO requestDTO = reviewRequest(destination.getId(), (byte) 5);
        requestDTO.setContents("' OR 1=1 -- 는 SQL 예시로 자주 보입니다.");

        reviewService.updateReview(user.getId(), review.getId(), requestDTO);

        ReviewResponseDTO updatedReview = reviewService.getMyReview(
                user.getId(),
                ItemType.DESTINATION.name(),
                destination.getId()
        ).orElseThrow();
        assertThat(updatedReview.getRating()).isEqualTo((byte) 5);
        assertThat(updatedReview.getContents()).isEqualTo("' OR 1=1 -- 는 SQL 예시로 자주 보입니다.");
    }

    @Test
    void publicReviewListsExcludeHiddenAndDeletedReviews() {
        User hiddenUser = saveUser("hidden-review@example.com", "hidden-review");
        User deletedUser = saveUser("deleted-review@example.com", "deleted-review");
        User visibleUser = saveUser("visible-review@example.com", "visible-review");
        Destination destination = saveDestination("Visible review list destination");

        ReviewResponseDTO hiddenReview = reviewService.createReview(
                hiddenUser.getId(),
                reviewRequest(destination.getId(), (byte) 5)
        );
        ReviewResponseDTO deletedReview = reviewService.createReview(
                deletedUser.getId(),
                reviewRequest(destination.getId(), (byte) 4)
        );
        ReviewResponseDTO visibleReview = reviewService.createReview(
                visibleUser.getId(),
                reviewRequest(destination.getId(), (byte) 3)
        );

        reviewModerationService.hideReview(hiddenReview.getId());
        reviewModerationService.deleteReview(deletedReview.getId());

        assertThat(reviewService.getAllReviewsByTargetID(
                0,
                10,
                "id",
                "ASC",
                ItemType.DESTINATION.name(),
                destination.getId()
        ).getContent())
                .extracting(ReviewResponseDTO::getId)
                .containsExactly(visibleReview.getId());
    }

    @Test
    void myReviewIgnoresHiddenAndDeletedReviews() {
        User hiddenUser = saveUser("my-hidden-review@example.com", "my-hidden-review");
        User deletedUser = saveUser("my-deleted-review@example.com", "my-deleted-review");
        Destination hiddenDestination = saveDestination("My hidden review destination");
        Destination deletedDestination = saveDestination("My deleted review destination");

        ReviewResponseDTO hiddenReview = reviewService.createReview(
                hiddenUser.getId(),
                reviewRequest(hiddenDestination.getId(), (byte) 5)
        );
        ReviewResponseDTO deletedReview = reviewService.createReview(
                deletedUser.getId(),
                reviewRequest(deletedDestination.getId(), (byte) 4)
        );

        reviewModerationService.hideReview(hiddenReview.getId());
        reviewModerationService.deleteReview(deletedReview.getId());

        assertThat(reviewService.getMyReview(
                hiddenUser.getId(),
                ItemType.DESTINATION.name(),
                hiddenDestination.getId()
        )).isEmpty();
        assertThat(reviewService.getMyReview(
                deletedUser.getId(),
                ItemType.DESTINATION.name(),
                deletedDestination.getId()
        )).isEmpty();
    }

    @Test
    void repeatedCreateForSameUserTargetReturnsExistingReview() {
        User user = saveUser("duplicate-review@example.com", "duplicate-review");
        Destination destination = saveDestination("Duplicate review destination");

        ReviewResponseDTO firstReview = reviewService.createReview(
                user.getId(),
                reviewRequest(destination.getId(), (byte) 5)
        );
        ReviewResponseDTO secondReview = reviewService.createReview(
                user.getId(),
                reviewRequest(destination.getId(), (byte) 1)
        );

        assertThat(secondReview.getId()).isEqualTo(firstReview.getId());
        assertThat(secondReview.getRating()).isEqualTo((byte) 5);
        assertThat(reviewService.getAllReviewsByTargetID(
                0,
                10,
                "id",
                "ASC",
                ItemType.DESTINATION.name(),
                destination.getId()
        ).getContent())
                .extracting(ReviewResponseDTO::getId)
                .containsExactly(firstReview.getId());
    }

    @Test
    void reviewCreateRejectsNonReviewableItemTypes() {
        User user = saveUser("invalid-type-review@example.com", "invalid-type-review");
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setItemType(ItemType.CUSTOM_PLACE);
        requestDTO.setTargetId(1L);
        requestDTO.setRating((byte) 3);
        requestDTO.setContents("Invalid type");

        assertThatThrownBy(() -> reviewService.createReview(user.getId(), requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리뷰를 작성할 수 없는 itemType");
    }

    private ReviewRequestDTO reviewRequest(Long targetId, byte rating) {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setItemType(ItemType.DESTINATION);
        requestDTO.setTargetId(targetId);
        requestDTO.setRating(rating);
        requestDTO.setContents("Review content");
        return requestDTO;
    }

    private void assertDestinationRating(Long destinationId, float expectedRating) {
        Destination destination = destinationRepository.findById(destinationId).orElseThrow();
        assertThat(destination.getRating()).isEqualTo(expectedRating);
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

    private Destination saveDestination(String name) {
        Destination destination = Destination.builder()
                .name(name)
                .description("Destination description")
                .build();
        return destinationRepository.save(destination);
    }
}
