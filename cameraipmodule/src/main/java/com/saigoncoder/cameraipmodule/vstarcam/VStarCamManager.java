package com.saigoncoder.cameraipmodule.vstarcam;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.saigoncoder.cameraipmodule.LOG;
import com.saigoncoder.cameraipmodule.SharedPreferencesUtil;
import com.saigoncoder.cameraipmodule.db.MySQLiteDataSource;
import com.saigoncoder.cameraipmodule.model.CameraDB;
import com.saigoncoder.cameraipmodule.model.CameraManager;

import java.util.ArrayList;

import vstc2.nativecaller.NativeCaller;

public class VStarCamManager {
    private static final VStarCamManager ourInstance = new VStarCamManager();

    public static VStarCamManager getInstance() {
        return ourInstance;
    }

    private boolean isReady = false;
    private Context context;
    MySQLiteDataSource sqLiteDataSource;

    private VStarCamManager() {
    }

    public void init(Context c){
        this.context = c;
        sqLiteDataSource = new MySQLiteDataSource(context);
        Intent intent = new Intent();
        intent.setClass(context, BridgeService.class);
        context.startService(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
                    Thread.sleep(3000);
                    NativeCaller.SetAPPDataPath(context.getFilesDir().getAbsolutePath());
                    LOG.e("VStartcam ready");
                    isReady = true;
                } catch (Exception e) {
                    isReady = false;

                }
                responseReady();
            }
        }).start();
    }


    public void updateVStarcamPassword(CameraDB camera){
        sqLiteDataSource.updateCamera(camera);
    }

    public void deleteVStarcam(int id){
        sqLiteDataSource.deleteCamera(id);
    }

    public void stopVStarcamService(){
        Intent intent = new Intent();
        intent.setClass(context, BridgeService.class);
        context.stopService(intent);
    }

    public void getListCamera(){
        int user_id = SharedPreferencesUtil.getUser(context);
        ArrayList<CameraDB> list = sqLiteDataSource.getCameras(user_id,CameraManager.TYPE_VSTARCAM);
        responseGetListCameras(list);
    }



    public void responseReady(){
        if(listener != null){
            listener.vstarcamReady(isReady);
        }
    }

    public void responseGetListCameras(ArrayList<CameraDB> list){
        if(listener != null){
            listener.getCamerasResponse(list);
        }
    }

    public void setVStarCamListener(VStarCamListener l){
        listener = l;
    }

    public interface VStarCamListener{
        void vstarcamReady(boolean ready);
        void getCamerasResponse(ArrayList<CameraDB> list);
    }

    private VStarCamListener listener;
}
