//package com.synectiks.app.service;
//
//import ai.djl.Application;
//import ai.djl.ModelException;
//import ai.djl.inference.Predictor;
//import ai.djl.modality.nlp.bert.BertTokenizer;
//import ai.djl.translate.Batchifier;
//import ai.djl.translate.Translator;
//import ai.djl.translate.TranslatorContext;
//import ai.djl.repository.zoo.Criteria;
//import ai.djl.ndarray.NDList;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.io.IOException;
//import java.nio.file.Paths;
//
//@Service
//public class BertService {
//    private Predictor<String, float[]> predictor;
//
//    @Autowired
//    public BertService() throws ModelException, IOException {
//        // Load model using Hugging Face's BERT base uncased
//        String modelPath = "bert-base-uncased";  // Using model from Hugging Face
//        Criteria<String, float[]> criteria = Criteria.builder()
//                .optApplication(Application.NLP.TEXT_EMBEDDING)
//                .setTypes(String.class, float[].class)
//                .optModelPath(Paths.get(modelPath))  // This will fetch the model from Hugging Face repository
//                .optEngine("PyTorch") // Make sure the model is compatible with PyTorch
//                .optTranslator(new BertEmbeddingTranslator())
//                .build();
//
//        this.predictor = criteria.loadModel().newPredictor();
//    }
//
//    public float[] getBertEmbeddings(String text) throws Exception {
//        return predictor.predict(text);
//    }
//
//    private static class BertEmbeddingTranslator implements Translator<String, float[]> {
//        private BertTokenizer tokenizer;
//
//        public BertEmbeddingTranslator() {
//            // Initialize tokenizer for BERT (using HuggingFace's tokenizer)
//            this.tokenizer = new BertTokenizer();
//        }
//
//        @Override
//        public Batchifier getBatchifier() {
//            return null; // Not batching for now
//        }
//
//        @Override
//        public float[] processOutput(TranslatorContext ctx) {
//            // Process model output and return embeddings
//            NDList output = (NDList) ctx.getOutput();
//            return output.singletonOrThrow().toFloatArray(); // Convert NDList to float[]
//        }
//
//        @Override
//        public String processInput(TranslatorContext ctx) {
//            // Preprocess input: Tokenizing text
//            String text = ctx.getInput().getAsString();
//            return text;
//        }
//
//        @Override
//        public NDList processInput(TranslatorContext ctx, String input) throws Exception {
//            // Tokenize input and convert to NDList for model processing
//            String[] tokens = tokenizer.encode(input); // This will tokenize the text
//            return new NDList(tokens);
//        }
//
//        @Override
//        public float[] processOutput(TranslatorContext ctx, NDList list) throws Exception {
//            // Process the model's output and return the embeddings
//            return list.singletonOrThrow().toFloatArray(); // Convert NDList to float[]
//        }
//    }
//}
