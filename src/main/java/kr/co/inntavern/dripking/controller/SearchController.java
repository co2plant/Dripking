package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.Item;
import kr.co.inntavern.dripking.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/api/search/{name}")
    public List<Item> searchByName(@PathVariable String name){
        return searchService.searchByName(name);
    }
}
