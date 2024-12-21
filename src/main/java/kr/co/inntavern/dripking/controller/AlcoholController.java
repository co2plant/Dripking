package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.service.AlcoholService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlcoholController {
    @Autowired
    AlcoholService alcoholService;


    @GetMapping("/api/alcohols")
    public List<Alcohol> getAlcohols(){
        return alcoholService.findAll();
    }

    @GetMapping("/api/alcohol/{alcoholId}")
    public Alcohol getAlcohol(@PathVariable Long alcoholId) {
        return alcoholService.findById((Long)alcoholId);
    }

    @GetMapping("/api/alcohol/search/{name}")
    public List<Alcohol> getAlcoholByName(@PathVariable String name){
        return alcoholService.searchByName(name);
    }
}

