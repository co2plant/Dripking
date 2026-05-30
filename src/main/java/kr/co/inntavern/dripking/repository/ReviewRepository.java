package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Review;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.status IS NULL OR r.status = :visibleStatus")
    Page<Review> findAllVisible(@Param("visibleStatus") ReviewStatus visibleStatus, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    Page<Review> findAllByUserId(@Param("userId")Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND (r.status IS NULL OR r.status = :visibleStatus)")
    Page<Review> findAllVisibleByUserId(@Param("userId")Long userId, @Param("visibleStatus") ReviewStatus visibleStatus, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.targetId = :targetId AND r.itemType = :itemType")
    Page<Review> findAllByTargetId(@Param("targetId")Long targetId, @Param("itemType") ItemType itemType, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.targetId = :targetId AND r.itemType = :itemType AND (r.status IS NULL OR r.status = :visibleStatus)")
    Page<Review> findAllVisibleByTargetId(@Param("targetId")Long targetId,
                                          @Param("itemType") ItemType itemType,
                                          @Param("visibleStatus") ReviewStatus visibleStatus,
                                          Pageable pageable);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r " +
            "WHERE r.targetId = :targetId " +
            "AND r.itemType = :itemType " +
            "AND (r.status IS NULL OR r.status = :visibleStatus)")
    Double calculateVisibleAverageRating(@Param("targetId") Long targetId,
                                         @Param("itemType") ItemType itemType,
                                         @Param("visibleStatus") ReviewStatus visibleStatus);

    Optional<Review> findByUserIdAndItemTypeAndTargetId(Long userId, ItemType itemType, Long targetId);
}
