package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@RequiredArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id", unique = true, nullable=false)
    //@NotNull
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Date plan_date;

    @Column
    private Date plan_time;

    @Column
    private Long location_id;

    @OneToMany
    @JoinColumn(name = "trip_id")
    private List<Trip> trip;
}