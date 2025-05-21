package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
public class Destination extends Item {

    @Column
    private Double latitude;// 위도

    @Column
    private Double longitude;// 경도

    @ManyToOne
    @JoinColumn(name = "cityId")
    private City city;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
