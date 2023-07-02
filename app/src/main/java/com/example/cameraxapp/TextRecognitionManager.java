package com.example.cameraxapp;



import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


// TODO: Settle on text processing
// TODO: Or use image classification model

public class TextRecognitionManager {
    private static String test = "test";
    public static String extractWords(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        //TODO maybe need to add a loading beach ball?
        System.out.println("Extracting words");


        recognizer.process(image)
                .addOnSuccessListener(visionText -> System.out.println(visionText))
                .addOnFailureListener(exception -> System.out.println(exception));
        //TODO figure out what to do with a failure

//        Task<Text> oldTask = recognizer.process(image);
//
//        oldTask.addOnCompleteListener(new OnCompleteListener<Text>() {
//            @Override
//            public void onComplete(@NonNull Task<Text> task) {
//                if (task.isSuccessful()) {
//                    // Task completed successfully
//                    Text text = task.getResult();
//                    String words = text.getText();
//                } else {
//                    // Task failed with an exception
//                    Exception exception = task.getException();
//
//                }
//            }
//        });
//
//        Text newText = recognizer.process(image).getResult();
//        String completedText = newText.getText();

        recognizer.close();
        return test;


    }
    private static List<String> processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        List<String> words = new ArrayList<>();

        if (blocks.size() == 0) {
            System.out.println("No text found");
            return words;
        }

        for (Text.TextBlock block : blocks) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements()) {
                    words.add(element.getText());
                }
            }
        }
        return words;
    }

    private static Constants.BottleType getBottleType(List<String> words) {
        Constants.BottleType classifiedBottleType;

        EnumMap<Constants.BottleType, LabelRecognitionManager.HelperType> matches = LabelRecognitionManager.getRecognitionMap(words);
        Map.Entry<Constants.BottleType, LabelRecognitionManager.HelperType> exactMatch = null;

        for (Map.Entry<Constants.BottleType, LabelRecognitionManager.HelperType> match : matches.entrySet()) {
            LabelRecognitionManager.HelperType value = match.getValue();
            if (exactMatch == null || value.score > exactMatch.getValue().score) {
                exactMatch = match;
            }
        }

        if (exactMatch != null) {
            classifiedBottleType = exactMatch.getKey();
        } else {
            EnumMap<Constants.BottleType, LabelRecognitionManager.HelperType> fuzzyMatches = LabelRecognitionManager.getFuzzyRecognitionMap(words);
            Map.Entry<Constants.BottleType, LabelRecognitionManager.HelperType> fuzzyMatch = null;

            for (Map.Entry<Constants.BottleType, LabelRecognitionManager.HelperType> match : fuzzyMatches.entrySet()) {
                LabelRecognitionManager.HelperType value = match.getValue();
                if (fuzzyMatch == null || value.score > fuzzyMatch.getValue().score) {
                    fuzzyMatch = match;
                }
            }

            classifiedBottleType = fuzzyMatch.getKey();
        }


        System.out.println("Classified Bottle Type " + classifiedBottleType);

        return classifiedBottleType;
    }


}
