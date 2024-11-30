package kr.co.inntavern.dripking.service;

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
    public List<Distillery> getDistilleries(){
        return distilleryRepository.findAll();
    }

    public Distillery createDistillery(Distillery distillery){
        return distilleryRepository.save(distillery);
    }

    public Distillery getDistillery(Long Id){
        return distilleryRepository.findById(Id).orElse(null);
    }

}
