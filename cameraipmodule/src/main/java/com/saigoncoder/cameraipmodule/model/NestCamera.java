package com.saigoncoder.cameraipmodule.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tiencao on 6/18/16.
 */
public class NestCamera implements Parcelable {
    public int id;
    public String device_id;
    public String name;
    public String where_name;
    public String name_long;
    public String web_url;
    public String app_url;

    @Override
    public String toString() {
        String str = "{" +
                "id: " + id + "," +
                "device_id: " + device_id + "," +
                "name: " + name +
                "}";

        return str;
    }

    public NestCamera() {
        id = 0;
        device_id = "id";
        name = "name cam";
    }

    public static final Creator<NestCamera> CREATOR = new Creator<NestCamera>() {

        public NestCamera createFromParcel(Parcel in) {

            NestCamera item = new NestCamera();

            item.id = in.readInt();
            return item;

        }

        public NestCamera[] newArray(int size) {

            return new NestCamera[size];

        }

    };

    @Override

    public int describeContents() {

        return 0;

    }

    @Override

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);

    }
}