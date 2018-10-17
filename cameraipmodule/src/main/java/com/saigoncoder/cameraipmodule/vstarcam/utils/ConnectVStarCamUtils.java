package com.saigoncoder.cameraipmodule.vstarcam.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.vstarcam.BridgeService;

import vstc2.nativecaller.NativeCaller;

public class ConnectVStarCamUtils {
    private static final ConnectVStarCamUtils ourInstance = new ConnectVStarCamUtils();

    public static ConnectVStarCamUtils getInstance() {
        return ourInstance;
    }

    private ConnectVStarCamUtils() {
    }

    private void startCameraPPPP() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        if(SystemValue.deviceId.toLowerCase().startsWith("vsta"))
        {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK",0);
        }else if(SystemValue.deviceId.toLowerCase().startsWith("vstd"))
        {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$",1);
        }else if(SystemValue.deviceId.toLowerCase().startsWith("vstf"))
        {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$",1);
        }
        else if(SystemValue.deviceId.toLowerCase().startsWith("vste"))
        {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL",0);
        }
        else if(SystemValue.deviceId.toLowerCase().startsWith("vstg"))
        {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018",0);
        }else if(SystemValue.deviceId.toLowerCase().startsWith("vstb")||SystemValue.deviceId.toLowerCase().startsWith("vstc"))
        {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL",0);
        }
        else {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass,1,"","",0);
        }
        //int result = NativeCaller.StartPPPP(SystemValue.deviceId, SystemValue.deviceName,
        //		SystemValue.devicePass,1,"");
        //Log.i("ip", "result:"+result);
    }

    private class StartPPPPThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                startCameraPPPP();
            } catch (Exception e) {

            }
        }
    }

    public void connectCamera(String strUID, String strUser, String strPwd) {
        Intent in = new Intent();
        in.putExtra(ContentCommon.CAMERA_OPTION, ContentCommon.ADD_CAMERA);
        in.putExtra(ContentCommon.STR_CAMERA_ID, strUID);
        in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
        in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
        in.putExtra(ContentCommon.STR_CAMERA_TYPE, ContentCommon.CAMERA_TYPE_MJPEG);
        SystemValue.deviceName = strUser;
        SystemValue.deviceId = strUID;
        SystemValue.devicePass = strPwd;
        BridgeService.setIpcamClientInterface(new BridgeService.IpcamClientInterface() {
            @Override
            public void BSMsgNotifyData(String did, int msgType, int msgParam) {
                switch (msgType) {
                    case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
                        int resid = 0;
                        switch (msgParam) {
                            case ContentCommon.PPPP_STATUS_CONNECTING://0
                                //Connecting
                                resid = R.string.pppp_status_connecting;
//                                progressBar.setVisibility(View.VISIBLE);
                                tag = 2;
                                break;
                            case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
                                //Connect failed
                                resid = R.string.pppp_status_connect_failed;
//                                progressBar.setVisibility(View.GONE);
                                tag = 0;
                                break;
                            case ContentCommon.PPPP_STATUS_DISCONNECT://4
                                //Camera disconnected
                                resid = R.string.pppp_status_disconnect;
//                                progressBar.setVisibility(View.GONE);
                                tag = 0;
                                break;
                            case ContentCommon.PPPP_STATUS_INITIALING://1
                                //Start init, begin add
                                resid = R.string.pppp_status_initialing;
//                                progressBar.setVisibility(View.VISIBLE);
                                tag = 2;
                                break;
                            case ContentCommon.PPPP_STATUS_INVALID_ID://5
                                //Wrong id
                                resid = R.string.pppp_status_invalid_id;
//                                progressBar.setVisibility(View.GONE);
                                tag = 0;
                                break;
                            case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
                                //Device online
//                                resid = R.string.pppp_status_online;
//                                progressBar.setVisibility(View.GONE);
                                String cmd="get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                                        + "&user=admin&pwd=" + SystemValue.devicePass;
                                NativeCaller.TransferMessage(did, cmd, 1);
                                tag = 1;
                                break;
                            case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
                                //Device not online
                                resid = R.string.device_not_on_line;
//                                progressBar.setVisibility(View.GONE);
                                tag = 0;
                                break;
                            case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
                                //Timeout
                                resid = R.string.pppp_status_connect_timeout;
//                                progressBar.setVisibility(View.GONE);
                                tag = 0;
                                break;
                            case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
                                //Wrong password
                                resid =R.string.pppp_status_pwd_error;
                                tag = 0;
                                break;
                            default:{
                                resid = R.string.pppp_status_unknown;
                            }
                            if(listener != null){
                                listener.connectStatus(resid);
                            }
                        }


                        if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                            NativeCaller.PPPPGetSystemParams(did,ContentCommon.MSG_TYPE_GET_PARAMS);
                            if(listener != null){
                                listener.connectSuccess();
                            }
                        }

                        if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
                                || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
                                || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
                                || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
                                || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                            NativeCaller.StopPPPP(did);
                            if(listener != null){
                                listener.connecerror();
                            }
                        }
                        break;
                    case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
                        break;

                }
                }

            @Override
            public void BSSnapshotNotify(String did, byte[] bImage, int len) {
                Log.i("ip", "BSSnapshotNotify---len"+len);
            }

            @Override
            public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {

            }

            @Override
            public void CameraStatus(String did, int status) {

            }
        });
        NativeCaller.Init();
        new Thread(new StartPPPPThread()).start();
    }

    public void stopCameraPPPP(){
        NativeCaller.StopPPPP(SystemValue.deviceId);
    }



    public void setListener(ConnectVStarCamListener l){
        listener = l;
    }

    int tag;

    ConnectVStarCamListener listener;


    public interface ConnectVStarCamListener{
        void connectStatus(int resid);
        void connectSuccess();
        void connecerror();
    }

}
