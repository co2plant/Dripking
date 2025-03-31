package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public void createCategory(Category category){
        categoryRepository.save(category);
    }

    public void updateCategory(Long categoryId, Category category){
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다."));

        categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 카테고리입니다."));
        categoryRepository.deleteById(id);
    }
}
