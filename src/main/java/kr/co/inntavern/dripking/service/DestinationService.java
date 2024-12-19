package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationService {
    @Autowired
    DestinationRepository destinationRepository;

    public List<Destination> findAll(){
        return destinationRepository.findAll();
    }

    public Destination findById(Long Id){
        return destinationRepository.findById(Id).orElse(null);
    }
}
