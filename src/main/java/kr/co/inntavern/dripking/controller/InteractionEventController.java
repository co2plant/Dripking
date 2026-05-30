package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.InteractionEventRequestDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.InteractionEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interaction-events")
public class InteractionEventController {
    private final InteractionEventService interactionEventService;

    public InteractionEventController(InteractionEventService interactionEventService) {
        this.interactionEventService = interactionEventService;
    }

    @PostMapping
    public ResponseEntity<Void> recordEvent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @RequestBody InteractionEventRequestDTO requestDTO) {
        interactionEventService.recordEvent(requestDTO, customUserDetails);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
