package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.ReviewReportResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.ReviewModerationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminReviewModerationController {
    private final ReviewModerationService reviewModerationService;

    public AdminReviewModerationController(ReviewModerationService reviewModerationService) {
        this.reviewModerationService = reviewModerationService;
    }

    @GetMapping("/review-reports")
    public ResponseEntity<Page<ReviewReportResponseDTO>> getReviewReports(
            @RequestParam(required = false, value = "page", defaultValue = "0") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size,
            @RequestParam(required = false, value = "status") ReviewReportStatus status) {
        return ResponseEntity.ok(reviewModerationService.getReviewReports(page, size, status));
    }

    @PostMapping("/review-reports/{reportId}/resolve")
    public ResponseEntity<ReviewReportResponseDTO> resolveReport(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                 @PathVariable Long reportId) {
        return ResponseEntity.ok(reviewModerationService.resolveReport(reportId, customUserDetails.getId()));
    }

    @PostMapping("/reviews/{reviewId}/hide")
    public ResponseEntity<Void> hideReview(@PathVariable Long reviewId) {
        reviewModerationService.hideReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewModerationService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
