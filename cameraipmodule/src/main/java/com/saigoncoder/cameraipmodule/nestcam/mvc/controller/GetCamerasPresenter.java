package com.saigoncoder.cameraipmodule.nestcam.mvc.controller;

import android.content.Context;


import com.saigoncoder.cameraipmodule.model.NestCamera;
import com.saigoncoder.cameraipmodule.nestcam.mvc.view.iView;
import com.saigoncoder.cameraipmodule.nestcam.mvc.worker.GetCamerasWorker;
import com.saigoncoder.cameraipmodule.nestcam.mvc.worker.WorkerCallback;

import java.util.ArrayList;


/**
 * Created by tiencao on 9/23/15.
 */
public class GetCamerasPresenter extends iPresenter<ArrayList<NestCamera>> {

    Context mContext;
    public GetCamerasPresenter(Context c){
        mContext = c;
    }



    public void get(String token){
//        Log.e("test", "loadHistoryDatabase");
        GetCamerasWorker presenter = new GetCamerasWorker(mContext, token);
        presenter.setCallback(new WorkerCallback<ArrayList<NestCamera>>() {
            @Override
            public void result(ArrayList<NestCamera> nestCameras) {
                if(callback != null){
                    callback.response(nestCameras);
                }
            }

            @Override
            public void fail() {
                if(callback != null){
                    callback.error(0);
                }
            }
        });
        presenter.execute();

    }

    @Override
    public void setPresenterListener(iView<ArrayList<NestCamera>> callback) {
        this.callback = callback;
    }
}
