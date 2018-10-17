package com.saigoncoder.cameraipmodule.nestcam.mvc.controller;

import android.content.Context;

import com.saigoncoder.cameraipmodule.nestcam.mvc.view.iView;
import com.saigoncoder.cameraipmodule.nestcam.mvc.worker.GetCameraUrlWorker;
import com.saigoncoder.cameraipmodule.nestcam.mvc.worker.WorkerCallback;


/**
 * Created by tiencao on 9/23/15.
 */
public class GetCameraUrlPresenter extends iPresenter<String> {

    Context mContext;
    public GetCameraUrlPresenter(Context c){
        mContext = c;
    }



    public void get(String token, String deviceid){
//        Log.e("test", "loadHistoryDatabase");
        GetCameraUrlWorker presenter = new GetCameraUrlWorker(mContext, token, deviceid);
        presenter.setCallback(new WorkerCallback<String>() {
            @Override
            public void result(String s) {
                if(callback != null){
                    callback.response(s);
                }
            }

            @Override
            public void fail() {

            }
        });
        presenter.execute();

    }

    @Override
    public void setPresenterListener(iView<String> callback) {
        this.callback = callback;
    }
}
