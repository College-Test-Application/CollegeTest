//package com.synectiks.app.controller;
//
//
//
//import ai.djl.translate.TranslateException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//
//import com.synectiks.app.service.CosineSimilarityService;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
////
//public class BertControllerForValidatingText {
//
////    @Autowired
////    private BertService bertService;
//
//    @Autowired
//    private CosineSimilarityService cosineSimilarityService;
//
//    @PostMapping("/evaluate-topic-relevance")
//    public Map<String, Object> evaluateRelevance(@RequestBody Map<String, String> request) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String topic = request.get("topic");
//            String text = request.get("text");
//
//            // Get embeddings
//            float[] topicEmbedding = bertService.getEmbedding(topic);
//            float[] textEmbedding = bertService.getEmbedding(text);
//
//            // Calculate cosine similarity
//            double similarity = cosineSimilarityService.computeCosineSimilarity(topicEmbedding, textEmbedding);
//
//            // Convert similarity to percentage score
//            double percentageScore = (similarity + 1) * 50;
//            percentageScore = Math.max(percentageScore, 0); // Ensure non-negative
//
//            response.put("relevance_score_percentage", percentageScore);
//        } catch (TranslateException e) {
//            response.put("error", "Error processing embeddings: " + e.getMessage());
//        } catch (Exception e) {
//            response.put("error", "Unexpected error: " + e.getMessage());
//        }
//
//        return response;
//    }
//}