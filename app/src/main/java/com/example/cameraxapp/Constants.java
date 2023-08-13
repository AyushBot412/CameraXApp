package com.example.cameraxapp;

//TODO: Implement NDC

public final class Constants {
    private Constants() {

    }
    enum BottleTypes_All {
        ALPHAGAN,
        COMBIGAN,
        DORZOLAMIDE_BLUE,
        DORZOLAMIDE_ORANGE,
        LATANOPROST,
        PREDFORTE,
        PREDFORTE_OFF_BRAND,
        RHOPRESSA,
        ROCKLATAN,
        VIGAMOX,
        VIGAMOX_OFF_BRAND,

        NULL;

        boolean isEqual(BottleType type) {
            switch (this) {
                case ALPHAGAN:
                    return type == BottleType.ALPHAGAN;
                case COMBIGAN:
                    return type == BottleType.COMBIGAN;
                case DORZOLAMIDE_BLUE:
                case DORZOLAMIDE_ORANGE:
                    return type == BottleType.DORZOLAMIDE;
                case LATANOPROST:
                    return type == BottleType.LATANOPROST;
                case PREDFORTE:
                case PREDFORTE_OFF_BRAND:
                    return type == BottleType.PREDFORTE;
                case RHOPRESSA:
                    return type == BottleType.RHOPRESSA;
                case ROCKLATAN:
                    return type == BottleType.ROCKLATAN;
                case VIGAMOX:
                case VIGAMOX_OFF_BRAND:
                    return type == BottleType.VIGAMOX;
                case NULL:
                    return type == BottleType.NULL;
            }
            return false;
        }

    }
    enum BottleType {
        ALPHAGAN,
        COMBIGAN,
        DORZOLAMIDE,
        LATANOPROST,
        PREDFORTE,
        RHOPRESSA,
        ROCKLATAN,
        VIGAMOX,
        NULL

    }
}
