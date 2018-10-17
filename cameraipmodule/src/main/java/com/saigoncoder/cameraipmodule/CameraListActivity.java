package com.saigoncoder.cameraipmodule;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.saigoncoder.cameraipmodule.adapter.CameraManagerdapter;
import com.saigoncoder.cameraipmodule.model.CameraDB;
import com.saigoncoder.cameraipmodule.model.CameraManager;
import com.saigoncoder.cameraipmodule.nestcam.CameraViewActivity;
import com.saigoncoder.cameraipmodule.nestcam.NestCamManager;
import com.saigoncoder.cameraipmodule.nestcam.OathActivity;
import com.saigoncoder.cameraipmodule.model.NestCamera;
import com.saigoncoder.cameraipmodule.vstarcam.AddCameraActivity;
import com.saigoncoder.cameraipmodule.vstarcam.VStarCamManager;

import java.util.ArrayList;

import vstc2.nativecaller.NativeCaller;

import static com.saigoncoder.cameraipmodule.nestcam.NestCamManager.REQUEST_LOGIN_CODE;

public class CameraListActivity extends AppCompatActivity {

    public int user_id = 0;
    private Context context;

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
                cameraManager.vstarcamReady = ready;
                if(ready){
                    VStarCamManager.getInstance().getListCamera();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyChangeData();
                    }
                });


            }

            @Override
            public void getCamerasResponse(ArrayList<CameraDB> list) {

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

        initRecyclerView();
        NestCamManager.getInstance().getCameraList();
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

                }else if(type == CameraManager.TYPE_NEST_CAM){
                    Log.e("came", "Click nest cam");
                    for(int i = 0; i < cameraManager.nestCameras.size(); i++){
                        NestCamera item = cameraManager.nestCameras.get(i);
                        String token = SharedPreferencesUtil.geNestCamToken(context);
                        String deviceid = item.device_id;
                        Intent intent = new Intent(context, CameraViewActivity.class);
                        intent.putExtra("NAME", item.name);
                        intent.putExtra("TOKEN", token);
                        intent.putExtra("DEVICE", deviceid);
                        intent.putExtra("WEBURL", item.web_url);
                        intent.putExtra("APPURL", item.app_url);
                        context.startActivity(intent);
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
}
