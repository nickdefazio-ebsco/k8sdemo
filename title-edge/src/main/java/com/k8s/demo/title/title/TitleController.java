package com.k8s.demo.title.title;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/title-edge")
public class TitleController {

    @RequestMapping(method = RequestMethod.GET, path = "/title")
    public void search(@RequestParam("title") final String title){
        final HttpClient client = HttpClients.createDefault();
        final HttpGet get = new HttpGet("http://title-middle:8080/title");

        try{
            final HttpResponse response = client.execute(get);
            System.out.println(response.getStatusLine());
            System.out.println(response.getEntity());
        }catch(IOException e){
            e.printStackTrace(); //For example only
        }
    }
}
