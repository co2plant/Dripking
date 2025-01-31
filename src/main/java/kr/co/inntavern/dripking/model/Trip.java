package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Trip extends Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", unique = true, nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    private Date start_date;

    private Date end_date;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;
}