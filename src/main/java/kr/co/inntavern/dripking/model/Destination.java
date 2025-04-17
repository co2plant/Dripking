package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Destination extends Item {

    @Column
    private Double latitude;// 위도

    @Column
    private Double longitude;// 경도

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
