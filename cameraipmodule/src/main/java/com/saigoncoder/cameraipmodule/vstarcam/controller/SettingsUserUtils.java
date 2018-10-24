package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.os.Handler;
import android.util.Log;


import com.saigoncoder.cameraipmodule.vstarcam.BridgeService;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/16/18.
 */

public class SettingsUserUtils {
    private static final SettingsUserUtils ourInstance = new SettingsUserUtils();

    public static SettingsUserUtils getInstance() {
        return ourInstance;
    }

    private SettingsUserUtils() {
    }


    private String operatorName="";
    private String operatorPwd="";
    private String visitorName="";
    private String visitorPwd="";
    private String adminName="";
    private String adminPwd="";
    private String strDID;
    private final int FAILED=0;
    private final int SUCCESS=1;
    private final int PARAMS=3;


    public void setDID(String did) {
        strDID = did;
        setListenerInterface();
        getParameterUser();
    }

    public void getParameterUser(){
        NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_PARAMS);
    }
    public void setListenerInterface() {
        BridgeService.setUserInterface(new BridgeService.UserInterface() {
            @Override
            public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {
                adminName = user3;
                adminPwd = pwd3;
                operatorName = user2;
                operatorPwd = pwd2;
                mHandler.sendEmptyMessage(PARAMS);
            }

            @Override
            public void callBackSetSystemParamsResult(String did, int paramType, int result) {
                mHandler.sendEmptyMessage(result);
            }

            @Override
            public void callBackPPPPMsgNotifyData(String did, int type, int param) {

            }
        });
    }


    public void setUser(String user, String password){
        adminName = user;
        adminPwd = password;

        Log.e("setUser","visitorName: " + visitorName);
        Log.e("setUser","visitorPwd: " + visitorPwd);
        Log.e("setUser","operatorName: " + operatorName);
        Log.e("setUser","operatorPwd: " + operatorPwd);
        Log.e("setUser","adminName: " + adminName);
        Log.e("setUser","adminPwd: " + adminPwd);

        NativeCaller.PPPPUserSetting(strDID, visitorName, visitorPwd, operatorName, operatorPwd, adminName, adminPwd);
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FAILED://set failed
                    if(mListener != null){
                        mListener.set_failed();
                    }
                    break;
                case SUCCESS://set success

                    NativeCaller.PPPPRebootDevice(strDID);
                    Log.d("info","user:"+ adminName+" pwd:"+adminPwd);

                    if(mListener != null){
                        mListener.set_success();
                    }
                    break;
                case PARAMS://get user params
                    if(mListener != null){
                        mListener.get_user_params(adminName,adminPwd);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public void setListener(SettingUtilsListener listener){
        mListener = listener;
    }

    SettingUtilsListener mListener = null;
    public interface  SettingUtilsListener{
        void get_user_params(String user, String password);
        void set_success();
        void set_failed();
    }

}
