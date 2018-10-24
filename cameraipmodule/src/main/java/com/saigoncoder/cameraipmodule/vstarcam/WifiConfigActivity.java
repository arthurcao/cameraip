package com.saigoncoder.cameraipmodule.vstarcam;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.utils.ProgressDialog;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;
import com.saigoncoder.cameraipmodule.vstarcam.utils.InternetUtil;

import java.util.ArrayList;
import java.util.List;

import voice.encoder.DataEncoder;
import voice.encoder.VoicePlayer;
import vstc2.nativecaller.NativeCaller;


public class WifiConfigActivity extends AppCompatActivity implements OnClickListener {


    private VoicePlayer player = new VoicePlayer();


    private EditText wifi_name,wifi_pwd;
    private Button sure,cancel;

    private String sendMac = null;
    private String wifiName;
    private String currentBssid;
    private String currentSSID = "";
    private String currentMac = "";

    private WifiManager mWifiManager;

    private String mConnectedSsid = "";
    private String mConnectedPassword;
    private String mAuthString;
    private byte mAuthMode;
    private byte AuthModeOpen = 0x00;
    private byte AuthModeShared = 0x01;
    private byte AuthModeAutoSwitch = 0x02;
    private byte AuthModeWPA = 0x03;
    private byte AuthModeWPAPSK = 0x04;
    private byte AuthModeWPANone = 0x05;
    private byte AuthModeWPA2 = 0x06;
    private byte AuthModeWPA2PSK = 0x07;
    private byte AuthModeWPA1WPA2 = 0x08;
    private byte AuthModeWPA1PSKWPA2PSK = 0x09;

    Handler handler = new Handler();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_config);
        context = this;

        findView();


        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable home_btn = getResources().getDrawable(R.drawable.ic_back_home_orange);
            getSupportActionBar().setHomeAsUpIndicator(home_btn);
            int color_orange = getResources().getColor(R.color.orange);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + color_orange + "\">" + "Wifi Configuration" + "</font>"));
        }



        InternetUtil.getInstance().setContext(this);
        InternetUtil.getInstance().setListener(new InternetUtil.InternetUtilListener() {
            @Override
            public void wifiDisconnected() {}

            @Override
            public void wifiConnected(String ip, final String ssid, String mac, String bssid) {}

            @Override
            public void checkCoasrseLocationGranted(boolean isPermission) { }

            @Override
            public void startRequestCoasrseLocationGranted() {}

            @Override
            public void scanListResultWifi(List<ScanResult> results,String ip,  String ssid, String mac, String bssid) {

                currentBssid = bssid;
                currentSSID = ssid;
                currentMac = mac;
                mConnectedSsid = ssid;
                wifi_name.setText(mConnectedSsid);
                int iLen = mConnectedSsid.length();
                if (mConnectedSsid.startsWith("\"") && mConnectedSsid.endsWith("\"")) {
                    mConnectedSsid = mConnectedSsid.substring(1, iLen - 1);
                }

                ArrayList<String> mList = new ArrayList<String>();
                List<ScanResult> ScanResultlist = results;
                for (int i = 0, len = ScanResultlist.size(); i < len; i++) {
                    ScanResult AccessPoint = ScanResultlist.get(i);
                    mList.add(AccessPoint.BSSID);
                    if (AccessPoint.SSID.equals(mConnectedSsid)) {
                        boolean WpaPsk = AccessPoint.capabilities.contains("WPA-PSK");
                        boolean Wpa2Psk = AccessPoint.capabilities.contains("WPA2-PSK");
                        boolean Wpa = AccessPoint.capabilities.contains("WPA-EAP");
                        boolean Wpa2 = AccessPoint.capabilities.contains("WPA2-EAP");

                        if (AccessPoint.capabilities.contains("WEP")) {
                            mAuthString = "OPEN-WEP";
                            mAuthMode = AuthModeOpen;
                            break;
                        }

                        if (WpaPsk && Wpa2Psk) {
                            mAuthString = "WPA-PSK WPA2-PSK";
                            mAuthMode = AuthModeWPA1PSKWPA2PSK;
                            break;
                        } else if (Wpa2Psk) {
                            mAuthString = "WPA2-PSK";
                            mAuthMode = AuthModeWPA2PSK;
                            break;
                        } else if (WpaPsk) {
                            mAuthString = "WPA-PSK";
                            mAuthMode = AuthModeWPAPSK;
                            break;
                        }

                        if (Wpa && Wpa2) {
                            mAuthString = "WPA-EAP WPA2-EAP";
                            mAuthMode = AuthModeWPA1WPA2;
                            break;
                        } else if (Wpa2) {
                            mAuthString = "WPA2-EAP";
                            mAuthMode = AuthModeWPA2;
                            break;
                        } else if (Wpa) {
                            mAuthString = "WPA-EAP";
                            mAuthMode = AuthModeWPA;
                            break;
                        }

                        mAuthString = "OPEN";
                        mAuthMode = AuthModeOpen;
                        currentBssid = AccessPoint.BSSID;

                    }
                }
                if(currentBssid != null) {
                    sendMac = null;
                    String tomacaddress[] = currentBssid.split(":");
                    int currentLen = currentBssid.split(":").length;

                    for (int m = currentLen - 1; m > -1; m--) {
                        for (int j = mList.size() - 1; j > -1; j--) {
                            if (!currentBssid.equals(mList.get(j))) {
                                String array[] = mList.get(j).split(":");
                                if (!tomacaddress[m].equals(array[m])) {
                                    mList.remove(j);//
                                }
                            }
                        }
                        if (mList.size() == 1 || mList.size() == 0) {
                            if (m == 5) {
                                sendMac = tomacaddress[m].toString();
                            } else if (m == 4) {
                                sendMac = tomacaddress[m].toString()
                                        + tomacaddress[m + 1].toString();
                            } else {
                                sendMac = tomacaddress[5].toString()
                                        + tomacaddress[4].toString()
                                        + tomacaddress[3].toString();
                            }
                            break;
                        }
                    }
                }
                scanWifiFinished();
            }
        });
        InternetUtil.getInstance().registerReceiver();
        boolean connected = InternetUtil.getInstance().isWifiConnected();
        InternetUtil.getInstance().checkPermisionCoasrseLocationGranted(this);
        ProgressDialog.showProgressDialog(this,false);
        sure.setEnabled(false);
        cancel.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.dismiss();
                sure.setEnabled(false);
                cancel.setEnabled(false);
            }
        },10000);
    }

    void scanWifiFinished(){
        handler.removeCallbacksAndMessages(null);
        ProgressDialog.dismiss();
        if(sendMac != null){
            //Can config
            sure.setEnabled(true);
            cancel.setEnabled(true);
        }else{
            sure.setEnabled(false);
            cancel.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        InternetUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        InternetUtil.getInstance().unregisterReceiver();
        if (player != null)        {
            player.stop();
        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void findView()
    {
        wifi_name=(EditText) findViewById(R.id.wifi_name);
        wifi_pwd=(EditText) findViewById(R.id.wifi_pwd);
        wifi_pwd.setText("caotieuvy2016");


        sure=(Button) findViewById(R.id.wifi_config_send);
        cancel=(Button) findViewById(R.id.wifi_config_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }



    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        int i = arg0.getId();
        if (i == R.id.wifi_config_send) {
            if (wifi_pwd.getText().toString() == null || wifi_pwd.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(WifiConfigActivity.this, "Wifi Password/Name is null", Toast.LENGTH_LONG).show();
                return;
            }

            sendSonic(sendMac, wifi_pwd.getText().toString());

        } else if (i == R.id.wifi_config_cancel) {
            player.stop();

        } else {
        }
    }


    @Override
    public void finish() {
        super.finish();
    }

    private  void  sendSonic(String mac, final String wifi)
    {
        byte[] midbytes = null;

        try {
            midbytes = HexString2Bytes(mac);
            printHexString(midbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (midbytes.length > 6)
        {
            Toast.makeText(WifiConfigActivity.this, "no support", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] b = null;
        int num = 0;
        if (midbytes.length == 2) {
            b = new byte[] { midbytes[0], midbytes[1] };
            num = 2;
        } else if (midbytes.length == 3) {
            b = new byte[] { midbytes[0], midbytes[1], midbytes[2] };
            num = 3;
        } else if (midbytes.length == 4) {
            b = new byte[] { midbytes[0], midbytes[1], midbytes[2], midbytes[3] };
            num = 4;
        } else if (midbytes.length == 5) {
            b = new byte[] { midbytes[0], midbytes[1], midbytes[2],
                    midbytes[3], midbytes[4] };
            num = 5;
        } else if (midbytes.length == 6) {
            b = new byte[] { midbytes[0], midbytes[1], midbytes[2],
                    midbytes[3], midbytes[4], midbytes[5] };
            num = 6;
        } else if (midbytes.length == 1) {
            b = new byte[] { midbytes[0] };
            num = 1;
        }

        int a[] = new int[19];
        a[0] = 6500;
        int i, j;
        for (i = 0; i < 18; i++)
        {
            a[i + 1] = a[i] + 200;
        }

        player.setFreqs(a);

        player.play(DataEncoder.encodeMacWiFi(b, wifi.trim()), 5, 1000);

    }

    private static byte uniteBytes(byte src0, byte src1)
    {
        byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private static byte[] HexString2Bytes(String src)
    {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++)
        {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static void printHexString(byte[] b) {
        // System.out.print(hint);
        for (int i = 0; i < b.length; i++)
        {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print("aaa" + hex.toUpperCase() + " ");
        }
        System.out.println("");
    }

}
