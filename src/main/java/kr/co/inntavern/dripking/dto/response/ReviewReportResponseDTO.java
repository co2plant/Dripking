package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewReportStatus;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewReportResponseDTO {
    private Long id;
    private ReviewReportStatus status;
    private String reason;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    private Long reviewId;
    private ReviewStatus reviewStatus;
    private ItemType itemType;
    private Long targetId;
    private Byte rating;
    private String contents;
    private Long authorUserId;
    private String authorNickname;

    private Long reporterUserId;
    private String reporterNickname;
    private Long resolvedByUserId;
    private String resolvedByNickname;
}
