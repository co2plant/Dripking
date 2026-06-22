package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TastingNoteResponseDTO {
    private Long id;
    private Long userId;
    private Long alcoholId;
    private String alcoholName;
    private LocalDate tastedAt;
    private PlaceDTO place;
    @Schema(description = "색/질감 평점", example = "3", minimum = "1", maximum = "5")
    private Integer appearance;
    @Schema(description = "향 평점", example = "4", minimum = "1", maximum = "5")
    private Integer aroma;
    @Schema(description = "맛 평점", example = "4", minimum = "1", maximum = "5")
    private Integer palate;
    @Schema(description = "여운 평점", example = "3", minimum = "1", maximum = "5")
    private Integer finish;
    @Schema(description = "총점", example = "4", minimum = "1", maximum = "5")
    private Integer overall;
    private List<String> aromaTags;
    private List<String> palateTags;
    private List<String> finishTags;
    private String pairing;
    private String memo;
    private int photoCount;
    private Object primaryPhoto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class PlaceDTO {
        private String name;
        private BigDecimal lat;
        private BigDecimal lng;
    }
}
