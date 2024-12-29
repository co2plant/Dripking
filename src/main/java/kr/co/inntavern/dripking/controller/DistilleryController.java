package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.service.DistilleryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DistilleryController {
    private final DistilleryService distilleryService;
    public DistilleryController(DistilleryService distilleryService){
        this.distilleryService = distilleryService;
    }

    @GetMapping("/api/distilleries")
    public ResponseEntity<Page<Distillery>> getAlcohols(@RequestParam(value="page", defaultValue="0") int page){
        Page<Distillery> paging = distilleryService.findAll(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/api/distilleries/search/{searchKeyword}")
    public ResponseEntity<Page<Distillery>> getAlcoholByName(@RequestParam(value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<Distillery> paging = distilleryService.searchByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping("/api/distilleries")
    public void createDistillery(@RequestBody Distillery newDistillery){
        distilleryService.createDistillery(newDistillery);
    }

    @GetMapping("/api/distillery/{distilleryId}")
    public Distillery getDistillery(@PathVariable Long distilleryId){
        return distilleryService.findById(distilleryId);
    }


}
