package com.saigoncoder.cameraipmodule.nestcam.mvc.controller;

import android.content.Context;

import com.saigoncoder.cameraipmodule.nestcam.mvc.view.iView;
import com.saigoncoder.cameraipmodule.nestcam.mvc.worker.GetAccessTokenWorker;
import com.saigoncoder.cameraipmodule.nestcam.mvc.worker.WorkerCallback;


/**
 * Created by tiencao on 9/23/15.
 */
public class GetAccessTokenPresenter extends iPresenter<String> {

    Context mContext;
    public GetAccessTokenPresenter(Context c){
        mContext = c;
    }



    public void get(String code){
//        Log.e("test", "loadHistoryDatabase");
        GetAccessTokenWorker presenter = new GetAccessTokenWorker(mContext, code);
        presenter.setCallback(new WorkerCallback<String>() {
            @Override
            public void result(String code) {
                if(callback != null){
                    callback.response(code);
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
