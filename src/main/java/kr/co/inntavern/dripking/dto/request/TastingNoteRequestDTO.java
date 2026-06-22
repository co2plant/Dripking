package kr.co.inntavern.dripking.dto.request;

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
        private Byte appearance;
        private Byte aroma;
        private Byte palate;
        private Byte finish;
        private Byte overall;
    }

    @Getter
    @Setter
    public static class TagsDTO {
        private List<String> aroma;
        private List<String> palate;
        private List<String> finish;
    }
}
