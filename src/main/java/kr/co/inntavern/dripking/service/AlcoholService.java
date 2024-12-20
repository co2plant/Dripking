package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Item;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlcoholService {
    @Autowired
    private AlcoholRepository alcoholRepository;

    public List<Alcohol> findAll(){
        return alcoholRepository.findAll();
    }

    public Alcohol createAlcohol(Alcohol alcohol){
        return alcoholRepository.save(alcohol);
    }

    public Alcohol findById(Long Id){
        return alcoholRepository.findById(Id).orElse(null);
    }

    public List<Alcohol> searchByName(String name){
        return alcoholRepository.findByName(name);
    }
}
