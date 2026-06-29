package kr.co.inntavern.dripking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreditBalanceResponseDTO {
    private int balance;
    private LocalDateTime lastChargedAt;
}
