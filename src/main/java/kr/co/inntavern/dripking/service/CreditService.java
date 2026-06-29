package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.CreditBalanceResponseDTO;
import kr.co.inntavern.dripking.model.CreditLedger;
import kr.co.inntavern.dripking.model.GenerationLog;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.UserCredit;
import kr.co.inntavern.dripking.model.enumType.CreditLedgerReason;
import kr.co.inntavern.dripking.repository.CreditLedgerRepository;
import kr.co.inntavern.dripking.repository.GenerationLogRepository;
import kr.co.inntavern.dripking.repository.UserCreditRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreditService {
    public static final int SIGNUP_CREDIT_AMOUNT = 50;
    public static final int COURSE_GENERATION_COST = 10;

    private final UserCreditRepository userCreditRepository;
    private final CreditLedgerRepository creditLedgerRepository;
    private final GenerationLogRepository generationLogRepository;
    private final UserRepository userRepository;

    public CreditService(UserCreditRepository userCreditRepository,
                         CreditLedgerRepository creditLedgerRepository,
                         GenerationLogRepository generationLogRepository,
                         UserRepository userRepository) {
        this.userCreditRepository = userCreditRepository;
        this.creditLedgerRepository = creditLedgerRepository;
        this.generationLogRepository = generationLogRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public CreditBalanceResponseDTO getBalance(Long userId) {
        User user = findUser(userId);
        UserCredit userCredit = getOrCreateCredit(user);
        CreditBalanceResponseDTO responseDTO = new CreditBalanceResponseDTO();
        responseDTO.setBalance(userCredit.getBalance());
        responseDTO.setLastChargedAt(creditLedgerRepository
                .findFirstByUserAndDeltaGreaterThanOrderByCreatedAtDesc(user, 0)
                .map(CreditLedger::getCreatedAt)
                .orElse(null));
        return responseDTO;
    }

    @Transactional
    public GenerationCreditResult chargeForCourseGeneration(Long userId, String courseId, String inputHash) {
        User user = findUser(userId);
        UserCredit userCredit = getOrCreateCredit(user);
        if (userCredit.getBalance() < COURSE_GENERATION_COST) {
            throw new InsufficientCreditException(COURSE_GENERATION_COST, userCredit.getBalance());
        }

        userCredit.setBalance(userCredit.getBalance() - COURSE_GENERATION_COST);
        userCreditRepository.save(userCredit);

        CreditLedger ledger = new CreditLedger();
        ledger.setUser(user);
        ledger.setDelta(-COURSE_GENERATION_COST);
        ledger.setReason(CreditLedgerReason.GENERATE);
        ledger.setRefId(courseId);
        creditLedgerRepository.save(ledger);

        GenerationLog generationLog = new GenerationLog();
        generationLog.setUser(user);
        generationLog.setInputHash(inputHash);
        generationLog.setCourseId(courseId);
        generationLog.setTokensIn(0);
        generationLog.setTokensOut(0);
        generationLog.setEstCost(BigDecimal.ZERO);
        generationLog.setSavedToTrip(false);
        generationLogRepository.save(generationLog);

        return new GenerationCreditResult(COURSE_GENERATION_COST, userCredit.getBalance());
    }

    private UserCredit getOrCreateCredit(User user) {
        return userCreditRepository.findById(user.getId())
                .orElseGet(() -> grantSignupCredit(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    public record GenerationCreditResult(int creditCharged, int remainingCredit) {
    }
}
