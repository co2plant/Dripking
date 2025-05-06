package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.CityResponseDTO;
import kr.co.inntavern.dripking.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cities")
// TODO: Add methods for create, update, delete if needed, using appropriate DTOs and security checks.
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    // TODO: Add endpoint to get all cities with pagination, e.g., @GetMapping
    // public ResponseEntity<Page<CityResponseDTO>> getAllCities(...) { ... }

    @GetMapping("/{cityId}") // Consistent naming: cityId
    public ResponseEntity<CityResponseDTO> getCityById(@PathVariable Long cityId){
        CityResponseDTO cityDTO = cityService.getCityById(cityId);
        return ResponseEntity.ok(cityDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<CityResponseDTO> getCityByName(@RequestParam(value = "name") String cityName) {
        // Consider returning List<CityResponseDTO> if multiple cities can match the name
        CityResponseDTO cityDTO = cityService.getCityByName(cityName);
        return ResponseEntity.ok(cityDTO);
    }
}
