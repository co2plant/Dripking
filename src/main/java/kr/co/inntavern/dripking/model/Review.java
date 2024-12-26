package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.sql.Time;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @Column
    private float Rating;

    @Column
    private String contents;

    @Column
    private Time createTime;


}