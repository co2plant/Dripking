package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.service.DestinationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DestinationController
{
    DestinationService destinationService;

    public DestinationController(DestinationService destinationService)
    {
        this.destinationService = destinationService;
    }

    @GetMapping("/api/destinations")
    public ResponseEntity<Page<Destination>> getDestinations(@RequestParam(value="page", defaultValue="0") int page){
        Page<Destination> paging = destinationService.findAll(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/api/destinations/search/{searchKeyword}")
    public ResponseEntity<Page<Destination>> getDestinationByName(@RequestParam(value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<Destination> paging = destinationService.searchByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/api/destination/{alcoholId}")
    public Destination getDestination(@PathVariable Long destinationId) {
        return destinationService.findById(destinationId);
    }
}
