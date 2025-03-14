package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Alcohol extends Item{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alcohol_id", unique = true, nullable=false)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "distillery_id")
    private Distillery distillery;

    @Column
    private float strength;

    @Column(length = 50)
    private String stated_age;

    @Column
    private float size;

//    @Column
//    private int numberOfBottles;

//    @Column
//    private int caskNumber;

//    @Column float rating;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Time datetime;

    @Column(nullable = false)
    private String img_url;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

}
