package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.RecommendationResponseDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.PopularItemResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.InteractionEventService;
import kr.co.inntavern.dripking.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final InteractionEventService interactionEventService;
    private final RecommendationService recommendationService;

    public RecommendationController(InteractionEventService interactionEventService,
                                    RecommendationService recommendationService) {
        this.interactionEventService = interactionEventService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<RecommendationResponseDTO> getRecommendations(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String flavorTags,
            @RequestParam(required = false) Long countryId,
            @RequestParam(defaultValue = "20") int limit) {
        Long userId = customUserDetails == null ? null : customUserDetails.getId();
        return ResponseEntity.ok(recommendationService.getRecommendations(userId, categoryId, flavorTags, countryId, limit));
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
