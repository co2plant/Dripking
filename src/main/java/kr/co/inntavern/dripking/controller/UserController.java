package kr.co.inntavern.dripking.controller;

import jakarta.validation.Valid;
import kr.co.inntavern.dripking.dto.Response.JwtResponse;
import kr.co.inntavern.dripking.dto.Request.SignInRequest;
import kr.co.inntavern.dripking.dto.Request.SignUpRequest;
import kr.co.inntavern.dripking.security.JwtUtils;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        if(!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("passwordCheck", "Password does not match");
            return ResponseEntity.badRequest().body(errorMap);
        }

        userService.userSignUp(signUpRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.issueJwtToken(authentication);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new JwtResponse(jwt,
                customUserDetails.getNickname(),
                customUserDetails.getEmail(),
                roles),
                httpHeaders, HttpStatus.OK);
    }
}
