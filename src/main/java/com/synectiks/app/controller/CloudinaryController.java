package com.synectiks.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.synectiks.app.service.CloudinaryService;
import com.synectiks.app.service.CollegeQuestionFireBaseService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
public class CloudinaryController {
	   @Autowired
	    private CollegeQuestionFireBaseService service;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }
    @PostMapping("/uploadlmsquestions")
    public ResponseEntity<Map<String, String>> uploadQuestion(
            @RequestParam(value = "questionfile", required = false) MultipartFile questionfile,  
            @RequestParam(value = "question", required = false) String question,  
            @RequestParam(value = "explanationfile", required = false) MultipartFile explanationfile,  
            @RequestParam(value = "explanation", required = false) String explanation,  
            @RequestParam(value = "option1file", required = false) MultipartFile option1file,  
            @RequestParam(value = "option1", required = false) String option1,  
            @RequestParam(value = "option2file", required = false) MultipartFile option2file,  
            @RequestParam(value = "option2", required = false) String option2,  
            @RequestParam(value = "option3file", required = false) MultipartFile option3file,  
            @RequestParam(value = "option3", required = false) String option3,
            @RequestParam(value = "option4file", required = false) MultipartFile option4file,  
            @RequestParam(value = "option4", required = false) String option4 ,
            @RequestParam(value = "answerfile", required = false) MultipartFile answerfile,  
            @RequestParam(value = "answer", required = false) String answer ,
            @RequestParam(value = "course", required = false) String course 
    ) {
        Map<String, String> response = new HashMap<>();
       

        try {
         
            if (questionfile != null && !questionfile.isEmpty()) {
            	question = cloudinaryService.uploadImage(questionfile);
            
            } if (explanationfile != null && !explanationfile.isEmpty()) {
            	explanation = cloudinaryService.uploadImage(explanationfile);
            
            } if (option1file != null && !option1file.isEmpty()) {
            	option1 = cloudinaryService.uploadImage(option1file);
            
            } if (option2file != null && !option2file.isEmpty()) {
            	option2 = cloudinaryService.uploadImage(option2file);
            
            }
        if (option3file != null && !option3file.isEmpty()) {
        	option3 = cloudinaryService.uploadImage(option3file);
        
        }
    if (option4file != null && !option4file.isEmpty()) {
    	option4 = cloudinaryService.uploadImage(option4file);
    
    }
    if (answerfile != null && !answerfile.isEmpty()) {
    	answer = cloudinaryService.uploadImage(answerfile);
    
    }
    String id = UUID.randomUUID().toString();
    response.put("qid", id);
    response.put("question", question);
    response.put("explanation", explanation);
    response.put("option1", option1);
    response.put("option2", option2);
    response.put("option3", option3);
    response.put("option4", option4);
    response.put("course", course);
    response.put("answer", answer);
    service.addQuestionsToFirebaseforlms(response);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

