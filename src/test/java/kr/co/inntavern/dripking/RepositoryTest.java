package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.ReviewType;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import kr.co.inntavern.dripking.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RepositoryTest {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    private UsersRepository usersRepository;

    @Test
    void findAllReviewsByUserID(){
        //given
        Review review = Review.builder()
                .contents("Review Content")
                .reviewType(ReviewType.ALCOHOL)
                .users(usersRepository.findById((long)1).get())
                .build();

        reviewRepository.save(review);
        //when
        boolean result = reviewRepository.existsById((long)100);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Review> pageables = reviewRepository.findAllByUserId(1L, reviewTypeEnum, pageable);

        //then
        assertEquals(result, true);
    }
}
