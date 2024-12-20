package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Destination save(Destination destination);
    Optional<Destination> findById(Long id);
    List<Destination> findAll();
    List<Destination> findByName(String name);
}
