package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.PlanRequestDTO;
import kr.co.inntavern.dripking.dto.response.PlanResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService){
        this.planService = planService;
    }

    @GetMapping("/api/trips/{tripId}/plans")
    public ResponseEntity<List<PlanResponseDTO>> getPlansByTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @PathVariable Long tripId){
        return ResponseEntity.ok(planService.getPlansByTripId(tripId, customUserDetails.getId()));
    }

    @PostMapping("/api/trips/{tripId}/plans")
    public ResponseEntity<PlanResponseDTO> createPlanByTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable Long tripId,
                                                            @RequestBody PlanRequestDTO planRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.createPlan(tripId, planRequestDTO, customUserDetails.getId()));
    }

    @PutMapping("/api/trips/{tripId}/plans/{planId}")
    public ResponseEntity<PlanResponseDTO> updatePlanByTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable Long tripId,
                                                            @PathVariable Long planId,
                                                            @RequestBody PlanRequestDTO planRequestDTO){
        return ResponseEntity.ok(planService.updatePlan(tripId, planId, planRequestDTO, customUserDetails.getId()));
    }

    @DeleteMapping("/api/trips/{tripId}/plans/{planId}")
    public ResponseEntity<Void> deletePlanByTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @PathVariable Long tripId,
                                                 @PathVariable Long planId){
        planService.deletePlanById(tripId, planId, customUserDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/api/plans")
    public ResponseEntity<PlanResponseDTO> createPlan(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @RequestBody PlanRequestDTO planRequestDTO){
        PlanResponseDTO responseDTO = planService.createPlan(planRequestDTO, customUserDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/api/plans")
    public ResponseEntity<PlanResponseDTO> updatePlan(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @RequestParam Long id,
                                                      @RequestBody PlanRequestDTO planRequestDTO){
        PlanResponseDTO responseDTO = planService.updatePlan(id, planRequestDTO, customUserDetails.getId());

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/api/plans")
    public ResponseEntity<Void> deletePlan(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestParam Long id){
        planService.deletePlanById(id, customUserDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
