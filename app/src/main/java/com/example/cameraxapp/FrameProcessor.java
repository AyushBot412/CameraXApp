package com.example.cameraxapp;

import com.google.mlkit.vision.common.InputImage;

public class FrameProcessor {
    public String text;
    public boolean isClassifying = false;

    void processImage(InputImage image) {
        if (!isClassifying) {
            isClassifying = true;
            String word = TextRecognitionManager.extractWords(image);
            setText(word);
            isClassifying = false;
        }


    }

    public void setText(String newText) {
        text = newText;

    }

    public String getText() {
        return text;
    }


}
