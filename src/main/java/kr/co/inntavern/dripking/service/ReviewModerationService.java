package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.ReviewReportRequestDTO;
import kr.co.inntavern.dripking.dto.response.ReviewReportResponseDTO;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.ReviewReport;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import kr.co.inntavern.dripking.repository.ReviewReportRepository;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReviewModerationService {
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewRatingService reviewRatingService;

    public ReviewModerationService(ReviewReportRepository reviewReportRepository,
                                   ReviewRepository reviewRepository,
                                   UserRepository userRepository,
                                   ReviewRatingService reviewRatingService) {
        this.reviewReportRepository = reviewReportRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewRatingService = reviewRatingService;
    }

    @Transactional
    public ReviewReportResponseDTO reportReview(Long reviewId, Long reporterUserId, ReviewReportRequestDTO requestDTO) {
        return reviewReportRepository
                .findByReviewIdAndReporterUserIdAndStatus(reviewId, reporterUserId, ReviewReportStatus.OPEN)
                .map(this::mapToResponseDTO)
                .orElseGet(() -> createReviewReport(reviewId, reporterUserId, requestDTO));
    }

    @Transactional(readOnly = true)
    public Page<ReviewReportResponseDTO> getReviewReports(int page, int size, ReviewReportStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
        Page<ReviewReport> reports = status == null
                ? reviewReportRepository.findAll(pageable)
                : reviewReportRepository.findAllByStatus(status, pageable);
        return reports.map(this::mapToResponseDTO);
    }

    @Transactional
    public ReviewReportResponseDTO resolveReport(Long reportId, Long adminUserId) {
        ReviewReport report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리뷰 신고가 존재하지 않습니다."));
        User admin = getUser(adminUserId);

        report.setStatus(ReviewReportStatus.RESOLVED);
        report.setResolvedByUser(admin);
        report.setResolvedAt(LocalDateTime.now());
        return mapToResponseDTO(reviewReportRepository.save(report));
    }

    @Transactional
    public void hideReview(Long reviewId) {
        Review review = getReview(reviewId);
        review.setStatus(ReviewStatus.HIDDEN);
        reviewRepository.save(review);
        reviewRatingService.recalculate(review.getItemType(), review.getTargetId());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = getReview(reviewId);
        review.setStatus(ReviewStatus.DELETED);
        reviewRepository.save(review);
        reviewRatingService.recalculate(review.getItemType(), review.getTargetId());
    }

    private ReviewReportResponseDTO createReviewReport(Long reviewId, Long reporterUserId, ReviewReportRequestDTO requestDTO) {
        Review review = getReview(reviewId);
        User reporter = getUser(reporterUserId);

        ReviewReport report = new ReviewReport();
        report.setReview(review);
        report.setReporterUser(reporter);
        report.setStatus(ReviewReportStatus.OPEN);
        if (requestDTO != null) {
            report.setReason(requestDTO.getReason());
            report.setMemo(requestDTO.getMemo());
        }

        return mapToResponseDTO(reviewReportRepository.save(report));
    }

    private Review getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다."));
        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 리뷰입니다.");
        }
        return review;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
    }

    private ReviewReportResponseDTO mapToResponseDTO(ReviewReport report) {
        ReviewReportResponseDTO responseDTO = new ReviewReportResponseDTO();
        Review review = report.getReview();
        User author = review.getUser();
        User reporter = report.getReporterUser();
        User resolver = report.getResolvedByUser();

        responseDTO.setId(report.getId());
        responseDTO.setStatus(report.getStatus());
        responseDTO.setReason(report.getReason());
        responseDTO.setMemo(report.getMemo());
        responseDTO.setCreatedAt(report.getCreatedAt());
        responseDTO.setResolvedAt(report.getResolvedAt());

        responseDTO.setReviewId(review.getId());
        responseDTO.setReviewStatus(review.getStatus() == null ? ReviewStatus.VISIBLE : review.getStatus());
        responseDTO.setItemType(review.getItemType());
        responseDTO.setTargetId(review.getTargetId());
        responseDTO.setRating(review.getRating());
        responseDTO.setContents(review.getContents());
        if (author != null) {
            responseDTO.setAuthorUserId(author.getId());
            responseDTO.setAuthorNickname(author.getNickname());
        }

        if (reporter != null) {
            responseDTO.setReporterUserId(reporter.getId());
            responseDTO.setReporterNickname(reporter.getNickname());
        }

        if (resolver != null) {
            responseDTO.setResolvedByUserId(resolver.getId());
            responseDTO.setResolvedByNickname(resolver.getNickname());
        }

        return responseDTO;
    }
}
