package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.PlanRequestDTO;
import kr.co.inntavern.dripking.dto.response.PlanResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.model.Plan;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.PlanRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final TripRepository tripRepository;
    private final AlcoholRepository alcoholRepository;
    private final DistilleryRepository distilleryRepository;
    private final DestinationRepository destinationRepository;

    public PlanService(PlanRepository planRepository,
                       TripRepository tripRepository,
                       AlcoholRepository alcoholRepository,
                       DistilleryRepository distilleryRepository,
                       DestinationRepository destinationRepository){
        this.planRepository = planRepository;
        this.tripRepository = tripRepository;
        this.alcoholRepository = alcoholRepository;
        this.distilleryRepository = distilleryRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public PlanResponseDTO createPlan(PlanRequestDTO planRequestDTO, Long userId){
        if(planRequestDTO.getTripId() == null){
            throw new IllegalArgumentException("tripId가 필요합니다.");
        }
        return createPlan(planRequestDTO.getTripId(), planRequestDTO, userId);
    }

    @Transactional(readOnly = true)
    public List<PlanResponseDTO> getPlansByTripId(Long tripId, Long userId){
        getOwnedTrip(tripId, userId);
        return planRepository.findAllByTripIdOrderBySortOrderAscPlanDateAscStartTimeAscIdAsc(tripId)
                .stream()
                .map(this::mapToPlanResponseDTO)
                .toList();
    }

    @Transactional
    public PlanResponseDTO createPlan(Long tripId, PlanRequestDTO planRequestDTO, Long userId){
        Trip trip = getOwnedTrip(tripId, userId);
        Plan plan = new Plan();
        plan.setTrip(trip);
        applyPlanRequest(plan, planRequestDTO, true);

        return mapToPlanResponseDTO(planRepository.save(plan));
    }

    @Transactional
    public PlanResponseDTO updatePlan(Long planId, PlanRequestDTO planRequestDTO, Long userId){
        Plan plan = getOwnedPlan(planId, userId);
        applyPlanRequest(plan, planRequestDTO, false);

        return mapToPlanResponseDTO(planRepository.save(plan));
    }

    @Transactional
    public PlanResponseDTO updatePlan(Long tripId, Long planId, PlanRequestDTO planRequestDTO, Long userId){
        getOwnedTrip(tripId, userId);
        Plan plan = getPlanInTrip(tripId, planId);
        applyPlanRequest(plan, planRequestDTO, false);

        return mapToPlanResponseDTO(planRepository.save(plan));
    }

    @Transactional
    public void deletePlanById(Long planId, Long userId){
        Plan plan = getOwnedPlan(planId, userId);
        planRepository.delete(plan);
    }

    @Transactional
    public void deletePlanById(Long tripId, Long planId, Long userId){
        getOwnedTrip(tripId, userId);
        Plan plan = getPlanInTrip(tripId, planId);
        planRepository.delete(plan);
    }

    @Transactional
    public void deletePlanByTripId(Long tripId, Long userId){
        getOwnedTrip(tripId, userId);
        planRepository.deleteAllByTripId(tripId);
    }

    private void applyPlanRequest(Plan plan, PlanRequestDTO planRequestDTO, boolean forceSnapshotRefresh){
        ItemType nextItemType = planRequestDTO.getItemType();
        Long nextTargetId = planRequestDTO.getResolvedTargetId();
        boolean targetChanged = !Objects.equals(plan.getItemType(), nextItemType)
                || !Objects.equals(plan.getTargetId(), nextTargetId);
        boolean customPlace = nextItemType == null || nextItemType == ItemType.CUSTOM_PLACE;

        plan.setName(planRequestDTO.getName());
        plan.setDescription(planRequestDTO.getDescription());
        plan.setPlanDate(planRequestDTO.getPlanDate());
        plan.setStartTime(planRequestDTO.getStartTime());
        plan.setEndTime(planRequestDTO.getEndTime());
        plan.setItemType(nextItemType);
        plan.setTargetId(nextTargetId);
        plan.setCustomPlaceName(planRequestDTO.getCustomPlaceName());
        plan.setCustomPlaceAddress(planRequestDTO.getCustomPlaceAddress());
        plan.setSortOrder(planRequestDTO.getSortOrder());

        if(forceSnapshotRefresh || targetChanged || customPlace || plan.getSnapshotName() == null){
            applySnapshot(plan, planRequestDTO);
        }
    }

    private void applySnapshot(Plan plan, PlanRequestDTO planRequestDTO){
        if(plan.getItemType() == null || plan.getItemType() == ItemType.CUSTOM_PLACE){
            plan.setSnapshotName(firstNonBlank(planRequestDTO.getCustomPlaceName(), planRequestDTO.getName()));
            plan.setSnapshotAddress(planRequestDTO.getCustomPlaceAddress());
            plan.setSnapshotLatitude(null);
            plan.setSnapshotLongitude(null);
            return;
        }

        Long targetId = plan.getTargetId();
        if(targetId == null){
            plan.setSnapshotName(planRequestDTO.getName());
            plan.setSnapshotAddress(planRequestDTO.getCustomPlaceAddress());
            plan.setSnapshotLatitude(null);
            plan.setSnapshotLongitude(null);
            return;
        }

        switch(plan.getItemType()){
            case ALCOHOL -> applyAlcoholSnapshot(plan, targetId);
            case DISTILLERY -> applyDistillerySnapshot(plan, targetId);
            case DESTINATION -> applyDestinationSnapshot(plan, targetId);
            default -> {
                plan.setSnapshotName(firstNonBlank(planRequestDTO.getCustomPlaceName(), planRequestDTO.getName()));
                plan.setSnapshotAddress(planRequestDTO.getCustomPlaceAddress());
                plan.setSnapshotLatitude(null);
                plan.setSnapshotLongitude(null);
            }
        }
    }

    private void applyAlcoholSnapshot(Plan plan, Long alcoholId){
        Alcohol alcohol = alcoholRepository.findById(alcoholId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
        plan.setSnapshotName(alcohol.getName());

        Distillery distillery = alcohol.getDistillery();
        if(distillery != null){
            plan.setSnapshotAddress(distillery.getAddress());
            plan.setSnapshotLatitude(distillery.getLatitude());
            plan.setSnapshotLongitude(distillery.getLongitude());
        }
    }

    private void applyDistillerySnapshot(Plan plan, Long distilleryId){
        Distillery distillery = distilleryRepository.findById(distilleryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 양조장이 존재하지 않습니다."));
        plan.setSnapshotName(distillery.getName());
        plan.setSnapshotAddress(distillery.getAddress());
        plan.setSnapshotLatitude(distillery.getLatitude());
        plan.setSnapshotLongitude(distillery.getLongitude());
    }

    private void applyDestinationSnapshot(Plan plan, Long destinationId){
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행지가 존재하지 않습니다."));
        plan.setSnapshotName(destination.getName());
        plan.setSnapshotAddress(buildDestinationAddress(destination));
        plan.setSnapshotLatitude(destination.getLatitude());
        plan.setSnapshotLongitude(destination.getLongitude());
    }

    private String buildDestinationAddress(Destination destination){
        if(destination.getCity() == null){
            return null;
        }
        if(destination.getCity().getCountry() == null){
            return destination.getCity().getName();
        }
        return destination.getCity().getName() + ", " + destination.getCity().getCountry().getName();
    }

    private PlanResponseDTO mapToPlanResponseDTO(Plan plan){
        PlanResponseDTO responseDTO = new PlanResponseDTO();
        responseDTO.setId(plan.getId());
        responseDTO.setTripId(plan.getTrip().getId());
        responseDTO.setName(plan.getName());
        responseDTO.setDescription(plan.getDescription());
        responseDTO.setPlanDate(plan.getPlanDate());
        responseDTO.setStartTime(plan.getStartTime());
        responseDTO.setEndTime(plan.getEndTime());
        responseDTO.setItemType(plan.getItemType());
        responseDTO.setTargetId(plan.getTargetId());
        responseDTO.setCustomPlaceName(plan.getCustomPlaceName());
        responseDTO.setCustomPlaceAddress(plan.getCustomPlaceAddress());
        responseDTO.setSnapshotName(plan.getSnapshotName());
        responseDTO.setSnapshotAddress(plan.getSnapshotAddress());
        responseDTO.setSnapshotLatitude(plan.getSnapshotLatitude());
        responseDTO.setSnapshotLongitude(plan.getSnapshotLongitude());
        responseDTO.setSortOrder(plan.getSortOrder());
        return responseDTO;
    }

    private Trip getOwnedTrip(Long tripId, Long userId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));
        if(trip.getUser() == null || !trip.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("해당 여행에 접근할 권한이 없습니다.");
        }
        return trip;
    }

    private Plan getOwnedPlan(Long planId, Long userId){
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다."));
        if(plan.getTrip() == null || plan.getTrip().getUser() == null || !plan.getTrip().getUser().getId().equals(userId)){
            throw new IllegalArgumentException("해당 일정에 접근할 권한이 없습니다.");
        }
        return plan;
    }

    private Plan getPlanInTrip(Long tripId, Long planId){
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다."));
        if(plan.getTrip() == null || !plan.getTrip().getId().equals(tripId)){
            throw new IllegalArgumentException("해당 여행에 포함된 일정이 아닙니다.");
        }
        return plan;
    }

    private String firstNonBlank(String primary, String fallback){
        if(primary != null && !primary.isBlank()){
            return primary;
        }
        return fallback;
    }
}
