package com.saigoncoder.cameraipmodule.nestcam.mvc.worker;

import android.os.AsyncTask;

/**
 * Created by tiencao on 9/23/15.
 */
public abstract class Worker<T> extends AsyncTask<Void,Void,Void> {

    WorkerCallback<T> callback;

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    public abstract void setCallback(WorkerCallback<T> callback);
}
