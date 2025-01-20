package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.Response.AlcoholResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlcoholService {
    private final  AlcoholRepository alcoholRepository;

    public AlcoholService(AlcoholRepository alcoholRepository){
        this.alcoholRepository = alcoholRepository;
    }

    // ---------------------------------------------------------------------
    // Select Methods: 모든 엔티티를 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<AlcoholResponseDTO> getAllAlcohols(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return alcoholRepository.findAll(pageable).map(this::mapToAlcoholResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Select Methods: 특정 Id를 가진 엔티티를 반환하는 메서드
    // ---------------------------------------------------------------------
    public Alcohol getAlcoholById(Long Id){
        return alcoholRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    // ---------------------------------------------------------------------
    // Select Methods: 이름이 포함된(대소문자 무시) 컬럼을 검색하여 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<AlcoholResponseDTO> getAllAlcoholsByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return alcoholRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToAlcoholResponseDTO);
    }

    public Page<AlcoholResponseDTO> getAllAlcoholsByDistilleryId(Long distilleryId){
        Pageable pageable = PageRequest.of(0, 10);
        return alcoholRepository.findAllByDistilleryId(pageable, distilleryId).map(this::mapToAlcoholResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Create Methods: 엔티티를 생성하는 메서드
    // ---------------------------------------------------------------------
    public Alcohol createAlcohol(Alcohol alcohol){
        return alcoholRepository.save(alcohol);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
    public Alcohol updateAlcohol(Long id, Alcohol alcohol){
        return alcoholRepository.save(alcohol);
    }

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteAlcoholById(Long id){
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
