package kr.co.inntavern.dripking.dto.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PlanRequestDTO {
    private Long trip_id;

    private Long place_id;

    private String name;

    private String description;

    private Date plan_date;

    private Date start_time;

    private Date end_time;
}
