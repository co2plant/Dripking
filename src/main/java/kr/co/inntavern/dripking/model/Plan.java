package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id", unique = true, nullable = false)
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
}