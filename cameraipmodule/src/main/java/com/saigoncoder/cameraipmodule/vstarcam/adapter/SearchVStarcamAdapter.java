package com.saigoncoder.cameraipmodule.vstarcam.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.model.VStarCamera;

import java.util.ArrayList;

/**
 * Created by tiencao on 1/22/18.
 */

public class SearchVStarcamAdapter extends RecyclerView.Adapter<SearchVStarcamAdapter.ViewHolder> {


    private ArrayList<VStarCamera> listItems = new ArrayList<>();
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
    public SearchVStarcamAdapter(ArrayList<VStarCamera> list) {
        listItems = list;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public SearchVStarcamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        VStarCamera item = listItems.get(position);

        holder.title.setTextColor(Color.BLACK);
        holder.title.setBackgroundColor(Color.TRANSPARENT);
        holder.title.setPadding(0,0,0,0);


        holder.layout.setBackgroundColor(Color.WHITE);
        holder.title.setGravity(Gravity.START);
        holder.title.setVisibility(View.VISIBLE);
        holder.subtitle.setVisibility(View.VISIBLE);

        holder.title.setText(item.name);
        holder.subtitle.setText(item.did);





        holder.layout.setTag(R.id.tag_search_vstarcam_id,position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    int position = (int)v.getTag(R.id.tag_search_vstarcam_id);
                    VStarCamera camera = listItems.get(position);
                    if(mListener != null){
                        mListener.onClickItem(camera);
                    }
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setListener(SearchVStarcamAdapterListener l){
        mListener = l;
    }

    SearchVStarcamAdapterListener mListener;

    public interface SearchVStarcamAdapterListener{
        void onClickItem(VStarCamera camera);
    }
}