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

    public static CityResponseDTO fromEntity(City city) {
        if (city == null) {
            return null;
        }
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .description(city.getDescription())
                .build();
    }
}
