package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DestinationController
{
    @Autowired
    DestinationRepository destinationRepository;

    @GetMapping("/api/destinations")
    public List<Destination> getDestinations()
    {
        return destinationRepository.findAll();
    }

    @GetMapping("/api/destination/{destinationId}")
    public Destination getDestination(@PathVariable Long destinationId) {
        return destinationRepository.findById((Long)destinationId).orElse(null);
    }
}
