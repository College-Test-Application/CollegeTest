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
public class UserCourseService {

    Firestore db = FirestoreClient.getFirestore();

    public List<Map<String, Object>> getCoursesWithDetails(String courseType, String sid) {
        CollectionReference courses = db.collection("courses");
        ApiFuture<QuerySnapshot> querySnapshot = courses.whereEqualTo("courseType", courseType).get();
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> courseData = document.getData();

                // Retrieve user-specific data for the course
                Map<String, Object> userCourseData = getUserCourseData(sid, (String) courseData.get("courseName"));

                // Combine course data with user data
                courseData.putAll(userCourseData);

                result.add(courseData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving courses with details: " + e.getMessage());
        }

        return result;
    }

    private Map<String, Object> getUserCourseData(String sid, String courseName) {
        // Get lmsStudentDetails document
        DocumentReference lmsStudentDetailsDocRef = db.collection("lmsStudentDetails").document(sid);
        ApiFuture<DocumentSnapshot> lmsStudentDetailsFuture = lmsStudentDetailsDocRef.get();
        DocumentSnapshot lmsStudentDetailsDoc;
        Map<String, Object> studentDetailsData;

        try {
            lmsStudentDetailsDoc = lmsStudentDetailsFuture.get();
            studentDetailsData = lmsStudentDetailsDoc.getData();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            studentDetailsData = new HashMap<>();
        }

        // Attempt to retrieve data for the specified course.
        Object score = studentDetailsData != null ? studentDetailsData.get(courseName) : null;


        Map<String, Object> userCourseData = new HashMap<>();

        // Determine the score based on whether it was successfully retrieved.
        if (score != null) {
            Double percentage = convertScoreToPercentage(score, sid, courseName);
            userCourseData.put("recentAttemptedScorePercentage", percentage);
        } else {
            userCourseData.put("recentAttemptedScorePercentage", 0.0);
        }

        return userCourseData;
    }

    private Double convertScoreToPercentage(Object score, String sid, String courseName) {
        // Retrieve total number of questions from the course to calculate the percentage
        // Create a query
        CollectionReference courses = db.collection("courses");
        //TEST PRINT STATEMENTS
        System.out.println("PRINTING OBJECT SCORE " + score);
        System.out.println("PRINTING OBJECT SCORE CLASS " + score.getClass().getName());

        ApiFuture<QuerySnapshot> query = courses.whereEqualTo("courseName", courseName).get(); // CHANGED: to search based on object Score, but must be a string!
        Double totalNumberOfQuestions = 0.0;
        // Execute query, then retrieve total number of questions
        try {
           List<QueryDocumentSnapshot> documents = query.get().getDocuments();
           Map<String, Object>  data = documents.get(0).getData();
           System.out.println("ALL MAP DATA TEST" + data.toString());
          //Test

           totalNumberOfQuestions =  Double.parseDouble(data.get("numberOfQuestions").toString());  // Double
        } catch (InterruptedException | ExecutionException e) {
            // In case of any error, log it and return null
            e.printStackTrace();
            return 0.0;
        }
      Double intScore = getRecentAttemptedScorePercentage(sid, courseName);
         System.out.println("PRINT IT OUT! " + intScore + " with Total Value " + totalNumberOfQuestions);

        // Get percentage from score and totalNumberOfQuestions
        return (intScore/totalNumberOfQuestions) * 100;
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

                    if (score instanceof Number) {
                      //  Long longScore = (Long) score;
                        return ((Number) score).doubleValue();
                    } else{
                        return 0.0;
                    }
                }
            }
            return 0.0; // Course not attempted yet
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0; // Indicate an error
        }
    }
}