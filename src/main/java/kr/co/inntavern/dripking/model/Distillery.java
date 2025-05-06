package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder; // @Builder 대신 사용

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
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
