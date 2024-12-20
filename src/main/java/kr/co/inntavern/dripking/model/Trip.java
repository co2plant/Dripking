package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Trip extends Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", unique = true, nullable = false)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Date start_date;

    @Column
    private Date end_date;
}