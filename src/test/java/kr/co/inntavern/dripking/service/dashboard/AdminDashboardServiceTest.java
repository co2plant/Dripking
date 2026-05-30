package kr.co.inntavern.dripking.service.dashboard;

import kr.co.inntavern.dripking.dto.response.dashboard.DashboardSummaryResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.model.Plan;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.ReviewReport;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.PlanRepository;
import kr.co.inntavern.dripking.repository.ReviewReportRepository;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:admin-dashboard-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class AdminDashboardServiceTest {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private DistilleryRepository distilleryRepository;

    @Autowired
    private AlcoholRepository alcoholRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewReportRepository reviewReportRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void summaryCountsAdminDashboardMetricsFromRepositories() {
        User user = saveUser("dashboard-user@example.com", "dashboard-user");
        Category category = categoryRepository.save(Category.builder()
                .name("위스키")
                .description("Whisky")
                .build());
        Destination completeDestination = saveDestination("Complete destination", 34.9d, 135.6d);
        Destination missingDestination = saveDestination("Missing destination", null, 135.7d);
        Distillery completeDistillery = saveDistillery("Complete distillery", completeDestination, 34.8d, 135.5d);
        saveDistillery("Missing distillery", completeDestination, 34.7d, null);
        alcoholRepository.save(Alcohol.builder()
                .name("Single Malt")
                .description("Alcohol")
                .category(category)
                .distillery(completeDistillery)
                .strength(46.0f)
                .size(700.0f)
                .build());
        Trip trip = tripRepository.save(Trip.builder()
                .name("Dashboard trip")
                .description("Trip")
                .user(user)
                .startDate(new Date())
                .endDate(new Date())
                .build());
        planRepository.save(Plan.builder()
                .name("Dashboard plan")
                .description("Plan")
                .trip(trip)
                .itemType(ItemType.DESTINATION)
                .targetId(completeDestination.getId())
                .build());
        Review review = reviewRepository.save(Review.builder()
                .user(user)
                .itemType(ItemType.DESTINATION)
                .targetId(missingDestination.getId())
                .rating((byte) 4)
                .contents("Review")
                .status(ReviewStatus.VISIBLE)
                .build());
        saveReport(review, user, ReviewReportStatus.OPEN);
        saveReport(review, user, ReviewReportStatus.RESOLVED);

        DashboardSummaryResponseDTO summary = adminDashboardService.getSummary();

        assertThat(summary.getTotalUsers()).isEqualTo(1);
        assertThat(summary.getTotalDestinations()).isEqualTo(2);
        assertThat(summary.getTotalDistilleries()).isEqualTo(2);
        assertThat(summary.getTotalAlcohols()).isEqualTo(1);
        assertThat(summary.getTotalTrips()).isEqualTo(1);
        assertThat(summary.getTotalPlans()).isEqualTo(1);
        assertThat(summary.getOpenReviewReports()).isEqualTo(1);
        assertThat(summary.getMissingDestinationCoordinates()).isEqualTo(1);
        assertThat(summary.getMissingDistilleryCoordinates()).isEqualTo(1);
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

    private Destination saveDestination(String name, Double latitude, Double longitude) {
        return destinationRepository.save(Destination.builder()
                .name(name)
                .description("Destination")
                .latitude(latitude)
                .longitude(longitude)
                .build());
    }

    private Distillery saveDistillery(String name, Destination destination, Double latitude, Double longitude) {
        return distilleryRepository.save(Distillery.builder()
                .name(name)
                .description("Distillery")
                .address(name + " address")
                .latitude(latitude)
                .longitude(longitude)
                .destination(destination)
                .build());
    }

    private void saveReport(Review review, User reporter, ReviewReportStatus status) {
        ReviewReport report = new ReviewReport();
        report.setReview(review);
        report.setReporterUser(reporter);
        report.setStatus(status);
        reviewReportRepository.save(report);
    }
}
