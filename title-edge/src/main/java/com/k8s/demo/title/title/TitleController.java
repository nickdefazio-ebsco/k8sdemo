package com.k8s.demo.title.title;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/title-edge")
public class TitleController {

    @RequestMapping(method = RequestMethod.GET, path = "/title")
    public ResponseEntity<List<Title>> search(@RequestParam("title") final String title){
        final HttpClient client = HttpClients.createDefault();
        final HttpGet get = new HttpGet("http://title-middle:8080/title");

        try{
            final HttpResponse response = client.execute(get);
            System.out.println(response.getStatusLine());
            if(response.getStatusLine().getStatusCode() == 200){
                final StringWriter sw = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), sw);
                
                final List<Title> titles = new ObjectMapper().readValue(sw.toString(), new TypeReference<List<Title>>() {});
                return new ResponseEntity<>(titles, HttpStatus.OK);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
