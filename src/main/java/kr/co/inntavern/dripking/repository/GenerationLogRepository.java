package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.GenerationLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface GenerationLogRepository extends JpaRepository<GenerationLog, Long> {
    long countByAnonId(String anonId);

    @Query("""
            select coalesce(sum(g.estCost), 0)
            from GenerationLog g
            where g.createdAt >= :startAt
              and g.createdAt < :endAt
            """)
    BigDecimal sumEstCostBetween(@Param("startAt") LocalDateTime startAt,
                                 @Param("endAt") LocalDateTime endAt);
}
