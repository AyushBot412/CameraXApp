package com.example.cameraxapp;

import android.content.Context;
import android.graphics.Bitmap;


import androidx.camera.core.ImageProxy;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.net.URL;

public class InferenceLocal {


    public String inference(Context context, ImageProxy imageProxy) throws IOException {


        // Creating bitmap from ImageProxy
        Bitmap testBitmap = null;
        try {
            testBitmap = imageProxy.toBitmap();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Format.");
        } catch (UnsupportedOperationException e) {
            System.out.println("Conversion to Bitmap failed.");
        }


        //Creating new file in order to write to from bitmap
        File testFile = new File(context.getFilesDir() + "currentTestFile");
        // Compressing bitmap into File
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(testFile))){
            testBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            //e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Test Bitmap is null.");
        }






        //Starting Roboflow Inference Modeling

        //Base 64 Encoding
        FileInputStream fileInputStreamReader = new FileInputStream(testFile);
        byte[] bytesTwo = new byte[(int) testFile.length()];
        fileInputStreamReader.read(bytesTwo);
        String encodedFile = new String(android.util.Base64.encode(bytesTwo, android.util.Base64.DEFAULT), StandardCharsets.US_ASCII);


        String API_KEY = "bxmL0nN6Wj2JNXXHPclD"; // Our API key
        // !! DISCLAIMER: DO NOT SHARE THIS KEY WITH ANYONE OUTSIDE OF THIS PROJECT. !!


        String MODEL_ENDPOINT = "eyedrop-bottle-project/3"; // model endpoint

        // Construct the URL
        String uploadURL = "https://detect.roboflow.com/" + MODEL_ENDPOINT + "?api_key=" + API_KEY
                + "&name=currentImage.jpg";
        // I just gave it a random name

        // Http Request
        HttpURLConnection connection = null;


        // Result Classification
        String result = "";


        try {
            // Configure connection to URL
            URL url = new URL(uploadURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", Integer.toString(encodedFile.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(encodedFile);
            wr.close();

            // Get Response
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("Alphagan")) {
                    result = "ALPHAGAN";
                } else if (line.contains("Combigan")) {
                    result = "COMBIGAN";
                } else if (line.contains("Dorzolamide")) {
                    result = "DORZOLAMIDE";
                } else if (line.contains("Latanoprost")) {
                    result = "LATANOPROST";
                } else if (line.contains("Predforte")) {
                    result = "PREDFORTE";
                } else if (line.contains("Rhopressa")) {
                    result = "RHOPRESSA";
                } else if (line.contains("Rocklatan")) {
                    result = "ROCKLATAN";
                } else if (line.contains("Vigamox")) {
                    result = "VIGAMOX";
                }


            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;

    }
}
