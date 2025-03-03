package com.synectiks.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;



@Service
public class WordEmbeddingservice {

    @Value("${huggingface.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;
//    private final String apiUrl = "https://api-inference.huggingface.co/models/sentence-transformers/paraphrase-MiniLM-L6-v2";
    private final String apiUrl = "https://api-inference.huggingface.co/models/sentence-transformers/all-MiniLM-L6-v2";
    public WordEmbeddingservice(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getSimilarity(String sentence1, String sentence2) {
    	 RestTemplate restTemplate = new RestTemplate();
       		;
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         headers.set("Authorization", "Bearer " + apiToken);

         String requestBody = "{ \"inputs\": { \"source_sentence\": \"" + sentence1 + "\", \"sentences\": [\"" + sentence2 + "\"] } }";

         HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

         ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
         String res=response.getBody();
         
         res = res.replace("[", "").replace("]", "");
        
         double result=Double.parseDouble(res);
         System.out.println(result);
    	
    	return result;

    }

}
