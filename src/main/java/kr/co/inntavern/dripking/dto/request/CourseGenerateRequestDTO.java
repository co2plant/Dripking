package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CourseGenerateRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String countryName;
    private String regionHint;
    private TasteDTO taste = new TasteDTO();
    private List<Long> wishlistItemIds = new ArrayList<>();
    private String anonId;
    private String captchaToken;

    @Getter
    @Setter
    public static class TasteDTO {
        private List<Long> categories = new ArrayList<>();
        private List<String> flavorTags = new ArrayList<>();
    }
}
