package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.CountryRequestDTO;
import kr.co.inntavern.dripking.dto.response.CountryResponseDTO;
import kr.co.inntavern.dripking.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public List<CountryResponseDTO> getAllCountries(){
        return countryService.getAllCountries();
    }

    @PostMapping
    public ResponseEntity<CountryResponseDTO> createCountry(@RequestBody CountryRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.createCountry(requestDTO));
    }

    @PutMapping("/{countryId}")
    public ResponseEntity<CountryResponseDTO> updateCountry(@PathVariable Long countryId,
                                                            @RequestBody CountryRequestDTO requestDTO) {
        return ResponseEntity.ok(countryService.updateCountry(countryId, requestDTO));
    }

    @DeleteMapping("/{countryId}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long countryId) {
        countryService.deleteCountryById(countryId);
        return ResponseEntity.ok().build();
    }
}
