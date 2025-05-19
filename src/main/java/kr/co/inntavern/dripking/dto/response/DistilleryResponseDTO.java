package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistilleryResponseDTO {
    private Long id;

    private String name;

    private String address;

    private String description;

    private String imgUrl;

    private Double latitude;//위도

    private Double longitude;//경도

    private Long destinationId;
}
