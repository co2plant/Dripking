package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Alcohol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlcoholRepository extends JpaRepository<Alcohol, Long> {
    Page<Alcohol> findAll(Pageable pageable);

    Page<Alcohol> findAllByNameContainingIgnoreCase(Pageable pageable, String name);

    @Query("SELECT a FROM Alcohol a WHERE a.distillery.id = :distillery_id")
    Page<Alcohol> findAllByDistilleryId(Pageable pageable, @Param("distillery_id")Long distillery_id);

    @Query("SELECT a FROM Alcohol a WHERE a.category.id = :category_id")
    Page<Alcohol> findAllByCategoryId(Pageable pageable, @Param("category_id")Long category_id);

    Alcohol save(Alcohol alcohol);

    Optional<Alcohol> findById(Long id);


}
