package kr.co.inntavern.dripking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponseDTO {
    private Long id;
    private String nickname;
    private Long userId;
    private ItemType itemType;
    private Long targetId;
    @Schema(description = "리뷰 평점", example = "4", minimum = "1", maximum = "5")
    private Integer rating;
    private String contents;
    private ReviewStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
