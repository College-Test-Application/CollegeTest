package com.synectiks.app.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;
import java.util.Map;
import org.apache.http.impl.client.HttpClients;
import java.util.concurrent.ExecutionException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.Value;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
//import com.synectiks.app.entity.AnswerDto;
import com.synectiks.app.service.CollegeQuestionsvalidatingFireBaseService;
import com.synectiks.app.service.SimilarityUtil;
import com.synectiks.app.service.WordEmbeddingservice;

@RestController
@CrossOrigin
public class CollegeQuestionsvalidatingFireBaseController {
	
//	@Value("${languagetool.api.url}")
//    private String languageToolApiUrl;
	private String languageToolApiUrl= "https://api.languagetool.org/v2/check";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private SimilarityUtil similarityUtil;
    @Autowired
    private WordEmbeddingservice wordEmbeddingservice;
	@Autowired
	private CollegeQuestionsvalidatingFireBaseService collegeQuestionsvalidatingFireBaseService;

	@PostMapping("/submitfirebase/{category}/{UserUID}")
	public ResponseEntity<Long> validateQuestion(
	    @RequestBody List<Map<String, Object>> details,
	    @PathVariable String category,
	    @PathVariable String UserUID
	) throws InterruptedException, ExecutionException {
	    Long result = collegeQuestionsvalidatingFireBaseService.validateAnswer(details, category, UserUID);
	    return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	@PostMapping("/submitfirebase1/listening/{UserUID}")
	public ResponseEntity<Integer> validateQuestionlistening(
	    @RequestBody List<Map<String, Object>> details,
	  
	    @PathVariable String UserUID
	) throws InterruptedException, ExecutionException {
	    int result = collegeQuestionsvalidatingFireBaseService.validateAnswerlistening(details, "listening", UserUID);
	    return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	@PostMapping("/submitfirebase1/reading/{UserUID}")
	public ResponseEntity<Integer> validateQuestionreading(
	    @RequestBody List<Map<String, Object>> details,  
	    @PathVariable String UserUID
	) throws InterruptedException, ExecutionException {
	    int result = collegeQuestionsvalidatingFireBaseService.validateAnswerlistening(details, "reading", UserUID);
	    return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	

	@PostMapping("/evaluate-writing/{UserUID}")
	public ResponseEntity<Map<String, Object>> evaluateWriting(@RequestBody Map<String, Object> request, @PathVariable String UserUID) {
	    try {
	        // Parse and validate request data
	        String response = (String) request.get("response");
	        if (response == null || response.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body(Map.of("error", "The 'response' field is required and cannot be null or empty."));
	        }

	        // Time and other request parameters
	        String topic = (String) request.get("topic");
//	        LocalDateTime startTime = LocalDateTime.parse((String) request.get("start_time"));
//	        LocalDateTime endTime = LocalDateTime.parse((String) request.get("end_time"));
//	        int timeLimitMinutes = (int) request.getOrDefault("time_limit_minutes", 30);
//	        if (timeLimitMinutes <= 0) {
//	            return ResponseEntity.badRequest().body(Map.of("error", "'time_limit_minutes' should be a positive number."));
//	        }

	        Map<String, Integer> scoringWeights = (Map<String, Integer>) request.getOrDefault("scoring_weights", new HashMap<>());
//	        int minWordCount = (int) request.getOrDefault("min_word_count", 150);
//	        int maxWordCount = (int) request.getOrDefault("max_word_count", 300);
	        int minWordCount = (int) request.get("min_word_count");
	        int maxWordCount = (int) request.get("max_word_count");

	        double totalScore = 0;
	        Map<String, Object> feedback = new HashMap<>();

//	        // Time evaluation
//	        long elapsedMinutes = Duration.between(startTime, endTime).toMinutes();
//	        double timeScore = Math.max(0, 1 - Math.abs(elapsedMinutes - timeLimitMinutes) / (double) timeLimitMinutes) * scoringWeights.getOrDefault("time_limit", 20);
//	        totalScore += timeScore;
//	        feedback.put("Time score", timeScore);

	        // Word count evaluation
	        int wordCount = response.split("\\s+").length;
	        double wordCountScore = wordCount >= minWordCount && wordCount <= maxWordCount ? 100 : Math.max(0, 100 - Math.abs(wordCount - minWordCount));
	        System.out.println("@#@#!!!"+wordCountScore);
//	        totalScore += wordCountScore * (scoringWeights.getOrDefault("word_count", 20) / 100.0);
	        feedback.put("Word count score", wordCountScore);

	        // Topic relevance evaluation (Placeholder for NLP model integration)
	        
	        double similarity=wordEmbeddingservice.getSimilarity(topic, response);
	        System.out.println("#$$"+similarity);
//	        double relevanceScore = 80.0; // Simulate relevance score
//	        totalScore += relevanceScore * (scoringWeights.getOrDefault("relevance", 40) / 100.0);
	        feedback.put("Relevance score", similarity);

	        // Grammar check using LanguageTool API
	        Map<String, Object> grammarResult = calculateGrammarScore(response);
	        double grammarScore = (double) grammarResult.get("score");
//	        totalScore += grammarScore * (scoringWeights.getOrDefault("grammar", 20) / 100.0);
	        feedback.put("Grammar score", grammarScore);

	        Firestore db = FirestoreClient.getFirestore();
	        DocumentReference testResultsRef = db.collection("StudentResultTable").document(UserUID); 
//	        System.out.println("!@#"+testResultsRef);
	        DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();
//	        System.out.println("!@#"+testResultsSnapshot);
	        Map<String, Object> testResults = initializeTestResults(testResultsRef,"Writting");
	      
	        if (similarity >= 0.8 && grammarScore>=75 && wordCount >=minWordCount&& wordCount<=maxWordCount) {
	        
	            incrementTestResult(testResults, "WrittingTest", 5); // Add 4 if total score >= 80
	        } 
	        else if(similarity >= 0.6 && similarity < 0.8 && grammarScore >= 70 && wordCount >=minWordCount&& wordCount<=maxWordCount) {
	        	 incrementTestResult(testResults, "WrittingTest", 4);
	        }
	        else if(similarity >= 0.6 && similarity < 0.8 && grammarScore >= 70 && wordCount >=minWordCount&& wordCount<=maxWordCount)
	        {
	        	 incrementTestResult(testResults, "WrittingTest", 3);
	        }
	        else if(similarity >= 0.4 && similarity < 0.6 && grammarScore >= 65 && wordCount >=minWordCount&& wordCount<=maxWordCount)
	        {
	        	 incrementTestResult(testResults, "WrittingTest", 2);
	        }
	        else {
	            incrementTestResult(testResults, "WrittingTest", 0); // Add 3 if total score < 80
	        }
	       
	        testResultsRef.set(testResults);
	        System.out.println("!@#"+testResults); 
	        Map<String, Object> result = new HashMap<>();
	        result.put("similarity", similarity);
	        result.put("grammarScore", grammarScore);
	        result.put("wordCount",wordCount);
	        
	        return ResponseEntity.ok(result);
	        

	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
	    }
	}

	private Map<String, Object> calculateGrammarScore(String text) throws Exception {
	    try (CloseableHttpClient client = HttpClients.createDefault()) {
	        HttpPost post = new HttpPost(languageToolApiUrl);

	        // Use application/x-www-form-urlencoded format
	        List<NameValuePair> params = new ArrayList<>();
	        params.add(new BasicNameValuePair("text", text));
	        params.add(new BasicNameValuePair("language", "en-US"));
	        post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
	        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
   System.out.println(params);
	        try (CloseableHttpResponse response = client.execute(post)) {
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != 200) {
	                String responseBody = EntityUtils.toString(response.getEntity());
	                throw new RuntimeException("Failed to get a valid response from LanguageTool API. Status: " + statusCode + ", Response: " + responseBody);
	            }

	            // Parse the JSON response
	            String jsonResponse = EntityUtils.toString(response.getEntity());
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

	            // Extract matches and calculate the grammar score
	            int errorCount = jsonNode.get("matches").size();
	            double score = Math.max(0, 100 - errorCount * 2);
	            String feedbackMessage = score >= 90 ? "Excellent grammar" : score >= 70 ? "Good grammar with minor issues" : "Needs improvement";

	            // Prepare and return the result
	            Map<String, Object> result = new HashMap<>();
	            result.put("score", score);
	            result.put("errors", jsonNode.get("matches"));
	            result.put("message", feedbackMessage);
	            return result;
	        }
	    }
	}
	 private Map<String, Object> initializeTestResults(DocumentReference testResultsRef, String category) throws InterruptedException, ExecutionException {
		    DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();
		    Map<String, Object> testResults = testResultsSnapshot.exists() ? testResultsSnapshot.getData() : new HashMap<>();

		    if (testResults == null) {
		        testResults = new HashMap<>();
		    }

		    // Initialize default fields if they don't exist
		    testResults.putIfAbsent("sid", testResultsRef.getId());
		    testResults.putIfAbsent("testDate", FieldValue.serverTimestamp());
		    testResults.putIfAbsent("PsychometricTest", 0);
		    testResults.putIfAbsent("BehaviouralTest", 0);
		    testResults.putIfAbsent("ListeningTest", 0);
		    testResults.putIfAbsent("SpeakingTest", 0);
		    testResults.putIfAbsent("ReadingTest", 0);
		    testResults.putIfAbsent("WrittingTest", 0);
		    // Normalize category value to lowercase for case-insensitive comparison
		    category = category != null ? category.toLowerCase() : "";

		    // Reset the specific category field to 0
		    switch (category) {
		        case "psychometric":
		            testResults.put("PsychometricTest", 0);
		            break;
		        case "behavioural":
		            testResults.put("BehaviouralTest", 0);
		            break;
		        case "listening":
		            testResults.put("ListeningTest", 0);
		            break;
		        case "speaking":
		            testResults.put("SpeakingTest", 0);
		            break;
		        case "reading":
		            testResults.put("ReadingTest", 0);
		            break;
		        case "writting":
		            testResults.put("WrittingTest", 0);
		            break;
		        default:
		            // Log the invalid category and throw exception
		            System.err.println("Invalid category passed: " + category);
		            throw new IllegalArgumentException("Invalid category: " + category);
		    }

		    return testResults;
		}

	   private void incrementTestResult(Map<String, Object> testResults, String key, int incrementValue) {
		    Object currentValueObj = testResults.getOrDefault(key, 0);
		    int currentValue = 0;

		    if (currentValueObj instanceof Long) {
		        currentValue = ((Long) currentValueObj).intValue(); // Convert Long to int
		    } else if (currentValueObj instanceof Integer) {
		        currentValue = (Integer) currentValueObj; // Use as is if already Integer
		    }

		    System.out.println(currentValue);
		    testResults.put(key, currentValue + incrementValue);
		    System.out.println(currentValue);
		 
		}

	   @PostMapping("/submit/checkText/{id}")
	    public String checkText(@PathVariable String id,@RequestBody Map<String, Object> request) throws InterruptedException, ExecutionException {
		   Firestore db = FirestoreClient.getFirestore();
	        DocumentReference testResultsRef = db.collection("StudentResultTable").document(id); 
//	        System.out.println("!@#"+testResultsRef);
	        DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();
		   Map<String, Object> testResults = initializeTestResults(testResultsRef,"Speaking");
		   String topic = (String) request.get("topic");
		   String text = (String) request.get("text");
		      
	        try {
	        
	        	  if (countWords(text) <= 40) {
	                  return "Please provide a more detailed response with more than 40 words.";
	              }
	        	double similarity=wordEmbeddingservice.getSimilarity(topic, text);
	        	
	        	 Map<String, Object> grammarResult = calculateGrammarScore(text);
	 	        double grammarScore = (double) grammarResult.get("score");
	 	     
	            double simy = similarityUtil.calculateCosineSimilarity(topic, text);
	            if (similarity >= 0.8 && grammarScore >= 85) {
	                incrementTestResult(testResults, "SpeakingTest", 5);
	            } else if (similarity >= 0.6 && similarity < 0.8 && grammarScore >= 75) {
	            
	                incrementTestResult(testResults, "SpeakingTest", 4);
	            } else if (similarity >= 0.4 && similarity < 0.6 && grammarScore >= 70) {
	                
	                incrementTestResult(testResults, "SpeakingTest", 3);
	            } else if (similarity >= 0.3 && similarity < 0.4 && grammarScore >= 70) {
	              
	                incrementTestResult(testResults, "SpeakingTest", 2);
	            } else {
	              
	                incrementTestResult(testResults, "SpeakingTest", 0);
	            }

	            testResultsRef.set(testResults);


	            return "results :" +testResults;
	        } catch (Exception e) {
	            return "Error calculating similarity: " + e.getMessage();
	        }
	    }
	  
	    private static int countWords(String text) {
	        String[] words = text.split("\\s+"); 
	        return words.length;
	    }
}
