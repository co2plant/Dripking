package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.CreditLedger;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.UserCredit;
import kr.co.inntavern.dripking.model.enumType.CreditLedgerReason;
import kr.co.inntavern.dripking.repository.CreditLedgerRepository;
import kr.co.inntavern.dripking.repository.UserCreditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditService {
    public static final int SIGNUP_CREDIT_AMOUNT = 50;
    public static final int COURSE_GENERATION_COST = 10;

    private final UserCreditRepository userCreditRepository;
    private final CreditLedgerRepository creditLedgerRepository;

    public CreditService(UserCreditRepository userCreditRepository,
                         CreditLedgerRepository creditLedgerRepository) {
        this.userCreditRepository = userCreditRepository;
        this.creditLedgerRepository = creditLedgerRepository;
    }

    @Transactional
    public UserCredit grantSignupCredit(User user) {
        if (userCreditRepository.existsById(user.getId())) {
            return userCreditRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalStateException("크레딧 계정을 찾을 수 없습니다."));
        }

        UserCredit userCredit = new UserCredit();
        userCredit.setUser(user);
        userCredit.setBalance(SIGNUP_CREDIT_AMOUNT);
        UserCredit savedCredit = userCreditRepository.save(userCredit);

        CreditLedger ledger = new CreditLedger();
        ledger.setUser(user);
        ledger.setDelta(SIGNUP_CREDIT_AMOUNT);
        ledger.setReason(CreditLedgerReason.SIGNUP);
        creditLedgerRepository.save(ledger);

        return savedCredit;
    }
}
