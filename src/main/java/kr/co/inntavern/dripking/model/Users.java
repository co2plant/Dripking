package kr.co.inntavern.dripking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable=false)
    //@NotNull
    private Long id;

    @Column
    private String authentication_email;

    @Column
    private boolean isEmailVerified;

    @Column
    private boolean isLocked;

    @Column
    @JsonIgnore
    private String authentication_pw;

    @Column(unique = true)
    private String nickname;

    @Column
    private String phoneNumber;

    @Column
    private String address;

}