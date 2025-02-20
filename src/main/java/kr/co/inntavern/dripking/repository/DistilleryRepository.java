package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Distillery;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistilleryRepository extends JpaRepository<Distillery, Long> {
    Page<Distillery> findAll(Pageable pageable);
    Distillery save(Distillery distillery);
    Page<Distillery> findAllByDestinationId(Pageable pageable, Long destinationId);
    Optional<Distillery> findById(Long id);

    @NonNull
    @Query("SELECT d FROM Distillery d WHERE d.latitude BETWEEN :minLatitude AND :maxLatitude AND d.longitude BETWEEN :minLongitude AND :maxLongitude")
    Page<Distillery> findAllByLatitudeAndLongitude(Pageable pageable, Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude);

    Page<Distillery> findAllByNameContainingIgnoreCase(Pageable pageable, String name);

}
