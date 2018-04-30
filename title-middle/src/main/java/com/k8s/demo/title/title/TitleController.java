package com.k8s.demo.title.title;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/title")
public class TitleController {

    private final Map<String, Title> titles;

    public TitleController() {
        this.titles = new HashMap<>();
        this.titles.put("1", new Title("1", "Title 1"));
        this.titles.put("2", new Title("2", "Title 2"));
        this.titles.put("3", new Title("3", "Title 3"));
        this.titles.put("4", new Title("4", "Title 4"));
        this.titles.put("5", new Title("5", "Title 5"));
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{titleId}")
    public ResponseEntity<Title> getTitleById(@PathVariable final String titleId){
        final Title title = this.titles.get(titleId);
        if(title != null){
            return new ResponseEntity<Title>(title, HttpStatus.OK);
        }

        return new ResponseEntity<Title>(HttpStatus.NOT_FOUND);
    }


}
