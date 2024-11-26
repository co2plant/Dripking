package kr.co.inntavern.dripking.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;

@Entity
public class Distillery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long distillery_id;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String description;
}
