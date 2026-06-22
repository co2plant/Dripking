package kr.co.inntavern.dripking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TastingNoteRequestDTO {
    private Long alcoholId;
    private String alcoholName;
    private LocalDate tastedAt;
    private PlaceDTO place;
    private RatingsDTO ratings;
    private TagsDTO tags;
    private String pairing;
    private String memo;

    @Getter
    @Setter
    public static class PlaceDTO {
        private String name;
        private BigDecimal lat;
        private BigDecimal lng;
    }

    @Getter
    @Setter
    public static class RatingsDTO {
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
    }

    @Getter
    @Setter
    public static class TagsDTO {
        private List<String> aroma;
        private List<String> palate;
        private List<String> finish;
    }
}
