package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tag_id;

    @Column
    private String name;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @ManyToMany(mappedBy = "tags")
    private Set<Alcohol> alcohols;
}
