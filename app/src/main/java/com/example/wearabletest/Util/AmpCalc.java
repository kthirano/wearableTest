package com.example.wearabletest.Util;

public class AmpCalc {
    public static float dbCount = 40;
    private static float prevDb = dbCount;
    private static float min = 0.5f;
    private static float value = 0;
    public static void setDbCount(float dbValue) {
        if (dbValue > prevDb) {
            value = dbValue - prevDb > min ? dbValue - prevDb : min;
        }
        else {
            value = dbValue - prevDb < -min ? dbValue - prevDb : -min;
        }
        dbCount = prevDb + value * 0.2f;
        prevDb = dbCount;
    }
}
