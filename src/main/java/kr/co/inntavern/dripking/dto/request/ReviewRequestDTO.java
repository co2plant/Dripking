package kr.co.inntavern.dripking.dto.request;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long userId;
    private ItemType itemType;
    private Long targetId;
    private Byte rating;
    private String contents;
}
