package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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