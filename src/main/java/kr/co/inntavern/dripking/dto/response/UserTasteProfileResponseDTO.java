package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserTasteProfileResponseDTO {
    private Long userId;
    private List<Long> categories;
    private List<String> flavorTags;
    private LocalDateTime updatedAt;
}
