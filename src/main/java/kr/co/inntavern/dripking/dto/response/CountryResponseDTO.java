package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.Country;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountryResponseDTO {
    private Long id;
    private String name;
    private String description;

    public static CountryResponseDTO fromEntity(Country country) {
        if (country == null) {
            return null;
        }
        return CountryResponseDTO.builder()
                .id(country.getId())
                .name(country.getName())
                .description(country.getDescription())
                .build();
    }
}
