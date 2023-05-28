package com.example.cameraxapp;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class LabelRecognitionManager {
    private static int golden_inc = 10;
    // FuzzySearch

    static class HelperType {
        ArrayList<String> matches;
        int score;

        public HelperType(String word, int inc) {
            this.matches = new ArrayList<>();
            this.matches.add(word);
            this.score = inc;
        }
    }

    public static EnumMap<Constants.BottleType, HelperType> getRecognitionMap(List<String> words) {
        EnumMap<Constants.BottleType, HelperType> map = new EnumMap<>(Constants.BottleType.class);

        for (String word : words) {
            for (Map.Entry<Constants.BottleType, Set<String>> entry : BottleDictionary.strictBottleTypeMap.entrySet()) {
                Constants.BottleType bottleType = entry.getKey();
                Set<String> set = entry.getValue();
                if (set.contains(word)) {
                    int inc = (BottleDictionary.goldenBottleTypeMap.get(bottleType).contains(word)) ? golden_inc : 1;
                    HelperType item;
                    if ((item = map.get(bottleType)) != null) {
                        item.matches.add(word);
                        item.score += inc;
                    }
                    else {
                        map.put(bottleType, new HelperType(word, inc));
                    }
                }
            }
        }
        return map;
    }

    private static Pattern pattern = Pattern.compile("-?\\d+");

    private static  boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public static EnumMap<Constants.BottleType, HelperType> getFuzzyRecognitionMap(List<String> words) {
        // TODO: Test Fuzzy Matching

        EnumMap<Constants.BottleType, HelperType> map = new EnumMap<>(Constants.BottleType.class);

        for (String word : words) {
            if (word.length() <= 3) continue;

            double threshold = 0.26;
            if ((isNumeric(String.valueOf(word.charAt(0))) &&
                    isNumeric(String.valueOf(word.charAt(1))))
                    || (isNumeric(String.valueOf(word.charAt(word.length() - 1))) &&
                    isNumeric(String.valueOf(word.charAt(word.length() - 2))))) {
                threshold = 0.2;
            } else if (word.length() < 5) {
                threshold = 0.15;
            }

            for (Map.Entry<Constants.BottleType, Set<String>> e : BottleDictionary.goldenBottleTypeMap.entrySet()) {
                Constants.BottleType bottleType = e.getKey();
                Set<String> set = e.getValue();
                ArrayList<String> fuzzyMatches = new ArrayList<>();

                for (String item : set) {
                    double diff = FuzzySearch.ratio(word, item);
                    diff = 1.0 - diff / 100.0;
                    if (diff < threshold) {
                        fuzzyMatches.add(item);
                    }
                }

                if (fuzzyMatches.size() > 0) {
                    int inc = 1;

                    for (String fuzzyMatch : fuzzyMatches) {
                        if (BottleDictionary.goldenBottleTypeMap.get(bottleType).contains(fuzzyMatch)) {
                            inc = golden_inc;
                            break;
                        }
                    }

                    if (map.containsKey(bottleType)) {
                        HelperType mapObj = map.get(bottleType);
                        mapObj.matches.add(word);
                        mapObj.score += inc;
                    } else {
                        map.put(bottleType, new HelperType(word, inc));
                    }
                }
            }
        }
        return map;
    }

}
