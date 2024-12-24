package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.service.AlcoholService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlcoholController {
    private AlcoholService alcoholService;

    @GetMapping("/api/alcohols")
    public List<Alcohol> getAlcohols(){
        return alcoholService.findAll();
    }

    @GetMapping("/api/alcohol/{alcoholId}")
    public Alcohol getAlcohol(@PathVariable Long alcoholId) {
        return alcoholService.findById(alcoholId);
    }

    @GetMapping("/api/alcohol/search/{searchKeyword}")
    public List<Alcohol> getAlcoholByName(@PathVariable String searchKeyword){
        return alcoholService.searchByName(searchKeyword);
    }

}

