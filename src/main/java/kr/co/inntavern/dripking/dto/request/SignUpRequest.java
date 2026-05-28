package kr.co.inntavern.dripking.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.inntavern.dripking.security.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @JsonProperty("email")
    private String email;

    @Size(min=16, max=32, message = "비밀번호는 16자 이상 32자 이하이어야 합니다.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @JsonProperty("nickname")
    private String nickname;

    @AssertTrue(message = "이용약관에 동의해주세요.")
    @JsonProperty("agreeToTerms")
    private boolean agreeToTerms;

    @JsonProperty("userRole")
    private UserRole userRole=UserRole.USER;
}
