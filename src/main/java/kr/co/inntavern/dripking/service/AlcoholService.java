package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlcoholService {
    private final  AlcoholRepository alcoholRepository;

    public AlcoholService(AlcoholRepository alcoholRepository){
        this.alcoholRepository = alcoholRepository;
    }

    public Page<Alcohol> findAll(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return alcoholRepository.findAll(pageable);
    }

    public Page<Alcohol> searchByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return alcoholRepository.findAllByNameContainingIgnoreCase(pageable, name);
    }

    public Alcohol createAlcohol(Alcohol alcohol){
        return alcoholRepository.save(alcohol);
    }

    public Alcohol findById(Long Id){
        return alcoholRepository.findById(Id).orElse(null);
    }


}
