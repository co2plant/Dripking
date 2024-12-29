package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Distillery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistilleryRepository extends JpaRepository<Distillery, Long> {
    Page<Distillery> findAll(Pageable pageable);
    Distillery save(Distillery distillery);
    Optional<Distillery> findById(Long id);
    Page<Distillery> findAllByNameContainingIgnoreCase(Pageable pageable, String name);

}
