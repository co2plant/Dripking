package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String authentication_email;

    @Column
    private String authentication_pw;

    @Column(unique = true)
    private String name;

    @Column
    private String phoneNumber;

    @Column
    private String address;
}