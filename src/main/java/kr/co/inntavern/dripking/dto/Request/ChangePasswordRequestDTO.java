package kr.co.inntavern.dripking.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDTO {
    private String currentPassword;

    private String newPassword;
}
