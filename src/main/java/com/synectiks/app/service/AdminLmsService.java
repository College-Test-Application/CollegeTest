package com.synectiks.app.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class AdminLmsService {

    Firestore db = FirestoreClient.getFirestore();

    // --- Course Type Management ---

    public String createCourseType(String courseTypeName) {
        // Check if course type already exists
        CollectionReference courseTypes = db.collection("courseTypes");
        ApiFuture<QuerySnapshot> query = courseTypes.whereEqualTo("name", courseTypeName).get();
        try {
            if (!query.get().getDocuments().isEmpty()) {
                return "Course type already exists with name: " + courseTypeName;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error checking course type: " + e.getMessage();
        }

        // If it doesn't exist, create the course type
        Map<String, Object> data = new HashMap<>();
        data.put("name", courseTypeName);
        ApiFuture<DocumentReference> addedDocRef = courseTypes.add(data);
        try {
            return "Added course type with ID: " + addedDocRef.get().getId();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error creating course type: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> getAllCourseTypes() {
        CollectionReference courseTypes = db.collection("courseTypes");
        ApiFuture<QuerySnapshot> querySnapshot = courseTypes.get();
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        try {
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                result.add(document.getData());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving course types: " + e.getMessage());
        }
        return result;
    }

    // --- Course Management ---

    public String createCourse(String courseType, String courseName, String difficultyLevel, int duration, int numberOfQuestions) {
        // Check if course already exists
        CollectionReference courses = db.collection("courses");
        ApiFuture<QuerySnapshot> query = courses.whereEqualTo("courseType", courseType).whereEqualTo("courseName", courseName).get();
        try {
            if (!query.get().getDocuments().isEmpty()) {
                return "Course already exists with name: " + courseName + " and type: " + courseType;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error checking course: " + e.getMessage();
        }

        // If it doesn't exist, create the course
        Map<String, Object> data = new HashMap<>();
        data.put("courseType", courseType);
        data.put("courseName", courseName);
        data.put("difficultyLevel", difficultyLevel);
        data.put("duration", duration);
        data.put("numberOfQuestions", numberOfQuestions);

        ApiFuture<DocumentReference> addedDocRef = courses.add(data);
        try {
            return "Added course with ID: " + addedDocRef.get().getId();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error creating course: " + e.getMessage();
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

    // --- Question Management ---

    public String createQuestion(String course, Map<String, Object> questionData) {
        CollectionReference questions = db.collection("lms");

        // Generate a unique ID for the question
        String qid = db.collection("lms").document().getId();
        questionData.put("qid", qid);  // Store qid as a field

        questionData.put("course", course); // Associate question with a course
        DocumentReference docRef = questions.document(qid);  // Use the generated qid as document id

        ApiFuture<WriteResult> addedDocRef = docRef.set(questionData); //set the data with id

        try {
             addedDocRef.get();
            return "Added question with ID: " + qid;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error creating question: " + e.getMessage();
        }
    }
    
    
    public String updateQuestion(String qid, Map<String, Object> questionData) {
        DocumentReference questionRef = db.collection("lms").document(qid);
        ApiFuture<WriteResult> future = questionRef.update(questionData);
        try {
            future.get();
            return "Question with ID: " + qid + " updated successfully.";
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error updating question: " + e.getMessage();
        }
    }

    public String deleteQuestion(String qid) {
        DocumentReference docRef = db.collection("lms").document(qid);
        ApiFuture<WriteResult> future = docRef.delete();
        try {
            future.get();
            return "Document with ID " + qid + " successfully deleted!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting document: " + e.getMessage();
        }
    }
}