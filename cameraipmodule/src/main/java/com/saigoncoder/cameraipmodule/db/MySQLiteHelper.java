package com.saigoncoder.cameraipmodule.db;

/**
 * Created by tiencao on 6/18/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.saigoncoder.cameraipmodule.db.table.CameraTable;


public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cameradatabase.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CameraTable.QUERY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CameraTable.TABLE_NAME);
        onCreate(db);
    }
}