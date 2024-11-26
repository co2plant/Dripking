package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    @Column
    private String authentication_id;

    @Column
    private String authentication_pw;

    @Column
    private String name;

    @Column
    private String phoneNumber;

    @Column
    private String address;

    @Column(unique=true)
    private String email_address;
}

/**
 * User {
 *     user_id Long PK
 *     authentication_id Varchar(50)
 *     authentication_pw Varchar(255)
 *     name Varchar
 *     phoneNumber String
 *     address String
 *     email_address String "unique"
 * }
 */