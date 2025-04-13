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
public class Destination extends Item{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "destination_id", unique = true, nullable=false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private Double latitude;//위도

    @Column
    private Double longitude;//경도

    @Column(nullable = false)
    private String img_url;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;
}
