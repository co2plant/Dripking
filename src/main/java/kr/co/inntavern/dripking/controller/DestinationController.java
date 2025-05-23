package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.DestinationResponseDTO;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.service.DestinationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                                           @RequestParam(required=false, value="countryId", defaultValue="0") Long countryId
    ){
        if(countryId != 0){
            Page<DestinationResponseDTO> paging = destinationService.getAllDestinationsByCountryId(page, countryId);
            return ResponseEntity.ok(paging);
        }
        else{
            Page<DestinationResponseDTO> paging = destinationService.getAllDestinations(page);
            return ResponseEntity.ok(paging);
        }

    }

    @GetMapping("/{destinationId}")
    public DestinationResponseDTO getDestinationById(@PathVariable Long destinationId) {
        return destinationService.getDestinationById(destinationId);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<DestinationResponseDTO>> searchDestinationsByName(@RequestParam(required=false, value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<DestinationResponseDTO> paging = destinationService.getAllDestinationsByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Destination> createDestination(@RequestBody Destination Destination){
        destinationService.createDestination(Destination);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{destinationId}")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long destinationId, @RequestBody Destination Destination){
        destinationService.updateDestination(destinationId, Destination);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{destinationId}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long destinationId)
    {
        destinationService.deleteDestinationById(destinationId);
        return ResponseEntity.ok().build();
    }
}
