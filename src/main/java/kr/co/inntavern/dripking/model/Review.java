package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.sql.Time;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private float Rating;

    @Column
    private String contents;

    @Column
    private Time createTime;


}