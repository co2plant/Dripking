package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.CityResponseDTO;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    // Renamed from getAllCitiesByCountryId as country filtering is not implemented yet
    public Page<CityResponseDTO> getAllCities(int page){
        Pageable pageable = PageRequest.of(page, 10);
        // TODO: Implement filtering by countryId if needed
        Page<City> cityPage = cityRepository.findAll(pageable);
        return cityPage.map(this::mapToCityResponseDTO);
    }

    public CityResponseDTO getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도시가 존재하지 않습니다."));
        return mapToCityResponseDTO(city);
    }

    public CityResponseDTO getCityByName(String name) {
        // Note: findByNameContainingIgnoreCase might return multiple results.
        // This implementation returns the first one found or throws an exception.
        // Consider returning List<CityResponseDTO> if multiple matches are expected.
        City city = cityRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 도시가 존재하지 않습니다."));
        return mapToCityResponseDTO(city);
    }

    // Create and Update methods still accept City entity for simplicity here.
    // Consider using CityRequestDTO for create/update operations for better API contract.
    public void createCity(City city) {
        cityRepository.save(city);
    }

    public void updateCity(Long CityId, City city) {
        City existingCity = cityRepository.findById(CityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도시가 존재하지 않습니다."));

        cityRepository.save(city);
    }

    public void deleteCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 도시입니다."));
        cityRepository.deleteById(id);
    }

    // Helper method to map City entity to CityResponseDTO
    private CityResponseDTO mapToCityResponseDTO(City city) {
        if (city == null) {
            return null;
        }
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .description(city.getDescription())
                .countryId(city.getCountry() != null ? city.getCountry().getId() : null)
                .countryName(city.getCountry() != null ? city.getCountry().getName() : null)
                .build();
    }
}
