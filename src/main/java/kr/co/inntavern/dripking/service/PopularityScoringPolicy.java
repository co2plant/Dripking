package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.enumType.InteractionEventType;
import org.springframework.stereotype.Component;

@Component
public class PopularityScoringPolicy {
    public int weightOf(InteractionEventType eventType) {
        return switch (eventType) {
            case LIST_CARD_CLICK -> 1;
            case DETAIL_VIEW -> 3;
            case WISHLIST_ADD -> 5;
            case TRIP_PLAN_ADD -> 8;
        };
    }
}
