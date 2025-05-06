package kr.co.inntavern.dripking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDTO {
    private String currentPassword;

    private String newPassword;
}
