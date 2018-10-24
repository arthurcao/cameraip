package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.SeekBar;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/16/18.
 */

public class DisplayUtils {
    private static final DisplayUtils ourInstance = new DisplayUtils();

    public static DisplayUtils getInstance() {
        return ourInstance;
    }

    private DisplayUtils() {
    }

    private String strDID;
    public void setDID(String did){
        strDID = did;
    }


    //TODO rotate screen
    public final static int ROTATE_0 = 0;
    public final static int ROTATE_0_FLIP_VERTICAL = 2;
    public final static int ROTATE_180 = 3;
    public final static int ROTATE_180_FLIP_VERTICAL = 1;

    public void rotateScreen(int rotate){
        Log.e("cameraplaying","rotateScreen rotate: " + rotate);
        NativeCaller.PPPPCameraControl(strDID, 5, rotate);
        if(mListener != null){
            mListener.rotate_screen(rotate);
        }
    }

    //TODO brightness
    public int nBrightness = 0;
    public void setBrightness(Context context){

        AlertDialog.Builder alert = new AlertDialog.Builder(context );
        final SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(255);
        seekBar.setProgress(nBrightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int BRIGHT = 1;
                nBrightness = progress;
                NativeCaller.PPPPCameraControl(strDID, BRIGHT, nBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {}
        });
        alert.setView(seekBar);
        alert.setCancelable(true);
        alert.setMessage("Set brightness");
        alert.show();

    }

    //TODO contrast
    public int nContrast = 0;
    public void setContrast(Context context){

        AlertDialog.Builder alert = new AlertDialog.Builder(context );
        final SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(255);
        seekBar.setProgress(nContrast);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int CONTRAST = 2;
                nContrast = progress;
                NativeCaller.PPPPCameraControl(strDID, CONTRAST, nContrast);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {}
        });
        alert.setView(seekBar);
        alert.setCancelable(true);
      //  alert.setPositiveButton("Close", null);
        alert.setMessage("Set Contrast");
        alert.show();

    }

    public void setListener(DisplayUtilsListener listener){
        mListener = listener;
    }

    DisplayUtilsListener mListener = null;
    public interface  DisplayUtilsListener{
        void rotate_screen(int status);
    }

}
