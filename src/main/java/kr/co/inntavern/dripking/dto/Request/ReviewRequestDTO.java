package kr.co.inntavern.dripking.dto.Request;

import kr.co.inntavern.dripking.model.enumType.ReviewType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long user_id;
    private ReviewType reviewType;
    private Long target_id;
    private Byte rating;
    private String contents;
}
