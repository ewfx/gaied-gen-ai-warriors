package com.mail.segregation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mail.segregation.request.Request;
import com.mail.segregation.service.ApiService;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    @Autowired
    ApiService apiService;

    @GetMapping("/test")
    public String testApi(){
        return "App loaded successfully";
    }

    @GetMapping("/readDocument")
    public List<Document> readDocument() throws IOException {
        return apiService.readText();
    }

    @PostMapping("/readDocument")
    public List<Object> readMessage(@RequestBody String message) throws IOException {
        List<Document> responseDoc = apiService.readMsg(message);
        return responseDoc.stream().map(r-> r.getMetadata().get("excerpt_keywords")).collect(Collectors.toList());
    }

    @PostMapping("/getSkills")
    public List<Object> getSkills(@RequestBody String resume) throws IOException{
        var prompt = """
                Get the list of the skills of the resume the candidate has in the tabular format.
                """;
        List<Document> responseDoc = apiService.readMsg(prompt+" " +resume);
        return responseDoc.stream().map(r-> r.getMetadata().get("excerpt_keywords")).collect(Collectors.toList());
    }

    @PostMapping("/getEmailContent")
    public List<Object> getEmailContent(@RequestBody Request request) throws IOException{
        Map<String,String> email = request.getContent();
        var prompt = """
                Analyze the following email and classify it into one or more request type categories
                [Adjustment,AU Transfer, Closing Notice, Commitment Change, Fee Payment, Money Movement-Inbound, Money Movement-Outbound]
                Provide the response by returning the request type               
                """;
        List<Document> responseDoc = apiService.readMsg(prompt+" " + email.get("email_content")+ " " + email.get("attachment_content"));
        return responseDoc.stream().map(r-> r.getMetadata().get("excerpt_keywords")).collect(Collectors.toList());
    }
}