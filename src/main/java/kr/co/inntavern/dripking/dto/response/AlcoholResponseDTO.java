package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class AlcoholResponseDTO {
    private Long id;

    private String name;

    private Long category_id;

    private Long distillery_id;

    private float strength;

    private String stated_age;

    private float size;

    private String description;

    private Time datetime;

    private String img_url;

    private ItemType itemType;
}
