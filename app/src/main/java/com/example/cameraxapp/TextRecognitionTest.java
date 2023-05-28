package com.example.cameraxapp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.io.InputStream;

public class TextRecognitionTest extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Text Recognition Test");
        try {
            int rangeMin = 1;
            int rangeMax = 1;
            AssetManager assetManager = getAssets();
            FrameProcessor processor = new FrameProcessor();

            for (int i = rangeMin; i <= rangeMax; i++) {
                InputStream istr = assetManager.open("IMG_" + i + ".png");
                Bitmap bitmap = BitmapFactory.decodeStream(istr);
                istr.close();

                InputImage im = InputImage.fromBitmap(bitmap, 0);
                processor.processImage(im);
//                System.out.println("Classified Bottle Type " + processor.text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}