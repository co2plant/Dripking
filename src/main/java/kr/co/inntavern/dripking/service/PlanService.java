package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.Request.PlanRequestDTO;
import kr.co.inntavern.dripking.model.Plan;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.repository.PlanRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final TripRepository tripRepository;

    public PlanService(PlanRepository planRepository, TripRepository tripRepository){
        this.planRepository = planRepository;
        this.tripRepository = tripRepository;
    }

    public void createPlan(PlanRequestDTO planRequestDTO){
        Trip trip = tripRepository.findById(planRequestDTO.getTrip_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));
        Plan plan = Plan.builder()
                .trip(trip)
                .place_id(planRequestDTO.getPlace_id())
                .name(planRequestDTO.getName())
                .description(planRequestDTO.getDescription())
                .plan_date(planRequestDTO.getPlan_date())
                .start_time(planRequestDTO.getStart_time())
                .end_time(planRequestDTO.getEnd_time())
                .build();

        planRepository.save(plan);
    }

    public void updatePlan(Long plan_id, PlanRequestDTO planRequestDTO){
        Plan plan = planRepository.findById(plan_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다."));
        plan.setPlace_id(planRequestDTO.getPlace_id());
        plan.setName(planRequestDTO.getName());
        plan.setDescription(planRequestDTO.getDescription());
        plan.setPlan_date(planRequestDTO.getPlan_date());
        plan.setStart_time(planRequestDTO.getStart_time());
        plan.setEnd_time(planRequestDTO.getEnd_time());

        planRepository.save(plan);
    }

    public void deletePlanById(Long plan_id){
        Plan plan = planRepository.findById(plan_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다."));
        planRepository.delete(plan);
    }

    public void deletePlanByTripId(Long trip_id){
        Trip trip = tripRepository.findById(trip_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));
        planRepository.deleteAllByTripId(trip_id);
    }
}
