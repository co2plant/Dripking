package kr.co.inntavern.dripking.dto;

import kr.co.inntavern.dripking.model.ReviewType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {
    private Byte Rating;

    private String contents;

    private Long target_id;

    private ReviewType reviewType;
}
