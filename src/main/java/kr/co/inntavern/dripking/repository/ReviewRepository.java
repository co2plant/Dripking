package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.enumType.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT count(r) > 0 FROM Review r WHERE r.id = :reviewId")
    boolean existsById(@Param(value = "reviewId") Long id);

    Review save(Review review);

    Optional<Review> findById(Long id);

    Page<Review> findAll(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    Page<Review> findAllByUserId(@Param("userId")Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.targetId = :targetId AND r.reviewType = :reviewType")
    Page<Review> findAllByTargetId(@Param("targetId")Long targetId, @Param("reviewType")ReviewType reviewType, Pageable pageable);
}
