package com.saigoncoder.cameraipmodule.model;

/**
 * Created by tiencao on 6/18/16.
 */
public class CameraDB {
    public int id;
    public String token; //did vscam, token nestcam
    public int user = 0; //id user
    public int type = 0; //type, 0: vscam, 1: nestcam
    public String data; //datajson

}
