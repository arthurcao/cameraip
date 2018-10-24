package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.util.Log;


import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/16/18.
 */

class MotionUtils {
    private static final MotionUtils ourInstance = new MotionUtils();

    static MotionUtils getInstance() {
        return ourInstance;
    }

    private MotionUtils() {
    }

    private String strDID;
    public void setDID(String did){
        strDID = did;
    }

    boolean isTourHorizontal = false;
    public void tour_horizontal(){
        Log.e("cameraplaying","tour_horizontal strDID: " + strDID);

        if(isTourHorizontal){
            NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT_RIGHT_STOP);
        }else{
            NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT_RIGHT);
        }
        isTourHorizontal = !isTourHorizontal;
        if(mListener != null){
            mListener.tour_horizontal(isTourHorizontal);
        }

    }

    //TODO tour vertical: camera di chuyen tren duoi
    boolean isTourVertical = false;
    public void tour_vertical(){
        if(isTourVertical){
            NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP_DOWN_STOP);
        }else{
            NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP_DOWN);
        }
        isTourVertical = !isTourVertical;
        if(mListener != null){
            mListener.tour_vertical(isTourVertical);
        }
    }
    public void setListener(MotionUtilsListener listener){
        mListener = listener;
    }

    MotionUtilsListener mListener = null;
    public interface  MotionUtilsListener{
        void tour_horizontal(boolean isTour);
        void tour_vertical(boolean isTour);
    }
}
