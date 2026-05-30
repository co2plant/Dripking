package kr.co.inntavern.dripking.dto.response.dashboard;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardActivityResponseDTO {
    private String activityType;
    private String title;
    private String description;
    private LocalDateTime occurredAt;
}
