package kr.co.inntavern.dripking.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.inntavern.dripking.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    @JsonProperty("email")
    private String email;

    @Size(min=16, max=32)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @JsonProperty("password")
    private String password;

    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("agreeToTerms")
    private boolean agreeToTerms;

    public User toEntity(){
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);

        return user;
    }

    public User toEntity(String encodedPassword){
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setNickname(nickname);

        return user;
    }
}
