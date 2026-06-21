package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.UserTasteProfileRequestDTO;
import kr.co.inntavern.dripking.dto.response.UserTasteProfileResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.UserTasteProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taste-profile")
public class UserTasteProfileController {
    private final UserTasteProfileService userTasteProfileService;

    public UserTasteProfileController(UserTasteProfileService userTasteProfileService) {
        this.userTasteProfileService = userTasteProfileService;
    }

    @GetMapping
    public ResponseEntity<UserTasteProfileResponseDTO> getTasteProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(userTasteProfileService.getTasteProfile(customUserDetails.getId()));
    }

    @PostMapping
    public ResponseEntity<UserTasteProfileResponseDTO> saveTasteProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @RequestBody UserTasteProfileRequestDTO requestDTO) {
        return ResponseEntity.ok(userTasteProfileService.saveTasteProfile(customUserDetails.getId(), requestDTO));
    }
}
