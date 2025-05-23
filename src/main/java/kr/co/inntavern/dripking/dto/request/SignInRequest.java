package kr.co.inntavern.dripking.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @JsonProperty("password")
    private String password;

    @JsonProperty("rememberMe")
    private boolean rememberMe;
}
