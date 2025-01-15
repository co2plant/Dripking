package kr.co.inntavern.dripking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="site_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    //@NotNull
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Authority> roles = new HashSet<>();

    private boolean isEmailVerified;

    private boolean isLocked;

    @Column(unique = true)
    private String nickname;

    private String phoneNumber;

    private String address;
}