package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TripResponseDTO {
    private Long id;

    private Long userId;

    private String name;

    private String description;

    private Date startDate;

    private Date endDate;

    private String countryName;
}
