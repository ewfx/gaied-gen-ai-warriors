package com.mail.segregation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class ApiService {

    private ChatModel chatModel;

    private RestTemplate restTemplate;

    @Value("classpath:values-extraction.txt")
    Resource resource;

    public ApiService( ChatModel chatModel, RestTemplate restTemplate) {
        this.chatModel = chatModel;
        this.restTemplate = restTemplate;
    }

    public List<Document> readText() throws IOException {
        var textReader = new TextReader(resource);
        textReader.getCustomMetadata()
                .putAll(Map.of("length", resource.contentLength(), "last modified", LocalDateTime.ofInstant(Instant.ofEpochMilli(resource.lastModified()), ZoneId.systemDefault())));
        var documents = textReader.get();
        TokenTextSplitter splitter = new TokenTextSplitter(true);
        return keywordMetadataEnricher(splitter.apply(documents));
    }

    List<Document> keywordMetadataEnricher(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }

    public List<Document> readMsg(String message) {
        Document doc= new Document(message);
        TokenTextSplitter splitter = new TokenTextSplitter(true);
        return keywordMetadataEnricher(splitter.apply(List.of(doc)));
    }

}
