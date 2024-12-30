package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.ReviewDTO;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    // ---------------------------------------------------------------------
    // Select Methods: 모든 엔티티를 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<Review> getAllReviews(int page, int size, String criteria, String sort){
        Pageable pageable = (sort.equals("ASC") ?
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, criteria))
                : PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, criteria)));
        return reviewRepository.findAll(pageable);
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
    public Review createReview(ReviewDTO reviewDTO){
        Review review = new Review();
        //user에 관한 내용은 login 기능이 구현되면 추가할 예정
        review.setRating(reviewDTO.getRating());
        review.setReviewType(reviewDTO.getReviewType());
        review.setTarget_id(reviewDTO.getTarget_id());
        review.setContents(reviewDTO.getContents());
        return reviewRepository.save(review);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
    public Review updateReview(Long id, Review Review){
        return reviewRepository.save(Review);
    }

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteReviewById(Long id){
        reviewRepository.deleteById(id);
    }
    
}
