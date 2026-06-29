package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.CreditBalanceResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.CreditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credits")
public class CreditController {
    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping
    public ResponseEntity<CreditBalanceResponseDTO> getCredits(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(creditService.getBalance(customUserDetails.getId()));
    }
}
