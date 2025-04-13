package kr.co.inntavern.dripking.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String grantType;

    public JwtTokenResponseDTO(String accessToken, String refreshToken, String grantType){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
    }
}
