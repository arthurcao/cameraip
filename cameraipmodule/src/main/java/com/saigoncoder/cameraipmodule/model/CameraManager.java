package com.saigoncoder.cameraipmodule.model;

import java.util.ArrayList;

public class CameraManager {

    public boolean nestCamIsLogin = false;
    public String nestCamHeader = "Nest Camera";
    public String nestCamAdd = "ADD";
    public String nestCamLogin = "LOGIN";
    public ArrayList<NestCamera> nestCameras = new ArrayList<>();


    public String vstarcamHeader = "VStarcam";
    public String vstarcamAdd = "Add";
    public boolean vstarcamReady = false;
    public ArrayList<VStarCamera> vStarCameras = new ArrayList<>();




    public static final int TYPE_VSTARCAM = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_VSCAM_BTN_ADD = 3;
    public static final int TYPE_VSCAM_BTN_CONFIG_WIFI = 7;
    public static final int TYPE_NEST_CAM = 4;
    public static final int TYPE_NEST_CAM_BTN_ADD = 5;
    public static final int TYPE_NEST_CAM_BTN_LOGIN = 6;





}
