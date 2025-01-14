package kr.co.inntavern.dripking.service;

import jakarta.transaction.Transactional;
import kr.co.inntavern.dripking.Request.SignInRequest;
import kr.co.inntavern.dripking.Request.SignUpRequest;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkEmailDuplicate(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public void userSignUp(SignUpRequest signUpRequest) {
        userRepository.save(signUpRequest.toEntity());
    }

    public void userSignUpWithEncodedPassword(SignUpRequest signUpRequest) {
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        userRepository.save(signUpRequest.toEntity(encodedPassword));
    }

    public User userSignIn(SignInRequest signInRequest) {
        Optional<User> users = userRepository.findByEmail(signInRequest.getEmail());
        if(users.isEmpty()) throw new IllegalArgumentException("ss Invalid email or password");

        return users
                .filter(user -> user.getPassword().equals(passwordEncoder.encode(signInRequest.getPassword())))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }
}
