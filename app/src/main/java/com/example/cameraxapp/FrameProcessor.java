package com.example.cameraxapp;

import com.google.mlkit.vision.common.InputImage;

public class FrameProcessor {
    public String text;

    void processImage(InputImage image) {
        String word = TextRecognitionManager.runTextRecognition(image);
        setText(word);

    }

    public void setText(String newText) {
        text = newText;

    }

    public String getText() {
        return text;
    }


}
