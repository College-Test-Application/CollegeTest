package com.synectiks.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.app.service.CloudinaryService;
import com.synectiks.app.service.LmsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin
public class LmsController {
    @Autowired
    private CloudinaryService cloudinaryService;
	@Autowired
	private LmsService lmsService;
	 Firestore db = FirestoreClient.getFirestore();
	@GetMapping("/getlmsquestions")
	public ResponseEntity<List<Map<String, Object>>> fetchlmsQuestions(@RequestParam String course) {
		List<Map<String, Object>> lmsquestions=lmsService.getLmsQuestions(course);
		
		return ResponseEntity.ok(lmsquestions);
	}
	@PostMapping("/validatelmsquestions/{course}/{sid}")
	public int validatelms(@RequestBody List<Map<String, Object>> data,@PathVariable String course, @PathVariable String sid) {
		int marks=lmsService.validateLmsService(data,sid,course);
		return marks;
		
	}

	@DeleteMapping("/deletelmsQuestionById")
    public String deleteDocumentById(@RequestParam String qid) {
       

      
        DocumentReference docRef = db.collection("lms").document(qid);

        try {
            // Perform the delete operation
            ApiFuture<WriteResult> future = docRef.delete();


            future.get();

            // If successful, return success message
            return "Document with ID " + qid + " successfully deleted!";
        } catch (Exception e) {
            // Handle any errors that occur during the delete operation
            e.printStackTrace();
            return "Error deleting document: " + e.getMessage();
        }
    }
	
	@PutMapping("/updatelmsquestion")
	public ResponseEntity<Map<String, String>> updateQuestion(
	        @RequestParam("qid") String qid,  // Required to identify the question
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
	    CollectionReference lmsQuestions = db.collection("lms");
	    DocumentReference questionRef = lmsQuestions.document(qid);

	    try {
	        // Fetch existing data
	        ApiFuture<DocumentSnapshot> future = questionRef.get();
	        DocumentSnapshot questionDoc = future.get();

	        if (!questionDoc.exists()) {
	            response.put("error", "Question not found with qid: " + qid);
	            return ResponseEntity.status(404).body(response);
	        }

	        // Get existing values if no new input is provided
	        Map<String, Object> updatedData = new HashMap<>(questionDoc.getData());

	        // Upload files if provided, else keep existing data
	        if (questionfile != null && !questionfile.isEmpty()) {
	            updatedData.put("question", cloudinaryService.uploadImage(questionfile));
	        } else if (question != null) {
	            updatedData.put("question", question);
	        }

	        if (explanationfile != null && !explanationfile.isEmpty()) {
	            updatedData.put("explanation", cloudinaryService.uploadImage(explanationfile));
	        } else if (explanation != null) {
	            updatedData.put("explanation", explanation);
	        }

	        if (option1file != null && !option1file.isEmpty()) {
	            updatedData.put("option1", cloudinaryService.uploadImage(option1file));
	        } else if (option1 != null) {
	            updatedData.put("option1", option1);
	        }

	        if (option2file != null && !option2file.isEmpty()) {
	            updatedData.put("option2", cloudinaryService.uploadImage(option2file));
	        } else if (option2 != null) {
	            updatedData.put("option2", option2);
	        }

	        if (option3file != null && !option3file.isEmpty()) {
	            updatedData.put("option3", cloudinaryService.uploadImage(option3file));
	        } else if (option3 != null) {
	            updatedData.put("option3", option3);
	        }

	        if (option4file != null && !option4file.isEmpty()) {
	            updatedData.put("option4", cloudinaryService.uploadImage(option4file));
	        } else if (option4 != null) {
	            updatedData.put("option4", option4);
	        }

	        if (answerfile != null && !answerfile.isEmpty()) {
	            updatedData.put("answer", cloudinaryService.uploadImage(answerfile));
	        } else if (answer != null) {
	            updatedData.put("answer", answer);
	        }

	        if (course != null) {
	            updatedData.put("course", course);
	        }

	        // Update Firestore document
	        questionRef.update(updatedData).get();

	        response.put("message", "Question updated successfully");
	        response.put("qid", qid);
	        return ResponseEntity.ok(response);

	    } catch (InterruptedException | ExecutionException | IOException e) {
	        response.put("error", "Update failed: " + e.getMessage());
	        return ResponseEntity.status(500).body(response);
	    }
	}

	

}
