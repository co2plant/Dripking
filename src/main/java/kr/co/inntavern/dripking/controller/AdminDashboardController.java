package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {
    private final UserService userService;

    public AdminDashboardController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("user/number/total")
    public ResponseEntity<?> getNumberOfUsersByIsEmailVerified(boolean isEmailVerified){
        return ResponseEntity.ok(userService.getNumberOfUsersByIsEmailVerified(isEmailVerified));
    }
}
