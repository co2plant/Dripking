package kr.co.inntavern.dripking.service;

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

    public Page<City> getAllCitiesByCountryId(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return cityRepository.findAll(pageable);
    }

    public City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도시가 존재하지 않습니다."));
    }

    public City getCityByName(String name) {
        return cityRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 도시가 존재하지 않습니다."));
    }

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
}
