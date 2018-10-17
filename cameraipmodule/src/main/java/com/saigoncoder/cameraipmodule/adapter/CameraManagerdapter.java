package com.saigoncoder.cameraipmodule.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saigoncoder.cameraipmodule.LOG;
import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.model.*;

import java.util.ArrayList;

/**
 * Created by tiencao on 1/22/18.
 */

public class CameraManagerdapter extends RecyclerView.Adapter<CameraManagerdapter.ViewHolder> {

    private CameraManager manager = new CameraManager();
    private ArrayList<CameraView> listItem = new ArrayList<>();
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView subtitle;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = (TextView) v.findViewById(R.id.item_camera2_titile);
            subtitle = (TextView) v.findViewById(R.id.item_camera2_subTitle);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CameraManagerdapter(CameraManager manage) {
        this.manager = manage;
        notifyChangeData();
    }



    public void notifyChangeData(){
        listItem.clear();
        CameraView item = new CameraView();
        item.type = CameraManager.TYPE_HEADER;
        item.title = manager.nestCamHeader;
        listItem.add(item);


        for(int i = 0; i < manager.nestCameras.size(); i++){
            NestCamera camera = manager.nestCameras.get(i);

            item = new CameraView();
            item.type = CameraManager.TYPE_NEST_CAM;
            item.title = camera.name;
            item.subtitle = camera.device_id;
            item.id = camera.device_id;
            listItem.add(item);
        }

        if(manager.nestCamIsLogin){
//            LOG.e("Nest cam logined");
//            item = new CameraView();
//            item.type = CameraManager.TYPE_VS_CAM_BTN_ADD;
//            item.title = manager.nestCamAdd;
//            listItem.add(item);
        }else{
            item = new CameraView();
            item.type = CameraManager.TYPE_NEST_CAM_BTN_LOGIN;
            item.title = manager.nestCamLogin;
            listItem.add(item);
        }
        updateVStarCam();
        notifyDataSetChanged();
    }

    private void updateVStarCam(){
        CameraView item = new CameraView();
        item.type = CameraManager.TYPE_HEADER;
        item.title = manager.vstarcamHeader;
        listItem.add(item);
        LOG.e("updateVStarCam vstarcamReady: " + manager.vstarcamReady);

        if(manager.vstarcamReady){
            item = new CameraView();
            item.type = CameraManager.TYPE_VSCAM_BTN_ADD;
            item.title = manager.vstarcamAdd;
            listItem.add(item);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CameraManagerdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //View v = null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera2, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setTextColor(Color.BLACK);
        holder.title.setBackgroundColor(Color.TRANSPARENT);
        holder.title.setPadding(0,0,0,0);
        CameraView item = listItem.get(position);
        if(item.type == CameraManager.TYPE_HEADER){
            holder.title.setGravity(Gravity.START);
            holder.title.setVisibility(View.VISIBLE);
            holder.subtitle.setVisibility(View.GONE);
            holder.title.setText(listItem.get(position).title);

        }else if(item.type == CameraManager.TYPE_NEST_CAM_BTN_ADD){
            holder.layout.setBackgroundColor(Color.WHITE);

            holder.title.setGravity(Gravity.CENTER);
            holder.title.setVisibility(View.VISIBLE);

            holder.subtitle.setVisibility(View.GONE);

            holder.title.setText(item.title);

            holder.title.setTextColor(Color.WHITE);
            holder.title.setBackgroundColor(Color.parseColor("#e67e22"));
            holder.title.setPadding(0,16,0,16);

        }else if(item.type == CameraManager.TYPE_NEST_CAM_BTN_LOGIN){
            holder.layout.setBackgroundColor(Color.WHITE);

            holder.title.setGravity(Gravity.CENTER);
            holder.title.setVisibility(View.VISIBLE);

            holder.subtitle.setVisibility(View.GONE);

            holder.title.setText(item.title);

            holder.title.setTextColor(Color.WHITE);
            holder.title.setBackgroundColor(Color.parseColor("#3498db"));
            holder.title.setPadding(0,16,0,16);

        }else if(item.type == CameraManager.TYPE_VSCAM_BTN_ADD){
            holder.layout.setBackgroundColor(Color.WHITE);

            holder.title.setGravity(Gravity.CENTER);
            holder.title.setVisibility(View.VISIBLE);

            holder.subtitle.setVisibility(View.GONE);

            holder.title.setText(item.title);

            holder.title.setTextColor(Color.WHITE);
            holder.title.setBackgroundColor(Color.parseColor("#3498db"));
            holder.title.setPadding(0,16,0,16);

        }else{
            holder.layout.setBackgroundColor(Color.WHITE);

            holder.title.setGravity(Gravity.START);
            holder.title.setVisibility(View.VISIBLE);
            holder.subtitle.setVisibility(View.VISIBLE);

            holder.title.setText(listItem.get(position).title);
            holder.subtitle.setText(listItem.get(position).subtitle);
        }




        holder.layout.setTag(R.id.tag_camera_id,position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){

                    int position = (int)v.getTag(R.id.tag_camera_id);
                    LOG.e("Click position: " + position);
                    CameraView item = listItem.get(position);
                    if(mListener != null){
                        mListener.onClickItem(item.id, item.type);
                    }
                }
            }
        });

//        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if(mListener != null){
//                    int position = (int)v.getTag(R.id.tag_camera_id2);
//
//                }
//                return true;
//            }
//        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public void setListener(CameraManagerAdapterListener l){
        mListener = l;
    }

    CameraManagerAdapterListener mListener;

    public interface CameraManagerAdapterListener{
        void onClickItem(String id, int type);
//        void deleteItem(Camera item, int index);
    }
}