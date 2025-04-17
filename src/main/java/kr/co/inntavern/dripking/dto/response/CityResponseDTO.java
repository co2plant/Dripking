package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.City;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CityResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long countryId;
    private String countryName;

    public static CityResponseDTO fromEntity(City city) {
        if (city == null) {
            return null;
        }
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .description(city.getDescription())
                .countryId(city.getCountry() != null ? city.getCountry().getId() : null)
                .countryName(city.getCountry() != null ? city.getCountry().getName() : null)
                .build();
    }
}
