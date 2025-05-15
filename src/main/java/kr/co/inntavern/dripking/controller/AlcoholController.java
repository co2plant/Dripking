package kr.co.inntavern.dripking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.inntavern.dripking.dto.response.AlcoholResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.service.AlcoholService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Alcohol", description = "주류 API")
@RestController
@RequestMapping("/api/alcohols")
public class AlcoholController {
    private final AlcoholService alcoholService;

    public AlcoholController(AlcoholService alcoholService){
        this.alcoholService = alcoholService;
    }

    @GetMapping
    @Operation(summary = "모든 주류 조회", description = "모든 주류를 조회합니다.")
    @ApiResponse(responseCode="200", description = "성공", content = @Content(schema = @Schema(implementation = AlcoholResponseDTO.class)))
    public ResponseEntity<Page<AlcoholResponseDTO>> getAllAlcohols(@RequestParam(required=false,value="page", defaultValue="0") int page,
                                                        @RequestParam(required=false,value="size", defaultValue="10") int size,
                                                        @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
                                                        @RequestParam(required=false, value="category_id", defaultValue="0") Long categoryId){
        if(categoryId != 0){
            Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByCategoryId(page, size, categoryId);
            return ResponseEntity.ok(paging);
        }
        else{
            Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcohols(page);
            return ResponseEntity.ok(paging);
        }

    }

    @GetMapping("/distillery") //endpoint변경해야함.
    public ResponseEntity<Page<AlcoholResponseDTO>> getAllAlcoholsByDistilleryId(@RequestParam(value="distillery") Long distilleryId){
        Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByDistilleryId(distilleryId);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{alcoholId}")
    public Alcohol getAlcoholById(@PathVariable Long alcoholId) {
        return alcoholService.getAlcoholById(alcoholId);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<Page<AlcoholResponseDTO>> searchAlcoholsByName(@RequestParam(value="page", defaultValue="0") int page, @PathVariable String searchKeyword){
        Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByName(page, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<Alcohol> createAlcohol(@RequestBody Alcohol alcohol){
        alcoholService.createAlcohol(alcohol);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{alcohol_id}")
    public ResponseEntity<Alcohol> updateAlcohol(@PathVariable Long alcohol_id, @RequestBody Alcohol alcohol){
        alcoholService.updateAlcohol(alcohol_id, alcohol);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{alcohol_id}")
    public ResponseEntity<Void> deleteAlcohol(@PathVariable Long alcohol_id)
    {
        alcoholService.deleteAlcoholById(alcohol_id);
        return ResponseEntity.ok().build();
    }



}

