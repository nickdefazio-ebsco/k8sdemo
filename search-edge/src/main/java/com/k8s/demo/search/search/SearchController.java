package com.k8s.demo.search.search;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.StringWriter;

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


            final StringWriter sw = new StringWriter();
            org.apache.commons.io.IOUtils.copy(response.getEntity().getContent(), sw);
            System.out.println(sw.toString());


        }catch(IOException e){
            e.printStackTrace(); //For example only
        }
    }
}
