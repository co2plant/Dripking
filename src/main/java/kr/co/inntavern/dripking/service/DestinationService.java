package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.DestinationRequestDTO;
import kr.co.inntavern.dripking.dto.response.DestinationResponseDTO;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.CityRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.util.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    public DestinationService(DestinationRepository destinationRepository,
                              CityRepository cityRepository,
                              CategoryRepository categoryRepository){
        this.destinationRepository = destinationRepository;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<DestinationResponseDTO> getAllDestinations(int page, int size, String sort){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return destinationRepository.findAll(pageable).map(this::mapToDestinationResponseDTO);
    }

    public DestinationResponseDTO getDestinationById(Long Id){
        return destinationRepository.findById(Id).map(this::mapToDestinationResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    public Page<DestinationResponseDTO> getAllDestinationsByName(int page, int size, String sort, String name){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return destinationRepository.findAllByNameContainingIgnoreCase(pageable, name).map(this::mapToDestinationResponseDTO);
    }

    public Page<DestinationResponseDTO> getAllDestinationsByCountryId(int page, int size, String sort, Long countryId){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return destinationRepository.findAllByCountryId(pageable, countryId).map(this::mapToDestinationResponseDTO);
    }

    public Page<DestinationResponseDTO> getAllDestinationsByLatitudeAndLongitude(int page, int size, String sort, Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude){
        Pageable pageable = PageableUtils.pageRequest(page, size, sort);
        return destinationRepository.findAllByLatitudeAndLongitude(pageable, minLatitude, maxLatitude, minLongitude, maxLongitude).map(this::mapToDestinationResponseDTO);
    }

    public List<DestinationResponseDTO> getDestinationMarkers(){
        return destinationRepository.findAllByLatitudeIsNotNullAndLongitudeIsNotNull().stream()
                .map(this::mapToDestinationResponseDTO)
                .toList();
    }

    public DestinationResponseDTO createDestination(DestinationRequestDTO requestDTO){
        Destination destination = new Destination();
        applyRequest(destination, requestDTO);
        return mapToDestinationResponseDTO(destinationRepository.save(destination));
    }

    public DestinationResponseDTO updateDestination(Long destination_id, DestinationRequestDTO requestDTO){
        Destination exsitingDestination = destinationRepository.findById(destination_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 장소가 존재하지 않습니다."));

        applyRequest(exsitingDestination, requestDTO);
        return mapToDestinationResponseDTO(destinationRepository.save(exsitingDestination));
    }

    private void applyRequest(Destination destination, DestinationRequestDTO requestDTO) {
        validateRequest(requestDTO);
        City city = cityRepository.findById(requestDTO.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도시가 존재하지 않습니다."));
        Category category = null;
        if(requestDTO.getCategoryId() != null){
            category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다."));
        }

        destination.setName(requestDTO.getName());
        destination.setDescription(requestDTO.getDescription());
        destination.setImgUrl(requestDTO.getImgUrl());
        destination.setImgObjectKey(resolveImageObjectKey(destination.getImgObjectKey(), requestDTO.getImgObjectKey()));
        destination.setLatitude(requestDTO.getLatitude());
        destination.setLongitude(requestDTO.getLongitude());
        destination.setCity(city);
        destination.setCategory(category);
    }

    private void validateRequest(DestinationRequestDTO requestDTO) {
        if(requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()){
            throw new IllegalArgumentException("여행지 이름이 필요합니다.");
        }
        if(requestDTO.getCityId() == null){
            throw new IllegalArgumentException("cityId가 필요합니다.");
        }
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
        responseDTO.setImgObjectKey(destination.getImgObjectKey());
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

    private String resolveImageObjectKey(String currentObjectKey, String nextObjectKey) {
        if (nextObjectKey == null || nextObjectKey.isBlank()) {
            return currentObjectKey;
        }
        return nextObjectKey;
    }
}
