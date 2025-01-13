package kr.co.inntavern.dripking.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.inntavern.dripking.model.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    private String email;

    @Size(min=3, max=32)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    public Users toEntity(){
        return Users.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    public Users toEntity(String encodedPassword){
        return Users.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();
    }
}
