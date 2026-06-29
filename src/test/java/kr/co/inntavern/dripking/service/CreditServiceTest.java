package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.CreditLedger;
import kr.co.inntavern.dripking.model.GenerationLog;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.UserCredit;
import kr.co.inntavern.dripking.model.enumType.CreditLedgerReason;
import kr.co.inntavern.dripking.repository.CreditLedgerRepository;
import kr.co.inntavern.dripking.repository.GenerationLogRepository;
import kr.co.inntavern.dripking.repository.UserCreditRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private UserCreditRepository userCreditRepository;

    @Mock
    private CreditLedgerRepository creditLedgerRepository;

    @Mock
    private GenerationLogRepository generationLogRepository;

    @Mock
    private UserRepository userRepository;

    private CreditService creditService;

    @BeforeEach
    void setUp() {
        creditService = new CreditService(userCreditRepository, creditLedgerRepository, generationLogRepository, userRepository);
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

    @Test
    void chargeForCourseGenerationDeductsCreditAndRecordsLedgerAndGenerationLog() {
        User user = user(10L);
        UserCredit userCredit = userCredit(user, 50);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userCreditRepository.findById(10L)).thenReturn(Optional.of(userCredit));

        CreditService.GenerationCreditResult result = creditService.chargeForCourseGeneration(
                10L,
                "draft-course",
                "input-hash"
        );

        assertThat(result.creditCharged()).isEqualTo(CreditService.COURSE_GENERATION_COST);
        assertThat(result.remainingCredit()).isEqualTo(40);
        assertThat(userCredit.getBalance()).isEqualTo(40);
        verify(userCreditRepository).save(userCredit);

        ArgumentCaptor<CreditLedger> ledgerCaptor = ArgumentCaptor.forClass(CreditLedger.class);
        verify(creditLedgerRepository).save(ledgerCaptor.capture());
        assertThat(ledgerCaptor.getValue().getDelta()).isEqualTo(-CreditService.COURSE_GENERATION_COST);
        assertThat(ledgerCaptor.getValue().getReason()).isEqualTo(CreditLedgerReason.GENERATE);
        assertThat(ledgerCaptor.getValue().getRefId()).isEqualTo("draft-course");

        ArgumentCaptor<GenerationLog> generationLogCaptor = ArgumentCaptor.forClass(GenerationLog.class);
        verify(generationLogRepository).save(generationLogCaptor.capture());
        assertThat(generationLogCaptor.getValue().getUser()).isEqualTo(user);
        assertThat(generationLogCaptor.getValue().getCourseId()).isEqualTo("draft-course");
        assertThat(generationLogCaptor.getValue().getInputHash()).isEqualTo("input-hash");
        assertThat(generationLogCaptor.getValue().isSavedToTrip()).isFalse();
    }

    @Test
    void chargeForCourseGenerationRejectsInsufficientBalanceWithoutWrites() {
        User user = user(10L);
        UserCredit userCredit = userCredit(user, 5);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userCreditRepository.findById(10L)).thenReturn(Optional.of(userCredit));

        assertThatThrownBy(() -> creditService.chargeForCourseGeneration(10L, "draft-course", "input-hash"))
                .isInstanceOf(InsufficientCreditException.class)
                .hasMessage("크레딧이 부족합니다.");

        assertThat(userCredit.getBalance()).isEqualTo(5);
        verify(userCreditRepository, never()).save(any(UserCredit.class));
        verify(creditLedgerRepository, never()).save(any(CreditLedger.class));
        verify(generationLogRepository, never()).save(any());
    }

    @Test
    void getBalanceBackfillsMissingCreditAccountForExistingUser() {
        User user = user(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userCreditRepository.findById(10L)).thenReturn(Optional.empty());
        when(userCreditRepository.existsById(10L)).thenReturn(false);
        when(userCreditRepository.save(any(UserCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(creditService.getBalance(10L).getBalance()).isEqualTo(CreditService.SIGNUP_CREDIT_AMOUNT);

        verify(creditLedgerRepository).save(any(CreditLedger.class));
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user-%d@example.com".formatted(id));
        return user;
    }

    private UserCredit userCredit(User user, int balance) {
        UserCredit userCredit = new UserCredit();
        userCredit.setUser(user);
        userCredit.setUserId(user.getId());
        userCredit.setBalance(balance);
        return userCredit;
    }
}
