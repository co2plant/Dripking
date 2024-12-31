package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.ReviewResponseDTO;
import kr.co.inntavern.dripking.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                      @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                      @RequestParam(required=false, value="orderby", defaultValue="rating") String criteria,
                                                      @RequestParam(required=false, value="sort", defaultValue="DESC") String sort){
        Page<ReviewResponseDTO> paging = reviewService.getAllReviews(page, size, criteria, sort);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        reviewService.createReview(reviewRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
