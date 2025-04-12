package kr.co.inntavern.dripking.dto.Response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistilleryResponseDTO {
    private Long id;

    private String name;

    private String address;

    private String description;

    private String img_url;

    private Double latitude;//위도

    private Double longitude;//경도

    private Long destination_id;

    private ItemType itemType;
}
