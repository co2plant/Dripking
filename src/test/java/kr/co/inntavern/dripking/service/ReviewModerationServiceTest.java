package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.ReviewReportRequestDTO;
import kr.co.inntavern.dripking.dto.request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.response.ReviewReportResponseDTO;
import kr.co.inntavern.dripking.dto.response.ReviewResponseDTO;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.ReviewReportRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:review-moderation-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class ReviewModerationServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewModerationService reviewModerationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private ReviewReportRepository reviewReportRepository;

    @Test
    void duplicateOpenReportFromSameUserReturnsExistingReport() {
        User author = saveUser("report-author@example.com", "report-author");
        User reporter = saveUser("reporter@example.com", "reporter");
        Destination destination = saveDestination("Report destination");
        ReviewResponseDTO review = reviewService.createReview(author.getId(), reviewRequest(destination.getId()));

        ReviewReportResponseDTO firstReport = reviewModerationService.reportReview(
                review.getId(),
                reporter.getId(),
                reportRequest("SPAM", "First report")
        );
        ReviewReportResponseDTO secondReport = reviewModerationService.reportReview(
                review.getId(),
                reporter.getId(),
                reportRequest("ABUSE", "Second report")
        );

        assertThat(secondReport.getId()).isEqualTo(firstReport.getId());
        assertThat(secondReport.getStatus()).isEqualTo(ReviewReportStatus.OPEN);
        assertThat(secondReport.getReason()).isEqualTo("SPAM");
        assertThat(reviewReportRepository.count()).isEqualTo(1);
    }

    @Test
    void resolvedReportIsListedByStatusAndCanBeReportedAgain() {
        User author = saveUser("resolve-author@example.com", "resolve-author");
        User reporter = saveUser("resolve-reporter@example.com", "resolve-reporter");
        User admin = saveUser("resolve-admin@example.com", "resolve-admin");
        Destination destination = saveDestination("Resolve report destination");
        ReviewResponseDTO review = reviewService.createReview(author.getId(), reviewRequest(destination.getId()));
        ReviewReportResponseDTO openReport = reviewModerationService.reportReview(
                review.getId(),
                reporter.getId(),
                reportRequest("SPAM", "Resolve this")
        );

        ReviewReportResponseDTO resolvedReport = reviewModerationService.resolveReport(openReport.getId(), admin.getId());
        ReviewReportResponseDTO newOpenReport = reviewModerationService.reportReview(
                review.getId(),
                reporter.getId(),
                reportRequest("ABUSE", "After resolve")
        );

        assertThat(resolvedReport.getStatus()).isEqualTo(ReviewReportStatus.RESOLVED);
        assertThat(resolvedReport.getResolvedByUserId()).isEqualTo(admin.getId());
        assertThat(resolvedReport.getResolvedAt()).isNotNull();
        assertThat(newOpenReport.getId()).isNotEqualTo(openReport.getId());
        assertThat(reviewModerationService.getReviewReports(0, 10, ReviewReportStatus.RESOLVED).getContent())
                .extracting(ReviewReportResponseDTO::getId)
                .containsExactly(resolvedReport.getId());
        assertThat(reviewModerationService.getReviewReports(0, 10, ReviewReportStatus.OPEN).getContent())
                .extracting(ReviewReportResponseDTO::getId)
                .containsExactly(newOpenReport.getId());
    }

    @Test
    void moderationHideAndDeleteExposeReviewStatusInReports() {
        User author = saveUser("status-author@example.com", "status-author");
        User reporter = saveUser("status-reporter@example.com", "status-reporter");
        Destination destination = saveDestination("Status report destination");
        ReviewResponseDTO review = reviewService.createReview(author.getId(), reviewRequest(destination.getId()));
        ReviewReportResponseDTO report = reviewModerationService.reportReview(
                review.getId(),
                reporter.getId(),
                reportRequest("SPAM", "Check status")
        );

        reviewModerationService.hideReview(review.getId());
        ReviewReportResponseDTO hiddenReport = reviewModerationService.getReviewReports(0, 10, ReviewReportStatus.OPEN)
                .getContent()
                .stream()
                .filter(item -> item.getId().equals(report.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(hiddenReport.getReviewStatus()).isEqualTo(ReviewStatus.HIDDEN);

        reviewModerationService.deleteReview(review.getId());
        ReviewReportResponseDTO deletedReport = reviewModerationService.getReviewReports(0, 10, ReviewReportStatus.OPEN)
                .getContent()
                .stream()
                .filter(item -> item.getId().equals(report.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(deletedReport.getReviewStatus()).isEqualTo(ReviewStatus.DELETED);
    }

    @Test
    void reportReviewRejectsUnsafeMemo() {
        User author = saveUser("unsafe-report-author@example.com", "unsafe-report-author");
        User reporter = saveUser("unsafe-report-reporter@example.com", "unsafe-report-reporter");
        Destination destination = saveDestination("Unsafe report destination");
        ReviewResponseDTO review = reviewService.createReview(author.getId(), reviewRequest(destination.getId()));

        assertThatThrownBy(() -> reviewModerationService.reportReview(
                review.getId(),
                reporter.getId(),
                reportRequest("SPAM", "<script>alert(1)</script>")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XSS 위험 패턴");
        assertThat(reviewReportRepository.count()).isZero();
    }

    private ReviewRequestDTO reviewRequest(Long targetId) {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setItemType(ItemType.DESTINATION);
        requestDTO.setTargetId(targetId);
        requestDTO.setRating(4);
        requestDTO.setContents("Review content");
        return requestDTO;
    }

    private ReviewReportRequestDTO reportRequest(String reason, String memo) {
        ReviewReportRequestDTO requestDTO = new ReviewReportRequestDTO();
        requestDTO.setReason(reason);
        requestDTO.setMemo(memo);
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

    private Destination saveDestination(String name) {
        Destination destination = Destination.builder()
                .name(name)
                .description("Destination description")
                .build();
        return destinationRepository.save(destination);
    }
}
