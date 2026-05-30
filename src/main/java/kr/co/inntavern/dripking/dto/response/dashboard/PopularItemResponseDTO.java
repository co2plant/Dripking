package kr.co.inntavern.dripking.dto.response.dashboard;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularItemResponseDTO {
    private ItemType itemType;
    private Long targetId;
    private String name;
    private String description;
    private String imgUrl;
    private float rating;
    private double score;
    private int eventCount;
}
