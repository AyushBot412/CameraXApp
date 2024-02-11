package com.example.cameraxapp;

import java.util.HashSet;
import java.util.Set;
import android.speech.tts.TextToSpeech;
import android.util.Log;


import java.time.Month;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import com.google.mlkit.vision.text.Text;
import android.util.Log;
import android.view.ViewDebug;

public class FrameProcessor {
    public boolean isClassifying = false;
    public String text = "text";
    TextToSpeech t1;

    String processVisionText(Text visionText, String state) {

        if (!isClassifying) {
            isClassifying = true;
            System.out.println("Classifying\n");
            List<String> words = TextRecognitionManager.processTextRecognitionResult(visionText);
            System.out.println(words);

            // state determines whether to filter words bottletype or expdate
            if (state.equals("bottle_name")) {
                for(int i = 0; i < words.size(); i++) {
                    Log.w("words", words.get(i));
                }

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
            else if (state.equals("exp_date")) {
                for (int i = 0; i < words.size(); i++) {
                    Log.i("words", words.get(i));
                }

                String date = getExpDate(words);

                if (date != null) {
                    System.out.println("Classified Date: " + date);
                    isClassifying = false;
                    return date;
                } else {
                    System.out.println("No Date Found");
                    isClassifying = false;
                    return "No Date Found."; // change
                }
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


    public static String getMonth(String currLine)
    {
        //set to determine month from recognized text
        Set<String> monthSet = new HashSet<String>() {{
            add("JAN");
            add("FEB");
            add("MAR");
            add("APR");
            add("MAY");
            add("JUN");
            add("JUL");
            add("AUG");
            add("SEP");
            add("OCT");
            add("NOV");
            add("DEC");
        }};

        if (currLine.length() == 2)
        {


            boolean isnum = true;
            for (int j = 0; j < 2; j++) {
                if (!Character.isDigit(currLine.charAt(j))) {
                    isnum = false;
                }
            }

            //if 2 char string is valid integer that falls within appropriate range
            if (isnum) {
                int monthInt = Integer.parseInt(currLine);
                Month[] months = Month.values();

                if (monthInt >= 1 && monthInt <= 12){
                    return(months[monthInt - 1].toString());
                }
            }
        }
        else if (monthSet.contains(currLine))
        {
            return currLine;
        }
        return "";
    }

    public static String getYear(String yr)
    {
        boolean isnum = true;
        for (int j = 0; j < yr.length(); j++) {
            if (!Character.isDigit(yr.charAt(j))) {
                isnum = false;
            }
        }
        if (!isnum) {
            return "";
        }
        else {
            if (yr.length() == 2) {
                return "20" + yr;
            }
            else if (yr.length() == 4) {
                return yr;
            }
            else {
                return "";
            }
        }
    }

    public static String getExpDate(List<String> words) {

        //not needed
        //Constants.Month classifiedMonth = Constants.Month.JANUARY;
        String mo = "";
        String yr = "";


        //int idx = words.indexOf("EXP");
        int idxStart = 0;
        boolean monthFound = false;

        //xxxx xx xx/xx
        //check if keyword EXP exists
        //if (idx != -1) {
        //    idxStart = idx;
        //}

        for (int i = idxStart; i < words.size(); i++)
        {
            String currLine = words.get(i);

            if (words.get(i).contains("/"))
            {
                currLine = words.get(i).split("/")[0]; //divides words.get(i) string where / is present.
                mo = getMonth(currLine);

                // If month is empty, move on to next word in word list
                if (mo.length() == 0)
                {
                    continue;
                }

                String checkYear = words.get(i).split("/")[1];
                yr = getYear(checkYear);

                if (yr.length() == 0)
                {
                    continue;
                }

                return mo + " " + yr;
            }
            else
            {
                mo = getMonth(currLine);
                if (mo.length() == 0)
                {
                    continue;
                }
                idxStart = i + 1;
                break;


            }


        }

        for (int i = idxStart; i < words.size(); i++)
        {
            String currLine = words.get(i);
            yr = getYear(currLine);
            if (yr.length() == 0)
            {
                continue;
            }
            break;
        }

        if (mo.length() == 0 || yr.length() == 0) {
            return null;
        }
        else {
            return mo + " " + yr;
        }
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