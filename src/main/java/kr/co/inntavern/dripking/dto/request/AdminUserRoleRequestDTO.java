package kr.co.inntavern.dripking.dto.request;

import kr.co.inntavern.dripking.security.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserRoleRequestDTO {
    private UserRole role;
}
