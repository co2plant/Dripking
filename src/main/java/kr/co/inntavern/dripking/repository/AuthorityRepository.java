package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.security.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(UserRole role);

    List<Authority> findAllByName(UserRole role);
}
