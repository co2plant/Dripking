package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.Response.DistilleryResponseDTO;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DistilleryService {
    private final DistilleryRepository distilleryRepository;

    public DistilleryService(DistilleryRepository distilleryRepository){
        this.distilleryRepository = distilleryRepository;
    }
    // ---------------------------------------------------------------------
    // Select Methods: 모든 엔티티를 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<DistilleryResponseDTO> getAllDistilleries(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAll(pageable).map(this::mapToDistilleryResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Select Methods: 특정 Id를 가진 엔티티를 반환하는 메서드
    // ---------------------------------------------------------------------
    public DistilleryResponseDTO getDistilleryById(Long Id){
        return distilleryRepository.findById(Id).map(this::mapToDistilleryResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByDestinationId(Long destinationId){
        Pageable pageable = PageRequest.of(0, 10);
        return distilleryRepository.findAllByDestinationId(pageable, destinationId).map(this::mapToDistilleryResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Select Methods: 이름이 포함된(대소문자 무시) 컬럼을 검색하여 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<DistilleryResponseDTO> getAllDistilleriesByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToDistilleryResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Create Methods: 엔티티를 생성하는 메서드
    // ---------------------------------------------------------------------
    public Distillery createDistillery(Distillery Distillery){
        return distilleryRepository.save(Distillery);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
    public Distillery updateDistillery(Long id, Distillery Distillery){
        return distilleryRepository.save(Distillery);
    }

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteDistilleryById(Long id){
        distilleryRepository.deleteById(id);
    }

    private DistilleryResponseDTO mapToDistilleryResponseDTO(Distillery distillery){
        DistilleryResponseDTO responseDTO = new DistilleryResponseDTO();
        responseDTO.setId(distillery.getId());
        responseDTO.setName(distillery.getName());
        responseDTO.setAddress(distillery.getAddress());
        responseDTO.setDescription(distillery.getDescription());
        responseDTO.setImg_url(distillery.getImg_url());
        responseDTO.setDestination_id(distillery.getDestination().getId());
        responseDTO.setItemType(distillery.getItemType());
        return responseDTO;
    }

}
