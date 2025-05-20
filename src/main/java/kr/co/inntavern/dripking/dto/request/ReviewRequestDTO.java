package kr.co.inntavern.dripking.dto.request;

import kr.co.inntavern.dripking.model.enumType.ReviewType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long userId;
    private ReviewType reviewType;
    private Long targetId;
    private Byte rating;
    private String contents;
}
