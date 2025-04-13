package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TripRequestDTO{
    private Long user_id;

    private String name;

    private String description;

    private Date start_date;

    private Date end_date;
}
