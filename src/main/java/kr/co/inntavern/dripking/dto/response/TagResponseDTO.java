package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.TagGroup;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponseDTO {
    private Long id;
    private String name;
    private String description;
    private TagGroup group;
    private String groupLabel;
    private Integer sortOrder;
    private Boolean active;
}
