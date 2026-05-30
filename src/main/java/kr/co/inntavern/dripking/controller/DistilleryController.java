package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.DistilleryRequestDTO;
import kr.co.inntavern.dripking.dto.response.DistilleryResponseDTO;
import kr.co.inntavern.dripking.service.DistilleryService;
import kr.co.inntavern.dripking.util.CoordinateUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                                                               @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
                                                               @RequestParam(required=false, value="destinationId") Long destinationId){
        if (destinationId != null) {
            Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByDestinationId(page, size, sort, destinationId);
            return ResponseEntity.ok(paging);
        }
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleries(page, size, sort);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/destination") // Temporary compatibility alias; prefer GET /api/distilleries?destinationId=...
    public ResponseEntity<Page<DistilleryResponseDTO>> getAllDistilleriesByDestinationId(
            @RequestParam(value="destination") Long destinationId,
            @RequestParam(required=false,value="page", defaultValue="0") int page,
            @RequestParam(required=false,value="size", defaultValue="10") int size){
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByDestinationId(page, size, "DESC", destinationId);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{distilleryId}")
    public DistilleryResponseDTO getDistilleryById(@PathVariable Long distilleryId) {
        return distilleryService.getDistilleryById(distilleryId);
    }

    @GetMapping("/markers")
    public ResponseEntity<List<DistilleryResponseDTO>> getDistilleryMarkers() {
        return ResponseEntity.ok(distilleryService.getDistilleryMarkers());
    }

    @GetMapping("/latlng")
    public ResponseEntity<Page<DistilleryResponseDTO>>getAllDistilleriesByLatitudeAndLongitude(@RequestParam(value="minLatitude") Double minLatitude,
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

        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByLatitudeAndLongitude(page, size, sort, minLatitude, maxLatitude, minLongitude, maxLongitude);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<DistilleryResponseDTO>> searchDistilleriesByName(
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue="10") int size,
            @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
            @PathVariable String searchKeyword){
        Page<DistilleryResponseDTO> paging = distilleryService.getAllDistilleriesByName(page, size, sort, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<DistilleryResponseDTO> createDistillery(@RequestBody DistilleryRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(distilleryService.createDistillery(requestDTO));
    }

    @PutMapping("/{distilleryId}")
    public ResponseEntity<DistilleryResponseDTO> updateDistillery(@PathVariable Long distilleryId, @RequestBody DistilleryRequestDTO requestDTO){
        return ResponseEntity.ok(distilleryService.updateDistillery(distilleryId, requestDTO));
    }

    @DeleteMapping("/{distilleryId}")
    public ResponseEntity<Void> deleteDistillery(@PathVariable Long distilleryId)
    {
        distilleryService.deleteDistilleryById(distilleryId);
        return ResponseEntity.ok().build();
    }


}
