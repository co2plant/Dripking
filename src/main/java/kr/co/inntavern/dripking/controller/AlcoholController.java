package kr.co.inntavern.dripking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.inntavern.dripking.dto.request.AlcoholRequestDTO;
import kr.co.inntavern.dripking.dto.response.AlcoholResponseDTO;
import kr.co.inntavern.dripking.service.AlcoholService;
import org.springframework.http.HttpStatus;
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
                                                        @RequestParam(required=false, value="categoryId", defaultValue="0") Long categoryId,
                                                        @RequestParam(required=false, value="category_id") Long legacyCategoryId,
                                                        @RequestParam(required=false, value="distilleryId") Long distilleryId){
        Long selectedCategoryId = categoryId != 0 ? categoryId : legacyCategoryId;
        if(distilleryId != null){
            Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByDistilleryId(page, size, sort, distilleryId);
            return ResponseEntity.ok(paging);
        }
        else if(selectedCategoryId != null && selectedCategoryId != 0){
            Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByCategoryId(page, size, sort, selectedCategoryId);
            return ResponseEntity.ok(paging);
        }
        else{
            Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcohols(page, size, sort);
            return ResponseEntity.ok(paging);
        }

    }

    @GetMapping("/distillery") // Temporary compatibility alias; prefer GET /api/alcohols?distilleryId=...
    @Operation(summary = "양조장 관련 모든 주류 조회", description = "해당 양조장의 모든 주류를 조회합니다.")
    @ApiResponse(responseCode="200", description = "성공", content = @Content(schema = @Schema(implementation = AlcoholResponseDTO.class)))
    public ResponseEntity<Page<AlcoholResponseDTO>> getAllAlcoholsByDistilleryId(
            @RequestParam(value="distillery") Long distilleryId,
            @RequestParam(required=false,value="page", defaultValue="0") int page,
            @RequestParam(required=false,value="size", defaultValue="10") int size){
        Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByDistilleryId(page, size, "DESC", distilleryId);
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{alcoholId}")
    @Operation(summary = "특정 ID 주류 조회", description = "특정 ID의 주류를 조회합니다.")
    @ApiResponse(responseCode="200", description = "성공", content = @Content(schema = @Schema(implementation = AlcoholResponseDTO.class)))
    public AlcoholResponseDTO getAlcoholById(@PathVariable Long alcoholId) {
        return alcoholService.getAlcoholById(alcoholId);
    }

    @GetMapping("/search/{searchKeyword}")
    @Operation(summary = "특정 이름을 가지는 모든 주류 조회", description = "특정 이름을 가지는 모든 주류를 조회합니다.")
    @ApiResponse(responseCode="200", description = "성공", content = @Content(schema = @Schema(implementation = AlcoholResponseDTO.class)))
    public ResponseEntity<Page<AlcoholResponseDTO>> searchAlcoholsByName(
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue="10") int size,
            @RequestParam(required=false, value="sort", defaultValue="DESC") String sort,
            @PathVariable String searchKeyword){
        Page<AlcoholResponseDTO> paging = alcoholService.getAllAlcoholsByName(page, size, sort, searchKeyword);
        return ResponseEntity.ok(paging);
    }

    @PostMapping
    public ResponseEntity<AlcoholResponseDTO> createAlcohol(@RequestBody AlcoholRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(alcoholService.createAlcohol(requestDTO));
    }

    @PutMapping("/{alcoholId}")
    public ResponseEntity<AlcoholResponseDTO> updateAlcohol(@PathVariable Long alcoholId, @RequestBody AlcoholRequestDTO requestDTO){
        return ResponseEntity.ok(alcoholService.updateAlcohol(alcoholId, requestDTO));
    }

    @DeleteMapping("/{alcoholId}")
    public ResponseEntity<Void> deleteAlcohol(@PathVariable Long alcoholId)
    {
        alcoholService.deleteAlcoholById(alcoholId);
        return ResponseEntity.ok().build();
    }



}
