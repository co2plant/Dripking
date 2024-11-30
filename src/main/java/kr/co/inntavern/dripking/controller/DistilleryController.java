package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.service.DistilleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DistilleryController {

    @Autowired
    DistilleryService distilleryService;

    @GetMapping("/api/distilleries")
    public List<Distillery> getDistilleries(){
        return distilleryService.getDistilleries();
    }

    @PostMapping("/api/distilleries")
    public void createDistillery(@RequestBody Distillery newDistillery){
        distilleryService.createDistillery(newDistillery);
    }
}
