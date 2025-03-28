package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.Request.ReviewRequestDTO;
import kr.co.inntavern.dripking.dto.Response.ReviewResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
                                                      @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
                                                      @RequestParam(required = false, value="user_id") Long user_id,
                                                      @RequestParam(required=false, value="reviewType") String reviewType,
                                                      @RequestParam(required=false, value="target_id") Long target_id){
        if(user_id != null){
            Page<ReviewResponseDTO> paging = reviewService.getAllReviewsByUserID(page, size, criteria, sort, user_id);
            return ResponseEntity.ok(paging);
        }
        if(user_id==null && target_id!=null && reviewType!=null){
            Page<ReviewResponseDTO> paging = reviewService.getAllReviewsByTargetID(page, size, criteria, sort, reviewType, target_id);
            return ResponseEntity.ok(paging);
        }
        Page<ReviewResponseDTO> paging = reviewService.getAllReviews(page, size, criteria, sort);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            reviewService.createReview(customUserDetails.getId(), reviewRequestDTO);

            return ResponseEntity.status(HttpStatus.OK).build();
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public ResponseEntity<?> updateReview(@RequestParam Long review_id, @RequestBody ReviewRequestDTO reviewRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            reviewService.updateReview(customUserDetails.getId(), review_id, reviewRequestDTO);


            return ResponseEntity.status(HttpStatus.OK).build();
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@RequestParam Long id){
        reviewService.deleteReview(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
