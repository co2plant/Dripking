package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.TastingNoteRequestDTO;
import kr.co.inntavern.dripking.dto.response.TastingNoteResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.TastingNoteService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasting-notes")
public class TastingNoteController {
    private final TastingNoteService tastingNoteService;

    public TastingNoteController(TastingNoteService tastingNoteService) {
        this.tastingNoteService = tastingNoteService;
    }

    @GetMapping
    public ResponseEntity<Page<TastingNoteResponseDTO>> getTastingNotes(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) Long alcoholId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "recent") String sort) {
        return ResponseEntity.ok(tastingNoteService.getTastingNotes(
                customUserDetails.getId(),
                alcoholId,
                keyword,
                page,
                size,
                sort
        ));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<TastingNoteResponseDTO> getTastingNote(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(tastingNoteService.getTastingNote(customUserDetails.getId(), noteId));
    }

    @PostMapping
    public ResponseEntity<TastingNoteResponseDTO> createTastingNote(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody TastingNoteRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tastingNoteService.createTastingNote(customUserDetails.getId(), requestDTO));
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<TastingNoteResponseDTO> updateTastingNote(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long noteId,
            @RequestBody TastingNoteRequestDTO requestDTO) {
        return ResponseEntity.ok(tastingNoteService.updateTastingNote(customUserDetails.getId(), noteId, requestDTO));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteTastingNote(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long noteId) {
        tastingNoteService.deleteTastingNote(customUserDetails.getId(), noteId);
        return ResponseEntity.noContent().build();
    }
}
