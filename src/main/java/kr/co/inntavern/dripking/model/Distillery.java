package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.*;

@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
public class Distillery extends Item {

    @Column(nullable = false)
    private String address;

    @Column
    private Double latitude; // 위도

    @Column
    private Double longitude; // 경도

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;
}
