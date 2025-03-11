package kr.co.inntavern.dripking.dto.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PlanResponseDTO {
    private Long user_id;

    private String name;

    private String description;

    private Date plan_date;

    private Date start_time;

    private Date end_time;

    private Long place_id;

    private Long trip_id;
}
