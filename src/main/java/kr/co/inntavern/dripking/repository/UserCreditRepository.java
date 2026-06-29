package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, Long> {
}
