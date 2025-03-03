
package com.synectiks.app.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale.Category;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.app.entity.UserDetails;
import com.synectiks.app.service.CollegeQuestionFireBaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin
public class CollegeQuestionsFireBaseController {
	   @Autowired
	    private CollegeQuestionFireBaseService service;
	 @PostMapping("/firebaseaddquestions")
	 public ResponseEntity<String> addQuestion(@RequestBody List<Map<String, Object>> questions) {
	     try {
	         String id = service.addQuestionsToFirebase(questions);
	         return ResponseEntity.ok("Question added with ID: " + id);
	     } catch (Exception e) {
	         return ResponseEntity.status(500).body("Error: " + e.getMessage());
	     }
	 }
	 @PostMapping("/firebaselisteningQuestions")
	    public String uploadQuestionWithAudio(
	           @RequestBody Map<String, Object> requestBody) {
		 String category = (String) requestBody.get("category");
	        String audioUrl = (String) requestBody.get("audiourl");
	        Object questionsObj = requestBody.get("questions");
		        return service.storeQuestionWithAudio(category, audioUrl,questionsObj);
		   
	    }
	  @PutMapping("/updateQuestion/{id}")
	    public String updateQuestion(@PathVariable String id, @RequestBody Map<String, Object> updatedFields) {
	        Firestore db = FirestoreClient.getFirestore();

	        // Reference to the document to update by its ID
	        DocumentReference docRef = db.collection("Collegequestions").document(id);

	        // Perform the update operation
	        ApiFuture<WriteResult> future = docRef.update(updatedFields);

	        try {
	            // Wait for the update to finish
	            future.get();
	            return "Question with ID " + id + " successfully updated!";
	        } catch (Exception e) {
	            // Handle errors
	            e.printStackTrace();
	            return "Error updating document: " + e.getMessage();
	        }
	    }
	 @PostMapping("/userdetails/{userId}")
	    public String uploadUserDetails(
	           @RequestBody UserDetails requestBody,@PathVariable String userId) {
		 String name = requestBody.getName();
		    String mail = requestBody.getMail();
		    String mno = requestBody.getMno();
		    String university = requestBody.getUniversity();
		    String cgpa = requestBody.getCgpa();
		    String degree = requestBody.getDegree();
		    String passoutYear = requestBody.getPassoutYear();

		    // Handling skills and projects as lists
		    List<String> skills = requestBody.getSkills();
		    List<Map<String, Object>> experience=(List<Map<String, Object>>) requestBody.getExperience();
//		    String experience = requestBody.getExperience();
		    List<String> projects = requestBody.getProjects();
		    
		    
		    return service.storeUserDetails(userId, name, mail, mno, university, cgpa, degree, passoutYear, skills, experience, projects);
		   
	    }
	 @GetMapping("/userdetails/{userId}")
	    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String userId) {
	        try {
	           Map<String, Object> questions = service.getUserDetailssByCategory(userId);
	            if (questions.isEmpty()) {
	                return ResponseEntity.status(404).body(null);  // No questions found
	            }
	            return ResponseEntity.ok(questions);  // Return questions for the category
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body(null);  // Return error in case of exception
	        }
	    }

	 @PutMapping("/updateProfile/{id}")
	    public ResponseEntity<String> updateUserDetails(
	            @PathVariable String id, @RequestBody UserDetails updatedFields) {

	        if (updatedFields == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Updated fields cannot be null.");
	        }

	        Firestore db = FirestoreClient.getFirestore();
	        DocumentReference docRef = db.collection("UserProfiles").document(id);

	        try {
	            // Check if the document exists
	            if (!docRef.get().get().exists()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body("User with ID " + id + " not found.");
	            }

	            // Convert UserDetails to Map
	            ObjectMapper objectMapper = new ObjectMapper();
	            Map<String, Object> updateMap = objectMapper.convertValue(updatedFields, Map.class);

	            // Remove null or empty values
	            Map<String, Object> filteredMap = updateMap.entrySet().stream()
	                    .filter(entry -> entry.getValue() != null)
	                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	            if (filteredMap.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("No valid fields to update.");
	            }

	            // Update Firestore document
	            ApiFuture<WriteResult> future = docRef.update(filteredMap);
	            WriteResult writeResult = future.get();

	            return ResponseEntity.ok("User with ID " + id + " successfully updated at " + writeResult.getUpdateTime());
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error updating document: " + e.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Unexpected error: " + e.getMessage());
	        }
	    }
	 @GetMapping("/firebasefetchQuestionsByCategory")
	    public ResponseEntity<List<Map<String, Object>>> getQuestionsByCategory(@RequestParam String category) {
	        try {
	            List<Map<String, Object>> questions = service.getQuestionsByCategory(category);
	            if (questions.isEmpty()) {
	                return ResponseEntity.status(404).body(null); 
	            }
	            return ResponseEntity.ok(questions);  
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body(null);  
	        }
	    }
	 
	 @GetMapping("/TestResults/{category}/{sid}")
	 public ResponseEntity<?> getTestResults(@PathVariable String category, @PathVariable String sid) {
	     Map<String, Object> response = new HashMap<>();

	     try {
	         Firestore db = FirestoreClient.getFirestore();
	         
	         DocumentReference testResultsRef = db.collection("StudentResultTable").document(sid);
	         DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();

	         
	         if (!testResultsSnapshot.exists()) {
	             return ResponseEntity.status(HttpStatus.OK).body(Map.of(category, 0));
	         }

	         
	         Map<String, Object> testResults = testResultsSnapshot.getData();
	         System.out.println("##$$%"+testResults);
	         if(category.equalsIgnoreCase("LsrwTest"))
	         {
	        	 int writingTest = ((Number) testResults.getOrDefault("WrittingTest", 0)).intValue();
	        	    int speakingTest = ((Number) testResults.getOrDefault("SpeakingTest", 0)).intValue();
	        	    int listeningTest = ((Number) testResults.getOrDefault("ListeningTest", 0)).intValue();
	        	    int readingTest = ((Number) testResults.getOrDefault("ReadingTest", 0)).intValue();

	        	    int totalResults = writingTest + speakingTest + listeningTest + readingTest;
	                return ResponseEntity.ok(Map.of("LSRW_Total", totalResults));
	        	 }
	         if (testResults == null) {
	             return ResponseEntity.status(HttpStatus.OK).body(Map.of(category, 0));
	         }
     System.out.println(testResults);
	         // Reference the questions collection
	         CollectionReference questionsCollection = db.collection("Collegequestions");

	         // Query all documents in the questions collection
	         ApiFuture<QuerySnapshot> future = questionsCollection.get();
	         QuerySnapshot querySnapshot = future.get();

	         // Map to count the number of questions in each category
	         Map<String, Integer> categoryCounts = new HashMap<>();

	         // Loop through the documents and count categories
	         for (DocumentSnapshot document : querySnapshot.getDocuments()) {
	             String questionCategory = document.getString("category");

	             
	                 // Combine Listening, Speaking, Reading, and Writing into LSRW
	                 if (questionCategory.equalsIgnoreCase("Listening") ||
	                     questionCategory.equalsIgnoreCase("Speaking") ||
	                     questionCategory.equalsIgnoreCase("Reading") ||
	                     questionCategory.equalsIgnoreCase("Writing")) {
	                     questionCategory = "LSRW";
	                 }

	                
	                 categoryCounts.put(questionCategory, categoryCounts.getOrDefault(questionCategory, 0) + 1);
	         
	         }


	         response.put(category, testResults.getOrDefault(category, 0)); 
	         response.put("Total" , categoryCounts); // Total questions for the category

	         return ResponseEntity.ok(response);
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(Map.of("error", e.getMessage()));
	     }
	 }


	 @GetMapping("/TotalResults/{sid}")
	    public ResponseEntity<?> gettotalTestResults( @PathVariable String sid) {
	        try {
	            Firestore db = FirestoreClient.getFirestore();

	            // Reference the document by sid
	            DocumentReference testResultsRef = db.collection("StudentResultTable").document(sid);
	            DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();

	            if (!testResultsSnapshot.exists()) {
	            	return ResponseEntity.status(HttpStatus.OK)
	                        .body(Map.of("totalresults", 0));
	            }

	            // Extract relevant fields
	            Map<String, Object> testResults = testResultsSnapshot.getData();
	            if (testResults == null) {
	            	return ResponseEntity.status(HttpStatus.OK)
	                        .body(Map.of("totalresults", 0));
	            }

	            // Get PsychometricTest and UnimplementedPsychometric fields
	            Map<String, Object> response = new HashMap<>();
	            System.out.println("##########");
	            System.out.println(testResults.getOrDefault("PsychometricTest", 0));
//	            response.put(category, testResults.getOrDefault(category, 0));
	           Long BehavioralTest= (Long) testResults.getOrDefault("BehaviouralTest", 0);
	           Long ListeningTest=(Long) testResults.getOrDefault("ListeningTest", 0);
	           Long readingTest=(Long) testResults.getOrDefault("ReadingTest", 0);
	           Long speakingTest=(Long) testResults.getOrDefault("SpeakingTest", 0);
	           Long writtingTest=(Long) testResults.getOrDefault("WrittingTest", 0);
	           Long PsychometricTest=(Long) testResults.getOrDefault("PsychometricTest", 0);
	           System.out.println(""+speakingTest);
	           // response.put("UnimplementedPsychometric", testResults.getOrDefault("UnimplementedPsychometric", 0));
               long totalresult=BehavioralTest+ListeningTest+readingTest+speakingTest+writtingTest+PsychometricTest;
	            return ResponseEntity.ok(totalresult);
	        } catch (Exception e) {
	        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	        			 .body(Map.of("error", e.getMessage()));
	        }
	    } 


	 @GetMapping("/process/{sid}")
	 public Map<String, Double> getprocessResults(@PathVariable String sid) throws InterruptedException, ExecutionException {

	     Firestore db = FirestoreClient.getFirestore();
	     Map<String, Integer> categoryCounts = new HashMap<>();

	     try {
	         // Reference to the collection where questions are stored
	         CollectionReference questionsCollection = db.collection("Collegequestions"); 

	         // Query for all documents
	         ApiFuture<QuerySnapshot> future = questionsCollection.get();
	         QuerySnapshot querySnapshot = future.get();

	         // Loop through the documents and count categories
	         for (DocumentSnapshot document : querySnapshot.getDocuments()) {
	             String category = document.getString("category");

	             // Combine Listening, Speaking, Reading, and Writing into LSRW
	             if (category != null) {
	                 if (category.equalsIgnoreCase("Listening") || category.equalsIgnoreCase("Speaking")
	                         || category.equalsIgnoreCase("Reading") || category.equalsIgnoreCase("Writing")) {
	                     category = "LSRW";  // Combine into LSRW
	                 }

	                 // Increment the count for the specific category
	                 categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
	             }
	         }

	     } catch (InterruptedException | ExecutionException e) {
	         // Handle exceptions
	         System.err.println("Error fetching count: " + e.getMessage());
	     }

	     // Reference the document by sid
	     DocumentReference testResultsRef = db.collection("StudentResultTable").document(sid);
	     DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();

	     // If the student doesn't exist, return progress map with all zeros
	     if (!testResultsSnapshot.exists()) {
	         Map<String, Double> zeroProgressMap = new HashMap<>();
	         zeroProgressMap.put("Behavioural", 0.0);
	         zeroProgressMap.put("Psychometric", 0.0);
	         zeroProgressMap.put("LSRW", 0.0);
	         return zeroProgressMap;
	     }

	     // Extract relevant fields
	     Map<String, Object> testResults = testResultsSnapshot.getData();
	     System.out.println("testResults"+testResults);
	     if (testResults == null) {
	         // If no test results, return zero progress
	         Map<String, Double> zeroProgressMap = new HashMap<>();
	         zeroProgressMap.put("Behavioural", 0.0);
	         zeroProgressMap.put("Psychometric", 0.0);
	         zeroProgressMap.put("LSRW", 0.0);
	         return zeroProgressMap;
	     }

	     // Get PsychometricTest, BehavioralTest, and LSRWTest fields
	     Long BehavioralTest = ((Number) testResults.getOrDefault("BehaviouralTest", 0)).longValue();
	    
	     Long ListeningTest=(Long) testResults.getOrDefault("ListeningTest", 0);
         Long readingTest=(Long) testResults.getOrDefault("ReadingTest", 0);
         Long speakingTest=(Long) testResults.getOrDefault("SpeakingTest", 0);
         Long writtingTest=(Long) testResults.getOrDefault("WrittingTest", 0);
	     Long PsychometricTest = ((Number) testResults.getOrDefault("PsychometricTest", 0)).longValue();
       Long LSRWTest=readingTest+ListeningTest+speakingTest+writtingTest;
       System.out.println("LSRWTest"+LSRWTest);
	     // Retrieve question counts for each category
	     int BehavioralTestquestionscount = categoryCounts.getOrDefault("Behavioural", 0);
	     int psychometricTestquestionscount = categoryCounts.getOrDefault("psychometric", 0);
	     int Speakingquestionscount = categoryCounts.getOrDefault("Speaking", 0);
	     System.out.println("Speakingquestionscount"+Speakingquestionscount);
	     int LSRWquestionscount = categoryCounts.getOrDefault("LSRW", 0);
	    		
	     System.out.println(LSRWquestionscount);

	     // Calculate progress for each category
	     double behaviouralprogress = (BehavioralTest / (double) (BehavioralTestquestionscount * 5)) * 100;
	     double psychometricprogress = (PsychometricTest / (double) (psychometricTestquestionscount * 5)) * 100;
	     double LSRWTestprogresst = (LSRWTest / (double) (LSRWquestionscount * 5)) * 100;

	     // Prepare the response with progress data
	     Map<String, Double> progressMap = new HashMap<>();
	     progressMap.put("Behavioural", behaviouralprogress);
	     progressMap.put("Psychometric", psychometricprogress);
	     progressMap.put("LSRW", LSRWTestprogresst);

	     return progressMap;
	 }
	 
	 @GetMapping("/avgscore/{sid}")
	 public double getAvgScoreResults( @PathVariable String sid) throws InterruptedException, ExecutionException {
	   
	        	Firestore db = FirestoreClient.getFirestore();
	        	   Map<String, Integer> categoryCounts = new HashMap<>();
	        	 try {
	                 // Reference to the collection where questions are stored
	                 CollectionReference questionsCollection = db.collection("Collegequestions");  // Replace with your collection name

	                 // Query for all documents
	                 ApiFuture<QuerySnapshot> future = questionsCollection.get();
	                 QuerySnapshot querySnapshot = future.get();

	             
	                 for (DocumentSnapshot document : querySnapshot.getDocuments()) {
	                     String category = document.getString("category");

	                     // Combine Listening, Speaking, Reading, and Writing into LSRW
	                     if (category != null) {
	                         if (category.equalsIgnoreCase("Listening") || category.equalsIgnoreCase("Speaking")
	                                 || category.equalsIgnoreCase("Reading") || category.equalsIgnoreCase("Writing")) {
	                             category = "LSRW";  // Combine into LSRW
	                         }

	                         // Increment the count for the specific category
	                         categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
	                     }
	                 }

	                 // Output the counts for each category
	                 categoryCounts.forEach((category, count) -> {
	                     System.out.println(category + ": " + count);
	                 });

	             } catch (InterruptedException | ExecutionException e) {
	                 // Handle exceptions
	                 System.err.println("Error fetching count: " + e.getMessage());
	             }
	        	  // Reference the document by sid
		            DocumentReference testResultsRef = db.collection("StudentResultTable").document(sid);
		            DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();

		            
		            // Extract relevant fields
		            Map<String, Object> testResults = testResultsSnapshot.getData();
		            

		            // Get PsychometricTest and UnimplementedPsychometric fields
		            Map<String, Object> response = new HashMap<>();
//		            response.put(category, testResults.getOrDefault(category, 0));
		           Long BehavioralTest= (Long) testResults.getOrDefault("BehavioralTest", 0);
		           Long LSRWTest=(Long) testResults.getOrDefault("LSRWTest", 0);
		           Long PsychometricTest=(Long) testResults.getOrDefault("PsychometricTest", 0);
		           System.out.println(BehavioralTest+""+LSRWTest+""+PsychometricTest);
		           
		           int BehavioralTestquestionscount=categoryCounts.get("Behavioural");
		           int psychometricTestquestionscount=categoryCounts.get("psychometric");
		           int LSRWTestquestionscount=categoryCounts.get("LSRW");
		           System.out.println(BehavioralTestquestionscount);
		           double behaviouralprogress=(BehavioralTest/BehavioralTestquestionscount)*100;
		           double psychometricprogress=(PsychometricTest/psychometricTestquestionscount)*100;
		           double LSRWTestprogresst=(LSRWTest/LSRWTestquestionscount)*100;
		           System.out.println(behaviouralprogress+""+psychometricprogress+""+LSRWTestprogresst);
		           double averageScore=(BehavioralTest+LSRWTest+LSRWTest)/3*100;

				return averageScore;
	            
 }
 
}

