package com.saigoncoder.cameraipmodule.nestcam.mvc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ToolNetwork {


    public ToolNetwork(){}



    public static boolean checkConnectivityStatus(Context con){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(wifiInfo.isConnected() || mobileInfo.isConnected())
            {
                return true;
            }
        }
        catch(Exception e){
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }

        return false;
    }





}