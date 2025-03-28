package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.Response.DestinationResponseDTO;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    public DestinationService(DestinationRepository destinationRepository){
        this.destinationRepository = destinationRepository;
    }

    // ---------------------------------------------------------------------
    // Select Methods: 모든 엔티티를 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<DestinationResponseDTO> getAllDestinations(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAll(pageable).map(this::mapToDestinationResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Select Methods: 특정 Id를 가진 엔티티를 반환하는 메서드
    // ---------------------------------------------------------------------
    public DestinationResponseDTO getDestinationById(Long Id){
        return destinationRepository.findById(Id).map(this::mapToDestinationResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    // ---------------------------------------------------------------------
    // Select Methods: 이름이 포함된(대소문자 무시) 컬럼을 검색하여 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<DestinationResponseDTO> getAllDestinationsByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToDestinationResponseDTO);
    }

    public Page<DestinationResponseDTO> getAllDestinationsByCountryId(int page, Long countryId){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAllByCountryId(pageable, countryId).map(this::mapToDestinationResponseDTO);
    }

    // ---------------------------------------------------------------------
    // Create Methods: 엔티티를 생성하는 메서드
    // ---------------------------------------------------------------------
    public Destination createDestination(Destination Destination){
        return destinationRepository.save(Destination);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
    public Destination updateDestination(Long id, Destination Destination){
        return destinationRepository.save(Destination);
    }

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteDestinationById(Long id){
        destinationRepository.deleteById(id);
    }

    private DestinationResponseDTO mapToDestinationResponseDTO(Destination destination){
        DestinationResponseDTO responseDTO = new DestinationResponseDTO();
        responseDTO.setId(destination.getId());
        responseDTO.setName(destination.getName());
        responseDTO.setDescription(destination.getDescription());
        responseDTO.setImg_url(destination.getImg_url());
        responseDTO.setLatitude(destination.getLatitude());
        responseDTO.setLongitude(destination.getLongitude());
        responseDTO.setCountry_id(destination.getCountry().getId());
        responseDTO.setItemType(destination.getItemType());
        return responseDTO;
    }
}
