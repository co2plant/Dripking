package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.response.ContentImageUploadResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.service.ContentImageUploadException;
import kr.co.inntavern.dripking.service.ContentImageUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/content-images")
public class ContentImageController {
    private final ContentImageUploadService contentImageUploadService;

    public ContentImageController(ContentImageUploadService contentImageUploadService) {
        this.contentImageUploadService = contentImageUploadService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadContentImage(@RequestParam("file") MultipartFile file,
                                                @RequestParam("itemType") String itemType) {
        try {
            ContentImageUploadResponseDTO responseDTO = contentImageUploadService.upload(file, parseItemType(itemType));
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ContentImageUploadException exception) {
            return ResponseEntity.status(exception.getStatus()).body(Map.of("message", exception.getMessage()));
        }
    }

    private ItemType parseItemType(String itemType) {
        if (itemType == null || itemType.isBlank()) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "콘텐츠 타입이 필요합니다.");
        }
        try {
            return ItemType.valueOf(itemType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "지원하지 않는 콘텐츠 타입입니다.", exception);
        }
    }
}
