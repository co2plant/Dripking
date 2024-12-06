package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Alcohol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlcoholRepository extends JpaRepository<Alcohol, Long> {
    List<Alcohol> findAll();

    Alcohol save(Alcohol alcohol);

    Optional<Alcohol> findById(Long id);
}
