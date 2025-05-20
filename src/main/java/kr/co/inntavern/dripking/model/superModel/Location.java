package kr.co.inntavern.dripking.model.superModel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public class Location{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private float rating;

    @Column
    private String imgUrl;
    
    @Column
    private Double latitude;

    @Column
    private Double longitude;
}
