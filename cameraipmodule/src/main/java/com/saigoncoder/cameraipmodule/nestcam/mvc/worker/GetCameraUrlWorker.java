package com.saigoncoder.cameraipmodule.nestcam.mvc.worker;

import android.content.Context;
import android.util.Log;

import com.saigoncoder.cameraipmodule.model.NestCamera;
import com.saigoncoder.cameraipmodule.nestcam.mvc.HttpUtils;

import java.util.ArrayList;


/**
 * Created by tiencao on 9/23/15.
 */
public class GetCameraUrlWorker extends Worker<String>{
    //Get camera link from token and device id
    Context mContext;
    String access_token;
    String device_id;
    String responseLink = "";

    ArrayList<NestCamera> cameras;
    public GetCameraUrlWorker(Context c, String access_token, String device_id){
        mContext = c;
        this.access_token = access_token;
        this.device_id = device_id;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "https://developer-api.nest.com/devices/cameras/"+device_id+"/web_url?auth=" + access_token;
        Log.e("nestcamera","GetCameraUrlWorker url: " + url);
        responseLink = HttpUtils.getURL(url);
        Log.e("nestcamera","GetCameraUrlWorker responseLink: " + responseLink);

        return super.doInBackground(params);

    }





    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(callback != null){
            callback.result(responseLink);
        }
    }

    @Override
    public void setCallback(WorkerCallback<String> callback) {
        this.callback = callback;
    }

}
