//package com.synectiks.app.service;
//
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.CollectionReference;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.WriteResult;
//import com.google.firebase.cloud.FirestoreClient;
//import com.google.firebase.cloud.StorageClient;
//import com.google.cloud.storage.Blob;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ExecutionException;
//
//@Service
//public class CollegeQuestionFireBaseService {
//
//    private static final String COLLECTION_NAME = "Collegequestions";
//
//    /**
//     * Adds a list of questions to Firestore as separate documents.
//     * Each question will be stored with a unique ID.
//     *
//     * @param questions A list of maps containing question data.
//     * @return A message indicating the success of the operation.
//     */
//    public String addQuestionsToFirebase(List<Map<String, Object>> questions) {
//        Firestore db = FirestoreClient.getFirestore(); // Get Firestore instance
//        CollectionReference collection = db.collection(COLLECTION_NAME); // Get Firestore collection
//
//        // Loop through each question and store it as a separate document
//        for (Map<String, Object> questionData : questions) {
//            try {
//                // Create a unique ID for each question if not provided
//                String id = UUID.randomUUID().toString();
//                questionData.put("id", id); // Add the ID to the question data
//
//                // Save the document to Firestore with the unique ID
//                DocumentReference docRef = collection.document(id); // Reference to the Firestore document
//                ApiFuture<WriteResult> future = docRef.set(questionData); // Set the data asynchronously
//                future.get(); // Wait for the completion of the write operation
//
//            } catch (InterruptedException | ExecutionException e) {
//                // Handle exceptions and log them accordingly
//                throw new RuntimeException("Error adding question to Firestore", e);
//            }
//        }
//
//        return "Questions added successfully to Firestore";
//    }
//
//    /**
//     * Uploads an audio file to Firebase Storage and returns the public URL.
//     *
//     * @param file The audio file to be uploaded.
//     * @return The public URL of the uploaded audio file.
//     */
//    public String uploadAudioFile(MultipartFile file) throws IOException {
//        // Create a path to temporarily store the file
//        Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
//        file.transferTo(tempFile);
//
//        // Get Firebase Storage bucket reference
//        Blob blob = StorageClient.getInstance()
//                .bucket()
//                .create("audio/" + file.getOriginalFilename(), Files.readAllBytes(tempFile));
//
//        // Delete the temporary file after uploading
//        Files.delete(tempFile);
//
//        return blob.getMediaLink(); // Return the public URL to access the audio file
//    }
//
//    /**
//     * Stores a question with an associated audio file in Firestore.
//     *
//     * @param questionData The data of the question.
//     * @param audioFile    The audio file associated with the question.
//     * @return A message indicating the success of the operation.
//     */
//    public String storeQuestionWithAudio(Map<String, Object> questionData, MultipartFile audioFile) {
//        try {
//            // Upload the audio file and get the URL
//            String audioUrl = uploadAudioFile(audioFile);
//
//            // Add the audio URL to the question data
//            questionData.put("audioUrl", audioUrl);
//
//            // Create a unique ID for the question
//            String questionId = UUID.randomUUID().toString();
//            Firestore db = FirestoreClient.getFirestore(); // Get Firestore instance
//            CollectionReference collection = db.collection(COLLECTION_NAME); // Get Firestore collection
//
//            // Save the question data in Firestore asynchronously
//            DocumentReference docRef = collection.document(questionId);
//            ApiFuture<WriteResult> future = docRef.set(questionData);
//
//            // Wait for the operation to complete
//            future.get();  // Consider handling future.get() asynchronously
//
//            return "Question and audio uploaded successfully with ID: " + questionId;
//
//        } catch (IOException | InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            return "Error uploading question and audio: " + e.getMessage();
//        }
//    }
//
//	public String storeQuestionWithAudio(String audioId, String audioUrl, Object questionsObj) {
//		  Firestore db = FirestoreClient.getFirestore(); 
//	        CollectionReference collection = db.collection(COLLECTION_NAME); 
//
//	        // Prepare the question data with audioId and audioUrl
//	        Map<String, Object> questionData = Map.of(
//	                "audioId", audioId,
//	                "audioUrl", audioUrl,
//	                "questions", questionsObj // Assuming questionsObj is already in the correct format
//	        );
//
//	        // Generate a random UUID for the document ID
//	        String id = UUID.randomUUID().toString();
//
//	        // Create a document reference with the generated ID
//	        DocumentReference docRef = collection.document(id);
//
//	        // Set the data in Firestore (this will store the question data under the generated document ID)
//	        ApiFuture<WriteResult> future = docRef.set(questionData);
//
//	        try {
//	            // Wait for the operation to complete and get the result
//	            future.get();
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            return "Failed to add questions to Firestore: " + e.getMessage();
//	        }
//
//	        return "Questions added successfully to Firestore";
//	    }
//}





package com.synectiks.app.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.core.Repo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class CollegeQuestionFireBaseService {

    private static final String COLLECTION_NAME = "Collegequestions";
    private final Firestore firestore;

    public CollegeQuestionFireBaseService() {
        this.firestore = FirestoreClient.getFirestore(); 
    }

    /**
     * Adds a list of questions to Firestore as separate documents.
     * Each question will be stored with a unique ID.
     *
     * @param questions A list of maps containing question data.
     * @return A message indicating the success of the operation.
     */
    public String addQuestionsToFirebase(List<Map<String, Object>> questions) {
        CollectionReference collection = firestore.collection(COLLECTION_NAME); // Get Firestore collection

        System.out.println("$$$$$$$");
        for (Map<String, Object> questionData : questions) {
            try {
            	   System.out.println("$$$$$$$");
                String id = UUID.randomUUID().toString();
                questionData.put("id", id); 
                System.out.println("$$$$$$$");
                
                DocumentReference docRef = collection.document(id); // Reference to the Firestore document
                System.out.println("$$$$$$$");
                ApiFuture<WriteResult> future = docRef.set(questionData); // Set the data asynchronously
                System.out.println("$$$$$$$"+future);
                future.get(); // Wait for the completion of the write operation
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // Handle exceptions and log them accordingly
                throw new RuntimeException("Error adding question to Firestore", e);
            }
        }

        return "Questions added successfully to Firestore";
    }

    /**
     * Store question data with associated audio information to Firestore.
     *
     * @param audioId     The unique audio ID.
     * @param audioUrl    The URL where the audio is stored.
     * @param questionsObj The question data object.
     * @return A message indicating success or failure.
     */
    public String storeQuestionWithAudio(String category, String audioUrl, Object questionsObj) {
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        String id = UUID.randomUUID().toString();
        
        // Prepare the question data with audioId and audioUrl
        Map<String, Object> questionData = Map.of(
                "category", category,
                "audioUrl", audioUrl,
                "questions", questionsObj, 
                "id",id// Assuming questionsObj is already in the correct format
        );
     
       
        // Create a document reference with the generated ID
        DocumentReference docRef = collection.document(id);

        // Set the data in Firestore (this will store the question data under the generated document ID)
        ApiFuture<WriteResult> future = docRef.set(questionData);

        try {
            // Wait for the operation to complete and get the result
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add questions to Firestore: " + e.getMessage();
        }

        return "Questions added successfully to Firestore";
    }

    /**
     * Fetch questions from Firestore based on category.
     *
     * @param category The category of questions to fetch.
     * @return A list of questions that belong to the specified category.
     */
//    public List<Map<String, Object>> getQuestionsByCategory(String category) {
//        CollectionReference questionsCollection = firestore.collection(COLLECTION_NAME);
//
//        // Query Firestore to fetch documents that match the given category
//        ApiFuture<QuerySnapshot> query = questionsCollection.whereEqualTo("category", category).get();
//
//        // List to hold the results
//        List<Map<String, Object>> questions = new ArrayList<>();
//
//        try {
//            // Retrieve the query result and add to the list
//            for (QueryDocumentSnapshot document : query.get().getDocuments()) {
//                questions.add(document.getData());
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error fetching questions by category", e);
//        }
//
//        return questions;
//    }
//    public List<Map<String, Object>> getQuestionsByCategory(String category) {
//    	Firestore db = FirestoreClient.getFirestore();
//        CollectionReference questionsCollection = db.collection("Collegequestions");
//
//
//        ApiFuture<QuerySnapshot> query = questionsCollection.whereEqualTo("category", category).get();
//
//        List<Map<String, Object>> result = new ArrayList<>();
//        System.out.println(result);
//
//        try {
//            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
//
//            for (QueryDocumentSnapshot document : documents) {
//                Map<String, Object> data = document.getData();
//         System.out.println(":12");
//                if ("Reading".equalsIgnoreCase(category)) {
//                    
//                    if (data.containsKey("questions")) {
//                        List<Map<String, Object>> questionList = (List<Map<String, Object>>) data.get("questions");
//                        for (Map<String, Object> question : questionList) {
//                            question.remove("answer"); 
//                        }
//                    }
//                } else if ("Listening".equalsIgnoreCase(category)) {
//                 System.out.println("@@@@@@@@@@@2");
//                    if (data.containsKey("questions")) {
//                        List<Map<String, Object>> questionList = (List<Map<String, Object>>) data.get("questions");
//                        for (Map<String, Object> question : questionList) {
//                            question.remove("answer"); 
//                        }
//                    }
//                } else {
//                   
//                    if (data.containsKey("answer")) {
//                        data.remove("answer"); 
//                    }
//                }
//
//                result.add(data);
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException("Error retrieving questions from Firestore", e);
//        }
//
//        return result;
//    }
//
    
    public List<Map<String, Object>> getQuestionsByCategory(String category) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference questionsCollection = db.collection("Collegequestions");

        System.out.println("Executing Firestore query for category: " + category);
        ApiFuture<QuerySnapshot> query = questionsCollection.whereEqualTo("category", category).get();

        List<Map<String, Object>> result = new ArrayList<>();
        System.out.println("Query initialized.");

        try {
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            System.out.println("Documents retrieved: " + documents.size());

            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                System.out.println("Processing document: " + document.getId());

                if ("Reading".equalsIgnoreCase(category)) {
                    if (data.containsKey("questions")) {
                        List<Map<String, Object>> questionList = (List<Map<String, Object>>) data.get("questions");
                        for (Map<String, Object> question : questionList) {
                            question.remove("answer");
                        }
                    }
                } else if ("Listening".equalsIgnoreCase(category)) {
                    if (data.containsKey("questions")) {
                        List<Map<String, Object>> questionList = (List<Map<String, Object>>) data.get("questions");
                        for (Map<String, Object> question : questionList) {
                            question.remove("answer");
                        }
                    }
                } else {
                    if (data.containsKey("answer")) {
                        data.remove("answer");
                    }
                }

                result.add(data);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving questions from Firestore", e);
        }

        return result;
    }

	
	public String storeUserDetails(String userId, String name, String mail, String mno, String university,
			String cgpa,String degree, String passout_year ) {
		 CollectionReference collection = firestore.collection("UserProfiles");

	        // Prepare the question data with audioId and audioUrl
	        Map<String, Object> questionData = Map.of(
	                "name", name,
	                "mail", mail,
	                "mno", mno,
	                "university", university,
	                "cgpa", cgpa,
	                "degree",degree,
	                "passout_year",passout_year 
       
	               
	        );


	        DocumentReference docRef = collection.document(userId);

	        // Set the data in Firestore (this will store the question data under the generated document ID)
	        ApiFuture<WriteResult> future = docRef.set(questionData);

	        try {
	            // Wait for the operation to complete and get the result
	            future.get();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Failed to add questions to Firestore: " + e.getMessage();
	        }

	        return "user details added successfully to Firestore";
	}

	public Map<String, Object> getUserDetailssByCategory(String userId) {
		try {
            // Get a reference to the collection
            CollectionReference collection = firestore.collection("UserProfiles");

            // Get a reference to the specific document by its ID
            DocumentReference documentReference = collection.document(userId);

            // Fetch the document
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
System.out.println(document.getData());
            if (document.exists()) {
                // Return document data as a Map
                return (Map<String, Object>) document.getData();
            } else {
                System.out.println("No such document with ID: " + userId);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error fetching document: " + e.getMessage());
            return null;
        }
	}

	public String storeUserDetails(String userId, String name, String mail, String mno, String university, String cgpa,
	        String degree, String passout_year, List<String> skills,  List<Map<String, Object>> experiense, List<String> projects) {
	    
	    CollectionReference collection = firestore.collection("UserProfiles");

	    // Prepare the user profile data with the correct field types
	    Map<String, Object> userProfileData = new HashMap<>();
	    userProfileData.put("name", name);
	    userProfileData.put("mail", mail);
	    userProfileData.put("mno", mno);
	    userProfileData.put("university", university);
	    userProfileData.put("cgpa", cgpa);
	    userProfileData.put("degree", degree);
	    userProfileData.put("passout_year", passout_year);
	    userProfileData.put("skills", skills);  // Store skills as a List
	    userProfileData.put("experiense", experiense);
	    userProfileData.put("projects", projects);  // Store projects as a List

	    // Store the user data in Firestore under the specified userId
	    try {
	        // You can either set or update the document based on userId
	        DocumentReference userRef = collection.document(userId);
	        userRef.set(userProfileData);
	        return "User details successfully stored!";
	    } catch (Exception e) {
	        // Handle any errors that occur while storing the data
	        return "Error storing user details: " + e.getMessage();
	    }
	}

	public void addQuestionsToFirebaseforlms(Map<String, String> response) {
		
		 CollectionReference collection = firestore.collection("lms"); // Get Firestore collection

	        System.out.println("$$$$$$$");
	         {
	            try {
	            	   System.out.println("$$$$$$$");
//	                String id = UUID.randomUUID().toString();
	            	   
	                String id=response.get("qid");
	                response.put("id", id); 
	                System.out.println("$$$$$$$");
	                
	                DocumentReference docRef = collection.document(id); // Reference to the Firestore document
	                System.out.println("$$$$$$$");
	                ApiFuture<WriteResult> future = docRef.set(response); // Set the data asynchronously
	                System.out.println("$$$$$$$"+future);
	                future.get(); // Wait for the completion of the write operation
	                System.out.println(future.get());
	            } catch (InterruptedException | ExecutionException e) {
	                // Handle exceptions and log them accordingly
	                throw new RuntimeException("Error adding question to Firestore", e);
	            }
	        }

	       
	}

}
//}

