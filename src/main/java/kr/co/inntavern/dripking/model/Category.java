package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long category_id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;
}
