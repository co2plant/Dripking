package kr.co.inntavern.dripking.service.dashboard;

import kr.co.inntavern.dripking.dto.response.dashboard.DashboardActivityResponseDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.DashboardSummaryResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import kr.co.inntavern.dripking.repository.*;
import kr.co.inntavern.dripking.service.InteractionEventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final DistilleryRepository distilleryRepository;
    private final AlcoholRepository alcoholRepository;
    private final TripRepository tripRepository;
    private final PlanRepository planRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final InteractionEventService interactionEventService;

    public AdminDashboardService(UserRepository userRepository,
                                 DestinationRepository destinationRepository,
                                 DistilleryRepository distilleryRepository,
                                 AlcoholRepository alcoholRepository,
                                 TripRepository tripRepository,
                                 PlanRepository planRepository,
                                 ReviewReportRepository reviewReportRepository,
                                 InteractionEventService interactionEventService) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.distilleryRepository = distilleryRepository;
        this.alcoholRepository = alcoholRepository;
        this.tripRepository = tripRepository;
        this.planRepository = planRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.interactionEventService = interactionEventService;
    }

    public DashboardSummaryResponseDTO getSummary() {
        return DashboardSummaryResponseDTO.builder()
                .totalUsers(userRepository.count())
                .totalDestinations(destinationRepository.count())
                .totalDistilleries(distilleryRepository.count())
                .totalAlcohols(alcoholRepository.count())
                .totalTrips(tripRepository.count())
                .totalPlans(planRepository.count())
                .openReviewReports(reviewReportRepository.countByStatus(ReviewReportStatus.OPEN))
                .missingDestinationCoordinates(destinationRepository.countByLatitudeIsNullOrLongitudeIsNull())
                .missingDistilleryCoordinates(distilleryRepository.countByLatitudeIsNullOrLongitudeIsNull())
                .build();
    }

    public List<DashboardActivityResponseDTO> getActivity() {
        return interactionEventService.getRecentActivities();
    }
}
