package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.CategoryRequestDTO;
import kr.co.inntavern.dripking.dto.response.CategoryResponseDTO;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO){
        validateRequest(requestDTO);
        Category category = Category.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .build();
        return mapToResponseDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long categoryId, CategoryRequestDTO requestDTO){
        validateRequest(requestDTO);
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다."));

        Category category = Category.builder()
                .id(categoryId)
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .build();
        return mapToResponseDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategoryById(Long id){
        categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 카테고리입니다."));
        categoryRepository.deleteById(id);
    }

    private void validateRequest(CategoryRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()) {
            throw new IllegalArgumentException("카테고리 이름이 필요합니다.");
        }
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(category.getId());
        responseDTO.setName(category.getName());
        responseDTO.setDescription(category.getDescription());
        return responseDTO;
    }
}
