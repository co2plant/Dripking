package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.service.DistilleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class DistilleryController {

    @Autowired
    DistilleryService distilleryService;

    @GetMapping("/api/distilleries")
    public List<Distillery> getDistilleries(){
        return distilleryService.findAll();
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
