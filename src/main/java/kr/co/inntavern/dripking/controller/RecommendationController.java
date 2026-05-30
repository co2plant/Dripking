package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.dashboard.PopularItemResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.service.InteractionEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final InteractionEventService interactionEventService;

    public RecommendationController(InteractionEventService interactionEventService) {
        this.interactionEventService = interactionEventService;
    }

    @GetMapping("/popular-destinations")
    public ResponseEntity<List<PopularItemResponseDTO>> getPopularDestinations(@RequestParam(defaultValue = "7d") String window,
                                                                               @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(interactionEventService.getPopularItems(ItemType.DESTINATION, window, limit));
    }

    @GetMapping("/popular-alcohols")
    public ResponseEntity<List<PopularItemResponseDTO>> getPopularAlcohols(@RequestParam(defaultValue = "7d") String window,
                                                                           @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(interactionEventService.getPopularItems(ItemType.ALCOHOL, window, limit));
    }

    @GetMapping("/popular-distilleries")
    public ResponseEntity<List<PopularItemResponseDTO>> getPopularDistilleries(@RequestParam(defaultValue = "7d") String window,
                                                                               @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(interactionEventService.getPopularItems(ItemType.DISTILLERY, window, limit));
    }
}
