//package com.synectiks.app.service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//import org.springframework.stereotype.Service;
//
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.CollectionReference;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.QuerySnapshot;
//import com.google.firebase.cloud.FirestoreClient;
//
//@Service
//public class LmsService {
//
//    Firestore db = FirestoreClient.getFirestore();
//
//    public List<Map<String, Object>> getLmsQuestions(String course) {
//        CollectionReference questionsCollection = db.collection("lms");
//
//        System.out.println("Executing Firestore query for category: " + course);
//        ApiFuture<QuerySnapshot> queryFuture = questionsCollection.whereEqualTo("course", course).get();
//
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        try {
//            List<QueryDocumentSnapshot> documents = queryFuture.get().getDocuments();
//            System.out.println("Documents retrieved: " + documents.size());
//
//            for (QueryDocumentSnapshot document : documents) {
//                Map<String, Object> data = document.getData();
//                System.out.println("Processing document: " + document.getId());
//
//                // Remove "answer" field if present
//                data.remove("answer");
//
//                result.add(data);
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            Thread.currentThread().interrupt();  // Restore interrupted status
//            throw new RuntimeException("Error retrieving questions from Firestore", e);
//        }
//
//        return result;
//    }
//
//    public Map<String, Object> validateLmsService(List<Map<String, Object>> data, String sid, String course) {
//        CollectionReference lmsStudentDetails = db.collection("lmsStudentDetails");
//        DocumentReference docRef = lmsStudentDetails.document(sid);
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//
//        try {
//            DocumentSnapshot document = future.get();
//            if (!document.exists()) {
//                // Insert default values if sid doesn't exist
//                Map<String, Object> defaultData = new HashMap<>();
//                defaultData.put("sid", sid);
//                defaultData.put("chemistry", 0);
//                defaultData.put("math", 0);
//                defaultData.put("physics", 0);
//                docRef.set(defaultData);
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            // Returning a default map instead of -1 to indicate an error state
//            Map<String, Object> errorResult = new HashMap<>();
//            errorResult.put("error", "Error accessing student details.");
//            return errorResult;
//        }
//
//        CollectionReference lmsQuestions = db.collection("lms");
//        int overallScore = 0;
//        int correctAnswers = 0;
//        int wrongAnswers = 0;
//        int correctScoreSum = 0;
//        int wrongScoreSum = 0;
//
//        // Create a list to store details for each question
//        List<Map<String, Object>> questionDetailsList = new ArrayList<>();
//
//        for (Map<String, Object> entry : data) {
//            String questionId = (String) entry.get("qid");
//            String chosenOption = (String) entry.get("choosedoption");
//
//            //Map to store question details
//            Map<String, Object> questionDetail = new HashMap<>();
//            questionDetail.put("qid", questionId);
//            questionDetail.put("chosenOption", chosenOption);
//
//            if (questionId != null && chosenOption != null) {
//                ApiFuture<QuerySnapshot> questionFuture = lmsQuestions.whereEqualTo("qid", questionId).get();
//                try {
//                    QuerySnapshot questionSnapshot = questionFuture.get();
//                    System.out.println(questionSnapshot);
//                    if (!questionSnapshot.isEmpty()) {
//                        for (QueryDocumentSnapshot questionDoc : questionSnapshot) {  // Iterating over query results
//                            String correctAnswer = questionDoc.getString("answer");
//                            System.out.println("Correct Answer: " + correctAnswer);
//
//                            questionDetail.put("correctAnswer", correctAnswer);
//
//                            if (correctAnswer != null && correctAnswer.equals(chosenOption)) {
//                                System.out.println("Correct Answer for QID: " + questionId);
//                                overallScore += 4;
//                                correctAnswers++;
//                                correctScoreSum += 4;
//                                questionDetail.put("isCorrect", true);
//                                questionDetail.put("message", "Correct!");
//                            } else {
//                                System.out.println("Incorrect Answer for QID: " + questionId);
//                                overallScore -= 5;
//                                wrongAnswers++;
//                                wrongScoreSum -= 5;
//                                questionDetail.put("isCorrect", false);
//                                questionDetail.put("message", "Incorrect.");
//                            }
//                        }
//                    } else {
//                        System.out.println("Question not found for QID: " + questionId);
//                        questionDetail.put("correctAnswer", "Question Not Found");
//                        questionDetail.put("isCorrect", false);
//                        questionDetail.put("message", "Question not found in database.");
//                    }
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                    questionDetail.put("correctAnswer", "Error");
//                    questionDetail.put("isCorrect", false);
//                    questionDetail.put("message", "Error validating question.");
//                    // Returning a default map instead of -1 to indicate an error state
//                    Map<String, Object> errorResult = new HashMap<>();
//                    errorResult.put("error", "Error validating questions.");
//                    return errorResult;
//                }
//            } else {
//                questionDetail.put("correctAnswer", "Invalid Input");
//                questionDetail.put("isCorrect", false);
//                questionDetail.put("message", "Invalid question or answer data.");
//            }
//
//            questionDetailsList.add(questionDetail);
//        }
//
//        updatemarks(overallScore, course, sid);
//
//        Map<String, Object> results = new HashMap<>();
//        results.put("overallScore", overallScore);
//        results.put("correctAnswers", correctAnswers);
//        results.put("wrongAnswers", wrongAnswers);
//        results.put("correctScoreSum", correctScoreSum);
//        results.put("wrongScoreSum", wrongScoreSum);
//        results.put("questionDetails", questionDetailsList); // Add the list of question details
//
//        return results; // Success
//    }
//
//    // Update marks for the specific course while keeping other course data unchanged
//    private void updatemarks(int marks, String course, String sid) {
//        CollectionReference lmsStudentDetails = db.collection("lmsStudentDetails");
//        DocumentReference docRef = lmsStudentDetails.document(sid);
//
//        Map<String, Object> updateData = new HashMap<>();
//        updateData.put(course, marks);
//
//        try {
//            docRef.update(updateData).get();
//            System.out.println("Updated marks for " + course + ": " + marks);
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
//}