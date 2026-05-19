package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.response.ReviewResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(
            @RequestParam(required = false, value = "page", defaultValue = "0") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size,
            @RequestParam(required = false, value = "orderby", defaultValue = "rating") String criteria,
            @RequestParam(required = false, value = "sort", defaultValue = "DESC") String sort,
            @RequestParam(required = false, value = "userId") Long userId,
            @RequestParam(required = false, value = "itemType") String itemType,
            @RequestParam(required = false, value = "targetId") Long targetId) {
        if (userId != null) {
            Page<ReviewResponseDTO> paging = reviewService.getAllReviewsByUserID(page, size, criteria, sort, userId);
            return ResponseEntity.ok(paging);
        }
        if (targetId != null && itemType != null) {
            Page<ReviewResponseDTO> paging = reviewService.getAllReviewsByTargetID(page, size, criteria, sort,
                    itemType, targetId);
            return ResponseEntity.ok(paging);
        }
        Page<ReviewResponseDTO> paging = reviewService.getAllReviews(page, size, criteria, sort);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ReviewRequestDTO reviewRequestDTO) {
        if (customUserDetails != null) {
            reviewService.createReview(customUserDetails.getId(), reviewRequestDTO);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public ResponseEntity<?> updateReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long review_id, @RequestBody ReviewRequestDTO reviewRequestDTO) {
        return updateAuthenticatedReview(customUserDetails, review_id, reviewRequestDTO);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReviewByPath(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long reviewId, @RequestBody ReviewRequestDTO reviewRequestDTO) {
        return updateAuthenticatedReview(customUserDetails, reviewId, reviewRequestDTO);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long review_id) {
        return deleteAuthenticatedReview(customUserDetails, review_id);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReviewByPath(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long reviewId) {
        return deleteAuthenticatedReview(customUserDetails, reviewId);
    }

    private ResponseEntity<?> updateAuthenticatedReview(CustomUserDetails customUserDetails, Long reviewId,
            ReviewRequestDTO reviewRequestDTO) {
        if (customUserDetails != null) {
            reviewService.updateReview(customUserDetails.getId(), reviewId, reviewRequestDTO);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseEntity<Void> deleteAuthenticatedReview(CustomUserDetails customUserDetails, Long reviewId) {
        if (customUserDetails != null) {
            reviewService.deleteReview(customUserDetails.getId(), reviewId);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
