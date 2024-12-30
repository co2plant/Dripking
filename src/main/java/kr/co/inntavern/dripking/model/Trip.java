package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@RequiredArgsConstructor
public class Trip extends Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", unique = true, nullable=false)
    //@NotNull
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    private Date start_date;

    private Date end_date;
}