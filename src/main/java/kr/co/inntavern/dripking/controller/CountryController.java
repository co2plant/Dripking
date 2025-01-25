package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping
    public List<Country> getAllCountries(){
        return countryService.getAllCountries();
    }

}
