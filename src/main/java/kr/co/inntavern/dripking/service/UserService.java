package kr.co.inntavern.dripking.service;

import jakarta.transaction.Transactional;
import kr.co.inntavern.dripking.dto.request.SignInRequest;
import kr.co.inntavern.dripking.dto.request.SignUpRequest;
import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AuthorityRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.CustomUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.authorityRepository = authorityRepository;
    }

    public boolean checkEmailDuplicate(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Long getNumberOfUsersByIsEmailVerified(boolean isEmailVerified){
        return userRepository.countByIsEmailVerified(isEmailVerified);
    }

    @Transactional
    public void changePassword(String email, String newPassword){
        User user = userRepository.findByEmail(email).orElseThrow();

        if(passwordEncoder.matches(newPassword, user.getPassword())){
            throw new RuntimeException("New password must be different from the current password");
        }

        validateNewPassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void validateNewPassword(String newPassword){
        if(newPassword.length() < 16) {
            throw new RuntimeException("Password must be at least 16 characters long");
        }
        if(!newPassword.matches(".*[!@#$%^&*].*")){
            throw new RuntimeException("Password must contain at least one special character");
        }
    }

    @Transactional
    public void userSignUp(SignUpRequest signUpRequest) {
        Set<Authority> roles = new HashSet<>();
        Authority authority = Authority.builder().name(signUpRequest.getUserRole()).build();
        authorityRepository.save(authority);
        roles.add(authority);

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRoles(roles);
        user.setNickname(signUpRequest.getNickname());
        user.setLocked(false);
        //isEmailVerified <- 조정 필요

        userRepository.save(user);
    }

    public User userSignIn(SignInRequest signInRequest) {
        return userRepository.findByEmail(signInRequest.getEmail()).orElseThrow();
    }
}
