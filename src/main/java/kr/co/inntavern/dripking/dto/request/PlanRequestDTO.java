package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PlanRequestDTO {
    private Long tripId;

    private Long placeId;

    private String name;

    private String description;

    private Date planDate;

    private Date startTime;

    private Date endTime;
}
