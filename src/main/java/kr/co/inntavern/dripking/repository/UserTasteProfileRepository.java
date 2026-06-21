package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.UserTasteProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTasteProfileRepository extends JpaRepository<UserTasteProfile, Long> {
    Optional<UserTasteProfile> findByUserId(Long userId);
}
