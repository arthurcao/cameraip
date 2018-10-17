package com.saigoncoder.cameraipmodule.nestcam.mvc.worker;

import android.content.Context;


import com.saigoncoder.cameraipmodule.nestcam.mvc.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tiencao on 9/23/15.
 */
public class GetAccessTokenWorker extends Worker<String>{
    Context mContext;
    String code;
    String access_token = "";
    public GetAccessTokenWorker(Context c, String code){
        mContext = c;
        this.code = code;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "https://iot.kooltechs.com/nest/getToken?state=STATE&code=" + code;

        String response = HttpUtils.getURL(url);
        JSONObject reader;
        try {
            reader = new JSONObject(response);
            JSONObject token = reader.getJSONObject("token");
            access_token = token.getString("access_token");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return super.doInBackground(params);

    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(callback != null){
            callback.result(access_token);
        }
    }

    @Override
    public void setCallback(WorkerCallback<String> callback) {
        this.callback = callback;
    }

}
