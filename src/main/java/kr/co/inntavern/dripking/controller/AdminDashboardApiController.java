package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.dashboard.DashboardActivityResponseDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.DashboardSummaryResponseDTO;
import kr.co.inntavern.dripking.dto.response.dashboard.PopularItemResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.service.InteractionEventService;
import kr.co.inntavern.dripking.service.dashboard.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardApiController {
    private final AdminDashboardService adminDashboardService;
    private final InteractionEventService interactionEventService;

    public AdminDashboardApiController(AdminDashboardService adminDashboardService,
                                       InteractionEventService interactionEventService) {
        this.adminDashboardService = adminDashboardService;
        this.interactionEventService = interactionEventService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponseDTO> getSummary() {
        return ResponseEntity.ok(adminDashboardService.getSummary());
    }

    @GetMapping("/activity")
    public ResponseEntity<List<DashboardActivityResponseDTO>> getActivity() {
        return ResponseEntity.ok(adminDashboardService.getActivity());
    }

    @GetMapping("/popular-destinations")
    public ResponseEntity<List<PopularItemResponseDTO>> getPopularDestinations(@RequestParam(defaultValue = "7d") String window,
                                                                               @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(interactionEventService.getPopularItems(ItemType.DESTINATION, window, limit));
    }

    @GetMapping("/popular-alcohols")
    public ResponseEntity<List<PopularItemResponseDTO>> getPopularAlcohols(@RequestParam(defaultValue = "7d") String window,
                                                                           @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(interactionEventService.getPopularItems(ItemType.ALCOHOL, window, limit));
    }
}
