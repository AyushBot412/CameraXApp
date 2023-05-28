package com.example.cameraxapp;

import com.google.mlkit.vision.common.InputImage;

public class FrameProcessor {
    private InputImage currentImageFrame;
    public static String text;

    void processImage(InputImage image) {
        this.currentImageFrame = image;

        TextRecognitionManager.runTextRecognition(image);

    }

}
