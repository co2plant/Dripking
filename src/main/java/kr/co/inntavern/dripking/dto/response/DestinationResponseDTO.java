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

    private String img_url;

    private Long country_id;

    private Long city_id;
}
