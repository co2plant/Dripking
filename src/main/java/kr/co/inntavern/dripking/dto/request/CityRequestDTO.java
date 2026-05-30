package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityRequestDTO {
    private String name;
    private String description;
    private Long countryId;
}
