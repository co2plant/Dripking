package kr.co.inntavern.dripking.dto.Response;

import kr.co.inntavern.dripking.model.ItemType;
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
}
