package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.Request.TripRequestDTO;
import kr.co.inntavern.dripking.dto.Response.TripResponseDTO;
import kr.co.inntavern.dripking.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService){
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(@RequestBody TripRequestDTO tripRequestDTO){
        return ResponseEntity.ok(tripService.createTrip(tripRequestDTO));
    }

    @PutMapping
    public ResponseEntity<Void> updateTrip(@RequestParam Long id, @RequestBody TripRequestDTO tripRequestDTO){
        tripService.updateTrip(id, tripRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteTrip(@RequestParam Long id){
        tripService.deleteTripById(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
