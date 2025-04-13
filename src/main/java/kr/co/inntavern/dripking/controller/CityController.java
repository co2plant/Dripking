package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{city_id}")
    public ResponseEntity<?> getCityById(@PathVariable Long city_id){
        return ResponseEntity.ok(cityService.getCityById(city_id));
    }
    @GetMapping("/search")
    public ResponseEntity<?> getCityByName(@RequestParam(required = false, value = "name") String cityName) {
        return ResponseEntity.ok(cityService.getCityByName(cityName));
    }
}
