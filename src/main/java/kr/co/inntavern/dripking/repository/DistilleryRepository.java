package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Distillery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistilleryRepository extends JpaRepository<Distillery, Long> {
    List<Distillery> findAll();
    Distillery save(Distillery distillery);
    Optional<Distillery> findById(Long id);
}
