package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DistilleryService {
    private final DistilleryRepository distilleryRepository;

    public DistilleryService(DistilleryRepository distilleryRepository){
        this.distilleryRepository = distilleryRepository;
    }
    //양조장 목록을 반환하는 메소드
    //
    public Page<Distillery> findAll(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAll(pageable);
    }

    public Page<Distillery> searchByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAllByNameContainingIgnoreCase(pageable, name);
    }

    public void createDistillery(Distillery distillery){
        distilleryRepository.save(distillery);
    }

    public Distillery findById(Long Id){
        return distilleryRepository.findById(Id).orElse(null);
    }

}
