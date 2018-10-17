package com.saigoncoder.cameraipmodule.vstarcam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.SharedPreferencesUtil;
import com.saigoncoder.cameraipmodule.db.MySQLiteDataSource;
import com.saigoncoder.cameraipmodule.model.CameraDB;
import com.saigoncoder.cameraipmodule.model.VStarCamera;
import com.saigoncoder.cameraipmodule.utils.ProgressDialog;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ConnectVStarCamUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SystemValue;

import java.util.Map;

import vstc2.nativecaller.NativeCaller;

public class AddCameraActivity extends AppCompatActivity {


    TextView tvStatus;
    EditText tvName;
    EditText tvUUID;
    EditText tvPassword;

    Button btnConnect;
    Button btnView;
    Button btnAdd;


    Context context;

    public final static int START_SEARCH_VSTARCAM  = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        context = this;
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.nest_cam_orange), PorterDuff.Mode.SRC_ATOP);

            }
            getSupportActionBar().setHomeAsUpIndicator(drawable);

            int color_orange = getResources().getColor(R.color.nest_cam_orange);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + color_orange + "\">Add VStarcam</font>"));
        }


        ConnectVStarCamUtils.getInstance().setListener(new ConnectVStarCamUtils.ConnectVStarCamListener() {
            @Override
            public void connectStatus(final int resid) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText(resid);
                    }
                });
            }

            @Override
            public void connectSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText("Connected");
                        btnConnect.setEnabled(false);
                        btnView.setEnabled(true);
                        btnAdd.setEnabled(true);
                        ProgressDialog.dismiss();
                    }
                });

            }

            @Override
            public void connecerror() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText("Connect failed");
                        btnConnect.setEnabled(true);
                        btnView.setEnabled(false);
                        btnAdd.setEnabled(false);
                        ProgressDialog.dismiss();
                    }
                });

            }
        });
        initView();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == START_SEARCH_VSTARCAM) {
            if(resultCode == Activity.RESULT_OK){
                String did = data.getStringExtra("DID");
                String name = data.getStringExtra("NAME");
                tvName.setText(name);
                tvUUID.setText(did);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_cam_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        int id = item.getItemId();

        if(id == R.id.add_cam_menu_search){
           Intent i = new Intent(context, SearchVStarCamActivity.class);
           startActivityForResult(i,START_SEARCH_VSTARCAM);
        }

        return super.onOptionsItemSelected(item);
    }



    private void initView(){
        btnConnect = (Button) findViewById(R.id.add_cam_btn_connect);
        btnView = (Button) findViewById(R.id.add_cam_btn_view);
        btnAdd = (Button) findViewById(R.id.add_cam_btn_add);

        btnConnect.setEnabled(true);
        btnView.setEnabled(false);
        btnAdd.setEnabled(false);


        tvStatus = (TextView) findViewById(R.id.add_cam_status);
        tvName = (EditText) findViewById(R.id.add_cam_name);
        tvUUID = (EditText) findViewById(R.id.add_cam_uuid);
        tvPassword = (EditText) findViewById(R.id.add_cam_password);
    }


    public void connectCamera(View view){
        String name = tvName.getText().toString().trim();
        String strDID = tvUUID.getText().toString().trim();
        String strPwd = tvPassword.getText().toString().trim();

        if (name.length() < 0) {
            Toast.makeText(this, "Name is null", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strDID.length() < 0) {
            Toast.makeText(this, "UUID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strPwd.length() < 0) {
            Toast.makeText(this, "Password is null", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO add camera
        if (option == ContentCommon.INVALID_OPTION) {
            option = ContentCommon.ADD_CAMERA;
        }
        String strUser = "admin";
        Intent in = new Intent();
        in.putExtra(ContentCommon.CAMERA_OPTION, option);
        in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
        in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
        in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
        in.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);

        SystemValue.deviceName = strUser;
        SystemValue.deviceId = strDID;
        SystemValue.devicePass = strPwd;
        SystemValue.name = name;

        ConnectVStarCamUtils.getInstance().connectCamera(strDID,strUser,strPwd);
        ProgressDialog.showProgressDialog(this,true);
    }

    public void viewCamera(View view){
//        Intent intent = new Intent(this,PlayActivity.class);
//        startActivity(intent);
    }

    public void addCamera(View view){
        String strName = tvName.getText().toString().trim();
        String strDID = tvUUID.getText().toString().trim();
        String strPwd = tvPassword.getText().toString().trim();

        String data = VStarCamera.toJsonData(strDID,strName, strPwd);

        CameraDB camera = new CameraDB();
        camera.data = data;
        camera.user = SharedPreferencesUtil.getUser(context);
        camera.token = strDID;
        camera.type = 0;


        MySQLiteDataSource sqLiteDataSource = new MySQLiteDataSource(context);
        sqLiteDataSource.addCamera(camera);

    }

    private int option = ContentCommon.INVALID_OPTION;
    private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
}
