package com.k8s.demo.search.search;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search-edge")
public class SearchController {

    @Autowired
    public SearchController(final SearchService searchService){
        this.searchService = searchService;
    }

    private final SearchService searchService;

    @RequestMapping(method = RequestMethod.GET, path = "/search")
    public ResponseEntity<List<Title>> search() {

        final List<Title> titles = this.searchService.search();

        return new ResponseEntity<>(titles, HttpStatus.OK);
    }
}
