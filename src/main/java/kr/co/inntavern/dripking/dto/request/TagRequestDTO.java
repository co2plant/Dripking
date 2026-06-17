package kr.co.inntavern.dripking.dto.request;

import kr.co.inntavern.dripking.model.enumType.TagGroup;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequestDTO {
    private String name;
    private String description;
    private TagGroup group;
    private Integer sortOrder;
    private Boolean active;
}
