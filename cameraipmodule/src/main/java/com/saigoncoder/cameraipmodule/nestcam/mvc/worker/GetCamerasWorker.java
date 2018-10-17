package com.saigoncoder.cameraipmodule.nestcam.mvc.worker;

import android.content.Context;
import android.util.Log;


import com.saigoncoder.cameraipmodule.model.NestCamera;
import com.saigoncoder.cameraipmodule.nestcam.mvc.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tiencao on 9/23/15.
 */
public class GetCamerasWorker extends Worker<ArrayList<NestCamera>>{
    Context mContext;
    String access_token;
    ArrayList<NestCamera> cameras;

    int error = 0;
    public GetCamerasWorker(Context c, String access_token){
        mContext = c;
        this.access_token = access_token;
        cameras = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "https://developer-api.nest.com/?auth=" + access_token;
        Log.e("nestcam","GetCamerasWorker url: " + url);
        String response = HttpUtils.getURL(url);
        Log.e("nestcam","GetCamerasWorker response: " + response);
        if(response.contains("unauthorized")){
            //return
            error = 1;
            return super.doInBackground(params);
        }
        JSONObject reader;
        try {
            reader = new JSONObject(response);
            JSONObject devices = reader.getJSONObject("devices");
            JSONObject cameras = devices.getJSONObject("cameras");
            Iterator<String> iter = cameras.keys();

            while (iter.hasNext()){
                String key = iter.next();
                JSONObject camera = cameras.getJSONObject(key);
                String name = camera.getString("name");
                String device_id = camera.getString("device_id");
                String where_name = camera.getString("where_name");
                String name_long = camera.getString("name_long");
                String web_url = camera.getString("web_url");
                String app_url = camera.getString("app_url");

                NestCamera nestCamera = new NestCamera();
                nestCamera.name = name;
                nestCamera.device_id = device_id;
                nestCamera.where_name = where_name;
                nestCamera.name_long = name_long;
                nestCamera.web_url = web_url;
                nestCamera.app_url = app_url;
                Log.e("nestcam","GetCamerasWorker nestCamera: " + nestCamera.toString());
                this.cameras.add(nestCamera);

            }

            error = 0;
        } catch (JSONException e) {

        }

        return super.doInBackground(params);

    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(callback != null){
            if(error == 0){
                callback.result(cameras);
            }else{
                callback.fail();
            }
        }
    }

    @Override
    public void setCallback(WorkerCallback<ArrayList<NestCamera>> callback) {
        this.callback = callback;
    }

}
