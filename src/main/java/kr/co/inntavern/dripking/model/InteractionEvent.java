package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import kr.co.inntavern.dripking.model.enumType.InteractionEventType;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_interaction_event_item_window", columnList = "itemType,targetId,eventType,occurredAt"),
        @Index(name = "idx_interaction_event_occurred_at", columnList = "occurredAt")
})
public class InteractionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interactionEventId", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(length = 80)
    private String anonymousKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "itemType", nullable = false)
    private ItemType itemType;

    @Column(name = "targetId", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "eventType", nullable = false)
    private InteractionEventType eventType;

    @Column(name = "occurredAt", nullable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    void prePersist() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
