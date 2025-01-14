package kr.co.inntavern.dripking.controller;

import jakarta.validation.Valid;
import kr.co.inntavern.dripking.Request.SignInRequest;
import kr.co.inntavern.dripking.Request.SignUpRequest;
import kr.co.inntavern.dripking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMap.put(error.getField(), error.getDefaultMessage()));

            return ResponseEntity.badRequest().body(errorMap);
        }

        if(!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("passwordCheck", "Password does not match");;
            return ResponseEntity.badRequest().body(errorMap);
        }

        userService.userSignUpWithEncodedPassword(signUpRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok().build();
    }
}
