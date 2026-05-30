package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.ReviewReport;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    Optional<ReviewReport> findByReviewIdAndReporterUserIdAndStatus(Long reviewId, Long reporterUserId, ReviewReportStatus status);

    Page<ReviewReport> findAllByStatus(ReviewReportStatus status, Pageable pageable);

    long countByStatus(ReviewReportStatus status);
}
