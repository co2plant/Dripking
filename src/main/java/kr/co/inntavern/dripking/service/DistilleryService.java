package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.DistilleryResponseDTO;
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
    public Page<DistilleryResponseDTO> getAllDistilleries(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAll(pageable).map(this::mapToDistilleryResponseDTO);
    }

    public DistilleryResponseDTO getDistilleryById(Long Id){
        return distilleryRepository.findById(Id).map(this::mapToDistilleryResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByDestinationId(Long destinationId){
        Pageable pageable = PageRequest.of(0, 10);
        return distilleryRepository.findAllByDestinationId(pageable, destinationId).map(this::mapToDistilleryResponseDTO);
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByLatitudeAndLongitude(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude){
        Pageable pageable = PageRequest.of(0, 10);
        return distilleryRepository.findAllByLatitudeAndLongitude(pageable, minLatitude, maxLatitude, minLongitude, maxLongitude).map(this::mapToDistilleryResponseDTO);
    }

    public Page<DistilleryResponseDTO> getAllDistilleriesByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToDistilleryResponseDTO);
    }

    public void createDistillery(Distillery Distillery){
        distilleryRepository.save(Distillery);
    }

    public void updateDistillery(Long distillery_id, Distillery Distillery){
        Distillery existingDistillery = distilleryRepository.findById(distillery_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 증류소가 존재하지 않습니다."));

        distilleryRepository.save(Distillery);
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
        responseDTO.setImg_url(distillery.getImg_url());
        responseDTO.setDestination_id(distillery.getDestination().getId());
        responseDTO.setItemType(distillery.getItemType());
        return responseDTO;
    }

}
