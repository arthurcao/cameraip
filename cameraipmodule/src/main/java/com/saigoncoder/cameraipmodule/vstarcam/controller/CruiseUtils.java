package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/20/18.
 */

public class CruiseUtils {

    private String strDID;
    Activity activity;
    View layout;
    public boolean isShow;

    View btnTourVertical;
    View btnTourHorizontal;

    TextView btnFlipVertical;
    TextView btnFlipHorizontal;

    View btnClose;

    boolean isTourHorizontal = false;
    boolean isTourVertical = false;

    private boolean m_bUpDownMirror;
    private boolean m_bLeftRightMirror;


    public CruiseUtils(String did, Activity activity){
        this.activity = activity;
        strDID = did;
        layout = activity.findViewById(R.id.activity_play_cruise);

        btnTourVertical = (View) layout.findViewById(R.id.camera_play_cruise_tour_vertical);
        btnTourHorizontal = (View) layout.findViewById(R.id.camera_play_cruise_tour_horizontal);

        btnFlipVertical = (TextView) layout.findViewById(R.id.camera_play_cruise_flip_vertical);
        btnFlipHorizontal = (TextView) layout.findViewById(R.id.camera_play_cuise_flip_horizontal);

        btnFlipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value;
                if (m_bUpDownMirror) {

                    if (m_bLeftRightMirror) {
                        value = 2;
                    } else {
                        value = 0;
                    }
                } else {
                    if (m_bLeftRightMirror) {
                        value = 3;
                    } else {
                        value = 1;
                    }
                }
                m_bUpDownMirror = !m_bUpDownMirror;
                NativeCaller.PPPPCameraControl(strDID, 5, value);
            }
        });

        btnFlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value1;
                if (m_bLeftRightMirror) {
                    if (m_bUpDownMirror) {
                        value1 = 1;
                    } else {
                        value1 = 0;
                    }
                } else {
                    if (m_bUpDownMirror) {
                        value1 = 3;
                    } else {
                        value1 = 2;
                    }
                }
                m_bLeftRightMirror = !m_bLeftRightMirror;
                NativeCaller.PPPPCameraControl(strDID, 5, value1);
            }
        });


        btnClose = (View)layout.findViewById(R.id.camera_play_cruise_tour_close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });


        btnTourVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTourVertical){
                    NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP_DOWN_STOP);
                }else{
                    NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP_DOWN);
                }
                isTourVertical = !isTourVertical;
            }
        });

        btnTourHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTourHorizontal){
                    NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT_RIGHT_STOP);
                }else{
                    NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT_RIGHT);
                }
                isTourHorizontal = !isTourHorizontal;
            }
        });
    }

    public void showMenu(){
        isShow = true;
        Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.bottom_up);
        layout.startAnimation(bottomUp);
        layout.setVisibility(View.VISIBLE);

    }

    public void hideMenu(){
        isShow = false;
        Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.bottom_down);
        layout.startAnimation(bottomUp);
        layout.setVisibility(View.GONE);
    }



    public void initFlip(int flip){
        int what = flip;
        switch (what)
        {
            case 0:
                m_bUpDownMirror = false;
                m_bLeftRightMirror = false;
                btnFlipVertical.setSelected(false);
                btnFlipHorizontal.setSelected(false);

                break;
            case 1:
                m_bUpDownMirror = true;
                m_bLeftRightMirror = false;
                btnFlipHorizontal.setSelected(false);
                btnFlipVertical.setSelected(true);

                break;
            case 2:
                m_bUpDownMirror = false;
                m_bLeftRightMirror = true;
                btnFlipHorizontal.setSelected(true);
                btnFlipVertical.setSelected(false);
                break;
            case 3:
                m_bUpDownMirror = true;
                m_bLeftRightMirror = true;
                btnFlipVertical.setSelected(true);
                btnFlipHorizontal.setSelected(true);
                break;
            default:
                break;
        }
    }



}
