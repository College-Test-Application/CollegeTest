package com.synectiks.app.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@RestController
@CrossOrigin
public class CollegeAdminPageController {


	@GetMapping("/getStudentResults")
	public ResponseEntity<List<Map<String, Object>>> getStudentResults() {
	    try {
	        // Get Firestore instance
	        Firestore db = FirestoreClient.getFirestore();

	        // Fetch all documents from "StudentResultTable"
	        ApiFuture<QuerySnapshot> future = db.collection("StudentResultTable").get();
	        QuerySnapshot querySnapshot = future.get();

	        // Create a list to store student results and extract "sid" values
	        List<Map<String, Object>> studentResults = new ArrayList<>();
	        Set<String> sids = new HashSet<>();

	        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
	            Map<String, Object> data = document.getData();

	            // Remove all "Unimplemented*" fields
	            data.keySet().removeIf(key -> key.startsWith("Unimplemented"));

	            // Add "sid" to the set
	            if (data.containsKey("sid")) {
	                sids.add(data.get("sid").toString());
	            }

	            // Add filtered data to the list
	            studentResults.add(data);
	        }

	        // Fetch all UserProfiles documents in one batch
	        List<ApiFuture<DocumentSnapshot>> userFutures = new ArrayList<>();
	        for (String sid : sids) {
	            userFutures.add(db.collection("UserProfiles").document(sid).get());
	        }

	        // Wait for all futures to complete and collect user profile data
	        Map<String, Map<String, Object>> userProfiles = new HashMap<>();
	        for (ApiFuture<DocumentSnapshot> futureDoc : userFutures) {
	            DocumentSnapshot userSnapshot = futureDoc.get();
	            if (userSnapshot.exists()) {
	                userProfiles.put(userSnapshot.getId(), userSnapshot.getData());
	            }
	        }

	        // Combine StudentResultTable data with UserProfiles data
	        List<Map<String, Object>> combinedResults = new ArrayList<>();
	        for (Map<String, Object> studentResult : studentResults) {
	            String sid = (String) studentResult.get("sid");

	            // Find the matching user profile
	            Map<String, Object> userProfile = userProfiles.get(sid);
	            if (userProfile != null) {
	                // Merge student result with user profile
	                Map<String, Object> combinedData = new HashMap<>(userProfile);
	                combinedData.putAll(studentResult);
	                combinedResults.add(combinedData);
	            }
	        }

	        // Return the combined results as a JSON response
	        return ResponseEntity.ok(combinedResults);

	    } catch (InterruptedException | ExecutionException e) {
	        // Log the error and return an internal server error response
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}


	@DeleteMapping("/deleteQuestionById")
    public String deleteDocumentById(@RequestParam String documentId) {
        Firestore db = FirestoreClient.getFirestore();

        // Reference to the document to delete by its ID
        DocumentReference docRef = db.collection("Collegequestions").document(documentId);

        try {
            // Perform the delete operation
            ApiFuture<WriteResult> future = docRef.delete();


            future.get();

            // If successful, return success message
            return "Document with ID " + documentId + " successfully deleted!";
        } catch (Exception e) {
            // Handle any errors that occur during the delete operation
            e.printStackTrace();
            return "Error deleting document: " + e.getMessage();
        }
    }
	

	@DeleteMapping("/deleteStudentDetails")
    public String deleteStudentDetailsbyId(@RequestParam String sid) {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference docRef = db.collection("UserProfiles").document(sid);

        try {
    
            ApiFuture<WriteResult> future = docRef.delete();

          
            future.get();

       
            return "Document with ID " + sid + " successfully deleted!";
        } catch (Exception e) {
      
            e.printStackTrace();
            return "Error deleting document: " + e.getMessage();
        }
    }
	
	
	 @PutMapping("/replaceCollection")
	    public ResponseEntity<String> replaceCollection(@RequestBody List<Map<String, Object>> newDocumentsData) {
	       
	        Firestore db = FirestoreClient.getFirestore();
	        CollectionReference collectionRef = db.collection("AdminSelectedQuestions");

	        try {
	            
	            ApiFuture<QuerySnapshot> query = collectionRef.get();
	            QuerySnapshot querySnapshot = query.get();
	            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

	            WriteBatch deleteBatch = db.batch();
	            for (DocumentSnapshot doc : documents) {
	                deleteBatch.delete(doc.getReference());
	            }
	            deleteBatch.commit().get();
	           
	            WriteBatch addBatch = db.batch();
	            for (Map<String, Object> docData : newDocumentsData) {
	            	Object id=docData.get("id");
	             
	            	   DocumentReference docRef = collectionRef.document((String) id); 
	                   
	                   ApiFuture<WriteResult> future = docRef.set(docData); 
	         
	                   future.get(); 
	            }
	         
	            List<WriteResult> addResults = addBatch.commit().get();
	            

	            return ResponseEntity.ok("Collection replaced successfully with " + addResults.size() + " new documents.");
	        } catch (InterruptedException | ExecutionException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
	                    .body("Error replacing collection: " + e.getMessage());
	        }
	    }
}
