package com.saigoncoder.cameraipmodule.vstarcam.utils;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;

public class InternetUtil {

    //<uses-permission android:name="android.permission.INTERNET" />
   // <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
   // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    private static final InternetUtil ourInstance = new InternetUtil();

    public static InternetUtil getInstance() {
        return ourInstance;
    }
    WifiManager wifiManager;

    private InternetUtil() {

    }


    public void setContext(Context c){
        context = c;
        wifiManager = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private Context context;
    private boolean connected = false;

    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected())
        {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected())
        {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            return true;
        }
        return false;
    }


    //======== check coarse location
    final static int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    public void checkPermisionCoasrseLocationGranted(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

                if(listener != null){
                    listener.startRequestCoasrseLocationGranted();
                }
            }else{
                if(listener != null){
                    listener.checkCoasrseLocationGranted(true);
                    startScanListWifi();
                }
            }
        }else{
            startScanListWifi();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
//        @Override
//        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//            InternetUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
//        }
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(listener != null){
                    listener.checkCoasrseLocationGranted(true);
                }
            }else{
                if(listener != null){
                    listener.checkCoasrseLocationGranted(false);
                }
            }
            startScanListWifi();
        }
    }

    public void startScanListWifi(){

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }

    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        if(listener != null){
            WifiInfo info = wifiManager.getConnectionInfo();
            String mac = info.getMacAddress();
            String ssid = info.getSSID();
            String bssid = info.getBSSID();
            ssid = ssid.replace("\"","");
            int ip = info.getIpAddress();
            String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));

            listener.scanListResultWifi(results,ipAddress,ssid,mac,bssid);
        }
    }

    private void scanFailure() {
        List<ScanResult> results = wifiManager.getScanResults();
        if(listener != null){
            WifiInfo info = wifiManager.getConnectionInfo();
            String mac = info.getMacAddress();
            String ssid = info.getSSID();
            String bssid = info.getBSSID();
            ssid = ssid.replace("\"","");
            int ip = info.getIpAddress();
            String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));
            listener.scanListResultWifi(results,ipAddress,ssid,mac,bssid);
        }
    }

    private BroadcastReceiver broadcastReceiver;
    public void registerReceiver(){
        if(context == null){
            return;
        }
        if(broadcastReceiver == null) {
             broadcastReceiver = new BroadcastReceiver() {
                 @Override
                 public void onReceive(Context ct, Intent intent) {
                     final String action = intent.getAction();
                     Log.e("test1223", "broadcastReceiver: " + action );
                     boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                     if (success) {
                         scanSuccess();
                     } else {
                         // scan failure handling
                         scanFailure();
                     }

                     if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                        if (intent.hasExtra("wifi_state")) {
                            int wifistate = intent.getIntExtra("wifi_state", 1);
                            if (wifistate == 1) {
                                //Disconnected
                                if (listener != null) {
                                    listener.wifiDisconnected();
                                }


                            }else if (wifistate == 3) {
                                final WifiManager wifiManager = (WifiManager) ct.getSystemService(Context.WIFI_SERVICE);
                                // Need to wait a bit for the SSID to get picked up;

                                WifiInfo info = wifiManager.getConnectionInfo();
                                String mac = info.getMacAddress();
                                String ssid = info.getSSID();
                                String bssid = info.getBSSID();
                                int ip = info.getIpAddress();
                                 String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                                        (ip >> 24 & 0xff));
                                if (listener != null) {
                                    ssid = ssid.replace("\"","");
                                    listener.wifiConnected(ipAddress,ssid,mac,bssid);
                                }
                            }
                        }
                     }


                 }


             };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(broadcastReceiver, intentFilter);
        }
    }




    public void unregisterReceiver(){
        if(context == null){
            return;
        }
        if(broadcastReceiver != null){
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private InternetUtilListener listener;
    public void setListener(InternetUtilListener l){
        listener = l;
    }
    public interface InternetUtilListener{
        void wifiDisconnected();
        void wifiConnected(String ip, String ssid, String mac, String bssid);
        void checkCoasrseLocationGranted(boolean isPermission);
        void startRequestCoasrseLocationGranted();
        void scanListResultWifi(List<ScanResult> results,String ip, String ssid, String mac, String bssid);
    }
}