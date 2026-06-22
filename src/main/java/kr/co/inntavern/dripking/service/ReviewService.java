package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.response.ReviewResponseDTO;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.util.PlainTextSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewRatingService reviewRatingService;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         ReviewRatingService reviewRatingService){
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewRatingService = reviewRatingService;
    }

    public Page<ReviewResponseDTO> getAllReviews(int page, int size, String criteria, String sort){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));

        return reviewRepository.findAllVisible(ReviewStatus.VISIBLE, pageable).map(this::mapToReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getAllReviewsByUserID(int page, int size, String criteria, String sort, Long userId){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));

        return reviewRepository.findAllVisibleByUserId(userId, ReviewStatus.VISIBLE, pageable).map(this::mapToReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getAllReviewsByTargetID(int page, int size, String criteria, String sort, String itemType, Long targetId){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));
        ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase());

        return reviewRepository.findAllVisibleByTargetId(targetId, itemTypeEnum, ReviewStatus.VISIBLE, pageable).map(this::mapToReviewResponseDTO);
    }

    public Optional<ReviewResponseDTO> getMyReview(Long userId, String itemType, Long targetId) {
        ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase());
        validateReviewableItemType(itemTypeEnum);
        return reviewRepository.findByUserIdAndItemTypeAndTargetId(userId, itemTypeEnum, targetId)
                .filter(review -> review.getStatus() == null || review.getStatus() == ReviewStatus.VISIBLE)
                .map(this::mapToReviewResponseDTO);
    }

    @Transactional
    public ReviewResponseDTO createReview(Long userId, ReviewRequestDTO reviewRequestDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
        validateReviewRequest(reviewRequestDTO);
        String contents = PlainTextSecurityUtils.validateAndNormalize(
                reviewRequestDTO.getContents(),
                PlainTextSecurityUtils.REVIEW_CONTENTS
        );

        Optional<Review> existingReview = reviewRepository.findByUserIdAndItemTypeAndTargetId(
                userId,
                reviewRequestDTO.getItemType(),
                reviewRequestDTO.getTargetId()
        );
        if(existingReview.isPresent()) {
            return mapToReviewResponseDTO(existingReview.get());
        }

        Review review = Review.builder()
                .user(user)
                .rating(reviewRequestDTO.getRating().byteValue())
                .itemType(reviewRequestDTO.getItemType())
                .targetId(reviewRequestDTO.getTargetId())
                .contents(contents)
                .status(ReviewStatus.VISIBLE)
                .build();

        Review savedReview = reviewRepository.save(review);
        reviewRatingService.recalculate(savedReview.getItemType(), savedReview.getTargetId());
        return mapToReviewResponseDTO(savedReview);
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long userId, Long reviewId, ReviewRequestDTO reviewRequestDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
        if(reviewRequestDTO == null) {
            throw new IllegalArgumentException("리뷰 정보가 필요합니다.");
        }
        validateRating(reviewRequestDTO.getRating());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다."));

        if(!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 리뷰를 수정할 권한이 없습니다.");
        }
        String contents = PlainTextSecurityUtils.validateAndNormalize(
                reviewRequestDTO.getContents(),
                PlainTextSecurityUtils.REVIEW_CONTENTS
        );

        review.setRating(reviewRequestDTO.getRating().byteValue());
        review.setContents(contents);

        Review savedReview = reviewRepository.save(review);
        reviewRatingService.recalculate(savedReview.getItemType(), savedReview.getTargetId());
        return mapToReviewResponseDTO(savedReview);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 리뷰입니다."));

        if(!review.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("해당 리뷰를 삭제할 권한이 없습니다.");
        }

        ItemType itemType = review.getItemType();
        Long targetId = review.getTargetId();
        reviewRepository.deleteById(reviewId);
        reviewRatingService.recalculate(itemType, targetId);
    }

    private void validateReviewRequest(ReviewRequestDTO reviewRequestDTO) {
        if(reviewRequestDTO == null) {
            throw new IllegalArgumentException("리뷰 정보가 필요합니다.");
        }
        validateReviewableItemType(reviewRequestDTO.getItemType());
        if(reviewRequestDTO.getTargetId() == null) {
            throw new IllegalArgumentException("targetId가 필요합니다.");
        }
        validateRating(reviewRequestDTO.getRating());
    }

    private void validateReviewableItemType(ItemType itemType) {
        if(itemType != ItemType.ALCOHOL
                && itemType != ItemType.DISTILLERY
                && itemType != ItemType.DESTINATION) {
            throw new IllegalArgumentException("리뷰를 작성할 수 없는 itemType입니다.");
        }
    }

    private void validateRating(Integer rating) {
        if(rating == null) {
            throw new IllegalArgumentException("평점이 필요합니다.");
        }
        if(rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1점에서 5점 사이여야 합니다.");
        }
    }

    private Integer toInteger(Byte value) {
        return value == null ? null : value.intValue();
    }

    private ReviewResponseDTO mapToReviewResponseDTO(Review review){
        ReviewResponseDTO responseDTO = new ReviewResponseDTO();

        responseDTO.setId(review.getId());
        responseDTO.setNickname(review.getUser().getNickname());
        responseDTO.setUserId(review.getUser().getId());
        responseDTO.setItemType(review.getItemType());
        responseDTO.setTargetId(review.getTargetId());
        responseDTO.setRating(toInteger(review.getRating()));
        responseDTO.setContents(review.getContents());
        responseDTO.setStatus(review.getStatus() == null ? ReviewStatus.VISIBLE : review.getStatus());
        responseDTO.setCreatedTime(review.getCreatedAt());
        responseDTO.setModifiedTime(review.getModifiedAt());

        return responseDTO;
    }
}
