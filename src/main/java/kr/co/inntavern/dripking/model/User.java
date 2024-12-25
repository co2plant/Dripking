package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String authentication_email;

    @Column
    private boolean isEmailVerified;

    @Column
    private boolean isLocked;

    @Column
    private String authentication_pw;

    @Column(unique = true)
    private String nickname;

    @Column
    private String phoneNumber;

    @Column
    private String address;

}