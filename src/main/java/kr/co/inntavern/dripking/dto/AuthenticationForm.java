package kr.co.inntavern.dripking.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationForm {
    @NotEmpty
    @NotBlank
    @Email
    private String authentication_email;
    @NotBlank
    @NotEmpty
    private String authentication_pw;
    @NotBlank
    @NotEmpty
    private String authentication_pw_check;
    @NotBlank
    @NotEmpty
    @Size(min = 2, max = 20)
    private String name;

}
