package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category save(Category category);
    List<Category> findAll();
}
