package kr.co.inntavern.dripking.controller.dashboard;

import kr.co.inntavern.dripking.dto.response.dashboard.DestinationDashboardResponseDTO;
import kr.co.inntavern.dripking.service.UserService;
import kr.co.inntavern.dripking.service.dashboard.DestinationDashboardService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class AdminDashboardController {
    private final UserService userService;
    private final DestinationDashboardService destinationDashboardService;

    public AdminDashboardController(UserService userService, DestinationDashboardService destinationDashboardService){
        this.userService = userService;
        this.destinationDashboardService = destinationDashboardService;
    }
    @GetMapping("/user/number/total")
    public ResponseEntity<?> getNumberOfUsersByIsEmailVerified(boolean isEmailVerified){
        return ResponseEntity.ok(userService.getNumberOfUsersByIsEmailVerified(isEmailVerified));
    }

    //destination dashboard main
    @GetMapping("/destinations")
    public ResponseEntity<Page<DestinationDashboardResponseDTO>> getDestinationForDashboard(@RequestParam(required=false,value="size", defaultValue="10") int size,
                                                                                            @RequestParam(value="page", defaultValue="1") int page){
        return ResponseEntity.ok(destinationDashboardService.findAllDestinationForDashboard(page, size));
    }
}
