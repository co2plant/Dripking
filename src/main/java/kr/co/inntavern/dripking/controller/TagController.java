package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.TagRequestDTO;
import kr.co.inntavern.dripking.dto.response.TagResponseDTO;
import kr.co.inntavern.dripking.model.enumType.TagGroup;
import kr.co.inntavern.dripking.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<TagResponseDTO> getAllTags(@RequestParam(required = false) TagGroup group) {
        return tagService.getAllTags(group);
    }

    @PostMapping
    public ResponseEntity<TagResponseDTO> createTag(@RequestBody TagRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(requestDTO));
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponseDTO> updateTag(@PathVariable Long tagId,
                                                    @RequestBody TagRequestDTO requestDTO) {
        return ResponseEntity.ok(tagService.updateTag(tagId, requestDTO));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTagById(tagId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
