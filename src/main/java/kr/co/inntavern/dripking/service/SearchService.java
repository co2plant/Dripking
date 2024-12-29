package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Item;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    AlcoholRepository alcoholRepository;
    @Autowired
    DestinationRepository destinationRepository;
    @Autowired
    DistilleryRepository distilleryRepository;

    public List<Item> searchByName(String name){
        List<Item> items = null;
        //items.addAll(alcoholRepository.findAllByNameContainingIgnoreCase(name));
        //items.addAll(destinationRepository.findAllByNameContainingIgnoreCase(name));
        //items.addAll(distilleryRepository.findAllByNameContainingIgnoreCase(name));

        return items;
    }

}
