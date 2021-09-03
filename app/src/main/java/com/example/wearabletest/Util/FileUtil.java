package com.example.wearabletest.Util;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    private static final String FILE_UTIL_LOG = "FILE_UTIL_LOG";

    public static File createFile(String name){
        File newFile = new File(name);
        if (newFile.exists()){
            newFile.delete();
        }
        try{
            newFile.createNewFile();
        } catch (IOException e){
            Log.d(FILE_UTIL_LOG, "Unable to create file: " + e.getMessage());
        }
        return newFile;
    }
}
