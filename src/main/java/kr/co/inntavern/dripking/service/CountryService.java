package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.CountryRequestDTO;
import kr.co.inntavern.dripking.dto.response.CountryResponseDTO;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository){
        this.countryRepository = countryRepository;
    }

    public List<CountryResponseDTO> getAllCountries(){
        return countryRepository.findAll().stream()
                .map(CountryResponseDTO::fromEntity)
                .toList();
    }

    public CountryResponseDTO createCountry(CountryRequestDTO requestDTO){
        validateRequest(requestDTO);
        Country country = new Country();
        applyRequest(country, requestDTO);
        return CountryResponseDTO.fromEntity(countryRepository.save(country));
    }

    public CountryResponseDTO updateCountry(Long countryId, CountryRequestDTO requestDTO){
        validateRequest(requestDTO);
        Country existingCountry = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 국가가 존재하지 않습니다."));
        applyRequest(existingCountry, requestDTO);
        return CountryResponseDTO.fromEntity(countryRepository.save(existingCountry));
    }

    public void deleteCountryById(Long id){
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 국가입니다."));
        countryRepository.deleteById(id);
    }

    private void validateRequest(CountryRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()) {
            throw new IllegalArgumentException("국가 이름이 필요합니다.");
        }
    }

    private void applyRequest(Country country, CountryRequestDTO requestDTO) {
        country.setName(requestDTO.getName());
        country.setDescription(requestDTO.getDescription());
    }
}
