package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id", unique = true, nullable=false)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
