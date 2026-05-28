package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;
import kr.co.inntavern.dripking.model.enumType.ItemType;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PlanResponseDTO {
    private Long id;

    private Long tripId;

    private String name;

    private String description;

    private LocalDate planDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private ItemType itemType;

    private Long targetId;

    private String customPlaceName;

    private String customPlaceAddress;

    private String snapshotName;

    private String snapshotAddress;

    private Double snapshotLatitude;

    private Double snapshotLongitude;

    private Integer sortOrder;

}
