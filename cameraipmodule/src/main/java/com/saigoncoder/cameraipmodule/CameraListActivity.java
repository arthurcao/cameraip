package com.saigoncoder.cameraipmodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.saigoncoder.cameraipmodule.adapter.CameraManagerdapter;
import com.saigoncoder.cameraipmodule.model.CameraDB;
import com.saigoncoder.cameraipmodule.model.CameraManager;
import com.saigoncoder.cameraipmodule.model.VStarCamera;
import com.saigoncoder.cameraipmodule.nestcam.CameraViewActivity;
import com.saigoncoder.cameraipmodule.nestcam.NestCamManager;
import com.saigoncoder.cameraipmodule.nestcam.OathActivity;
import com.saigoncoder.cameraipmodule.model.NestCamera;
import com.saigoncoder.cameraipmodule.utils.ProgressDialog;
import com.saigoncoder.cameraipmodule.vstarcam.AddCameraActivity;
import com.saigoncoder.cameraipmodule.vstarcam.PlayActivity;
import com.saigoncoder.cameraipmodule.vstarcam.VStarCamManager;
import com.saigoncoder.cameraipmodule.vstarcam.WifiConfigActivity;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ConnectVStarCamUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;
import com.saigoncoder.cameraipmodule.vstarcam.utils.InternetUtil;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SystemValue;

import java.util.ArrayList;

import vstc2.nativecaller.NativeCaller;

import static com.saigoncoder.cameraipmodule.nestcam.NestCamManager.REQUEST_LOGIN_CODE;

public class CameraListActivity extends AppCompatActivity {

    public int user_id = 0;
    private Context context;
    public void test(){

    }

    int mReady = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        context = this;
        if(getIntent() != null){
            Intent intent = getIntent();
            if(intent.hasExtra("USER_ID")){
                user_id = intent.getIntExtra("USER_ID",0);
                SharedPreferencesUtil.saveUser(context,user_id);
            }
        }
        VStarCamManager.getInstance().init(this);
        VStarCamManager.getInstance().setVStarCamListener(new VStarCamManager.VStarCamListener() {
            @Override
            public void vstarcamReady(boolean ready) {
                mReady--;
                checkInitReady();
                cameraManager.vstarcamReady = ready;
                if( cameraManager.vstarcamReady){
                    VStarCamManager.getInstance().getListCamera();
                }


            }

            @Override
            public void getCamerasResponse(ArrayList<CameraDB> list) {
                LOG.e("list vstarcam size: " + list.size());
                cameraManager.vStarCameras.clear();
                for(int i = 0; i < list.size(); i++){
                    VStarCamera cam = VStarCamera.convertToVStarcam(list.get(i));
                    LOG.e(i + " cam: " + cam.toString());
                    cameraManager.vStarCameras.add(cam);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyChangeData();
                    }
                });
            }
        });

        NestCamManager.getInstance().init(this);
        NestCamManager.getInstance().setListener(new NestCamManager.NestCamListener() {
            @Override
            public void nestCamResponse(ArrayList<NestCamera> nestCameras) {
                cameraManager.nestCameras.addAll(nestCameras);
                mAdapter.notifyChangeData();



            }

            @Override
            public void updateLoginStatus(boolean isLogin) {
                mReady--;
                checkInitReady();
                cameraManager.nestCameras.clear();
                cameraManager.nestCamIsLogin = isLogin;
                mAdapter.notifyChangeData();
            }

            @Override
            public void loginResult(boolean islogin) {
                cameraManager.nestCamIsLogin = islogin;
                if(islogin) {
                    NestCamManager.getInstance().getCameraList();
                    mAdapter.notifyChangeData();
                }
            }
        });

        ConnectVStarCamUtils.getInstance().setListener(new ConnectVStarCamUtils.ConnectVStarCamListener() {
            @Override
            public void connectStatus(final int resid) {
            }

            @Override
            public void connectSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // tvStatus.setText("Connected");

                        ProgressDialog.dismiss();
                        Intent intent = new Intent(context,PlayActivity.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void connecerror() {

                LOG.e("connect failed");
            }

            @Override
            public void connectWrongPassword() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tvStatus.setText("Connect failed");
                        ProgressDialog.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        final EditText edittext = new EditText(context);
                        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
                        alert.setTitle("Wrong Password. Enter new password.");


                        alert.setView(edittext);

                        alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //What ever you want to do with the value
                                Editable YouEditTextValue = edittext.getText();
                                //OR
                                String password = edittext.getText().toString();
                                if(password.length() > 0){
                                    String data = VStarCamera.toJsonData( SystemValue.deviceId, SystemValue.name, password);
                                    CameraDB camera = new CameraDB();
                                    camera.id = SystemValue.dbid;
                                    camera.data = data;
                                    camera.user = SharedPreferencesUtil.getUser(context);
                                    camera.token = SystemValue.deviceId;
                                    camera.type = CameraManager.TYPE_VSTARCAM;
                                    VStarCamManager.getInstance().updateVStarcamPassword(camera);
                                    VStarCamManager.getInstance().getListCamera();
                                }else{
                                    Toast.makeText(context,"Password is null.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        alert.setNegativeButton("Cancel", null);
                        alert.show();
                    }
                });
            }
        });

        initRecyclerView();
        NestCamManager.getInstance().getCameraList();
        test();
        mReady = 2;
        ProgressDialog.showProgressDialog(this, true);
    }

    void checkInitReady(){
        if(mReady <= 0){
            ProgressDialog.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if( cameraManager.vstarcamReady){
            VStarCamManager.getInstance().getListCamera();
            mAdapter.notifyChangeData();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NestCamManager.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    CameraManager cameraManager = new CameraManager();
    RecyclerView mRecyclerView;
    CameraManagerdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    private void initRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.camera_recycler_view);
        mRecyclerView.setHasFixedSize(true);
// use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CameraManagerdapter(cameraManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new CameraManagerdapter.CameraManagerAdapterListener() {
            @Override
            public void onClickItem(String id, int type) {
                if(type == CameraManager.TYPE_HEADER){
                    Log.e("came", "Click header");
                }else if(type == CameraManager.TYPE_NEST_CAM_BTN_ADD){
                    Log.e("came", "Click add nest cam");
                }else if(type == CameraManager.TYPE_NEST_CAM_BTN_LOGIN){
                    Log.e("came", "Click login nest cam");
                    Intent i = new Intent(context, OathActivity.class);
                    startActivityForResult(i,REQUEST_LOGIN_CODE);
                }else if(type == CameraManager.TYPE_VSCAM_BTN_ADD){
                    Log.e("came", "Click login nest cam");
                    Intent i = new Intent(context, AddCameraActivity.class);
                    startActivity(i);

                }else if(type == CameraManager.TYPE_VSCAM_BTN_CONFIG_WIFI){
                    Log.e("came", "Click login nest cam");
                    Intent i = new Intent(context, WifiConfigActivity.class);
                    startActivity(i);

                }else if(type == CameraManager.TYPE_NEST_CAM){
                    Log.e("came", "Click nest cam");
                    for(int i = 0; i < cameraManager.nestCameras.size(); i++){
                        NestCamera item = cameraManager.nestCameras.get(i);
                        String token = SharedPreferencesUtil.geNestCamToken(context);
                        String deviceid = item.device_id;
                        if(id.equals(deviceid)) {
                            Intent intent = new Intent(context, CameraViewActivity.class);
                            intent.putExtra("NAME", item.name);
                            intent.putExtra("TOKEN", token);
                            intent.putExtra("DEVICE", deviceid);
                            intent.putExtra("WEBURL", item.web_url);
                            intent.putExtra("APPURL", item.app_url);
                            context.startActivity(intent);
                            break;
                        }
                    }
                }else if(type == CameraManager.TYPE_VSTARCAM){
                    for(int i = 0; i < cameraManager.vStarCameras.size(); i++){
                        VStarCamera item = cameraManager.vStarCameras.get(i);
                        String deviceid = item.did;
                        if(id.equals(deviceid)) {
                            //TODO choose vstarcam
                            LOG.e("vstarcam choosen: " + item.toString());
                            connectVStarCamera(item.id, item.name, item.did, item.password);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onLongClickItem(String id, int type) {
                if(type == CameraManager.TYPE_VSTARCAM){
                    for(int i = 0; i < cameraManager.vStarCameras.size(); i++){
                        final VStarCamera item = cameraManager.vStarCameras.get(i);
                        String deviceid = item.did;
                        if(id.equals(deviceid)) {
                            //TODO choose vstarcam
                            //Delete camera
                            LOG.e("Do you want to delete " + deviceid);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Do you want to delete " + deviceid + "?");
                            builder.setCancelable(true);
                            builder.setNegativeButton("Cancel", null);
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    VStarCamManager.getInstance().deleteVStarcam(item.id);
                                    VStarCamManager.getInstance().getListCamera();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NativeCaller.Free();
        VStarCamManager.getInstance().stopVStarcamService();
    }



    public void connectVStarCamera(int id, String name, String strDID, String strPwd){

        //TODO add camera
        if (ConnectVStarCamUtils.option == ContentCommon.INVALID_OPTION) {
            ConnectVStarCamUtils.option = ContentCommon.ADD_CAMERA;
        }
        String strUser = "admin";
        Intent in = new Intent();
        in.putExtra(ContentCommon.CAMERA_OPTION, ConnectVStarCamUtils.option);
        in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
        in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
        in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
        in.putExtra(ContentCommon.STR_CAMERA_TYPE, ConnectVStarCamUtils.CameraType);

        SystemValue.dbid = id;
        SystemValue.deviceName = strUser;
        SystemValue.deviceId = strDID;
        SystemValue.devicePass = strPwd;
        SystemValue.name = name;

        ConnectVStarCamUtils.getInstance().connectCamera(strDID,strUser,strPwd);
        ProgressDialog.showProgressDialog(this,true);
    }
}
