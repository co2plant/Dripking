package kr.co.inntavern.dripking.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDashboardResponseDTO {
    private Long id;

    private String name;

    private String countryName;

    private String cityName;

    private String categoryName;

    private double reviewRating;
}
