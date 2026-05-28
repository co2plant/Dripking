package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;
import kr.co.inntavern.dripking.model.enumType.ItemType;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PlanRequestDTO {
    private Long tripId;

    private Long placeId;

    private ItemType itemType;

    private Long targetId;

    private String name;

    private String description;

    private LocalDate planDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String customPlaceName;

    private String customPlaceAddress;

    private Integer sortOrder;

    public Long getResolvedTargetId() {
        return targetId != null ? targetId : placeId;
    }
}
