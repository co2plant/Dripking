package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.CreditLedger;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.CreditLedgerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CreditLedgerRepository extends JpaRepository<CreditLedger, Long> {
    Optional<CreditLedger> findFirstByUserAndReasonAndDeltaLessThanOrderByCreatedAtDesc(
            User user,
            CreditLedgerReason reason,
            int delta
    );

    Optional<CreditLedger> findFirstByUserAndCreatedAtBeforeOrderByCreatedAtDesc(User user, LocalDateTime before);
}
