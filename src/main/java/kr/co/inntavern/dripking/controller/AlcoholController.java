package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.service.AlcoholService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alcohols")
public class AlcoholController {
    private final AlcoholService alcoholService;

    public AlcoholController(AlcoholService alcoholService){
        this.alcoholService = alcoholService;
    }

    @GetMapping
    public ResponseEntity<Page<Alcohol>> getAllAlcohols(@RequestParam(value="page", defaultValue="0") int page){
        Page<Alcohol> paging = alcoholService.getAllAlcohols(page);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{alcoholId}")
    public Alcohol getAlcoholById(@PathVariable Long alcoholId) {
        return alcoholService.getAlcoholById(alcoholId);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<Alcohol>> searchAlcoholsByName(@RequestParam(value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<Alcohol> paging = alcoholService.getAllAlcoholsByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Alcohol> createAlcohol(@RequestBody Alcohol alcohol){
        Alcohol createdAlcohol = alcoholService.createAlcohol(alcohol);
        return ResponseEntity.ok(createdAlcohol);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alcohol> updateAlcohol(@PathVariable Long id, @RequestBody Alcohol alcohol){
        Alcohol updatedAlcohol = alcoholService.updateAlcohol(alcohol.getId(), alcohol);
        return ResponseEntity.ok(updatedAlcohol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlcohol(@PathVariable Long id)
    {
        alcoholService.deleteAlcoholById(id);
        return ResponseEntity.ok().build();
    }



}

