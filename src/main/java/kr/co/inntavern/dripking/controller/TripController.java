package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.TripRequestDTO;
import kr.co.inntavern.dripking.dto.response.TripContainCountryResponseDTO;
import kr.co.inntavern.dripking.dto.response.TripResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.TripService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService){
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<Page<TripResponseDTO>> getAllTripByToken(Authentication authentication,
                                                                   @RequestParam(required = false, value = "page", defaultValue="0") int page,
                                                                   @RequestParam(required = false, value = "size", defaultValue="5") int size,
                                                                   @RequestParam(required = false, value = "sortBy", defaultValue="DESC") String sortBy){

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(tripService.getAllTripByUserId(page, size, userId, sortBy));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> getTripById(Authentication authentication,
                                                       @PathVariable Long tripId){
        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(tripService.getTripById(tripId, userId));
    }

    //테스트를 위해서 작성한 것으로 앞으로 들어갈 TripResponseDTO를 country lat, count lng를 포함한 TripContainCountryResponseDTO로 변경할 예정
    @GetMapping("/testMap")
    public ResponseEntity<Page<TripContainCountryResponseDTO>> getAllTripsByUserId(Authentication authentication,
                                                                                   @RequestParam(required = false, value = "page", defaultValue="0") int page,
                                                                                   @RequestParam(required = false, value = "size", defaultValue="5") int size,
                                                                                   @RequestParam(required = false, value = "sortBy", defaultValue="DESC") String sortBy){

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(tripService.getAllTripContainCountryByUserId(page, size, userId, sortBy));
    }

    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(Authentication authentication,
                                                      @RequestBody TripRequestDTO tripRequestDTO){
        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(tripService.createTrip(tripRequestDTO, userId));
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<Void> updateTrip(Authentication authentication,
                                           @PathVariable Long tripId,
                                           @RequestBody TripRequestDTO tripRequestDTO){
        Long userId = getCurrentUserId(authentication);
        tripService.updateTrip(tripId, tripRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateTripByQueryParam(Authentication authentication,
                                                       @RequestParam Long id,
                                                       @RequestBody TripRequestDTO tripRequestDTO){
        Long userId = getCurrentUserId(authentication);
        tripService.updateTrip(id, tripRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(Authentication authentication,
                                           @PathVariable Long tripId){
        Long userId = getCurrentUserId(authentication);
        tripService.deleteTripById(tripId, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTripByQueryParam(Authentication authentication,
                                                       @RequestParam Long id){
        Long userId = getCurrentUserId(authentication);
        tripService.deleteTripById(id, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private Long getCurrentUserId(Authentication authentication){
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required.");
        }

        return customUserDetails.getId();
    }
}
