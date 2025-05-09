package kr.co.inntavern.dripking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AlcoholRequestDTO {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    private Long distilleryId;

    @Range(min = 0, max = 100, message = "도수는 0%에서 100% 사이여야 합니다.")
    private float strength;

    @Positive(message = "용량은 양수여야 합니다.")
    private float size;

    private String description;

    private ItemType itemType;

    private String img_url;

    @Length(max = 50, message = "숙성 연도는 50자를 넘을 수 없습니다.")
    private String stated_age;
}
