package com.example.cameraxapp;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class BottleDictionary {
    static EnumMap<Constants.BottleType, Set<String>> bottleTypeMap;
    static EnumMap<Constants.BottleType, Set<String>> strictBottleTypeMap;
    static EnumMap<Constants.BottleType, Set<String>> goldenBottleTypeMap;

    private BottleDictionary() {}

    public static void initialize() {
        bottleTypeMap = new EnumMap<>(Constants.BottleType.class);
        bottleTypeMap.put(Constants.BottleType.ALPHAGAN, UniqueWords.ALPHAGAN);
        bottleTypeMap.put(Constants.BottleType.DORZOLAMIDE, UniqueWords.DORZOLAMIDE);
        bottleTypeMap.put(Constants.BottleType.PREDFORTE, UniqueWords.PREDFORTE);
        bottleTypeMap.put(Constants.BottleType.VIGAMOX, UniqueWords.VIGAMOX);
        bottleTypeMap.put(Constants.BottleType.LATANOPROST, UniqueWords.LATANOPROST);
        bottleTypeMap.put(Constants.BottleType.COMBIGAN, UniqueWords.COMBIGAN);
        bottleTypeMap.put(Constants.BottleType.RHOPRESSA, UniqueWords.RHOPRESSA);
        bottleTypeMap.put(Constants.BottleType.ROCKLATAN, UniqueWords.ROCKLATAN);

        strictBottleTypeMap = new EnumMap<>(Constants.BottleType.class);
        strictBottleTypeMap.put(Constants.BottleType.ALPHAGAN, StrictUniqueWords.ALPHAGAN);
        strictBottleTypeMap.put(Constants.BottleType.DORZOLAMIDE, StrictUniqueWords.DORZOLAMIDE);
        strictBottleTypeMap.put(Constants.BottleType.PREDFORTE, StrictUniqueWords.PREDFORTE);
        strictBottleTypeMap.put(Constants.BottleType.VIGAMOX, StrictUniqueWords.VIGAMOX);
        strictBottleTypeMap.put(Constants.BottleType.LATANOPROST, StrictUniqueWords.LATANOPROST);
        strictBottleTypeMap.put(Constants.BottleType.COMBIGAN, StrictUniqueWords.COMBIGAN);
        strictBottleTypeMap.put(Constants.BottleType.RHOPRESSA, StrictUniqueWords.RHOPRESSA);
        strictBottleTypeMap.put(Constants.BottleType.ROCKLATAN, StrictUniqueWords.ROCKLATAN);

        goldenBottleTypeMap = new EnumMap<>(Constants.BottleType.class);
        goldenBottleTypeMap.put(Constants.BottleType.ALPHAGAN, StrictUniqueWords.goldenALPHAGAN);
        goldenBottleTypeMap.put(Constants.BottleType.DORZOLAMIDE, StrictUniqueWords.goldenDORZOLAMIDE);
        goldenBottleTypeMap.put(Constants.BottleType.PREDFORTE, StrictUniqueWords.goldenPREDFORTE);
        goldenBottleTypeMap.put(Constants.BottleType.VIGAMOX, StrictUniqueWords.goldenVIGAMOX);
        goldenBottleTypeMap.put(Constants.BottleType.LATANOPROST, StrictUniqueWords.goldenLATANOPROST);
        goldenBottleTypeMap.put(Constants.BottleType.COMBIGAN, StrictUniqueWords.goldenCOMBIGAN);
        goldenBottleTypeMap.put(Constants.BottleType.RHOPRESSA, StrictUniqueWords.goldenRHOPRESSA);
        goldenBottleTypeMap.put(Constants.BottleType.ROCKLATAN, StrictUniqueWords.goldenROCKLATAN);

    }

    private static class StrictUniqueWords {
        private final static Set<String> goldenALPHAGAN = new HashSet<>(Arrays.asList("AlphagarP",
                "Alphagan", "AlphaganP", "Alphagar", "0023-9321-10", "0023-9321", "9321-10",
                "0023-9321-05", "0023-9321", "9321-05"));
        private final static Set<String> ALPHAGAN = new HashSet<>(Arrays.asList("capacity", "AlphagarP",
                "0.1%", "times", "daily", "Alphagan", "three", "AlphaganP"));

        private final static Set<String> goldenDORZOLAMIDE = new HashSet<>(Arrays.asList("60429-115-10",
                "60429-115", "115-10", "60429-115-05", "60429-115", "115-05", "24208-485-10",
                "24208-485", "485-10", "HCI", "HCI-", "(LOMB", "Lomb", "(Dorzolamide",
                "Dorzolamide.", "Dorzolamide.", "Dorzolamide", "Bausch", "BAUSCH"));
        private final static Set<String> DORZOLAMIDE = new HashSet<>(Arrays.asList("33637", "+", "2%",
                "APPLICATION", "HCI-", "+LOMB", "mg/mL)", "Contains:", "Lomb", "Equivalent", "GSMS",
                "Tampa,", "9355700", "BAUSCH+LOMB", "22.3", "LOMB", "(Dorzolamide", "equivalent",
                "FL", "Timolol", "HCI", "*Each", "AB48509", "Dorzolamide.", "Dorzolamide", "Bausch",
                "Hydrochloride", "BAUSCH", "60429-115-10", "60429-115", "115-10", "60429-115-05",
                "60429-115", "115-05", "24208-485-10", "24208-485", "485-10"));

        private final static Set<String> goldenPREDFORTE = new HashSet<>(Arrays.asList("FORTE",
                "PREDNISOLONE", "prednisolone", "PACIFIC", "PHARMA.", "PRED", "acetate", "ACETATE",
                "(microfine", "60758-119-15", "60758-119", "119-15", "11980-180-05", "11980-180",
                "180-05", "11980-180-10", "11980-180", "180-10"));
        private final static Set<String> PREDFORTE = new HashSet<>(Arrays.asList("freezing", "All",
                "FORTE", "Shake", "position.", "suspension)", "well", "2020", "tempertures",
                "PREDNISOLONE", "acetate", "using.", "PRED", "PHARMA.", "reserved", "up",
                "suspension,", "freezing.", "(prednisolone", "1%", "ACETATE", "suspension",
                "07940", "prednisolone", "before", "PACIFIC", "(microfine", "Madison,", "upright",
                "temperatures", "rights", "60758-119-15", "60758-119", "119-15", "11980-180-05",
                "11980-180", "180-05", "11980-180-10", "11980-180", "180-10"));

        private final static Set<String> goldenVIGAMOX = new HashSet<>(Arrays.asList("VIGAMOX°",
                "Novartis", "VIGAMOX", "Moxifloxacin", "moxifloxacin", "0065-4013-03",
                "0781-7135-93", "0065-4013", "4013-03", "0781-7135", "7135-93"));
        private final static Set<String> VIGAMOX = new HashSet<>(Arrays.asList("Moxifloxacin",
                "moxifloxacin", "5.45mg", "2016", "enclosed", "hydochioride", "Bayer", "TX", "Read",
                "VIGAMOX°", "Each", "2-25", "Novartis", "OPHTHALMIC", "2°-25°C", "5.45", "OPTHALMIC",
                "0065-4013-03", "0065-4013", "4013-03", "0781-7135-93", "0781-7135", "7135-93",
                "base", "contains:", "Licensed", "AG", "VIGAMOX"));

        private final static Set<String> goldenLATANOPROST = new HashSet<>(Arrays.asList("61314-547-01",
                "61314-547", "547-01"));
        private final static Set<String> LATANOPROST = new HashSet<>(Arrays.asList("09/2016",
                "61314-547-01", "61314-547", "547-01", "Rev.", "Texas", "125", "evening.", "ug/2.5"));

        private final static Set<String> goldenCOMBIGAN = new HashSet<>(Arrays.asList("Combigan",
                "0023-9211-05", "0023-9211", "9211-05"));
        private final static Set<String> COMBIGAN = new HashSet<>(Arrays.asList("0.2%/0.5%", "Actives:",
                "0.2%/timolol", "(0.005%)", "Combigan", "0023-9211-05", "0023-9211", "9211-05" ));

        private final static Set<String> goldenRHOPRESSA = new HashSet<>(Arrays.asList("rhopressa",
                "70727-497-99", "70727-497", " 497-99"));
        private final static Set<String> RHOPRESSA = new HashSet<>(Arrays.asList("70727-497-99",
                "70727-497", " 497-99", "0.02%", "rhopressa"));

        private final static Set<String> goldenROCKLATAN = new HashSet<>(Arrays.asList("rocklatan",
                "70727-529-99", "70727-529", "529-99"));
        private final static Set<String> ROCKLATAN = new HashSet<>(Arrays.asList("rocklatan",
                "70727-529-99", "70727-529", "529-99", "0.02%/0.005%"));
    }

    private static class UniqueWords {
        private final static Set<String> ALPHAGAN = new HashSet<>(Arrays.asList("capacity", "AlphagarP",
                "0023-9321-10", "One", "0.1%", "times", "daily", "Alphagan", "0023-9321-05",
                "three", "eye(s).", "AlphaganP"));
        private final static Set<String> DORZOLAMIDE = new HashSet<>(Arrays.asList("33637", "60429-115-10",
                "+", "2%", "APPLICATION", "|", "HCI-", "+LOMB", "mg/mL)", "Contains:", "Lomb",
                "Equivalent", "GSMS", "Tampa,", "9355700", "10", "Solution,", "24208-485-05",
                "BAUSCH+LOMB", "22.3", "LOMB", "EYE", "mg", "Incorporated", "(Dorzolamide",
                "equivalent", "24208-485-10", "FL", "Timolol", "HCI", "*Each", "AB48509",
                "60429-115-05", "Dorzolamide.", "Dorzolamide", "Bausch", "Hydrochloride", "BAUSCH"));

        private final static Set<String> PREDFORTE = new HashSet<>(Arrays.asList("freezing", "All", "an",
                "FORTE", "Shake", "11980-180-10", "60758-119-15", "position.", "suspension)",
                "well", "2020", "tempertures", "PREDNISOLONE", "acetate", "using.", "PRED", "25°C",
                "PHARMA.", "reserved", "up", "suspension,", "freezing.", "(prednisolone", "1%",
                "ACETATE", "suspension", "07940", "prednisolone", "before", "PACIFIC", "11980-180-05",
                "(microfine", "Madison,", "upright", "2014", "temperatures", "Allergan.",
                "rights", "15"));

        private final static Set<String> VIGAMOX = new HashSet<>(Arrays.asList("Moxifloxacin",
                "moxifloxacin", "5.45mg", "2003,", "2016", "enclosed", "hydochioride",
                "Bayer", "TX", "Read", "VIGAMOX°", "mg.", "Each", "2-25", "Novartis", "OPHTHALMIC",
                "2°-25°C", "as", "5.45", "OPTHALMIC", "0065-4013-03", "0781-7135-93", "base",
                "contains:", "Licensed", "AG", "3", "O8540", "VIGAMOX", "Inc,", "ONLY."));

        private final static Set<String> LATANOPROST = new HashSet<>(Arrays.asList("1", "09/2016", "eye(s)",
                "Latanoprost", "EYES", "61314-547-01", "Rev.", "Texas", "125", "evening.", "ug/2.5"));

        private final static Set<String> COMBIGAN = new HashSet<>(Arrays.asList("0.2%/0.5%", "Actives:",
                "0.2%/timolol", "(0.005%)", "Combigan", "0023-9211-05"));
        private final static Set<String> RHOPRESSA = new HashSet<>(Arrays.asList("70727-497-99", "0.02%",
                "rhopressa"));

        private final static Set<String> ROCKLATAN = new HashSet<>(Arrays.asList("rocklatan", "and",
                "opthalmic", "latanoprost", "70727-529-99", "0.02%/0.005%"));

    }
}
