package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
public class Distillery extends Item{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="distillery_id", unique = true, nullable=false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column(nullable = false)
    private String img_url;

    @OneToMany
    @JoinColumn(name = "alcohol_id")
    private List<Alcohol> alcohol;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;
}
