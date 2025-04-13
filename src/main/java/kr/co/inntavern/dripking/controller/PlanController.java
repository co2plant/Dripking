package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.PlanRequestDTO;
import kr.co.inntavern.dripking.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService){
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<Void> createPlan(@RequestBody PlanRequestDTO planRequestDTO){
        planService.createPlan(planRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updatePlan(@RequestParam Long id, @RequestBody PlanRequestDTO planRequestDTO){
        planService.updatePlan(id, planRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePlan(@RequestParam Long id){
        planService.deletePlanById(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
