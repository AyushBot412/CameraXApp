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


//        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
//        byte[] bytes = new byte[buffer.capacity()];
//        buffer.get(bytes);
//        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

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



        //Creating file in order to write to from Bitmap
        //File file = new File("app\\src\\main\\java\\com\\example\\cameraxapp\\testFile.txt");

        //new BufferedWriter(new FileWriter(context.getExternalFilesDir() + "currentTestFile"));

        //compressing bitmap into file
//        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(context.getFilesDir() + "currentTestFile"))){
//            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
//            os.flush(); // try with syntax should automatically flush, but I'm just being cautious
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            //e.printStackTrace();
//        } catch (NullPointerException e) {
//            System.out.println("Bitmap is null.");
//        }






        //compressing bitmap into file
//        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
//        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
//        os.flush();
//        os.close();



        //Starting Roboflow Inference Modeling

        //Base 64 Encoding
        FileInputStream fileInputStreamReader = new FileInputStream(testFile);
        byte[] bytesTwo = new byte[(int) testFile.length()];
        fileInputStreamReader.read(bytesTwo);
        String encodedFile = new String(android.util.Base64.encode(bytesTwo, android.util.Base64.DEFAULT), StandardCharsets.US_ASCII);


        String API_KEY = "bxmL0nN6Wj2JNXXHPclD"; // Our API key
        // !! DISCLAIMER: DO NOT SHARE THIS KEY WITH ANYONE OUTSIDE OF THIS PROJECT. !!


        String MODEL_ENDPOINT = "eyedrop-bottle-project/1"; // model endpoint
        //TODO CHANGE VERSION to 2, or whatever comes next

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
                    result = "Alphagan";
                } else if (line.contains("Combigan")) {
                    result = "Combigan";
                } else if (line.contains("Dorzolamide")) {
                    result = "Dorzolamide";
                } else if (line.contains("Latanoprost")) {
                    result = "Latanoprost";
                } else if (line.contains("Predforte")) {
                    result = "Predforte";
                } else if (line.contains("Rhopressa")) {
                    result = "Rhopressa";
                } else if (line.contains("Rocklatan")) {
                    result = "Rocklatan";
                } else if (line.contains("Vigamox")) {
                    result = "Vigamox";
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
