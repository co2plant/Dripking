package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.dto.response.dashboard.DestinationDashboardResponseDTO;
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
    @Query("SELECT d FROM Destination d WHERE d.city.id = :cityId")
    Page<Destination> findAllByCountryId(Pageable pageable, Long cityId);

    @NonNull
    @Query("SELECT d FROM Destination d WHERE d.latitude BETWEEN :minLatitude AND :maxLatitude AND d.longitude BETWEEN :minLongitude AND :maxLongitude")
    Page<Destination> findAllByLatitudeAndLongitude(Pageable pageable, Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude);
    @NonNull
    Page<Destination> findAllByNameContainingIgnoreCase(Pageable pageable, String name);

    @Query("SELECT new kr.co.inntavern.dripking.dto.response.dashboard.DestinationDashboardResponseDTO(" +
            "d.id, " +
            "d.name, " +
            "co.name, " +
            "ci.name, " +
            "ca.name, " +
            "COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.target_id = d.id AND r.reviewType = d.itemType), 0.0)" +
            ") " +
            "FROM Destination d " +
            "LEFT JOIN d.city ci " +
            "LEFT JOIN ci.country co " +
            "LEFT JOIN d.category ca")
    Page<DestinationDashboardResponseDTO> findAllForDestinationDashboard(Pageable pageable);
}
