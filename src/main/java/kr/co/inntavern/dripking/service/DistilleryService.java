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
    // ---------------------------------------------------------------------
    // Select Methods: 모든 엔티티를 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<Distillery> getAllDistilleries(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAll(pageable);
    }

    // ---------------------------------------------------------------------
    // Select Methods: 특정 Id를 가진 엔티티를 반환하는 메서드
    // ---------------------------------------------------------------------
    public Distillery getDistilleryById(Long Id){
        return distilleryRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    // ---------------------------------------------------------------------
    // Select Methods: 이름이 포함된(대소문자 무시) 컬럼을 검색하여 페이지 형태로 반환하는 메서드
    // ---------------------------------------------------------------------
    public Page<Distillery> getAllDistilleriesByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return distilleryRepository.findAllByNameContainingIgnoreCase(pageable, name);
    }

    // ---------------------------------------------------------------------
    // Create Methods: 엔티티를 생성하는 메서드
    // ---------------------------------------------------------------------
    public Distillery createDistillery(Distillery Distillery){
        return distilleryRepository.save(Distillery);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
    public Distillery updateDistillery(Long id, Distillery Distillery){
        return distilleryRepository.save(Distillery);
    }

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteDistilleryById(Long id){
        distilleryRepository.deleteById(id);
    }

}
