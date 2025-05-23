package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TripResponseDTO {
    private Long id;

    private Long user_id;

    private String name;

    private String description;

    private Date start_date;

    private Date end_date;

    private ItemType itemType;

    private String country_name;
}
