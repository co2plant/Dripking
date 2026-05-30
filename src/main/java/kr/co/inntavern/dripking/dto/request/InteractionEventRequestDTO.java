package kr.co.inntavern.dripking.dto.request;

import kr.co.inntavern.dripking.model.enumType.InteractionEventType;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InteractionEventRequestDTO {
    private ItemType itemType;
    private Long targetId;
    private InteractionEventType eventType;
    private String anonymousKey;
}
