package kr.co.inntavern.dripking.dto.Reponse;

import lombok.Getter;

import java.util.List;

@Getter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String nickname;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String nickname, String email, List<String> roles){
        this.token = accessToken;
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.roles = roles;
    }
}
