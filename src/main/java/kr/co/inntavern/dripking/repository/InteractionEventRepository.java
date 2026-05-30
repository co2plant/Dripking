package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.InteractionEvent;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InteractionEventRepository extends JpaRepository<InteractionEvent, Long> {
    List<InteractionEvent> findAllByItemTypeOrderByOccurredAtDesc(ItemType itemType);

    List<InteractionEvent> findAllByItemTypeAndOccurredAtGreaterThanEqualOrderByOccurredAtDesc(ItemType itemType, LocalDateTime occurredAt);

    List<InteractionEvent> findTop10ByOrderByOccurredAtDesc();
}
