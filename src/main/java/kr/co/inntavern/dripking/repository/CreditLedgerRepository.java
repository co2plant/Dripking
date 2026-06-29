package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.CreditLedger;
import kr.co.inntavern.dripking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditLedgerRepository extends JpaRepository<CreditLedger, Long> {
    Optional<CreditLedger> findFirstByUserAndDeltaGreaterThanOrderByCreatedAtDesc(User user, int delta);
}
