package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.saigoncoder.cameraipmodule.R;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/18/18.
 */

public class MenuBrightness {

    private View layoutView;
    private Activity context;

    ImageButton btnClose;
    SeekBar brightnessBar;
    SeekBar contrastBar;
    String strDID;

    public int nBrightness = 0;
    public int nContrast = 0;

    public void setBrightness(int value){
        nBrightness = value;

    }

    public void setContrast(int value){
        nContrast = value;
    }

    public MenuBrightness(Activity activity, String did) {
        context = activity;
        View view = context.findViewById(R.id.activity_play_brightness_menu);

        layoutView = view;
        this.context = context;
        strDID = did;
        brightnessBar = (SeekBar) view.findViewById(R.id.camera_menu_brightness);
        contrastBar = (SeekBar)view.findViewById(R.id.camera_menu_contrast);
        brightnessBar.setMax(255);
        contrastBar.setMax(255);

        btnClose = (ImageButton) view.findViewById(R.id.camera_brightness_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                nBrightness = progress;
                Log.e("callback","set brightness nBrightness: " + nBrightness);
                Log.e("callback","set brightness strDID: " + strDID);
                NativeCaller.PPPPCameraControl(strDID, 1, nBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {}
        });

        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                nContrast = progress;
                NativeCaller.PPPPCameraControl(strDID, 2, nContrast);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {}
        });

    }

    public void showMenu(){
        brightnessBar.setProgress(nBrightness);
        contrastBar.setProgress(nContrast);

        Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bottom_up);
        layoutView.startAnimation(bottomUp);
        layoutView.setVisibility(View.VISIBLE);
    }

    public void hideMenu(){
        Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bottom_down);
        layoutView.startAnimation(bottomUp);
        layoutView.setVisibility(View.GONE);
    }

}
