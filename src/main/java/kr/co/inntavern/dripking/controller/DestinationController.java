package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DestinationController
{
    @Autowired
    DestinationService destinationService;

    @GetMapping("/api/destinations")
    public List<Destination> getDestinations()
    {
        return destinationService.findAll();
    }

    @GetMapping("/api/destination/{destinationId}")
    public Destination getDestination(@PathVariable Long destinationId) {
        return destinationService.findById(destinationId);
    }

    @GetMapping("/api/destination/search/{name}")
    public List<Destination> getDestinationByName(@PathVariable String name){
        return destinationService.searchByName(name);
    }
}
