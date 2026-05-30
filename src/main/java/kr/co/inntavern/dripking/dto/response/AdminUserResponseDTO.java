package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AdminUserResponseDTO {
    private Long id;
    private String email;
    private String nickname;
    private List<String> roles;
    private boolean locked;
    private boolean emailVerified;
    private String phoneNumber;
    private String address;
    private Date createdAt;
}
