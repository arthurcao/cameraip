package com.saigoncoder.cameraipmodule.db.table;

/**
 * Created by tiencao on 6/18/16.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.saigoncoder.cameraipmodule.model.CameraDB;

import java.util.ArrayList;

public class CameraTable {

    public static final String TABLE_NAME = "CAMERA_TABLE";

    public static final String ID = "ID";
    public static final String TOKEN = "TOKEN";
    public static final String USER = "USER";
    public static final String TYPE = "TYPE";
    public static final String DATA = "DATA";

    public static final String QUERY_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY, " +
            TOKEN + " Text, " +
            USER + " INTEGER, " +
            TYPE + " INTEGER, " +
            DATA + " DATA" +
            ");";

    private static final String[] columns = {ID, TOKEN, USER, TYPE, DATA};

    private ContentValues createContentValue(CameraDB item) {
        ContentValues values = new ContentValues();
        values.put(TOKEN, item.token);
        values.put(USER, item.user);
        values.put(TYPE, item.type);
        values.put(DATA, item.data);
        return values;
    }

    public CameraTable() {

    }

    public void clearTable(SQLiteDatabase db) {
        db.delete(TABLE_NAME, null, null);
    }


    public void deleteRecord(SQLiteDatabase db, String token) {
        String where = TOKEN + " = '" + TOKEN + "'";
        db.delete(TABLE_NAME, where, null);
    }

    public void insertRecord(SQLiteDatabase db, CameraDB item) {

        if (checkRecordExist(db, item)) {
            updateRecord(db, item);
        } else {
            ContentValues values = createContentValue(item);
            db.insert(TABLE_NAME, null, values);
        }

    }

    public void updateRecord(SQLiteDatabase db, CameraDB item) {
        ContentValues values = createContentValue(item);
        String where = TOKEN + " = '" + TOKEN + "'";
        db.update(TABLE_NAME, values, where, null);

    }

    public ArrayList<CameraDB> readRecords(SQLiteDatabase db, int userid, int type) {

        String where = USER + " = " + userid + " AND " + TYPE + "=" + type;

        ArrayList<CameraDB> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, columns, where, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CameraDB item = new CameraDB();
            item.id = cursor.getInt(0);
            item.token = cursor.getString(1);
            item.user = cursor.getInt(2);
            item.type = cursor.getInt(3);
            item.data = cursor.getString(4);
            list.add(item);
            cursor.moveToNext();
        }
        return list;
    }

    public boolean checkRecordExist(SQLiteDatabase db, CameraDB item) {
        String where = TOKEN + " = '" + TOKEN + "'";
        Cursor cursor = db.query(TABLE_NAME, columns, where, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }


}
