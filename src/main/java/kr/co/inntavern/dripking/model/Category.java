package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId", unique = true, nullable=false)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;
}
