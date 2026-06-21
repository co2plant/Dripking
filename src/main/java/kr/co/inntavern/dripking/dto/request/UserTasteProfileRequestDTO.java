package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserTasteProfileRequestDTO {
    private List<Long> categories;
    private List<String> flavorTags;
}
