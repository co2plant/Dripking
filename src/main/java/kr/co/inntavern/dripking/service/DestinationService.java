package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    public DestinationService(DestinationRepository destinationRepository){
        this.destinationRepository = destinationRepository;
    }

    public Page<Destination> findAll(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAll(pageable);
    }

    public Page<Destination> searchByName(int page, String name){
        Pageable pageable = PageRequest.of(page, 10);
        return destinationRepository.findAllByNameContainingIgnoreCase(pageable, name);
    }

    public Destination findById(Long Id){
        return destinationRepository.findById(Id).orElse(null);
    }

}
