package kr.co.inntavern.dripking.dto.request;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistItemRequestDTO {
    private ItemType itemType;
    private Long targetId;
}
