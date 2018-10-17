package com.saigoncoder.cameraipmodule.vstarcam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.model.VStarCamera;
import com.saigoncoder.cameraipmodule.vstarcam.adapter.SearchVStarcamAdapter;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ConnectVStarCamUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SearchVStarCamUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SystemValue;

import java.util.ArrayList;

import vstc2.nativecaller.NativeCaller;

public class SearchVStarCamActivity extends AppCompatActivity {

    boolean isSearch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_vstar_cam);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.nest_cam_orange), PorterDuff.Mode.SRC_ATOP);

            }
            getSupportActionBar().setHomeAsUpIndicator(drawable);

            int color_orange = getResources().getColor(R.color.nest_cam_orange);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + color_orange + "\">Search VStarcam</font>"));
        }
        initRecyclerView();

        SearchVStarCamUtils.getInstance().setListener(new SearchVStarCamUtils.SearchVStarCamListener() {
            @Override
            public void searchCameraResult(final VStarCamera camera) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        boolean exist = false;
                        for(int i = 0; i < listCams.size(); i++){
                            VStarCamera item = listCams.get(i);
                            if(item.name.endsWith(camera.name)){
                                exist = true;
                                break;
                            }
                        }

                        if(!exist) {
                            listCams.add(camera);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });


    }



    @Override
    protected void onStop() {
        super.onStop();
        SearchVStarCamUtils.getInstance().stopSearch();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Refresh")
                .setIcon(android.R.drawable.ic_menu_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        if (item.getTitle().equals("Refresh")) {
            if(isSearch){
                isSearch = false;
                item.setActionView(null);
                SearchVStarCamUtils.getInstance().stopSearch();
                return super.onOptionsItemSelected(item);
            }
            isSearch = true;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.progress_layout, null);
            item.setActionView(v);

            ConnectVStarCamUtils.getInstance().stopCameraPPPP();
            SystemValue.deviceId=null;
            SearchVStarCamUtils.getInstance().startSearch();
            // run for 3 secs
            getWindow().getDecorView().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            // just remove the custom view in order for the normal icon to show again
                            item.setActionView(null);
                            SearchVStarCamUtils.getInstance().stopSearch();
                        }
                    }, 10000);

        }

        return super.onOptionsItemSelected(item);
    }

    ArrayList<VStarCamera> listCams = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SearchVStarcamAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.search_vstarcam_list);

        //use this setting to improve performance if you know that changes
        //in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SearchVStarcamAdapter(listCams);
        mAdapter.setListener(new SearchVStarcamAdapter.SearchVStarcamAdapterListener() {
            @Override
            public void onClickItem(VStarCamera camera) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("NAME",camera.name);
                returnIntent.putExtra("DID",camera.did);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }

}
