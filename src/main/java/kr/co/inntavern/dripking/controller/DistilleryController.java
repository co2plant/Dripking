package kr.co.inntavern.dripking.controller;

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
    public ResponseEntity<Page<Distillery>> getAllDistilleries(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                               @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                               @RequestParam(required=false, value="sort", defaultValue="DESC") String sort){
        Page<Distillery> paging = distilleryService.getAllDistilleries(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{distilleryId}")
    public Distillery getDistilleryById(@PathVariable Long distilleryId) {
        return distilleryService.getDistilleryById(distilleryId);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<Distillery>> searchDistilleriesByName(@RequestParam(value="page", defaultValue="0") int page,
                                                                     @PathVariable String searchKeyword){
        Page<Distillery> paging = distilleryService.getAllDistilleriesByName(page, searchKeyword);
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
