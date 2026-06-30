package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CourseGenerateResponseDTO {
    private String courseId;
    private String generationMode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String countryName;
    private String regionHint;
    private int durationDays;
    private int sourceItemCount;
    private int creditCharged;
    private Integer remainingCredit;
    private Integer guestTrialLimit;
    private Integer guestTrialUsed;
    private Integer guestTrialRemaining;
    private boolean cacheHit;
    private List<DayDTO> days = new ArrayList<>();

    @Getter
    @Setter
    public static class DayDTO {
        private int day;
        private LocalDate date;
        private List<PlanDTO> plans = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PlanDTO {
        private int order;
        private ItemType itemType;
        private Long targetId;
        private String name;
        private String description;
        private String imgUrl;
        private float rating;
        private double score;
        private String source;
        private String time;
        private Integer travelMinutesFromPrev;
    }
}
