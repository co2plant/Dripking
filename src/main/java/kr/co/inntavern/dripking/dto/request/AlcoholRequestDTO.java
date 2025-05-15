package kr.co.inntavern.dripking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Data
public class AlcoholRequestDTO {
    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "이름", example = "맥주")
    private String name;

    @NotNull(message = "카테고리는 필수입니다.")
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "양조장 ID", example = "1")
    private Long distilleryId;

    @Range(min = 0, max = 100, message = "도수는 0%에서 100% 사이여야 합니다.")
    @Schema(description = "도수", example = "5.0")
    private float strength;

    @Positive(message = "용량은 양수여야 합니다.")
    @Schema(description = "용량", example = "500")
    private float size;

    @Schema(description = "설명", example = "맛있는 맥주 올드 라스푸틴")
    private String description;

    @Schema(description = "아이템 타입", example = "맥주")
    private ItemType itemType;
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String img_url;

    @Length(max = 50, message = "숙성 연도는 50자를 넘을 수 없습니다.")
    @Schema(description = "숙성 연도", example = "12년")
    private String stated_age;
}
