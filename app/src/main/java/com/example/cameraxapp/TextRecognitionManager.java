package com.example.cameraxapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class TextRecognitionManager {

    public static void runTextRecognition(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        System.out.println("Extracting words");

        recognizer.process(image)
                .addOnSuccessListener(visionText -> System.out.println("Vision text" + visionText))
                .addOnFailureListener(exception -> System.out.println(exception));

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
        // ToDo: Settle on text processing

        // ToDo: Or use image classification model

        System.out.println("Classified Bottle Type " + classifiedBottleType);

        return classifiedBottleType;
    }
}
