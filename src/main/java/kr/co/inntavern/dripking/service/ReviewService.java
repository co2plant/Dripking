package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.Request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.Response.ReviewResponseDTO;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.ReviewType;
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

    // ---------------------------------------------------------------------
    // Select Methods: 모든 엔티티를 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<ReviewResponseDTO> getAllReviews(int page, int size, String criteria, String sort){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));

        return reviewRepository.findAll(pageable).map(this::mapToReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getAllReviewsByUserID(int page, int size, String criteria, String sort, Long user_id){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));

        return reviewRepository.findAllByUserId(user_id, pageable).map(this::mapToReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getAllReviewsByTargetID(int page, int size, String criteria, String sort, String reviewType, Long target_id){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));
        ReviewType reviewTypeEnum = ReviewType.valueOf(reviewType.toUpperCase());

        return reviewRepository.findAllByTargetId(target_id, reviewTypeEnum, pageable).map(this::mapToReviewResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Select Methods: 특정 Id를 가진 엔티티를 반환하는 메서드
    // ---------------------------------------------------------------------
    public Review getReviewById(Long Id){
        return reviewRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다."));
    }

    // ---------------------------------------------------------------------
    // Create Methods: 엔티티를 생성하는 메서드
    // ---------------------------------------------------------------------
    public void createReview(Long user_id, ReviewRequestDTO reviewRequestDTO){
        Review review = new Review();

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));

        review.setUser(user);
        review.setRating(reviewRequestDTO.getRating());
        review.setReviewType(reviewRequestDTO.getReviewType());
        review.setTarget_id(reviewRequestDTO.getTarget_id());
        review.setContents(reviewRequestDTO.getContents());

        reviewRepository.save(review);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
    public void updateReview(Long user_id, Long review_id, ReviewRequestDTO reviewRequestDTO){
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));

        Review review = reviewRepository.findById(review_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다."));

        if(!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 리뷰를 수정할 권한이 없습니다.");
        }

        review.setReviewType(reviewRequestDTO.getReviewType());
        review.setRating(reviewRequestDTO.getRating());
        review.setContents(reviewRequestDTO.getContents());

        reviewRepository.save(review);
    }

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }

    private ReviewResponseDTO mapToReviewResponseDTO(Review review){
        ReviewResponseDTO responseDTO = new ReviewResponseDTO();

        responseDTO.setId(review.getId());
        responseDTO.setNickname(review.getUser().getNickname());
        responseDTO.setReviewType(review.getReviewType());
        responseDTO.setTarget_id(review.getTarget_id());
        responseDTO.setRating(review.getRating());
        responseDTO.setContents(review.getContents());
        responseDTO.setCreatedTime(review.getCreatedAt());
        responseDTO.setModifiedTime(review.getModifiedAt());

        return responseDTO;
    }
}
