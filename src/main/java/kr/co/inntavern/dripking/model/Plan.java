package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planId", unique = true, nullable=false)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column
    private LocalDate planDate;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column
    private Long targetId;

    @Column
    private String customPlaceName;

    @Column
    private String customPlaceAddress;

    @Column
    private String snapshotName;

    @Column
    private String snapshotAddress;

    @Column
    private Double snapshotLatitude;

    @Column
    private Double snapshotLongitude;

    @Column
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "tripId")
    private Trip trip;
}
