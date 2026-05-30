package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistilleryRequestDTO {
    private String name;
    private String description;
    private String imgUrl;
    private String imgObjectKey;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long destinationId;
}
