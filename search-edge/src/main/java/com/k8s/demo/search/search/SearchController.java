package com.k8s.demo.search.search;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/search")
public class SearchController {

    @RequestMapping(method = RequestMethod.GET)
    public void search() {
        final HttpClient client = HttpClients.createDefault();
        final HttpGet get = new HttpGet("http://title-middle.rma:8080/title");

        try{
            //Parse response, introduce istio?
            final HttpResponse response = client.execute(get);
            System.out.println(response.getStatusLine());
            System.out.println(response.getEntity());
        }catch(IOException e){
            e.printStackTrace(); //For example only
        }
    }
}
