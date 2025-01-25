package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Trip save(Trip trip);

    @Query("SELECT t FROM Trip t WHERE t.user.id = :user_id")
    Page<Trip> findAllById(@Param("user_id")Long user_id, Pageable pageable);

}
