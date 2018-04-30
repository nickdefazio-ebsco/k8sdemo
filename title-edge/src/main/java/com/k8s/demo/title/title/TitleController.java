package com.k8s.demo.title.title;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("title")
public class TitleController {

    @RequestMapping(method = RequestMethod.GET)
    public void search(@RequestParam("title") final String title){
        //Call out to title-middle
    }
}
