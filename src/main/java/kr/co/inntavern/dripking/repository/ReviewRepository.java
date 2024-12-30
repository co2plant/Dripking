package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review save(Review review);
    Page<Review> findAll(Pageable pageable);

}
