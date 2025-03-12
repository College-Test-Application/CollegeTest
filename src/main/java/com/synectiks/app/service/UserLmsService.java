package com.synectiks.app.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserLmsService {

    Firestore db = FirestoreClient.getFirestore();

    public List<Map<String, Object>> getLmsQuestions(String course) {
        CollectionReference questionsCollection = db.collection("lms"); // Corrected

        System.out.println("Executing Firestore query for category: " + course);
        ApiFuture<QuerySnapshot> queryFuture = questionsCollection.whereEqualTo("course", course).get();

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            List<QueryDocumentSnapshot> documents = queryFuture.get().getDocuments();
            System.out.println("Documents retrieved: " + documents.size());

            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                System.out.println("Processing document: " + document.getId());

                // Remove "answer" field if present
                //data.remove("answer");  // Commented out, let the answer in question for admin check
                result.add(data);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();  // Restore interrupted status
            throw new RuntimeException("Error retrieving questions from Firestore", e);
        }

        return result;
    }

    public Map<String, Object> validateLmsService(List<Map<String, Object>> data, String sid, String course) {
        CollectionReference lmsStudentDetails = db.collection("lmsStudentDetails");
        DocumentReference docRef = lmsStudentDetails.document(sid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        System.out.println("Validating questions for course: " + course + ", sid: " + sid);

        try {
            DocumentSnapshot document = future.get();
            if (!document.exists()) {
                // Insert default values if sid doesn't exist
                Map<String, Object> defaultData = new HashMap<>();
                defaultData.put("sid", sid);
                defaultData.put("chemistry", 0);
                defaultData.put("math", 0);
                defaultData.put("physics", 0);
                docRef.set(defaultData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Returning a default map instead of -1 to indicate an error state
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Error accessing student details.");
            return errorResult;
        }

        CollectionReference lmsQuestions = db.collection("lms"); // Corrected
        int overallScore = 0;
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int correctScoreSum = 0;
        int wrongScoreSum = 0;

        // Create a list to store details for each question
        List<Map<String, Object>> questionDetailsList = new ArrayList<>();

        for (Map<String, Object> entry : data) {
            String questionId = (String) entry.get("qid");
            String chosenOption = (String) entry.get("choosedoption");

            //Map to store question details
            Map<String, Object> questionDetail = new HashMap<>();
            questionDetail.put("qid", questionId);
            questionDetail.put("chosenOption", chosenOption);

            if (questionId != null && chosenOption != null) {
                ApiFuture<QuerySnapshot> questionFuture = lmsQuestions.whereEqualTo("qid", questionId).get();
                try {
                    QuerySnapshot questionSnapshot = questionFuture.get();
                    System.out.println(questionSnapshot);
                    if (!questionSnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot questionDoc : questionSnapshot) {  // Iterating over query results

                            //NEW TEST CODE TO CHECK IF COURSE IS RIGHT
                            if (questionDoc.get("course").toString().equalsIgnoreCase(course)){  // Added for checking
                                String correctAnswer = questionDoc.getString("answer");
                                System.out.println("Correct Answer: " + correctAnswer);

                                questionDetail.put("correctAnswer", correctAnswer);

                                if (correctAnswer != null && correctAnswer.equals(chosenOption)) {
                                    System.out.println("Correct Answer for QID: " + questionId);
                                    overallScore += 4;
                                    correctAnswers++;
                                    correctScoreSum += 4;
                                    questionDetail.put("isCorrect", true);
                                    questionDetail.put("message", "Correct!");
                                } else {
                                    System.out.println("Incorrect Answer for QID: " + questionId);
                                    overallScore -= 5;
                                    wrongAnswers++;
                                    wrongScoreSum -= 5;
                                    questionDetail.put("isCorrect", false);
                                    questionDetail.put("message", "Incorrect.");
                                }
                            } else{
                                 System.out.println("Question not found for QID: " + questionId);
                                questionDetail.put("correctAnswer", "Question Not Found");
                                questionDetail.put("isCorrect", false);
                                questionDetail.put("message", "Question not found for course.");
                            }
                        }
                    } else {
                        System.out.println("Question not found for QID: " + questionId);
                        questionDetail.put("correctAnswer", "Question Not Found");
                        questionDetail.put("isCorrect", false);
                        questionDetail.put("message", "Question not found in database.");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    questionDetail.put("correctAnswer", "Error");
                    questionDetail.put("isCorrect", false);
                    questionDetail.put("message", "Error validating question.");
                    // Returning a default map instead of -1 to indicate an error state
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("error", "Error validating questions.");
                    return errorResult;
                }
            } else {
                questionDetail.put("correctAnswer", "Invalid Input");
                questionDetail.put("isCorrect", false);
                questionDetail.put("message", "Invalid question or answer data.");
            }

            questionDetailsList.add(questionDetail);
        }

        updatemarks(overallScore, course, sid);

        Map<String, Object> results = new HashMap<>();
        results.put("overallScore", overallScore);
        results.put("correctAnswers", correctAnswers);
        results.put("wrongAnswers", wrongAnswers);
        results.put("correctScoreSum", correctScoreSum);
        results.put("wrongScoreSum", wrongScoreSum);
        results.put("questionDetails", questionDetailsList); // Add the list of question details

        return results; // Success
    }

    // Update marks for the specific course while keeping other course data unchanged
    private void updatemarks(int marks, String course, String sid) {
        CollectionReference lmsStudentDetails = db.collection("lmsStudentDetails");
        DocumentReference docRef = lmsStudentDetails.document(sid);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put(course, marks);

        try {
            docRef.update(updateData).get();
            System.out.println("Updated marks for " + course + ": " + marks);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getAllCourses(String courseType) {
        CollectionReference courses = db.collection("courses");
        ApiFuture<QuerySnapshot> querySnapshot = courses.whereEqualTo("courseType", courseType).get();
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        try {
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                result.add(document.getData());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving courses: " + e.getMessage());
        }
        return result;
    }

    // Other directory which will hold this information
    public Map<String, Object> getCourseTypeStats(String courseType, String sid) {
      Map<String, Object> stats = new HashMap<>();

      try {
          List<Map<String, Object>> courses = getAllCourses(courseType);
          int attemptedCourses = 0;
          double totalScore = 0.0;
          double bestScore = 0.0;

          for (Map<String, Object> course : courses) {
              String courseName = (String) course.get("courseName");
              Double recentAttemptedScorePercentage = getRecentAttemptedScorePercentage(sid, courseName);

              if (recentAttemptedScorePercentage != null && recentAttemptedScorePercentage > 0) {
                  attemptedCourses++;
                  totalScore += recentAttemptedScorePercentage;
                  bestScore = Math.max(bestScore, recentAttemptedScorePercentage);
              }
          }

          stats.put("testsAttempted", attemptedCourses + "/" + courses.size());
          stats.put("averageScore", courses.isEmpty() ? 0 : totalScore / courses.size());
          stats.put("bestPerformance", bestScore);

      } catch (Exception e) {
          e.printStackTrace();
      }

      return stats;
  }

  private Double getRecentAttemptedScorePercentage(String sid, String courseName) {
      CollectionReference lmsStudentDetails = db.collection("lmsStudentDetails");
      DocumentReference docRef = lmsStudentDetails.document(sid);

      try {
          DocumentSnapshot document = docRef.get().get();
          if (document.exists()) {
              Map<String, Object> data = document.getData();
              if (data != null && data.containsKey(courseName)) {
                  Object score = data.get(courseName);
                    if (score instanceof Long) {
                        Long longScore = (Long) score;
                      return longScore.doubleValue();
                    }
                return null; // Or handle differently if score isn't a number
              }
          }
          return 0.0; // Course not attempted yet
      } catch (Exception e) {
          e.printStackTrace();
          return null; // Indicate an error
      }
  }
}