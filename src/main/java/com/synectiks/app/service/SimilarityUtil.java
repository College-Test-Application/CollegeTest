package com.synectiks.app.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SimilarityUtil {

    // Utility method to calculate cosine similarity between two strings
    public static double calculateCosineSimilarity(String topic, String text) throws Exception {
        // Check if the number of words in the text is more than 80
        if (countWords(text) <= 40) {
            return 0.0; // If text has 80 or fewer words, return similarity of 0
        }

        // Perform TF-IDF vectorization using Lucene's StandardAnalyzer
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // Process the topic and text
        Map<String, Integer> topicTokens = tokenizeAndCalculateTFIDF(topic, analyzer);
        Map<String, Integer> textTokens = tokenizeAndCalculateTFIDF(text, analyzer);

        // Calculate cosine similarity
        return cosineSimilarity(topicTokens, textTokens);
    }

    // Tokenizes and calculates the term frequencies (TF) of a given string
    private static Map<String, Integer> tokenizeAndCalculateTFIDF(String text, StandardAnalyzer analyzer) throws Exception {
        Map<String, Integer> tokenMap = new HashMap<>();

        // Tokenize the input text
        StringReader reader = new StringReader(text);
        var tokenStream = analyzer.tokenStream(null, reader);

        // Reset the token stream
        tokenStream.reset();

        // Traverse through the token stream to calculate term frequencies
        while (tokenStream.incrementToken()) {
            // Retrieve the term from the token stream
            String term = tokenStream.getAttribute(CharTermAttribute.class).toString();
            tokenMap.put(term, tokenMap.getOrDefault(term, 0) + 1);
        }

        // Close the token stream
        tokenStream.end();
        tokenStream.close();

        return tokenMap;
    }

    // Compute the cosine similarity between two term frequency maps
    private static double cosineSimilarity(Map<String, Integer> topicTokens, Map<String, Integer> textTokens) {
        double dotProduct = 0;
        double topicMagnitude = 0;
        double textMagnitude = 0;

        // Calculate dot product and magnitudes of the vectors
        for (Map.Entry<String, Integer> entry : topicTokens.entrySet()) {
            String term = entry.getKey();
            int topicTermFrequency = entry.getValue();
            int textTermFrequency = textTokens.getOrDefault(term, 0);

            dotProduct += topicTermFrequency * textTermFrequency;
            topicMagnitude += topicTermFrequency * topicTermFrequency;
            textMagnitude += textTermFrequency * textTermFrequency;
        }

        // Handle edge case where the magnitude is zero
        if (topicMagnitude == 0 || textMagnitude == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(topicMagnitude) * Math.sqrt(textMagnitude));
    }

    // Helper method to count the number of words in the text
    private static int countWords(String text) {
        String[] words = text.split("\\s+"); // Split text by whitespace
        return words.length;
    }
}


//package com.synectiks.app.service;
//
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.util.BytesRef;
//import org.springframework.stereotype.Service;
//
//import java.io.StringReader;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class SimilarityUtil {
//
//    // Utility method to calculate cosine similarity between two strings
//    public static double calculateCosineSimilarity(String topic, String text) throws Exception {
//        // Perform TF-IDF vectorization using Lucene's StandardAnalyzer
//        StandardAnalyzer analyzer = new StandardAnalyzer();
//
//        // Process the topic and text
//        Map<String, Integer> topicTokens = tokenizeAndCalculateTFIDF(topic, analyzer);
//        Map<String, Integer> textTokens = tokenizeAndCalculateTFIDF(text, analyzer);
//
//        // Calculate cosine similarity
//        return cosineSimilarity(topicTokens, textTokens);
//    }
//
//    // Tokenizes and calculates the term frequencies (TF) of a given string
//    private static Map<String, Integer> tokenizeAndCalculateTFIDF(String text, StandardAnalyzer analyzer) throws Exception {
//        Map<String, Integer> tokenMap = new HashMap<>();
//
//        // Tokenize the input text
//        StringReader reader = new StringReader(text);
//        var tokenStream = analyzer.tokenStream(null, reader);
//
//        // Reset the token stream
//        tokenStream.reset();
//
//        // Traverse through the token stream to calculate term frequencies
//        while (tokenStream.incrementToken()) {
//            // Retrieve the term from the token stream
//            String term = tokenStream.getAttribute(CharTermAttribute.class).toString();
//            tokenMap.put(term, tokenMap.getOrDefault(term, 0) + 1);
//        }
//
//        // Close the token stream
//        tokenStream.end();
//        tokenStream.close();
//
//        return tokenMap;
//    }
//
//    // Compute the cosine similarity between two term frequency maps
//    private static double cosineSimilarity(Map<String, Integer> topicTokens, Map<String, Integer> textTokens) {
//        double dotProduct = 0;
//        double topicMagnitude = 0;
//        double textMagnitude = 0;
//
//        // Calculate dot product and magnitudes of the vectors
//        for (Map.Entry<String, Integer> entry : topicTokens.entrySet()) {
//            String term = entry.getKey();
//            int topicTermFrequency = entry.getValue();
//            int textTermFrequency = textTokens.getOrDefault(term, 0);
//
//            dotProduct += topicTermFrequency * textTermFrequency;
//            topicMagnitude += topicTermFrequency * topicTermFrequency;
//            textMagnitude += textTermFrequency * textTermFrequency;
//        }
//
//        // Handle edge case where the magnitude is zero
//        if (topicMagnitude == 0 || textMagnitude == 0) {
//            return 0.0;
//        }
//
//        return dotProduct / (Math.sqrt(topicMagnitude) * Math.sqrt(textMagnitude));
//    }
//}
