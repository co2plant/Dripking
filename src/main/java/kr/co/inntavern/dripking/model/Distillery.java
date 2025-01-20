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

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;
}
