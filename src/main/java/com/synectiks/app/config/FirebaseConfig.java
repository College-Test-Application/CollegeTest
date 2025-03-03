

package com.synectiks.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        // Load the service account key file
//    	//String serviceAccount = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
//        FileInputStream serviceAccount = 
//                new FileInputStream("src/main/resources/firebase-service-account.json");
//
//        // Set up FirebaseOptions
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://collegebackend-7e5c9.firebaseio.com")
//                .build();
//
//        // Initialize Firebase only if not already initialized
//        if (FirebaseApp.getApps().isEmpty()) {
//            FirebaseApp.initializeApp(options);   	
//        }

    	 InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-serice-account.json");

         // Check if the file is found in the classpath
         if (serviceAccount == null) {
             throw new IOException("firebase-service-account.json not found in classpath");
         }

         // Set up FirebaseOptions
         FirebaseOptions options = FirebaseOptions.builder()
                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                 .setDatabaseUrl("https://jahv1-2e258.firebaseio.com")
                 .build();
System.out.println(options);
         // Initialize Firebase only if not already initialized
         if (FirebaseApp.getApps().isEmpty()) {
             FirebaseApp.initializeApp(options);
         }
    	
  }
}


