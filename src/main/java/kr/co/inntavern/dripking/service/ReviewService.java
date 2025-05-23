package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.response.ReviewResponseDTO;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository){
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public Page<ReviewResponseDTO> getAllReviews(int page, int size, String criteria, String sort){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));

        return reviewRepository.findAll(pageable).map(this::mapToReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getAllReviewsByUserID(int page, int size, String criteria, String sort, Long userId){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));

        return reviewRepository.findAllByUserId(userId, pageable).map(this::mapToReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getAllReviewsByTargetID(int page, int size, String criteria, String sort, String itemType, Long targetId){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));
        ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase());

        return reviewRepository.findAllByTargetId(targetId, itemTypeEnum, pageable).map(this::mapToReviewResponseDTO);
    }

    public void createReview(Long userId, ReviewRequestDTO reviewRequestDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));

        Review review = Review.builder()
                .user(user)
                .rating(reviewRequestDTO.getRating())
                .itemType(reviewRequestDTO.getItemType())
                .targetId(reviewRequestDTO.getTargetId())
                .contents(reviewRequestDTO.getContents())
                .build();

        reviewRepository.save(review);
    }

    public void updateReview(Long userId, Long reviewId, ReviewRequestDTO reviewRequestDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다."));

        if(!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 리뷰를 수정할 권한이 없습니다.");
        }

        review.setItemType(reviewRequestDTO.getItemType());
        review.setRating(reviewRequestDTO.getRating());
        review.setContents(reviewRequestDTO.getContents());

        reviewRepository.save(review);
    }

    public void deleteReview(Long userId, Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 리뷰입니다."));

        if(!review.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("해당 리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponseDTO mapToReviewResponseDTO(Review review){
        ReviewResponseDTO responseDTO = new ReviewResponseDTO();

        responseDTO.setId(review.getId());
        responseDTO.setNickname(review.getUser().getNickname());
        responseDTO.setItemType(review.getItemType());
        responseDTO.setTargetId(review.getTargetId());
        responseDTO.setRating(review.getRating());
        responseDTO.setContents(review.getContents());
        responseDTO.setCreatedTime(review.getCreatedAt());
        responseDTO.setModifiedTime(review.getModifiedAt());

        return responseDTO;
    }
}
