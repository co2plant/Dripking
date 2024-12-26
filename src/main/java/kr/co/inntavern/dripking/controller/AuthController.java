package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.Reponse.LoginResponse;
import kr.co.inntavern.dripking.Request.LoginRequest;
import kr.co.inntavern.dripking.config.JwtIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtIssuer jwtIssuer;

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest){
        var token = jwtIssuer.issueToken(1L, loginRequest.getEmail(), List.of("USER"));
        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
