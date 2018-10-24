package com.saigoncoder.cameraipmodule.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.saigoncoder.cameraipmodule.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by tiencao on 6/18/16.
 */
public class VStarCamera{

    public int id = 0;
    public String did = ""; //device id, token
    public String user = "";
    public String password = "";
    public String name = "";
    public int user_id = 0;


    @Override
    public String toString() {
        String str = "{" +
                    "id: " + id + "," +
                    "name: " + name + "," +
                    "password: " + password + "," +
                    "did: " + did +
                "}";
        return str;
    }

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

    public final static VStarCamera convertToVStarcam(CameraDB cameraDB){
//        CameraDB camera = new CameraDB();
//        camera.data = data;
//        camera.user = SharedPreferencesUtil.getUser(context);
//        camera.token = strDID;
//        camera.type = CameraManager.TYPE_VSTARCAM;
        VStarCamera camera = new VStarCamera();
        camera.id = cameraDB.id;
        camera.user_id = cameraDB.user;
        camera.did = cameraDB.token;

        String respone = cameraDB.data;
        Object json = null;
        try {
            json = new JSONTokener(respone).nextValue();
            if(json instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)json;
                camera.did = jsonObject.getString("did");
                camera.name = jsonObject.getString("name");
                camera.password = jsonObject.getString("password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return camera;
    }
}