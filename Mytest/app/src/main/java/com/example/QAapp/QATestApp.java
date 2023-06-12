package com.example.QAapp;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public class QATestApp extends Application {

    private static Map GlobalVariable = new HashMap();

    public Object getGlobalVariable(String key) {
        return GlobalVariable.get(key);
    }

    public void setGlobalVariable(String key,String globalVariable) {
        if (GlobalVariable.containsKey(key)) {
            GlobalVariable.put(key,globalVariable);
        }else {
            GlobalVariable.put(key,globalVariable);
        }
    }
}
