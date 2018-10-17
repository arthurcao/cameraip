package com.saigoncoder.cameraip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.saigoncoder.cameraipmodule.CameraListActivity;
import com.saigoncoder.cameraipmodule.vstarcam.SearchVStarCamActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, CameraListActivity.class);
//        Intent i = new Intent(this, SearchVStarCamActivity.class);
        startActivity(i);
    }
}
