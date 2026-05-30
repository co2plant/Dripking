package kr.co.inntavern.dripking.dto.response.dashboard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponseDTO {
    private long totalUsers;
    private long totalDestinations;
    private long totalDistilleries;
    private long totalAlcohols;
    private long totalTrips;
    private long totalPlans;
    private long openReviewReports;
    private long missingDestinationCoordinates;
    private long missingDistilleryCoordinates;
}
