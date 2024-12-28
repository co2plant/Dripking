package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.sql.Time;

@Entity
@RequiredArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", unique = true, nullable=false)
    //@NotNull
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