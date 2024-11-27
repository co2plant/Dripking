package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.Set;

@Entity
public class Alcohol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alcohol_id", unique = true, nullable = false)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private float strength;

    @Column(length = 50)
    private String stated_age;

    @Column
    private float size;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Time datetime;

    @ManyToMany
    @JoinTable(
            name = "alcohol_tag",
            joinColumns = @JoinColumn(name = "alcohol_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    private Set<Tag> tags;
}
