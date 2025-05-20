package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planId", unique = true, nullable=false)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Date planDate;

    @Column
    private Date startTime;

    @Column
    private Date endTime;

    @Column
    private Long placeId;

    @ManyToOne
    @JoinColumn(name = "tripId")
    private Trip trip;
}