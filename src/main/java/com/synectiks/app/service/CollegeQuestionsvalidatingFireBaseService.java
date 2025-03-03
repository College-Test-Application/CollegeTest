
//package com.synectiks.app.service;
//
//import java.util.Currency;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//import org.springframework.stereotype.Service;
//
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.Firestore;
//import com.google.firebase.cloud.FirestoreClient;
//
//@Service
//public class CollegeQuestionsvalidatingFireBaseService {
//
//    public int validateAnswer(List<Map<String, Object>> details, String category, String userUID) throws InterruptedException, ExecutionException {
//        // Get Firestore instance
//        Firestore db = FirestoreClient.getFirestore();
//        DocumentReference testResultsRef = db.collection("StudentResultTable").document(userUID);
//
//        // Fetch existing test results from Firestore or initialize default values
//        Map<String, Object> testResults = initializeTestResults(testResultsRef);
//
//        // Process each answer
//        for (Map<String, Object> entry : details) {
//            String choosedAnswer = (String) entry.getOrDefault("choosedanswer", "");
//            String questionId = (String) entry.get("qid");
//System.out.println(choosedAnswer);
//            if (questionId == null || questionId.isEmpty()) {
//                throw new IllegalArgumentException("Question ID is missing in the input details.");
//            }
//
//            // Fetch correct answer and category from Firestore
//            DocumentReference questionRef = db.collection("Collegequestions").document(questionId);
//            DocumentSnapshot questionSnapshot = questionRef.get().get();
//
//            if (!questionSnapshot.exists()) {
//                throw new IllegalArgumentException("Question not found for ID: " + questionId);
//            }
//
//            String correctAnswer = "";
//            String questionCategory = "";
//            List<Map<String, Long>> correctAnswerforpsychometric;
//
//            if ("psychometric".equals(category)||"behavioural".equals(category)) {
//                correctAnswerforpsychometric = (List<Map<String, Long>>) questionSnapshot.get("answer");
//                questionCategory = questionSnapshot.getString("category");
//
//                Long value = null;
//                for (Map<String, Long> map : correctAnswerforpsychometric) {
//                    if (map.containsKey(choosedAnswer)) {
//                        value = map.get(choosedAnswer);  // Safely get the Long value
//System.out.println("valie"+value);
//                        if (value != null) {
//                            // Convert Long to Integer
//                            int intValue = value.intValue();  
//                            // You can then use intValue for further logic, like updating results
//                            System.out.println(intValue);
//                            Object currentValueObj = testResults.getOrDefault("PsychometricTest", 0);
//                            
//                            int currentValue = 0;
//                            System.out.println("***(*"+currentValueObj);
//                            if (currentValueObj instanceof Long) {
//                                currentValue = ((Long) currentValueObj).intValue();
//                                System.out.println("!@!@"+currentValue);// Convert Long to int
//                            } else if (currentValueObj instanceof Integer) {
//                                currentValue = (Integer) currentValueObj;
//                                System.out.println("!@#@!"+currentValue);// Use as is if already Integer
//                            }
//System.out.println("current value"+currentValue);
//                       if(category.equals("psychometric"))
//                       {
//                    	   testResults.put("PsychometricTest", currentValue + value);  
//                       }
//                       if(category.equals("behavioural"))
//                       {
//                    	   testResults.put("PsychometricTest", currentValue + value);  
//                       }
//                        }
//
//                        break; // Exit loop once the key is found
//                    }
//                }
//            } 
//            else if("listening".equals(category)||"reading".equals(category)) {
//            	
//            }
//            else {
//                correctAnswer = questionSnapshot.getString("answer");
//                questionCategory = questionSnapshot.getString("category");
//          
//
//            // Validate the fetched values
//            if (correctAnswer == null || questionCategory == null) {
//                throw new IllegalStateException("Missing 'answer' or 'category' for question ID: " + questionId);
//            }
//
//            updateTestResults(testResults, correctAnswer, choosedAnswer, questionCategory);
//        }
//
//        // Save the updated test results to Firestore
//        testResultsRef.set(testResults);
//
//        // Return the result for the specified category
//        return getResultByCategory(testResults, category);
//    }
//		return 0;
//    }
//
//    private Map<String, Object> initializeTestResults(DocumentReference testResultsRef) throws InterruptedException, ExecutionException {
//        DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();
//        Map<String, Object> testResults = testResultsSnapshot.exists() ? testResultsSnapshot.getData() : new HashMap<>();
//
//        if (testResults == null || testResults.isEmpty()) {
//            testResults = new HashMap<>();
//            testResults.put("sid", testResultsRef.getId());
//            testResults.put("LSRWTest", 0);
//            testResults.put("PsychometricTest", 0);
//            testResults.put("behaviouralTest", 0);
//            testResults.put("UnimplementedLSRW", 0);
//            testResults.put("UnimplementedPsychometric", 0);
//            testResults.put("Unimplementedbehavioural", 0);
//        }
//
//        return testResults;
//    }
//
//    private void updateTestResults(Map<String, Object> testResults, String correctAnswer, String choosedAnswer, String questionCategory) {
//        boolean isAnswerCorrect = correctAnswer.equals(choosedAnswer);
//        boolean isAnswerSkipped = choosedAnswer == null || choosedAnswer.isEmpty();
//
//        switch (questionCategory.toLowerCase()) {
//            case "reading":
//            case "listening":
//            case "writing":
//                if (isAnswerCorrect) {
//                    incrementTestResult(testResults, "LSRWTest");
//                } else if (isAnswerSkipped) {
//                    incrementTestResult(testResults, "UnimplementedLSRW");
//                }
//                break;
//
//            case "psychometric":
//                if (isAnswerCorrect) {
//                    incrementTestResult(testResults, "PsychometricTest");
//                } else if (isAnswerSkipped) {
//                    incrementTestResult(testResults, "UnimplementedPsychometric");
//                }
//                break;
//
//            case "behavioural":
//                if (isAnswerCorrect) {
//                    incrementTestResult(testResults, "behaviouralTest");
//                } else if (isAnswerSkipped) {
//                    incrementTestResult(testResults, "Unimplementedbehavioural");
//                }
//                break;
//
//            default:
//                throw new IllegalArgumentException("Invalid question category: " + questionCategory);
//        }
//    }
//
//    private void incrementTestResult(Map<String, Object> testResults, String key) {
//        Object currentValueObj = testResults.getOrDefault(key, 0);
//        int currentValue = 0;
//
//        if (currentValueObj instanceof Long) {
//            currentValue = ((Long) currentValueObj).intValue(); // Convert Long to int
//        } else if (currentValueObj instanceof Integer) {
//            currentValue = (Integer) currentValueObj; // Use as is if already Integer
//        }
//
//        testResults.put(key, currentValue + 1);
//    }
//
//    private int getResultByCategory(Map<String, Object> testResults, String category) {
//        switch (category.toLowerCase()) {
//            case "reading":
//            case "listening":
//            case "writing":
//                return getSafeIntValue(testResults, "LSRWTest");
//            case "psychometric":
//                return getSafeIntValue(testResults, "PsychometricTest");
//            case "behavioural":
//                return getSafeIntValue(testResults, "behaviouralTest");
//            default:
//                throw new IllegalArgumentException("Invalid category: " + category);
//        }
//    }
//
//    private int getSafeIntValue(Map<String, Object> testResults, String key) {
//        Object valueObj = testResults.getOrDefault(key, 0);
//        if (valueObj instanceof Long) {
//            return ((Long) valueObj).intValue(); // Convert Long to int
//        } else if (valueObj instanceof Integer) {
//            return (Integer) valueObj; // Use as is if already Integer
//        }
//        return 0;
//    }
//}
//



package com.synectiks.app.service;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class CollegeQuestionsvalidatingFireBaseService {

//	 public int validateAnswer(List<Map<String, Object>> details, String category, String userUID) throws InterruptedException, ExecutionException {
//	        // Get Firestore instance
//	        Firestore db = FirestoreClient.getFirestore();
//
//	        // Reference to the user's test results
//	        DocumentReference testResultsRef = db.collection("StudentResultTable").document(userUID);
//
//	        // Initialize or fetch existing test results
//	        Map<String, Object> testResults = initializeTestResults(testResultsRef,category);
//
//	        System.out.println("Details received: " + details);
//
//	        for (Map<String, Object> entry : details) {
//	            try {
//	                // Extract question ID and chosen answer
//	                String choosedAnswer = (String) entry.getOrDefault("choosedanswer", "");
//	                String questionId = (String) entry.get("qid");
//
//	                if (questionId == null || questionId.isEmpty()) {
//	                    System.err.println("Skipping entry due to missing question ID: " + entry);
//	                    continue; // Skip this entry
//	                }
//
//	       
//	                DocumentReference questionRef = db.collection("Collegequestions").document(questionId);
//	                DocumentSnapshot questionSnapshot = questionRef.get().get();
//
//	                if (!questionSnapshot.exists()) {
//	                    System.err.println("Skipping entry: Question not found for ID: " + questionId);
//	                    continue; // Skip this entry
//	                }
//	               
//	                if ("psychometric".equalsIgnoreCase(category) || "behavioural".equalsIgnoreCase(category)) {
//	                    List<Map<String, Long>> correctAnswer = (List<Map<String, Long>>) questionSnapshot.get("answer");
//
//	                    if (correctAnswer== null) {
//	                        System.err.println("Skipping entry: 'answer' field is null for question ID: " + questionId);
//	                        continue; 
//	                    }
//	                    for (Map<String, Long> map : correctAnswer) {
//	                        if (map.containsKey(choosedAnswer)) {
//	                            Long value = map.get(choosedAnswer); 
//	                            if (value != null) {
//	                                // Determine the field to update based on the category
//	                            	String testField = category.equalsIgnoreCase("psychometric") 
//	                                        ? "PsychometricTest" 
//	                                        : category.equalsIgnoreCase("behavioural") 
//	                                            ? "BehaviouralTest" 
//	                                            : "DefaultTest"; 
//
//System.out.println(testField+"@@@");
//
//	                                // Get the current value from testResults or default to 0
//                                  int currentValue = ((Number) testResults.getOrDefault(testField, 0)).intValue();
//	                                
//	                                // Update the test results map with the new value
//	                                testResults.put(testField, currentValue + value);
//
//	                                // Print the updated test results for debugging
//	                                System.out.println("Updated test results: " + testResults);
//	                            }
//	                        }
//	                    }
//
//	                }
//	            } catch (Exception e) {
//	                System.err.println("Error processing entry: " + entry + " - " + e.getMessage());
//	            }
//	        }
//
//	        // Update Firestore with the final test results
//	        testResultsRef.set(testResults);
//	        System.out.println(category);
//	        System.out.println(getResultByCategory(testResults, category));
//	        return getResultByCategory(testResults, category);
////	        return 0;
//	    }
	
	 public Long validateAnswer(List<Map<String, Object>> details, String category, String userUID) throws InterruptedException, ExecutionException {
	        // Get Firestore instance
	        Firestore db = FirestoreClient.getFirestore();

	        // Reference to the user's test results
	        DocumentReference testResultsRef = db.collection("StudentResultTable").document(userUID);

	        // Initialize or fetch existing test results
	        Map<String, Object> testResults = initializeTestResults(testResultsRef, category);
	        QuerySnapshot querySnapshot = null;
	        // Fetch questions from Firestore
	        CollectionReference questionsCollection = db.collection("Collegequestions");
	        if(category.equalsIgnoreCase("Behavioural"))
      {
	  ApiFuture<QuerySnapshot> query = questionsCollection.whereEqualTo("category", "Behavioural").get();
      querySnapshot = query.get();
}
	        if(category.equalsIgnoreCase("psychometric"))    
	        {
	        	ApiFuture<QuerySnapshot> query = questionsCollection.whereEqualTo("category", "psychometric").get();
	     querySnapshot = query.get();
	        }

	        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
System.out.println("##"+documents);
	        // Initialize totalScore to 0 to avoid null pointer issues
	        Long totalScore = 0L;

	        for (QueryDocumentSnapshot document : documents) {
	            String questionId = document.getId();
	            List<Map<String, Object>> correctAnswersList = (List<Map<String, Object>>) document.get("answer");
System.out.println("correctAnswersList"+correctAnswersList);
	            // Null check to prevent NullPointerException
	            if (correctAnswersList == null) continue;
System.out.println("questionId"+questionId);
	            // Check if the user has answered this question
	            for (Map<String, Object> userAnswer : details) {
	                if (userAnswer.get("qid").equals(questionId)) {
	                	System.out.println(userAnswer.get("qid")+"  "+questionId);
	                    String userSelectedAnswer = (String) userAnswer.get("choosedanswer");
System.out.println("userSelectedAnswer"+userSelectedAnswer);
	                    // Check if the answer is correct and get the corresponding score
	                    for (Map<String, Object> correctAnswerMap : correctAnswersList) {
	                        for (Map.Entry<String, Object> entry : correctAnswerMap.entrySet()) {
	                            String correctAnswerText = entry.getKey();  // Extract the correct answer text
	                            Object scoreValue = entry.getValue();  // Extract the associated score

	                            // Ensure the score is a Long value
	                            Long score = (scoreValue instanceof Number) ? ((Number) scoreValue).longValue() : 0L;

	                            System.out.println("Checking: " + correctAnswerText + " => " + score);

	                            if (correctAnswerText.equals(userSelectedAnswer)) {
	                                totalScore += score;
	                                System.out.println("Matched! User gets: " + score);
	                                break;
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        // Update Firestore with the final test results
	        // Store the total score in Firestore under the given category
	        if (testResults == null) {
	            testResults = new HashMap<>();
	        }
if(category.equalsIgnoreCase("psychometric"))
{
	 testResults.put("PsychometricTest", totalScore);
}
if(category.equalsIgnoreCase("Behavioural"))
{
	 testResults.put("BehaviouralTest", totalScore);
}

	       

	        // Update Firestore with the final test results
	        ApiFuture<WriteResult> future = testResultsRef.set(testResults, SetOptions.merge());
	        future.get(); // Ensure the write operation completes

	        System.out.println("Category: " + category);
	        System.out.println("Updated Firestore results: " + testResults);
	        System.out.println("Total Score: " + totalScore);

	    
	        return totalScore;
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

    private void updateTestResults(Map<String, Object> testResults, String correctAnswer, String choosedAnswer, String questionCategory) {
        boolean isAnswerCorrect = correctAnswer.equals(choosedAnswer);
        boolean isAnswerSkipped = choosedAnswer == null || choosedAnswer.isEmpty();

        switch (questionCategory.toLowerCase()) {
            case "reading":
            case "listening":
            case "writing":
                if (isAnswerCorrect) {
                    incrementTestResult(testResults, "LSRWTest");
                } else if (isAnswerSkipped) {
                    incrementTestResult(testResults, "UnimplementedLSRW");
                }
                break;

            case "psychometric":
                if (isAnswerCorrect) {
                    incrementTestResult(testResults, "PsychometricTest");
                } else if (isAnswerSkipped) {
                    incrementTestResult(testResults, "UnimplementedPsychometric");
                }
                break;

            case "behavioural":
                if (isAnswerCorrect) {
                    incrementTestResult(testResults, "behaviouralTest");
                } else if (isAnswerSkipped) {
                    incrementTestResult(testResults, "Unimplementedbehavioural");
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid question category: " + questionCategory);
        }
    }

    private void incrementTestResult(Map<String, Object> testResults, String key) {
        Object currentValueObj = testResults.getOrDefault(key, 0);
        int currentValue = 0;

        if (currentValueObj instanceof Long) {
            currentValue = ((Long) currentValueObj).intValue();
        } else if (currentValueObj instanceof Integer) {
            currentValue = (Integer) currentValueObj; 
        }

        testResults.put(key, currentValue + 1);
     testResults.put("testDate", FieldValue.serverTimestamp());
       
    }
    private void incrementTestResultforwrittingAndListening(Map<String, Object> testResults, String key,int correctanswers) {
//      Object currentValueObj =  testResults.getOrDefault(key, 0);
//      System.out.println("WWWW"+currentValueObj);
        int currentValue = 0;

//        if (currentValueObj instanceof Long) {
//            currentValue = ((Long) currentValueObj).intValue();
//        } else if (currentValueObj instanceof Integer) {
//            currentValue = (Integer) currentValueObj; 
//        }
System.out.println(currentValue + correctanswers);
        testResults.put(key, correctanswers);
     testResults.put("testDate", FieldValue.serverTimestamp());
       
    }
    private int getResultByCategory(Map<String, Object> testResults, String category) {
    	
        switch (category.toLowerCase()) {
            case "reading":
            case "listening":
            case "writing":
                return getSafeIntValue(testResults, "LSRWTest");
            case "psychometric":
                return getSafeIntValue(testResults, "PsychometricTest");
            case "behavioural":
                return getSafeIntValue(testResults, "BehaviouralTest");
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
    }

    private int getSafeIntValue(Map<String, Object> testResults, String key) {
        Object valueObj = testResults.getOrDefault(key, 0);
     
        if (valueObj instanceof Long) {
            return ((Long) valueObj).intValue(); // Convert Long to int
        } else if (valueObj instanceof Integer) {
            return (Integer) valueObj; // Use as is if already Integer
        }
        return 0;
    }

//    public int validateAnswerlistening(List<Map<String, Object>> details, String category, String userUID) throws InterruptedException, ExecutionException {
//        int currentvalue = 0;
//        Firestore db = FirestoreClient.getFirestore();
//
//        // Fetch the user's test results once (1 read)
//        DocumentReference testResultsRef = db.collection("StudentResultTable").document(userUID);
//        DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();
//
//        // Fetch all questions for the given category at once (1 read)
//        CollectionReference questionsCollection = db.collection("Collegequestions");
//        QuerySnapshot querySnapshot = null;
//
//        // Fetch questions from Firestore based on the category
//        ApiFuture<QuerySnapshot> query;
//        if (category.equalsIgnoreCase("reading")) {
//            query = questionsCollection.whereEqualTo("category", "Reading").get();
//        } else if (category.equalsIgnoreCase("Listening")) {
//            query = questionsCollection.whereEqualTo("category", "Listening").get();
//        } else {
//            throw new IllegalArgumentException("Invalid category: " + category);
//        }
//        querySnapshot = query.get();
//
//        List<QueryDocumentSnapshot> questionDocuments = querySnapshot.getDocuments();
//
//        // Initialize or fetch existing test results
//        Map<String, Object> testResults = initializeTestResults(testResultsRef, category);
//
//        // Map of all questions in memory by question ID for fast access
//        Map<String, List<Map<String, Object>>> questionsMap = new HashMap<>();
//        for (QueryDocumentSnapshot questionDoc : questionDocuments) {
//            List<Map<String, Object>> questionList = (List<Map<String, Object>>) questionDoc.get("questions");
//            questionsMap.put(questionDoc.getId(), questionList);
//            System.out.println("Loaded " + questionList.size() + " questions for question ID: " + questionDoc.getId());
//        }
//
//        // Process each answer from the input details
//        System.out.println("Processing answers...");
//        for (Map<String, Object> entry : details) {
//            List<Map<String, Object>> choosedAnswer = (List<Map<String, Object>>) entry.getOrDefault("choosedanswer", "");
//            String questionId = (String) entry.get("qid");
//
//            if (questionId == null || questionId.isEmpty()) {
//                throw new IllegalArgumentException("Question ID is missing in the input details.");
//            }
//
//            // Fetch the questions for the given question ID from the questions map (no additional Firestore calls)
//            List<Map<String, Object>> formattedQuestions = questionsMap.get(questionId);
//            if (formattedQuestions == null) {
//                System.out.println("Questions not found for ID: " + questionId);
//                continue; // Skip to the next question if no matching questions are found
//            }
//
//            // Process each answer and check against the correct answers
//            for (Map<String, Object> q2Entry : choosedAnswer) {
//                String q2Qt = (String) q2Entry.get("qt");
//                Object q2Answer = q2Entry.get("answer");
//                boolean found = false;
//
//                for (Map<String, Object> q1Entry : formattedQuestions) {
//                    String q1Qt = (String) q1Entry.get("qt");
//                    Object q1Answer = q1Entry.get("answer");
//
//                    if (q2Qt.equals(q1Qt)) {
//                        System.out.println("Matching question type found: " + q2Qt + " == " + q1Qt);
//                        found = true;
//                        if (q2Answer.equals(q1Answer)) {
//                            System.out.println("Correct answer for: " + q2Qt);
//                            if (category.equalsIgnoreCase("listening")) {
//                                currentvalue++;
//                                System.out.println("Listening current score: " + currentvalue);
//                                incrementTestResultforwrittingAndListening(testResults, "ListeningTest", currentvalue);
//                            } else if (category.equalsIgnoreCase("reading")) {
//                                currentvalue++;
//                                System.out.println("Reading current score: " + currentvalue);
//                                incrementTestResultforwrittingAndListening(testResults, "ReadingTest", currentvalue);
//                            }
//                        } else {
//                            System.out.println("Match found but answer is incorrect for: " + q2Qt);
//                        }
//                    }
//                }
//
//                if (!found) {
//                    System.out.println("Question not found in the list for: " + q2Qt);
//                }
//            }
//        }
//
//        // Perform the final write to Firestore once (1 write)
//        System.out.println("Writing final test results to Firestore.");
//        testResultsRef.set(testResults);
//        System.out.println("Final test results written: " + testResults);
//
//        return currentvalue;
//    }

	public int validateAnswerlistening(List<Map<String, Object>> details, String category, String userUID) throws InterruptedException, ExecutionException {
		   int currentvalue=0;
		 Firestore db = FirestoreClient.getFirestore();
	        DocumentReference testResultsRef = db.collection("StudentResultTable").document(userUID);

DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();


	        // Fetch existing test results from Firestore or initialize default values
	        Map<String, Object> testResults = initializeTestResults(testResultsRef,category);
	        System.out.println(testResults);
	        // Process each answer
	        for (Map<String, Object> entry : details) {
	            List<Map<String, Object>> choosedAnswer = (List<Map<String, Object>>) entry.getOrDefault("choosedanswer", "");
	            String questionId = (String) entry.get("qid");
	

	            if (questionId == null || questionId.isEmpty()) {
	                throw new IllegalArgumentException("Question ID is missing in the input details.");
	            }

	            DocumentReference questionRef1 = db.collection("Collegequestions").document(questionId);
             DocumentSnapshot questionSnapshot1 = questionRef1.get().get();
             
             // Check if the "questions" field is present and is a List
             List<Map<String, Object>> questionsList = (List<Map<String, Object>>) questionSnapshot1.get("questions");
             System.out.println(questionsList); // Verify that this is the correct structure
             List<Map<String, Object>> formattedQuestions = new ArrayList<>();
             // Process each question in the list
             for (Map<String, Object> question : questionsList) {
                 String questionText = (String) question.get("questionText");
                 String answer = (String) question.get("answer");

              
                 Map<String, Object> formattedQuestion = new HashMap<>();
             formattedQuestion.put("qt", questionText);
             formattedQuestion.put("answer", answer);

             // Add to the formatted questions list
             formattedQuestions.add(formattedQuestion);
            
             }
           
             for (Map<String, Object> q2Entry : choosedAnswer) {
            
                 String q2Qt = (String) q2Entry.get("qt");
             
                 Object q2Answer =  q2Entry.get("answer");
                 boolean found = false;
              
                 for (Map<String, Object> q1Entry : formattedQuestions) {
                	
                     String q1Qt = (String) q1Entry.get("qt");
                     Object q1Answer =  q1Entry.get("answer");
                
                     if (q2Qt.equals(q1Qt)) {
                    	System.out.println("$%##@@!!"+q2Qt+"  "+q1Qt);
                         found = true;
                         if (q2Answer.equals(q1Answer)) {
                        	 
                        	
                        	 if(category.equalsIgnoreCase("listening")) {
                        		 currentvalue++;
                        		 System.out.println(currentvalue);
                        		 incrementTestResultforwrittingAndListening(testResults, "ListeningTest",currentvalue); 
                          	   testResultsRef.set(testResults); 
//                        		 incrementTestResult(testResults, "ListeningTest");
//                          	   testResultsRef.set(testResults); 
//                          	   System.out.println(testResultsRef);
                        	 }
                        	 if(category.equalsIgnoreCase("reading")) {
                        		 currentvalue++;
                        		
                        		 System.out.println(currentvalue);
                        		 incrementTestResultforwrittingAndListening(testResults, "ReadingTest",currentvalue); 
                            	   testResultsRef.set(testResults); 
//                        		 incrementTestResult(testResults, "ReadingTest");
//                          	   testResultsRef.set(testResults); 
                          	 
                        	 }
                        	 
                        	 
                        	
                         } else {
                        
                             System.out.println("Match found but answer is incorrect for: " + q2Qt);
                         }
                        
                     }
                 }

                 if (!found) {
                     System.out.println("Question not found in q1: " + q2Qt);
                 }
             
             }
             
             
	           System.out.println("ddd"+formattedQuestions+"!@!@"+choosedAnswer);
		return 0;
	}
	

		return 0;
		// TODO Auto-generated method stub
		

	}

}

//private Map<String, Object> initializeTestResults(DocumentReference testResultsRef,String category) throws InterruptedException, ExecutionException {
//
//DocumentSnapshot testResultsSnapshot = testResultsRef.get().get();
//  Map<String, Object> testResults = testResultsSnapshot.exists() ? testResultsSnapshot.getData() : new HashMap<>();
//
//  if (testResults == null || testResults.isEmpty()) {
//      testResults = new HashMap<>();
//      testResults.put("sid", testResultsRef.getId());
//      testResults.put("LSRWTest", 0);
//      testResults.put("PsychometricTest", 0);
//      testResults.put("behaviouralTest", 0);
//      testResults.put("UnimplementedLSRW", 0);
//      testResults.put("UnimplementedPsychometric", 0);
//      testResults.put("Unimplementedbehavioural", 0);
//      testResults.put("testDate", FieldValue.serverTimestamp());
//  }
//
//  return testResults;
//}
//for (Map<String, Long> map : correctAnswer) {
//if (map.containsKey(choosedAnswer)) {
//    Long value = map.get(choosedAnswer);
//    if (value != null) {
//        int currentValue = ((Number) testResults.getOrDefault(
//            category.equals("psychometric") ? "PsychometricTest" : "behaviouralTest", 0)
//        ).intValue();
//
//        testResults.put(
//            category.equals("psychometric") ? "PsychometricTest" : "behaviouralTest",
//            currentValue + value
//        );
//
//        System.out.println("Updated test results: " + testResults);
//    }
//}
//}
