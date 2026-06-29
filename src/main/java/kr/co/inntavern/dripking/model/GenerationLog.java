package kr.co.inntavern.dripking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "generation_log", indexes = {
        @Index(name = "idx_generation_log_user_created", columnList = "user_id,created_at"),
        @Index(name = "idx_generation_log_input_hash", columnList = "input_hash")
})
public class GenerationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "anon_id", length = 64)
    private String anonId;

    @Column(name = "input_hash", nullable = false, length = 64)
    private String inputHash;

    @Column(name = "course_id", length = 64)
    private String courseId;

    @Column(name = "tokens_in")
    private int tokensIn;

    @Column(name = "tokens_out")
    private int tokensOut;

    @Column(name = "est_cost", precision = 10, scale = 4)
    private BigDecimal estCost;

    @Column(name = "saved_to_trip", nullable = false)
    private boolean savedToTrip;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
