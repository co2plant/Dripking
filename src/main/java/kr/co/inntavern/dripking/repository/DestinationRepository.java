package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Destination;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    @NonNull
    Destination save(Destination destination);

    @NonNull
    Optional<Destination> findById(Long id);
    @NonNull
    Page<Destination> findAll(Pageable pageable);
    @NonNull
    @Query("SELECT d FROM Destination d WHERE d.country.id = :countryId")
    Page<Destination> findAllByCountryId(Pageable pageable, Long countryId);
    @NonNull
    Page<Destination> findAllByNameContainingIgnoreCase(Pageable pageable, String name);
}
