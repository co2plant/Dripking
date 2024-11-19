package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plan_id;

    @Column
    private String name;
    @Column(length = 1000)
    private String description;
    @Column
    private Date plan_date;
    @Column
    private Date plan_time;
    @Column
    private Long location_id;
}