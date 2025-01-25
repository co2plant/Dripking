package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Destination save(Destination destination);
    Optional<Destination> findById(Long id);

    Page<Destination> findAll(Pageable pageable);

    @Query("SELECT d FROM Destination d WHERE d.country.id = :countryId")
    Page<Destination> findAllByCountryId(Pageable pageable, Long countryId);

    Page<Destination> findAllByNameContainingIgnoreCase(Pageable pageable, String name);
}
