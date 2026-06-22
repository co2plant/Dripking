package kr.co.inntavern.dripking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long userId;
    private ItemType itemType;
    private Long targetId;
    @Schema(description = "리뷰 평점", example = "4", minimum = "1", maximum = "5")
    private Integer rating;
    private String contents;
}
