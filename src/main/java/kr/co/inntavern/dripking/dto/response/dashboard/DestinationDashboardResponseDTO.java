package kr.co.inntavern.dripking.dto.response.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DestinationDashboardResponseDTO {
    private Long id;

    private String name;

    private String countryName;

    private String cityName;

    private String categoryName;

    private double reviewRating;
}
