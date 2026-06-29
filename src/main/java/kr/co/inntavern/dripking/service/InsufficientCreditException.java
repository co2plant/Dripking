package kr.co.inntavern.dripking.service;

import lombok.Getter;

@Getter
public class InsufficientCreditException extends RuntimeException {
    private final int requiredCredit;
    private final int currentBalance;

    public InsufficientCreditException(int requiredCredit, int currentBalance) {
        super("크레딧이 부족합니다.");
        this.requiredCredit = requiredCredit;
        this.currentBalance = currentBalance;
    }
}
