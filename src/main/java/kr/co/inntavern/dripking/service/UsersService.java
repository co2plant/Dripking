package kr.co.inntavern.dripking.service;

import jakarta.transaction.Transactional;
import kr.co.inntavern.dripking.Request.SignInRequest;
import kr.co.inntavern.dripking.Request.SignUpRequest;
import kr.co.inntavern.dripking.model.Users;
import kr.co.inntavern.dripking.repository.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UsersService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkEmailDuplicate(String email){
        return usersRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname){
        return usersRepository.existsByNickname(nickname);
    }

    public void userSignUp(SignUpRequest signUpRequest) {
        usersRepository.save(signUpRequest.toEntity());
    }

    public void userSignUpWithEncodedPassword(SignUpRequest signUpRequest, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        usersRepository.save(signUpRequest.toEntity(encodedPassword));
    }

    public Users userSignIn(SignInRequest signInRequest) {
        Optional<Users> users = usersRepository.findByEmail(signInRequest.getEmail());

        return users
                .filter(user -> user.getPassword().equals(signInRequest.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }
}
