package kr.co.inntavern.dripking.repository;


import kr.co.inntavern.dripking.model.City;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @NonNull
    City save(City city);

    @NonNull
    Optional<City> findById(Long id);

    @NonNull
    Optional<City> findByNameContainingIgnoreCase(String name);

    @NonNull
    Page<City> findAll(Pageable pageable);

    @NonNull
    Page<City> findAllByCountryId(Pageable pageable, Long countryId);

    void deleteById(Long id);
}
