package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.TripRequestDTO;
import kr.co.inntavern.dripking.dto.response.TripResponseDTO;
import kr.co.inntavern.dripking.security.JwtUtils;
import kr.co.inntavern.dripking.service.TripService;
import kr.co.inntavern.dripking.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public TripController(TripService tripService, JwtUtils jwtUtils, UserService userService){
        this.tripService = tripService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<TripResponseDTO>> getAllTripByToken(@RequestHeader HttpHeaders headers,
                                                                   @RequestParam(required = false, value = "page", defaultValue="0") int page,
                                                                   @RequestParam(required = false, value = "size", defaultValue="5") int size,
                                                                   @RequestParam(required = false, value = "sort", defaultValue="DESC") String sort){

        String token = headers.get("Authorization").toString().substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        Long user_id = userService.getUserByEmail(username).get().getId();

        return ResponseEntity.ok(tripService.getAllTripByUserId(page, size, user_id));
    }

    //테스트를 위해서 작성한 것으로 앞으로 들어갈 TripResponseDTO를 country lat, count lng를 포함한 TripContainCountryResponseDTO로 변경할 예정
    @GetMapping("/testMap")
    public ResponseEntity<Page<TripContainCountryResponseDTO>> getAllTripsByUserId(@RequestHeader HttpHeaders headers,
                                                                   @RequestParam(required = false, value = "page", defaultValue="0") int page,
                                                                   @RequestParam(required = false, value = "size", defaultValue="5") int size,
                                                                   @RequestParam(required = false, value = "sort", defaultValue="DESC") String sort){

        String token = headers.get("Authorization").toString().substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        Long user_id = userService.getUserByEmail(username).get().getId();

        return ResponseEntity.ok(tripService.getAllTripByUserId(page, size, user_id));
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

    @DeleteMapping
    public ResponseEntity<Void> deleteTrip(@RequestParam Long id){
        tripService.deleteTripById(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
