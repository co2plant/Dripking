package kr.co.inntavern.dripking.controller;

import jakarta.validation.Valid;
import kr.co.inntavern.dripking.dto.Request.ChangePasswordRequestDTO;
import kr.co.inntavern.dripking.dto.Response.JwtResponse;
import kr.co.inntavern.dripking.dto.Request.SignInRequest;
import kr.co.inntavern.dripking.dto.Request.SignUpRequest;
import kr.co.inntavern.dripking.security.JwtUtils;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, BCryptPasswordEncoder passwordEncoder){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        data.put("email", customUserDetails.getEmail());
        data.put("nickname", customUserDetails.getNickname());
        data.put("roles", customUserDetails.getAuthorities());

        response.put("success", true);
        response.put("message", "User is authenticated");
        response.put("data", data);

        return ResponseEntity.ok(response);
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

    // 로그인하지 않은 상태일때도 변경할 수 있도록 - 현재 로그인 상태인 유저를 위한 기능과 분리 필요
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, String> errorMap = new HashMap<>();

        if(!passwordEncoder.matches(changePasswordRequestDTO.getCurrentPassword(), customUserDetails.getPassword())){
            errorMap.put("passwordCheck", "Password does not match"+customUserDetails.getPassword()+" (((( "+passwordEncoder.encode(changePasswordRequestDTO.getCurrentPassword()));
            return ResponseEntity.badRequest().body(errorMap);
        }

        if(changePasswordRequestDTO.getNewPassword().length() < 16 || !(changePasswordRequestDTO.getNewPassword().matches(".*[!@#$%^&*].*"))){
            errorMap.put("passwordCheck", "Password must be at least 8 characters long and contain special characters");
            return ResponseEntity.badRequest().body(errorMap);
        }

        userService.changePassword(customUserDetails.getEmail(), changePasswordRequestDTO.getNewPassword());
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
        httpHeaders.add("Access-Control-Expose-Headers", "Authorization");

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new JwtResponse(jwt,
                customUserDetails.getNickname(),
                customUserDetails.getEmail(),
                roles),
                httpHeaders, HttpStatus.CREATED);
    }
}
