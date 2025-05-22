package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Time;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Alcohol extends Item {

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "distilleryId")
    private Distillery distillery;

    @Column
    private float strength;

    @Column(length = 50)
    private String stated_age;

    @Column
    private float size;

    @Column
    private Time datetime;

}
