package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationResponseDTO {
    private Long id;

    private String name;

    private String description;

    private Double latitude;//위도

    private Double longitude;//경도

    private String imgUrl;

    private Long countryId;

    private String countryName;

    private Long cityId;

    private String cityName;

    private Long categoryId;
}
