package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.service.AlcoholService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AlcoholController {
    private final AlcoholService alcoholService;

    public AlcoholController(AlcoholService alcoholService){
        this.alcoholService = alcoholService;
    }

    @GetMapping("/api/alcohols")
    public ResponseEntity<Page<Alcohol>> getAlcohols(@RequestParam(value="page", defaultValue="0") int page){
        Page<Alcohol> paging = alcoholService.findAll(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/api/alcohol/search/{searchKeyword}")
    public ResponseEntity<Page<Alcohol>> getAlcoholByName(@RequestParam(value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<Alcohol> paging = alcoholService.searchByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/api/alcohol/{alcoholId}")
    public Alcohol getAlcohol(@PathVariable Long alcoholId) {
        return alcoholService.findById(alcoholId);
    }



}

