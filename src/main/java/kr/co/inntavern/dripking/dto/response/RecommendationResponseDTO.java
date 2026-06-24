package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecommendationResponseDTO {
    private List<ItemDTO> items = new ArrayList<>();

    @Getter
    @Setter
    public static class ItemDTO {
        private ItemType itemType;
        private Long targetId;
        private String name;
        private String description;
        private String imgUrl;
        private float rating;
        private double score;
    }
}
