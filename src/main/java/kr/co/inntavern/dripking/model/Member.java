package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

@Entity
@Getter
public class Member implements UserDetails, Serializable {
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

    private Collection<GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword(){
        return authentication_pw;
    }

    @Override
    public String getUsername(){
        return id.toString();
        //현재 Entity에서는 id를 Long 형으로 지정했으나 이를 String으로 변환하여 반환
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return isEmailVerified && !isLocked;
    }

}