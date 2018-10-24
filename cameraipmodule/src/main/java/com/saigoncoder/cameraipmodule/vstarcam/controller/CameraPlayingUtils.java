package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.content.Context;
import android.util.Log;

/**
 * Created by tiencao on 1/15/18.
 */

public class CameraPlayingUtils {
    private static final CameraPlayingUtils ourInstance = new CameraPlayingUtils();

    public static CameraPlayingUtils getInstance() {
        return ourInstance;
    }

    private String strDID = null;

    private CameraPlayingUtils() {
        MotionUtils.getInstance().setListener(new MotionUtils.MotionUtilsListener() {
            @Override
            public void tour_horizontal(boolean isTour) {
                if(mListener != null){
                    mListener.tour_horizontal(isTour);
                }
            }

            @Override
            public void tour_vertical(boolean isTour) {
                if(mListener != null){
                    mListener.tour_vertical(isTour);
                }
            }
        });

        DisplayUtils.getInstance().setListener(new DisplayUtils.DisplayUtilsListener() {
            @Override
            public void rotate_screen(int status) {
                if(mListener != null){
                    mListener.rotate_screen(status);
                }
            }
        });
    }

    public void setDID(String did){
        strDID = did;
        MotionUtils.getInstance().setDID(strDID);
        DisplayUtils.getInstance().setDID(strDID);
    }

    public void rotateScreen(int rotate){
        Log.e("cameraplaying","rotateScreen rotate: " + rotate);
        DisplayUtils.getInstance().rotateScreen(rotate);
    }

    //TODO tour horizontal: camera di chuyen ngang doc
    public void tour_horizontal(){
        Log.e("cameraplaying","tour_horizontal strDID: " + strDID);
        MotionUtils.getInstance().tour_horizontal();
    }

    //TODO tour vertical: camera di chuyen tren duoi
    public void tour_vertical(){
        MotionUtils.getInstance().tour_vertical();
    }


    public void setBrightness(Context context){
        DisplayUtils.getInstance().setBrightness(context);
    }

    public void setContrast(Context context){
        DisplayUtils.getInstance().setContrast(context);
    }

    public void setListener(CameraPlayingListener listener){
        mListener = listener;
    }

    CameraPlayingListener mListener = null;
    public interface  CameraPlayingListener{
        void tour_horizontal(boolean isTour);
        void tour_vertical(boolean isTour);
        void rotate_screen(int status);
    }


}
