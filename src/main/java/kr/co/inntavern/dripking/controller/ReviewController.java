package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.ReviewRequestDTO;
import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<Page<Review>> getAllReviews(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                      @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                      @RequestParam(required=false, value="orderby", defaultValue="rating") String criteria,
                                                      @RequestParam(required=false, value="sort", defaultValue="DESC") String sort){
        Page<Review> paging = reviewService.getAllReviews(page, size, criteria, sort);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        Review createdReview = reviewService.createReview(reviewRequestDTO);
        return ResponseEntity.ok(createdReview);
    }
}
