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
public class Alcohol extends Item {

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

    @Column
    private Time datetime;

}
