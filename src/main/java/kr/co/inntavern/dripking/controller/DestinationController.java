package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.DestinationRequestDTO;
import kr.co.inntavern.dripking.dto.response.DestinationResponseDTO;
import kr.co.inntavern.dripking.service.DestinationService;
import kr.co.inntavern.dripking.util.CoordinateUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController
{
    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService)
    {
        this.destinationService = destinationService;
    }

    @GetMapping
    public ResponseEntity<Page<DestinationResponseDTO>> getAllDestinations(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                                           @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                                           @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
                                                                           @RequestParam(required=false, value="countryId", defaultValue="0") Long countryId,
                                                                           @RequestParam(required=false, value="country_id") Long legacyCountryId
    ){
        Long selectedCountryId = countryId != 0 ? countryId : legacyCountryId;
        if(selectedCountryId != null && selectedCountryId != 0){
            Page<DestinationResponseDTO> paging = destinationService.getAllDestinationsByCountryId(page, size, sort, selectedCountryId);
            return ResponseEntity.ok(paging);
        }
        else{
            Page<DestinationResponseDTO> paging = destinationService.getAllDestinations(page, size, sort);
            return ResponseEntity.ok(paging);
        }

    }

    @GetMapping("/{destinationId}")
    public DestinationResponseDTO getDestinationById(@PathVariable Long destinationId) {
        return destinationService.getDestinationById(destinationId);
    }

    @GetMapping("/markers")
    public ResponseEntity<List<DestinationResponseDTO>> getDestinationMarkers() {
        return ResponseEntity.ok(destinationService.getDestinationMarkers());
    }

    @GetMapping("/latlng")
    public ResponseEntity<Page<DestinationResponseDTO>> getAllDestinationsByLatitudeAndLongitude(@RequestParam(value="minLatitude") Double minLatitude,
                                                                                                 @RequestParam(value="maxLatitude") Double maxLatitude,
                                                                                                 @RequestParam(value="minLongitude") Double minLongitude,
                                                                                                 @RequestParam(value="maxLongitude") Double maxLongitude,
                                                                                                 @RequestParam(required=false,value="page", defaultValue="0") int page,
                                                                                                 @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                                                                 @RequestParam(required=false, value="sort", defaultValue="DESC") String sort){
        try {
            CoordinateUtils.validateBounds(minLatitude, maxLatitude, minLongitude, maxLongitude);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }

        Page<DestinationResponseDTO> paging = destinationService.getAllDestinationsByLatitudeAndLongitude(page, size, sort, minLatitude, maxLatitude, minLongitude, maxLongitude);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<DestinationResponseDTO>> searchDestinationsByName(
            @RequestParam(required=false, value="page", defaultValue="0") int page,
            @RequestParam(required=false, value="size", defaultValue="10") int size,
            @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
            @PathVariable String searchKeyword){
        Page<DestinationResponseDTO> paging = destinationService.getAllDestinationsByName(page, size, sort, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<DestinationResponseDTO> createDestination(@RequestBody DestinationRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(destinationService.createDestination(requestDTO));
    }

    @PutMapping("/{destinationId}")
    public ResponseEntity<DestinationResponseDTO> updateDestination(@PathVariable Long destinationId, @RequestBody DestinationRequestDTO requestDTO){
        return ResponseEntity.ok(destinationService.updateDestination(destinationId, requestDTO));
    }

    @DeleteMapping("/{destinationId}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long destinationId)
    {
        destinationService.deleteDestinationById(destinationId);
        return ResponseEntity.ok().build();
    }
}
