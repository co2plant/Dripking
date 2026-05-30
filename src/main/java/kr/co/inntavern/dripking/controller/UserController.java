package kr.co.inntavern.dripking.controller;

import jakarta.validation.Valid;
import kr.co.inntavern.dripking.dto.request.ChangePasswordRequestDTO;
import kr.co.inntavern.dripking.dto.request.UserProfileUpdateRequestDTO;
import kr.co.inntavern.dripking.dto.response.JwtTokenResponseDTO;
import kr.co.inntavern.dripking.dto.request.SignInRequest;
import kr.co.inntavern.dripking.dto.request.SignUpRequest;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.security.JwtUtils;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if(authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            //패턴 변수로 변경 가능
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> data = new HashMap<>();

            List<String> roles = customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            data.put("id", customUserDetails.getId());
            data.put("email", customUserDetails.getEmail());
            data.put("nickname", customUserDetails.getNickname());
            data.put("roles", roles);

            response.put("success", true);
            response.put("message", "User is authenticated");
            response.put("data", data);

            return ResponseEntity.ok(response);
        }else{
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User is not authenticated");

            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult){
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for(FieldError fieldError : bindingResult.getFieldErrors()){
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        if(!Objects.equals(signUpRequest.getPassword(), signUpRequest.getConfirmPassword())){
            fieldErrors.put("confirmPassword", "비밀번호가 일치하지 않습니다.");
        }

        if(signUpRequest.getEmail() != null && userService.checkEmailDuplicate(signUpRequest.getEmail())){
            fieldErrors.put("email", "이미 사용 중인 이메일입니다.");
        }

        if(signUpRequest.getNickname() != null && userService.checkNicknameDuplicate(signUpRequest.getNickname())){
            fieldErrors.put("nickname", "이미 사용 중인 닉네임입니다.");
        }

        if(!fieldErrors.isEmpty()){
            return ResponseEntity.badRequest().body(validationError(fieldErrors));
        }

        userService.userSignUp(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileUpdateRequestDTO requestDTO,
                                           BindingResult bindingResult,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for(FieldError fieldError : bindingResult.getFieldErrors()){
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        if(!fieldErrors.isEmpty()){
            return ResponseEntity.badRequest().body(validationError(fieldErrors));
        }

        User updatedUser = userService.updateProfile(customUserDetails.getEmail(), requestDTO);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated");
        response.put("data", userData(updatedUser, customUserDetails));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> validationError(Map<String, String> fieldErrors){
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("code", "VALIDATION_ERROR");
        response.put("message", "Validation failed");
        response.put("fieldErrors", fieldErrors);
        return response;
    }

    private Map<String, Object> userData(User user, CustomUserDetails customUserDetails){
        Map<String, Object> data = new LinkedHashMap<>();
        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        data.put("id", user.getId());
        data.put("email", user.getEmail());
        data.put("nickname", user.getNickname());
        data.put("roles", roles);
        return data;
    }

    // 현재 로그인한 사용자의 비밀번호 변경
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for(FieldError fieldError : bindingResult.getFieldErrors()){
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        if(!Objects.equals(changePasswordRequestDTO.getNewPassword(), changePasswordRequestDTO.getConfirmPassword())){
            fieldErrors.put("confirmPassword", "비밀번호가 일치하지 않습니다.");
        }

        if(!fieldErrors.isEmpty()){
            return ResponseEntity.badRequest().body(validationError(fieldErrors));
        }

        userService.changePassword(
                customUserDetails.getEmail(),
                changePasswordRequestDTO.getCurrentPassword(),
                changePasswordRequestDTO.getNewPassword()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtTokenResponseDTO jwtTokenResponseDTO = jwtUtils.issueJwtToken(authentication);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwtTokenResponseDTO.getAccessToken());
        httpHeaders.add("Access-Control-Expose-Headers", "Authorization");

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
