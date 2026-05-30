package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.DistilleryRequestDTO;
import kr.co.inntavern.dripking.dto.response.DistilleryResponseDTO;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.util.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistilleryService {
    private final DistilleryRepository distilleryRepository;
    private final DestinationRepository destinationRepository;

    public DistilleryService(DistilleryRepository distilleryRepository,
                             DestinationRepository destinationRepository){
        this.distilleryRepository = distilleryRepository;
        this.destinationRepository = destinationRepository;
    }
    public Page<DistilleryResponseDTO> getAllDistilleries(int page, int size, String sort){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return distilleryRepository.findAll(pageable).map(this::mapToDistilleryResponseDTO);
    }

    public DistilleryResponseDTO getDistilleryById(Long Id){
        return distilleryRepository.findById(Id).map(this::mapToDistilleryResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByDestinationId(int page, int size, String sort, Long destinationId){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return distilleryRepository.findAllByDestinationId(pageable, destinationId).map(this::mapToDistilleryResponseDTO);
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByLatitudeAndLongitude(int page, int size, String sort, Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return distilleryRepository.findAllByLatitudeAndLongitude(pageable, minLatitude, maxLatitude, minLongitude, maxLongitude).map(this::mapToDistilleryResponseDTO);
    }

    public List<DistilleryResponseDTO> getDistilleryMarkers(){
        return distilleryRepository.findAllByLatitudeIsNotNullAndLongitudeIsNotNull().stream()
                .map(this::mapToDistilleryResponseDTO)
                .toList();
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByName(int page, int size, String sort, String name){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return distilleryRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToDistilleryResponseDTO);
    }

    public DistilleryResponseDTO createDistillery(DistilleryRequestDTO requestDTO){
        Distillery distillery = new Distillery();
        applyRequest(distillery, requestDTO);
        return mapToDistilleryResponseDTO(distilleryRepository.save(distillery));
    }

    public DistilleryResponseDTO updateDistillery(Long distillery_id, DistilleryRequestDTO requestDTO){
        Distillery existingDistillery = distilleryRepository.findById(distillery_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 증류소가 존재하지 않습니다."));

        applyRequest(existingDistillery, requestDTO);
        return mapToDistilleryResponseDTO(distilleryRepository.save(existingDistillery));
    }

    private void applyRequest(Distillery distillery, DistilleryRequestDTO requestDTO) {
        validateRequest(requestDTO);
        Destination destination = destinationRepository.findById(requestDTO.getDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행지가 존재하지 않습니다."));

        distillery.setName(requestDTO.getName());
        distillery.setDescription(requestDTO.getDescription());
        distillery.setImgUrl(requestDTO.getImgUrl());
        distillery.setImgObjectKey(resolveImageObjectKey(distillery.getImgObjectKey(), requestDTO.getImgObjectKey()));
        distillery.setAddress(requestDTO.getAddress());
        distillery.setLatitude(requestDTO.getLatitude());
        distillery.setLongitude(requestDTO.getLongitude());
        distillery.setDestination(destination);
    }

    private void validateRequest(DistilleryRequestDTO requestDTO) {
        if(requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()){
            throw new IllegalArgumentException("양조장 이름이 필요합니다.");
        }
        if(requestDTO.getAddress() == null || requestDTO.getAddress().isBlank()){
            throw new IllegalArgumentException("주소가 필요합니다.");
        }
        if(requestDTO.getDestinationId() == null){
            throw new IllegalArgumentException("destinationId가 필요합니다.");
        }
    }

    public void deleteDistilleryById(Long distillery_id){
        Distillery distillery = distilleryRepository.findById(distillery_id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 증류소입니다."));

        distilleryRepository.deleteById(distillery_id);
    }

    private DistilleryResponseDTO mapToDistilleryResponseDTO(Distillery distillery){
        DistilleryResponseDTO responseDTO = new DistilleryResponseDTO();
        responseDTO.setId(distillery.getId());
        responseDTO.setName(distillery.getName());
        responseDTO.setAddress(distillery.getAddress());
        responseDTO.setDescription(distillery.getDescription());
        responseDTO.setLatitude(distillery.getLatitude());
        responseDTO.setLongitude(distillery.getLongitude());
        responseDTO.setImgUrl(distillery.getImgUrl());
        responseDTO.setImgObjectKey(distillery.getImgObjectKey());
        responseDTO.setDestinationId(distillery.getDestination().getId());
        return responseDTO;
    }

    private String resolveImageObjectKey(String currentObjectKey, String nextObjectKey) {
        if (nextObjectKey == null || nextObjectKey.isBlank()) {
            return currentObjectKey;
        }
        return nextObjectKey;
    }

}
