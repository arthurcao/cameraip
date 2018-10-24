package com.saigoncoder.cameraipmodule.vstarcam;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.SharedPreferencesUtil;
import com.saigoncoder.cameraipmodule.db.MySQLiteDataSource;
import com.saigoncoder.cameraipmodule.model.CameraDB;
import com.saigoncoder.cameraipmodule.model.CameraManager;
import com.saigoncoder.cameraipmodule.model.VStarCamera;
import com.saigoncoder.cameraipmodule.vstarcam.controller.SettingsUserUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SystemValue;

import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public final static String passwordKey = "password_text";

    String strName;
    String strDID;
    String strPassword;
    MySQLiteDataSource sqLiteDataSource;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
        context = this;
        strName = SystemValue.deviceName; //User, default: admin
        strDID = SystemValue.deviceId;
        strPassword = SystemValue.devicePass;
        SettingsUserUtils.getInstance().setListener(new SettingsUserUtils.SettingUtilsListener() {
            @Override
            public void get_user_params(String user, String password) {
                strName = user;
                strDID = password;
            }

            @Override
            public void set_success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Change password successful. It take 30 second to restart.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String data = VStarCamera.toJsonData( SystemValue.deviceId, SystemValue.name, strPassword);
                                CameraDB camera = new CameraDB();
                                camera.id = SystemValue.dbid;
                                camera.data = data;
                                camera.user = SharedPreferencesUtil.getUser(context);
                                camera.token = SystemValue.deviceId;
                                camera.type = CameraManager.TYPE_VSTARCAM;
                                VStarCamManager.getInstance().updateVStarcamPassword(camera);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }

            @Override
            public void set_failed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Change password failed.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        SettingsUserUtils.getInstance().setDID(strDID);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(passwordKey.equals(key)){
            String value = sharedPreferences.getString(key, "");
            Log.e("change", "key change: " + key);
            Log.e("change", "key value: " + value);
            String newpass = value;
            if(newpass.length() == 0){
                Toast.makeText(context,"Password is null", Toast.LENGTH_SHORT).show();
                return;
            }
            if(newpass.length() < 8){
                Toast.makeText(context,"Password is less 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            strPassword = newpass;
            SettingsUserUtils.getInstance().setUser(strName,newpass);
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment  {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            EditTextPreference edttxtpref = (EditTextPreference) getPreferenceScreen().findPreference(passwordKey);
            edttxtpref.setText("");


        }



    }


}
