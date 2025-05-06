package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.AlcoholResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlcoholService {
    private final  AlcoholRepository alcoholRepository;

    public AlcoholService(AlcoholRepository alcoholRepository){
        this.alcoholRepository = alcoholRepository;
    }

    public Page<AlcoholResponseDTO> getAllAlcohols(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return alcoholRepository.findAll(pageable).map(this::mapToAlcoholResponseDTO);
    }

    public Alcohol getAlcoholById(Long Id){
        return alcoholRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByCategoryId(int page, int size, Long categoryId){
        Pageable pageable = PageRequest.of(page, size);
        return alcoholRepository.findAllByCategoryId(pageable, categoryId).map(this::mapToAlcoholResponseDTO);
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return alcoholRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToAlcoholResponseDTO);
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByDistilleryId(Long distilleryId){
        Pageable pageable = PageRequest.of(0, 10);
        return alcoholRepository.findAllByDistilleryId(pageable, distilleryId).map(this::mapToAlcoholResponseDTO);
    }

    public void createAlcohol(Alcohol alcohol){
        alcoholRepository.save(alcohol);
    }

    public void updateAlcohol(Long alcohol_id, Alcohol alcohol){
        Alcohol existingAlcohol = alcoholRepository.findById(alcohol_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));

        alcoholRepository.save(alcohol);
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
        responseDTO.setCategory_id(alcohol.getCategory().getId());
        responseDTO.setDistillery_id(alcohol.getDistillery().getId());
        responseDTO.setStrength(alcohol.getStrength());
        responseDTO.setStated_age(alcohol.getStated_age());
        responseDTO.setSize(alcohol.getSize());
        responseDTO.setDescription(alcohol.getDescription());
        responseDTO.setDatetime(alcohol.getDatetime());
        responseDTO.setImg_url(alcohol.getImg_url());
        responseDTO.setItemType(alcohol.getItemType());
        return responseDTO;
    }

}
