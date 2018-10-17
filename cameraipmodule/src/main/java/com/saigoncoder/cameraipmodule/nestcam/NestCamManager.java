package com.saigoncoder.cameraipmodule.nestcam;

import android.content.Context;
import android.content.Intent;

import com.saigoncoder.cameraipmodule.LOG;
import com.saigoncoder.cameraipmodule.SharedPreferencesUtil;
import com.saigoncoder.cameraipmodule.model.NestCamera;
import com.saigoncoder.cameraipmodule.nestcam.mvc.controller.GetCamerasPresenter;
import com.saigoncoder.cameraipmodule.nestcam.mvc.view.iView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class NestCamManager {
    //Nest
    //myproxemail2018@gmail.com
    //Irvine!1

    private static final NestCamManager ourInstance = new NestCamManager();

    public static NestCamManager getInstance() {
        return ourInstance;
    }

    private NestCamManager() {
    }


    public void init(Context c){
        context = c;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOGIN_CODE) {
            if(resultCode == RESULT_OK){
                //Login successful
                String token = data.getStringExtra("ACCESS_TOKEN");
                SharedPreferencesUtil.saveNestCamToken(context,token);
                isLogin = true;
                responseActionLogin();
            }else{
                //Login failed
                isLogin = false;
                responseActionLogin();
            }
        }
    }

    public void getCameraList(){
        String token = SharedPreferencesUtil.geNestCamToken(context);
        LOG.e("token: " + token);
        if(token.length() > 0){
            //Get list camera
            GetCamerasPresenter presenter = new GetCamerasPresenter(context);
            presenter.setPresenterListener(new iView<ArrayList<NestCamera>>() {
                @Override
                public void response(ArrayList<NestCamera> nestCameras) {
                    //Update list camera

                    isLogin = true;
                    LOG.e("isLogin1: " + isLogin);
                    responseLogin();
                    responseGetListCam(nestCameras);

                }

                @Override
                public void error(int error) {
                    isLogin = false;
                    LOG.e("isLogin2: " + isLogin);
                    responseLogin();
                }
            });
            presenter.get(token);
        }else{
            isLogin = false;
            LOG.e("isLogin3: " + isLogin);
            responseLogin();
        }
    }

    private void responseActionLogin(){
        if(listener != null){
            listener.loginResult(isLogin);
        }
    }


    private void responseLogin(){
        if(listener != null){
            listener.updateLoginStatus(isLogin);
        }
    }

    private void responseGetListCam(ArrayList<NestCamera> nestCameras){
        if(listener != null){
            listener.nestCamResponse(nestCameras);
        }
    }

    public void setListener(NestCamListener l){
        listener = l;
    }

    public interface NestCamListener{
        void nestCamResponse(ArrayList<NestCamera> nestCameras);
        void updateLoginStatus(boolean isLogin);
        void loginResult(boolean islogin);
    }



    private boolean isLogin = false;
    private Context context;
    private int user_id = 0;
    private NestCamListener listener;

    public final static int REQUEST_LOGIN_CODE = 1;


}
