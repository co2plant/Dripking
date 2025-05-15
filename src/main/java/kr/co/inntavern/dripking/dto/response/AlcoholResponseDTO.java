package kr.co.inntavern.dripking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class AlcoholResponseDTO {
    @Schema(description = "주류 ID", example = "1")
    private Long id;

    @Schema(description = "주류 이름", example = "오비라거")
    private String name;

    @Schema(description = "카테고리 ID", example = "1")
    private Long category_id;

    @Schema(description = "양조장 ID", example = "2")
    private Long distillery_id;

    @Schema(description = "도수(%)", example = "4.5")
    private float strength;

    @Schema(description = "숙성 연도", example = "12년")
    private String stated_age;

    @Schema(description = "용량(ml)", example = "500")
    private float size;

    @Schema(description = "설명", example = "시원하고 깔끔한 맛이 특징인 라거 맥주")
    private String description;

    @Schema(description = "등록 시간", example = "14:30:00")
    private Time datetime;

    @Schema(description = "이미지 URL", example = "https://example.com/images/beer.jpg")
    private String img_url;

    @Schema(description = "아이템 타입", example = "BEER")
    private ItemType itemType;
}