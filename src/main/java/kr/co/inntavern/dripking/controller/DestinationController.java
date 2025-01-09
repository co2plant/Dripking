package kr.co.inntavern.dripking.controller;

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
    public ResponseEntity<Page<Destination>> getAllDestinations(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                                @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                                @RequestParam(required=false, value="sort", defaultValue="DESC") String sort){
        Page<Destination> paging = destinationService.getAllDestinations(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{DestinationId}")
    public Destination getDestinationById(@PathVariable Long DestinationId) {
        return destinationService.getDestinationById(DestinationId);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<Destination>> searchDestinationsByName(@RequestParam(required=false, value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<Destination> paging = destinationService.getAllDestinationsByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Destination> createDestination(@RequestBody Destination Destination){
        Destination createdDestination = destinationService.createDestination(Destination);
        return ResponseEntity.ok(createdDestination);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id, @RequestBody Destination Destination){
        Destination updatedDestination = destinationService.updateDestination(Destination.getId(), Destination);
        return ResponseEntity.ok(updatedDestination);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id)
    {
        destinationService.deleteDestinationById(id);
        return ResponseEntity.ok().build();
    }
}
