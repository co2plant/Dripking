package kr.co.inntavern.dripking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable=false)
    //@NotNull
    private Long id;

    private String email;

    private String role;

    private boolean isEmailVerified;

    private boolean isLocked;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String nickname;

    private String phoneNumber;

    private String address;


}