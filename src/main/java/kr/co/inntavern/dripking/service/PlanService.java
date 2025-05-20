package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.PlanRequestDTO;
import kr.co.inntavern.dripking.model.Plan;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.repository.PlanRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final TripRepository tripRepository;

    public PlanService(PlanRepository planRepository, TripRepository tripRepository){
        this.planRepository = planRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public void createPlan(PlanRequestDTO planRequestDTO){
        Trip trip = tripRepository.findById(planRequestDTO.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));
        Plan plan = Plan.builder()
                .trip(trip)
                .placeId(planRequestDTO.getPlaceId())
                .name(planRequestDTO.getName())
                .description(planRequestDTO.getDescription())
                .planDate(planRequestDTO.getPlanDate())
                .startTime(planRequestDTO.getStartTime())
                .endTime(planRequestDTO.getEndTime())
                .build();

        planRepository.save(plan);
    }

    public void updatePlan(Long planId, PlanRequestDTO planRequestDTO){
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다."));
        plan.setPlaceId(planRequestDTO.getPlaceId());
        plan.setName(planRequestDTO.getName());
        plan.setDescription(planRequestDTO.getDescription());
        plan.setPlanDate(planRequestDTO.getPlanDate());
        plan.setStartTime(planRequestDTO.getStartTime());
        plan.setEndTime(planRequestDTO.getEndTime());

        planRepository.save(plan);
    }

    public void deletePlanById(Long planId){
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다."));
        planRepository.delete(plan);
    }

    public void deletePlanByTripId(Long tripId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));
        planRepository.deleteAllByTripId(tripId);
    }
}
