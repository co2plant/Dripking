package kr.co.inntavern.dripking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlcoholController {
    @GetMapping("/api/alcohols")
    public List<String> getAlcohols(){
        return null;
    }

}

