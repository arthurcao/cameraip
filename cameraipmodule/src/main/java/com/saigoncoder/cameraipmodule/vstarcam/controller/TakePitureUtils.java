package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.saigoncoder.cameraipmodule.LOG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tiencao on 1/18/18.
 */

public class TakePitureUtils {

    Activity context;
    boolean permisstionSaveFile = false;

    public TakePitureUtils(String did, Activity context) {
        strDID = did;
        this.context = context;
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.e("test","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            permisstionSaveFile = true;
        }
    }

    public boolean requestPermissionSaveSDCard(){
        if (Build.VERSION.SDK_INT >= 23) {
            //TODO ngoctien
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {


                permisstionSaveFile = true;
                return true;
            }



            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            permisstionSaveFile = false;


            return false;


        }
        else { //permission is automatically granted on sdk<23 upon installation
            permisstionSaveFile = true;
            return true;
        }
    }

    private int i=0;//
    private boolean isPictSave = false;
    private String strDID;

    public void takePicture(final Bitmap bmp) {
        LOG.e("takePicture 1");
        if (!isPictSave) {
            isPictSave = true;
            new Thread() {
                public void run() {
                    savePicToSDcard(bmp);
                }
            }.start();
        } else {
            return;
        }
    }

    String generateName(){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }


    public synchronized void savePicToSDcard(final Bitmap bmp) {
        LOG.e("savePicToSDcard 1");
        String strDate = getStrDate();
        //String date = strDate.substring(0, 10);
        FileOutputStream fos = null;
        try {
            File div = new File(Environment.getExternalStorageDirectory(),"ipcamera/takepic");
            LOG.e("Get path: " + div.getAbsolutePath());
            if (!div.exists()) {
                boolean create = div.mkdirs();
                LOG.e("Create folder: " + create);
            }
            ++i;
            String name = generateName();
           String filename = strDate + "_"+ strDID + "_"+ name +".jpg";
            LOG.e("bmp name:" + filename);
            LOG.e("bmp count:" + bmp.getByteCount());

            File file = new File(div, filename);
            fos = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos))
            {
                fos.flush();
                LOG.e("save bmp success");
                if(mListener != null){
                    String path = div.getAbsolutePath() + "/" +  filename;
                    mListener.takePictureSuccess(path);
                }
            }else{
                LOG.e("save bmp failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(mListener != null){
                mListener.takePictureError();
            }
        } finally {
            isPictSave = false;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }
    }

    private String getStrDate() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String strDate = f.format(d);
        return strDate;
    }


    public void setListener(TakePitureUtilsListener l){
        mListener = l;
    }

    TakePitureUtilsListener mListener = null;

    public interface TakePitureUtilsListener{
        void takePictureSuccess(String path);
        void takePictureError();
    }
}
