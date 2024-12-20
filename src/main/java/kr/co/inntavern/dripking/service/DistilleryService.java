package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistilleryService {
    @Autowired
    private DistilleryRepository distilleryRepository;

    //양조장 목록을 반환하는 메소드
    //
    public List<Distillery> findAll(){
        return distilleryRepository.findAll();
    }

    public void createDistillery(Distillery distillery){
        distilleryRepository.save(distillery);
    }

    public Distillery findById(Long Id){
        return distilleryRepository.findById(Id).orElse(null);
    }

    public List<Distillery> searchByName(String name){
        return distilleryRepository.findByName(name);
    }
}
