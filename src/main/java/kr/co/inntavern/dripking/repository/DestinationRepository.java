package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Destination save(Destination destination);
}
