package com.synectiks.app.service;


import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import org.springframework.stereotype.Service;

@Service
public class CosineSimilarityService {

    public double computeCosineSimilarity(float[] vectorA, float[] vectorB) {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray vecA = manager.create(vectorA);
            NDArray vecB = manager.create(vectorB);

            double dotProduct = vecA.dot(vecB).getFloat();
            double magnitudeA = vecA.norm().getFloat();
            double magnitudeB = vecB.norm().getFloat();

            return dotProduct / (magnitudeA * magnitudeB);
        }
    }
}