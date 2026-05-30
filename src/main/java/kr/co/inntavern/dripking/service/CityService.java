package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.CityRequestDTO;
import kr.co.inntavern.dripking.dto.response.CityResponseDTO;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.repository.CityRepository;
import kr.co.inntavern.dripking.repository.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CityService(CityRepository cityRepository,
                       CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public Page<CityResponseDTO> getAllCities(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<City> cityPage = cityRepository.findAll(pageable);
        return cityPage.map(this::mapToCityResponseDTO);
    }

    public Page<CityResponseDTO> getCitiesByCountryId(Long countryId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<City> cityPage = cityRepository.findAllByCountryId(pageable, countryId);
        return cityPage.map(this::mapToCityResponseDTO);
    }

    public CityResponseDTO getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도시가 존재하지 않습니다."));
        return mapToCityResponseDTO(city);
    }

    public CityResponseDTO getCityByName(String name) {
        City city = cityRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 도시가 존재하지 않습니다."));
        return mapToCityResponseDTO(city);
    }

    public CityResponseDTO createCity(CityRequestDTO requestDTO) {
        validateRequest(requestDTO);
        City city = new City();
        applyRequest(city, requestDTO);
        return mapToCityResponseDTO(cityRepository.save(city));
    }

    public CityResponseDTO updateCity(Long cityId, CityRequestDTO requestDTO) {
        validateRequest(requestDTO);
        City existingCity = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도시가 존재하지 않습니다."));

        applyRequest(existingCity, requestDTO);
        return mapToCityResponseDTO(cityRepository.save(existingCity));
    }

    public void deleteCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 도시입니다."));
        cityRepository.deleteById(id);
    }

    private CityResponseDTO mapToCityResponseDTO(City city) {
        if (city == null) {
            return null;
        }
        return CityResponseDTO.fromEntity(city);
    }

    private void validateRequest(CityRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()) {
            throw new IllegalArgumentException("도시 이름이 필요합니다.");
        }
        if (requestDTO.getCountryId() == null) {
            throw new IllegalArgumentException("countryId가 필요합니다.");
        }
    }

    private void applyRequest(City city, CityRequestDTO requestDTO) {
        Country country = countryRepository.findById(requestDTO.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 국가가 존재하지 않습니다."));
        city.setName(requestDTO.getName());
        city.setDescription(requestDTO.getDescription());
        city.setCountry(country);
    }
}
