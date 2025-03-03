package com.synectiks.app.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.app.entity.CollegeReadingForFireBase;

@Service
public class CollegePsyBehaviouralReadWriteQuestionsqithFireBase {
	 private static final String COLLECTION_NAME = "CollegeportalPsyBehavioural";

	    public String addQuestion(CollegeReadingForFireBase question) throws ExecutionException, InterruptedException {
	        Firestore firestore = FirestoreClient.getFirestore();
	        CollectionReference collection = firestore.collection(COLLECTION_NAME);
	        ApiFuture<com.google.cloud.firestore.DocumentReference> result = collection.add(question);
	        return result.get().getId();
	    }
}
