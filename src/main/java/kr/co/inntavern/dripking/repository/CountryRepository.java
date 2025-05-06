package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Country save(Country country);
    Country findByName(String name);
    List<Country> findAll();
}
