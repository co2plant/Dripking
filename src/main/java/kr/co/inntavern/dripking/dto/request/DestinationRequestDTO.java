package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationRequestDTO {
    private String name;
    private String description;
    private String imgUrl;
    private String imgObjectKey;
    private Double latitude;
    private Double longitude;
    private Long cityId;
    private Long categoryId;
}
