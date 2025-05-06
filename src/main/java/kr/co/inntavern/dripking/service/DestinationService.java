package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.DestinationResponseDTO;
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

    public Page<DestinationResponseDTO> getAllDestinations(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAll(pageable).map(this::mapToDestinationResponseDTO);
    }

    public DestinationResponseDTO getDestinationById(Long Id){
        return destinationRepository.findById(Id).map(this::mapToDestinationResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<DestinationResponseDTO> getAllDestinationsByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToDestinationResponseDTO);
    }

    public Page<DestinationResponseDTO> getAllDestinationsByCountryId(int page, Long countryId){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAllByCountryId(pageable, countryId).map(this::mapToDestinationResponseDTO);
    }

    public void createDestination(Destination Destination){
        destinationRepository.save(Destination);
    }

    public void updateDestination(Long destination_id, Destination Destination){
        Destination exsitingDestination = destinationRepository.findById(destination_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 장소가 존재하지 않습니다."));

        destinationRepository.save(Destination);
    }

    public void deleteDestinationById(Long id){
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 장소입니다."));
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
        responseDTO.setCountry_id(destination.getCity().getId());
        responseDTO.setItemType(destination.getItemType());
        return responseDTO;
    }
}
