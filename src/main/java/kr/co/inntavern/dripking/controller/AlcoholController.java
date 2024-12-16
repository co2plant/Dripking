package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlcoholController {
    @Autowired
    AlcoholRepository alcoholRepository;

    @GetMapping("/api/alcohols")
    public List<Alcohol> getAlcohols(){
        return alcoholRepository.findAll();
    }

}

