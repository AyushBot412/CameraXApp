package com.example.cameraxapp;



import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TextRecognitionManager {


//    public static List<String> extractWords(InputImage image) {
//        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//
//
//        //TODO maybe need to add a throbber/progress bar?
//        System.out.println("Extracting words");
//
//        recognizer.process(image)
//                        .addOnSuccessListener(visionText -> {
//                            System.out.println("Vision Text " + visionText.getText());
//                            System.out.println("processTextRecognitionResult " + processTextRecognitionResult(visionText));
////                            words = processTextRecognitionResult(visionText);
//                        })
//                        .addOnFailureListener(
//                                e -> System.out.println(e));
//        return null;
//    }
    public static List<String> processTextRecognitionResult(Text texts) {
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
}
