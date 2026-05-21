package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.DestinationResponseDTO;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.util.PageableUtils;
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

    public Page<DestinationResponseDTO> getAllDestinations(int page, int size, String sort){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
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

    public Page<DestinationResponseDTO> getAllDestinationsByCountryId(int page, int size, String sort, Long countryId){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return destinationRepository.findAllByCountryId(pageable, countryId).map(this::mapToDestinationResponseDTO);
    }

    public Page<DestinationResponseDTO> getAllDestinationsByLatitudeAndLongitude(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude){
        Pageable pageable = PageRequest.of(0, 10);
        return destinationRepository.findAllByLatitudeAndLongitude(pageable, minLatitude, maxLatitude, minLongitude, maxLongitude).map(this::mapToDestinationResponseDTO);
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
        responseDTO.setImgUrl(destination.getImgUrl());
        responseDTO.setLatitude(destination.getLatitude());
        responseDTO.setLongitude(destination.getLongitude());
        if (destination.getCity() != null) {
            responseDTO.setCityId(destination.getCity().getId());
            responseDTO.setCityName(destination.getCity().getName());
            if (destination.getCity().getCountry() != null) {
                responseDTO.setCountryId(destination.getCity().getCountry().getId());
                responseDTO.setCountryName(destination.getCity().getCountry().getName());
            }
        }
        if (destination.getCategory() != null) {
            responseDTO.setCategoryId(destination.getCategory().getId());
        }
        return responseDTO;
    }
}
