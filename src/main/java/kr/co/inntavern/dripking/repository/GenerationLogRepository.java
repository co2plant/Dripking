package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.GenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenerationLogRepository extends JpaRepository<GenerationLog, Long> {
}
