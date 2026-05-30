package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.WishlistItem;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByUserIdOrderByCreatedAtAscIdAsc(Long userId);

    Optional<WishlistItem> findByUserIdAndItemTypeAndTargetId(Long userId, ItemType itemType, Long targetId);

    void deleteByUserIdAndItemTypeAndTargetId(Long userId, ItemType itemType, Long targetId);
}
