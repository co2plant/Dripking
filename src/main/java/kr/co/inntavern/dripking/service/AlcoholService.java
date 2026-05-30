package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.AlcoholRequestDTO;
import kr.co.inntavern.dripking.dto.response.AlcoholResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.util.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlcoholService {
    private final  AlcoholRepository alcoholRepository;
    private final CategoryRepository categoryRepository;
    private final DistilleryRepository distilleryRepository;

    public AlcoholService(AlcoholRepository alcoholRepository,
                          CategoryRepository categoryRepository,
                          DistilleryRepository distilleryRepository){
        this.alcoholRepository = alcoholRepository;
        this.categoryRepository = categoryRepository;
        this.distilleryRepository = distilleryRepository;
    }

    public Page<AlcoholResponseDTO> getAllAlcohols(int page, int size, String sort){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return alcoholRepository.findAll(pageable).map(this::mapToAlcoholResponseDTO);
    }

    public AlcoholResponseDTO getAlcoholById(Long Id){
        return alcoholRepository.findById(Id)
                .map(this::mapToAlcoholResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByCategoryId(int page, int size, String sort, Long categoryId){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return alcoholRepository.findAllByCategoryId(pageable, categoryId).map(this::mapToAlcoholResponseDTO);
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByName(int page, int size, String sort, String name){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return alcoholRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToAlcoholResponseDTO);
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByDistilleryId(int page, int size, String sort, Long distilleryId){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return alcoholRepository.findAllByDistilleryId(pageable, distilleryId).map(this::mapToAlcoholResponseDTO);
    }

    public AlcoholResponseDTO createAlcohol(AlcoholRequestDTO requestDTO){
        Alcohol alcohol = new Alcohol();
        applyRequest(alcohol, requestDTO);
        return mapToAlcoholResponseDTO(alcoholRepository.save(alcohol));
    }

    public AlcoholResponseDTO updateAlcohol(Long alcohol_id, AlcoholRequestDTO requestDTO){
        Alcohol existingAlcohol = alcoholRepository.findById(alcohol_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));

        applyRequest(existingAlcohol, requestDTO);
        return mapToAlcoholResponseDTO(alcoholRepository.save(existingAlcohol));
    }

    private void applyRequest(Alcohol alcohol, AlcoholRequestDTO requestDTO){
        validateRequest(requestDTO);
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다."));
        Distillery distillery = distilleryRepository.findById(requestDTO.getDistilleryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 양조장이 존재하지 않습니다."));

        alcohol.setName(requestDTO.getName());
        alcohol.setDescription(requestDTO.getDescription());
        alcohol.setImgUrl(requestDTO.getImgUrl());
        alcohol.setImgObjectKey(resolveImageObjectKey(alcohol.getImgObjectKey(), requestDTO.getImgObjectKey()));
        alcohol.setCategory(category);
        alcohol.setDistillery(distillery);
        alcohol.setStrength(requestDTO.getStrength());
        alcohol.setStated_age(requestDTO.getStatedAge());
        alcohol.setSize(requestDTO.getSize());
    }

    private void validateRequest(AlcoholRequestDTO requestDTO) {
        if(requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()){
            throw new IllegalArgumentException("술 이름이 필요합니다.");
        }
        if(requestDTO.getCategoryId() == null){
            throw new IllegalArgumentException("categoryId가 필요합니다.");
        }
        if(requestDTO.getDistilleryId() == null){
            throw new IllegalArgumentException("distilleryId가 필요합니다.");
        }
    }

    public void deleteAlcoholById(Long id){
        Alcohol alcohol = alcoholRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 술입니다."));

        alcoholRepository.deleteById(id);
    }

    private AlcoholResponseDTO mapToAlcoholResponseDTO(Alcohol alcohol){
        AlcoholResponseDTO responseDTO = new AlcoholResponseDTO();
        responseDTO.setId(alcohol.getId());
        responseDTO.setName(alcohol.getName());
        responseDTO.setCategoryId(alcohol.getCategory().getId());
        responseDTO.setDistilleryId(alcohol.getDistillery().getId());
        responseDTO.setStrength(alcohol.getStrength());
        responseDTO.setStatedAge(alcohol.getStated_age());
        responseDTO.setSize(alcohol.getSize());
        responseDTO.setDescription(alcohol.getDescription());
        responseDTO.setDatetime(alcohol.getDatetime());
        responseDTO.setImgUrl(alcohol.getImgUrl());
        responseDTO.setImgObjectKey(alcohol.getImgObjectKey());
        return responseDTO;
    }

    private String resolveImageObjectKey(String currentObjectKey, String nextObjectKey) {
        if (nextObjectKey == null || nextObjectKey.isBlank()) {
            return currentObjectKey;
        }
        return nextObjectKey;
    }

}
