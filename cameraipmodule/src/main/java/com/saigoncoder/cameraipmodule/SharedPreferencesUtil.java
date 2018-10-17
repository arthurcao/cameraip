package com.saigoncoder.cameraipmodule;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtil {

    public static void saveNestCamToken(Context context, String token){
        SharedPreferences preferences = context.getSharedPreferences("nestConfig", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public static String geNestCamToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences("nestConfig", MODE_PRIVATE);
       return preferences.getString("token", "");
    }


    public static void saveUser(Context context, int user){
        SharedPreferences preferences = context.getSharedPreferences("nestConfig", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("USER", user);
        editor.commit();
    }

    public static int getUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("nestConfig", MODE_PRIVATE);
        return preferences.getInt("USER", 0);
    }
}
