package com.k8s.demo.search.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {



    @HystrixCommand(fallbackMethod = "getDefaultTitles")
    public List<Title> search(){
        final HttpClient client = HttpClients.createDefault();
        final HttpGet get = new HttpGet("http://title-middle.rma:8080/title");

        try{
            final HttpResponse response = client.execute(get);
            System.out.println(response.getStatusLine());
            if(response.getStatusLine().getStatusCode() == 200){
                final StringWriter sw = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), sw);
                final List<Title> titles = new ObjectMapper().readValue(sw.toString(), new TypeReference<List<Title>>() {});
                final List<Title> filteredTitles = titles.stream().filter(t -> t.getTitleType().equals("Article")).collect(Collectors.toList());
                return filteredTitles;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        throw new RuntimeException("Unable to retrieve titles from title-middle");
    }


    public List<Title> getDefaultTitles(){
        final List<Title> titles = new ArrayList<>();

        return titles;
    }
}
