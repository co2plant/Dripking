package kr.co.inntavern.dripking.dto.Response;

import kr.co.inntavern.dripking.model.ItemType;

import java.util.Date;

public class TripResponseDTO {
    private Long id;

    private Long user_id;

    private String name;

    private String description;

    private Date start_date;

    private Date end_date;

    private ItemType ItemType;
}
