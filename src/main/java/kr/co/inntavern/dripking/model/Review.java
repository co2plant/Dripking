package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", unique = true, nullable=false)
    //@NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    private Long target_id;

    //Rating은 0이 0.0점 10이 5.0점으로 0.5점씩 증가하는 매커니즘을 가지고 있음.
    //float형은 4~8Byte이기때문에 1Byte로 변환 -> 공간적 이점을 가져감.
    @Range(min = 0, max = 10)
    private Byte rating;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime modifiedTime;

}

