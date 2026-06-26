package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.CourseGenerateRequestDTO;
import kr.co.inntavern.dripking.dto.response.CourseGenerateResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.CourseGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseGenerationService courseGenerationService;

    public CourseController(CourseGenerationService courseGenerationService) {
        this.courseGenerationService = courseGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<CourseGenerateResponseDTO> generateCourse(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CourseGenerateRequestDTO requestDTO) {
        Long userId = customUserDetails == null ? null : customUserDetails.getId();
        return ResponseEntity.ok(courseGenerationService.generate(userId, requestDTO));
    }
}
