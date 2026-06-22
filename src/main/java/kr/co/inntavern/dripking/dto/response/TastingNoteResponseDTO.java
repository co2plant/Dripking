package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

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
    private Byte appearance;
    private Byte aroma;
    private Byte palate;
    private Byte finish;
    private Byte overall;
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
