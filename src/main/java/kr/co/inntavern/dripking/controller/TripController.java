package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.TripRequestDTO;
import kr.co.inntavern.dripking.dto.response.TripContainCountryResponseDTO;
import kr.co.inntavern.dripking.dto.response.TripResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.TripService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService){
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<Page<TripResponseDTO>> getAllTripByToken(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                   @RequestParam(required = false, value = "page", defaultValue="0") int page,
                                                                   @RequestParam(required = false, value = "size", defaultValue="5") int size,
                                                                   @RequestParam(required = false, value = "sortBy", defaultValue="DESC") String sortBy){

        Long userId = customUserDetails.getId();

        return ResponseEntity.ok(tripService.getAllTripByUserId(page, size, userId, sortBy));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> getTripById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @PathVariable Long tripId){
        Long userId = customUserDetails.getId();

        return ResponseEntity.ok(tripService.getTripById(tripId, userId));
    }

    //테스트를 위해서 작성한 것으로 앞으로 들어갈 TripResponseDTO를 country lat, count lng를 포함한 TripContainCountryResponseDTO로 변경할 예정
    @GetMapping("/testMap")
    public ResponseEntity<Page<TripContainCountryResponseDTO>> getAllTripsByUserId(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                   @RequestParam(required = false, value = "page", defaultValue="0") int page,
                                                                                   @RequestParam(required = false, value = "size", defaultValue="5") int size,
                                                                                   @RequestParam(required = false, value = "sortBy", defaultValue="DESC") String sortBy){

        Long userId = customUserDetails.getId();

        return ResponseEntity.ok(tripService.getAllTripContainCountryByUserId(page, size, userId, sortBy));
    }

    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @RequestBody TripRequestDTO tripRequestDTO){
        Long userId = customUserDetails.getId();

        return ResponseEntity.ok(tripService.createTrip(tripRequestDTO, userId));
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<Void> updateTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable Long tripId,
                                           @RequestBody TripRequestDTO tripRequestDTO){
        Long userId = customUserDetails.getId();
        tripService.updateTrip(tripId, tripRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateTripByQueryParam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @RequestParam Long id,
                                                       @RequestBody TripRequestDTO tripRequestDTO){
        Long userId = customUserDetails.getId();
        tripService.updateTrip(id, tripRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable Long tripId){
        Long userId = customUserDetails.getId();
        tripService.deleteTripById(tripId, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTripByQueryParam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @RequestParam Long id){
        Long userId = customUserDetails.getId();
        tripService.deleteTripById(id, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
