package com.example.cameraxapp;

import com.google.mlkit.vision.common.InputImage;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrameProcessor {
//    static ExecutorService executor = Executors.newSingleThreadExecutor();

    private InputImage currentImageFrame;

    public static String text;

    void processImage(InputImage image) {
        this.currentImageFrame = image;

        TextRecognitionManager.runTextRecognition(image);

    }

}
