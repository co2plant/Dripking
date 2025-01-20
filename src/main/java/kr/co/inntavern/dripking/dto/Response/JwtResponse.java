package kr.co.inntavern.dripking.dto.Response;

import lombok.Getter;

import java.util.List;

@Getter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String nickname;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String nickname, String email, List<String> roles){
        this.token = accessToken;
        this.nickname = nickname;
        this.email = email;
        this.roles = roles;
    }
}
