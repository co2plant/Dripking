package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.Response.DistilleryResponseDTO;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.service.DistilleryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/distilleries")
public class DistilleryController {
    private final DistilleryService distilleryService;
    public DistilleryController(DistilleryService distilleryService){
        this.distilleryService = distilleryService;
    }

    @GetMapping
    public ResponseEntity<Page<DistilleryResponseDTO>> getAllDistilleries(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                               @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                               @RequestParam(required=false, value="sort", defaultValue="DESC") String sort){
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleries(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/destination") //endpoint변경해야함.
    public ResponseEntity<Page<DistilleryResponseDTO>> getAllDistilleriesByDestinationId(@RequestParam(required=true, value="destination") Long destinationId){
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByDestinationId(destinationId);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{distilleryId}")
    public DistilleryResponseDTO getDistilleryById(@PathVariable Long distilleryId) {
        return distilleryService.getDistilleryById(distilleryId);
    }

    @GetMapping("/latlng")
    public ResponseEntity<Page<DistilleryResponseDTO>>getAllDistilleriesByLatitudeAndLongitude(@RequestParam(required=true, value="minLatitude") Double minLatitude,
                                                                         @RequestParam(required=true, value="maxLatitude") Double maxLatitude,
                                                                         @RequestParam(required=true, value="minLongitude") Double minLongitude,
                                                                         @RequestParam(required=true, value="maxLongitude") Double maxLongitude){
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByLatitudeAndLongitude(minLatitude, maxLatitude, minLongitude, maxLongitude);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<DistilleryResponseDTO>> searchDistilleriesByName(@RequestParam(value="page", defaultValue="0") int page,
                                                                     @PathVariable String searchKeyword){
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Distillery> createDistillery(@RequestBody Distillery distillery){
        Distillery createdDistillery = distilleryService.createDistillery(distillery);
        return ResponseEntity.ok(createdDistillery);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Distillery> updateDistillery(@PathVariable Long id, @RequestBody Distillery distillery){
        Distillery updatedDistillery = distilleryService.updateDistillery(distillery.getId(), distillery);
        return ResponseEntity.ok(updatedDistillery);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDistillery(@PathVariable Long id)
    {
        distilleryService.deleteDistilleryById(id);
        return ResponseEntity.ok().build();
    }


}
