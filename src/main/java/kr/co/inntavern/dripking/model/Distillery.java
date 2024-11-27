package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

@Entity
public class Distillery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="distillery_id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String description;


    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
    public void setName(String name) {
        this.name = name;
    }
}
