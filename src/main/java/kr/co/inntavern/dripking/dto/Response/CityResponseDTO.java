package kr.co.inntavern.dripking.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long country_id;
}
