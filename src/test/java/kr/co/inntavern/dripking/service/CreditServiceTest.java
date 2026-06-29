package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.CreditLedger;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.UserCredit;
import kr.co.inntavern.dripking.model.enumType.CreditLedgerReason;
import kr.co.inntavern.dripking.repository.CreditLedgerRepository;
import kr.co.inntavern.dripking.repository.UserCreditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private UserCreditRepository userCreditRepository;

    @Mock
    private CreditLedgerRepository creditLedgerRepository;

    private CreditService creditService;

    @BeforeEach
    void setUp() {
        creditService = new CreditService(userCreditRepository, creditLedgerRepository);
    }

    @Test
    void grantSignupCreditCreatesCreditAccountAndLedger() {
        User user = user(10L);
        when(userCreditRepository.existsById(10L)).thenReturn(false);
        when(userCreditRepository.save(any(UserCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserCredit userCredit = creditService.grantSignupCredit(user);

        assertThat(userCredit.getUser()).isEqualTo(user);
        assertThat(userCredit.getBalance()).isEqualTo(CreditService.SIGNUP_CREDIT_AMOUNT);

        ArgumentCaptor<CreditLedger> ledgerCaptor = ArgumentCaptor.forClass(CreditLedger.class);
        verify(creditLedgerRepository).save(ledgerCaptor.capture());
        CreditLedger ledger = ledgerCaptor.getValue();
        assertThat(ledger.getUser()).isEqualTo(user);
        assertThat(ledger.getDelta()).isEqualTo(CreditService.SIGNUP_CREDIT_AMOUNT);
        assertThat(ledger.getReason()).isEqualTo(CreditLedgerReason.SIGNUP);
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user-%d@example.com".formatted(id));
        return user;
    }
}
