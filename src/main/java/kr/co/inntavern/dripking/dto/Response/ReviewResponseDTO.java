package kr.co.inntavern.dripking.dto.Response;

import kr.co.inntavern.dripking.model.ReviewType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponseDTO {
    private Long id;
    private String nickname;
    private ReviewType reviewType;
    private Long target_id;
    private Byte rating;
    private String contents;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
