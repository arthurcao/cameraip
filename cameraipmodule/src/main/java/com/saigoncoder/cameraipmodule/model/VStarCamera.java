package com.saigoncoder.cameraipmodule.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tiencao on 6/18/16.
 */
public class VStarCamera{

    public int id = 0;
    public String did = ""; //device id
    public String user = "";
    public String password = "";
    public String name = "";
    public int user_id = 0;


    public String toJsonData(){
        JSONObject json = new JSONObject();
        try {
            json.put("did",did);
            json.put("name",name);
            json.put("password",password);
            return json.toString();
        } catch (JSONException e) {

        }
        return "";
    }

    public static String toJsonData(String strDid, String strName, String strPwd){
        JSONObject json = new JSONObject();
        try {
            json.put("did",strDid);
            json.put("name",strName);
            json.put("password",strPwd);
            return json.toString();
        } catch (JSONException e) {

        }
        return "";
    }


    public VStarCamera(){

    }

    public final static void convertToVStarcam(){

    }
}