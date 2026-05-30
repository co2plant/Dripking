package kr.co.inntavern.dripking.service;

import jakarta.transaction.Transactional;
import kr.co.inntavern.dripking.dto.request.SignInRequest;
import kr.co.inntavern.dripking.dto.request.SignUpRequest;
import kr.co.inntavern.dripking.dto.request.UserProfileUpdateRequestDTO;
import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AuthorityRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.CustomUserDetailsService;
import kr.co.inntavern.dripking.security.UserRole;
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
    public User updateProfile(String email, UserProfileUpdateRequestDTO requestDTO){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다."));

        String nickname = requestDTO.getNickname().trim();
        if(nickname.isBlank()){
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }

        if(!nickname.equals(user.getNickname()) && userRepository.existsByNickname(nickname)){
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        user.setNickname(nickname);
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다."));

        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if(passwordEncoder.matches(newPassword, user.getPassword())){
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        validateNewPassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void validateNewPassword(String newPassword){
        if(newPassword == null || newPassword.length() < 16 || newPassword.length() > 32) {
            throw new IllegalArgumentException("비밀번호는 16자 이상 32자 이하이어야 합니다.");
        }
    }

    @Transactional
    public void userSignUp(SignUpRequest signUpRequest) {
        Set<Authority> roles = new HashSet<>();
        Authority authority = authorityRepository.findAllByName(UserRole.USER)
                .stream()
                .findFirst()
                .orElseGet(() -> authorityRepository.save(Authority.builder().name(UserRole.USER).build()));
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
