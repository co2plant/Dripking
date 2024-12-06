package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlcoholService {
    @Autowired
    private AlcoholRepository alcoholRepository;

    public List<Alcohol> getDistilleries(){
        return alcoholRepository.findAll();
    }

    public Alcohol createDistillery(Alcohol alcohol){
        return alcoholRepository.save(alcohol);
    }

    public Alcohol getDistillery(Long Id){
        return alcoholRepository.findById(Id).orElse(null);
    }

}
