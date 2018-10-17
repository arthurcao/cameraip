package com.saigoncoder.cameraipmodule.vstarcam.utils;

import android.content.Intent;
import android.util.Log;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.model.VStarCamera;
import com.saigoncoder.cameraipmodule.vstarcam.BridgeService;

import vstc2.nativecaller.NativeCaller;

public class SearchVStarCamUtils {
    private static final SearchVStarCamUtils ourInstance = new SearchVStarCamUtils();

    public static SearchVStarCamUtils getInstance() {
        return ourInstance;
    }

    private SearchVStarCamUtils() {
    }



    private class SearchThread implements Runnable {
        @Override
        public void run() {
            Log.d("tag", "startSearch");
            NativeCaller.StartSearch();
        }
    }

    public void stopSearch(){
        NativeCaller.StopSearch();
    }


    public void startSearch() {
        BridgeService.setAddCameraInterface(new BridgeService.AddCameraInterface() {
            @Override
            public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {

                VStarCamera camera = new VStarCamera();
                camera.did = strDeviceID;
                camera.name = strMac;
                camera.password = "";
                camera.user = "admin";
                if(listener != null){
                    listener.searchCameraResult(camera);
                }
            }
        });
        new Thread(new SearchThread()).start();
    }


    public void setListener(SearchVStarCamListener l){
        listener = l;
    }



    SearchVStarCamListener listener;


    public interface SearchVStarCamListener{
        void searchCameraResult(VStarCamera came);
    }

}
