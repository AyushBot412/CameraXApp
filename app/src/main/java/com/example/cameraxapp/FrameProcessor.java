package com.example.cameraxapp;

import android.speech.tts.TextToSpeech;

import com.google.mlkit.vision.common.InputImage;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.mlkit.vision.text.Text;

public class FrameProcessor {
    public String text = "text";
    public boolean isClassifying = false;
    TextToSpeech t1;

    String processVisionText(Text visionText) {


        if (!isClassifying) {
            isClassifying = true;
            System.out.println("Classifying\n");
            List<String> words = TextRecognitionManager.processTextRecognitionResult(visionText);
            System.out.println(words);
            Constants.BottleType classifiedBottleType = getBottleType(words);
            if (classifiedBottleType != Constants.BottleType.NULL) {
                System.out.println("Classified Bottle Type " + classifiedBottleType);
                isClassifying = false;
                return classifiedBottleType.toString();
            } else {
                System.out.println("No Bottle Type Found.");
                isClassifying = false;
                return "No Bottle Type Found.";
            }
        }
        return "";
    }

    public void setText(String newText) {
        text = newText;

    }

    public String getText() {
        return text;
    }

    public static Constants.BottleType getBottleType(List<String> words) {
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

            if (fuzzyMatch == null) {
                classifiedBottleType = Constants.BottleType.NULL;
            } else {
                classifiedBottleType = fuzzyMatch.getKey();
            }
        }

        return classifiedBottleType;
    }


}
