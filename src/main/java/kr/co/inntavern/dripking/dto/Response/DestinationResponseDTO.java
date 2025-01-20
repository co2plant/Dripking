package kr.co.inntavern.dripking.dto.Response;

import kr.co.inntavern.dripking.model.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationResponseDTO {
    private Long id;

    private String name;

    private String description;

    private float latitude;//위도

    private float longitude;//경도

    private String img_url;

    private ItemType itemType;
}
