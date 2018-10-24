package com.saigoncoder.cameraipmodule.vstarcam.utils;

import android.os.Environment;

/**
 * Created by tiencao on 1/18/18.
 */

public class SDCardUtils {
    private static final SDCardUtils ourInstance = new SDCardUtils();

   public static SDCardUtils getInstance() {
        return ourInstance;
    }

    private SDCardUtils() {
    }

    public boolean existSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
