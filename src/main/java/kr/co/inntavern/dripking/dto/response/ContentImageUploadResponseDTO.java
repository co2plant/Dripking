package kr.co.inntavern.dripking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentImageUploadResponseDTO {
    private String itemType;
    private String imgObjectKey;
    private String imgUrl;
    private String contentType;
    private long size;
}
