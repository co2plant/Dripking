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
    @Column(name = "plan_id", unique = true, nullable=false)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Date plan_date;

    @Column
    private Date start_time;

    @Column
    private Date end_time;

    @Column
    private Long place_id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
}