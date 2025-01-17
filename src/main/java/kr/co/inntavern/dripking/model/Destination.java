package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private float latitude;//위도

    @Column
    private float longitude;//경도

    @OneToMany
    @JoinColumn(name = "distillery_id")
    private List<Distillery> distillery;

    @Column(nullable = false)
    private String img_url;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;
}
