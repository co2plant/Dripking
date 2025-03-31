package kr.co.inntavern.dripking.service;

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

    public List<Country> getAllCountries(){
        return countryRepository.findAll();
    }

    public void createCountry(Country country){
        countryRepository.save(country);
    }

    public void updateCountry(Long countryId, Country country){
        Country existingCountry = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 국가가 존재하지 않습니다."));
        countryRepository.save(country);
    }

    public void deleteCountryById(Long id){
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 국가입니다."));
        countryRepository.deleteById(id);
    }
}
