package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "review",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_user_target",
                        columnNames = {"userId", "itemType", "targetId"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId", unique = true, nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private Long targetId;

    @Range(min = 0, max = 5)
    private Byte rating;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @PrePersist
    void prePersist() {
        if (status == null) {
            status = ReviewStatus.VISIBLE;
        }
    }

}
