package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan save(Plan plan);

    @Query("SELECT p FROM Plan p WHERE p.trip.id = :tripId")
    Page<Plan> findAllByTripId(@Param("tripId")Long tripId, Pageable pageable);

    void deleteById(Long id);

    @Query("DELETE FROM Plan p WHERE p.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId")Long tripId);
}
