package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.CityResponseDTO;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.service.CityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<CityResponseDTO> getCityById(@PathVariable Long cityId){
        CityResponseDTO cityDTO = cityService.getCityById(cityId);
        return ResponseEntity.ok(cityDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<CityResponseDTO> getCityByName(@RequestParam(value = "name") String cityName) {
        CityResponseDTO cityDTO = cityService.getCityByName(cityName);
        return ResponseEntity.ok(cityDTO);
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<Page<CityResponseDTO>> getCitiesByCountryId(@PathVariable Long countryId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Page<CityResponseDTO> cities = cityService.getCitiesByCountryId(countryId, page, size);
        return ResponseEntity.ok(cities);
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city){
        cityService.createCity(city);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cityId}")
    public ResponseEntity<City> updateCity(@PathVariable Long cityId, @RequestBody City city){
        cityService.updateCity(cityId, city);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cityId}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long cityId)
    {
        cityService.deleteCityById(cityId);
        return ResponseEntity.ok().build();
    }
}
