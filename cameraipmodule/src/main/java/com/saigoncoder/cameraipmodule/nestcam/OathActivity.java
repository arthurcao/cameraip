package com.saigoncoder.cameraipmodule.nestcam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saigoncoder.cameraipmodule.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OathActivity extends AppCompatActivity {
    private WebView oathWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oath_webview);
        oathWeb = findViewById(R.id.oathWeb);

        oathWeb.setWebViewClient(new webClient(this));
        oathWeb.getSettings().setLoadsImagesAutomatically(true);
        oathWeb.getSettings().setJavaScriptEnabled(true);

//        oathWeb.setWebViewClient(new WebViewClient());
        oathWeb.getSettings().setBuiltInZoomControls(true);
        oathWeb.getSettings().setJavaScriptEnabled(true);
        oathWeb.getSettings().setDomStorageEnabled(true);
        oathWeb.getSettings().setDatabaseEnabled(true);
        oathWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        oathWeb.getSettings().setAppCacheEnabled(true);



        oathWeb.loadUrl("https://home.nest.com/login/oauth2?client_id=404851d3-44b2-4200-8db5-06f7bc6ba2d9&state=STATE");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.nest_cam_orange), PorterDuff.Mode.SRC_ATOP);

            }
            getSupportActionBar().setHomeAsUpIndicator(drawable);

            int color_orange = getResources().getColor(R.color.nest_cam_orange);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + color_orange + "\">" + "Login" + "</font>"));
        }

        Log.e("tien","oathWebview");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        private Context context;

        public JsonTask(Context context){
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection;
            BufferedReader reader;
            try {
                URL url = new URL("https://iot.kooltechs.com/nest/getToken?state=STATE&code="+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("==========", s);
            JSONObject reader;
            String access_token = null;
            try {
                reader = new JSONObject(s);
                JSONObject token = reader.getJSONObject("token");
                access_token = token.getString("access_token");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra("ACCESS_TOKEN", access_token);
            setResult(RESULT_OK, intent);
            ((Activity)context).finish();
        }
    }

    private class webClient extends WebViewClient {
        Context context;

        public webClient(Context context){
            this.context = context;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("==========", url);
            Matcher matcher = Pattern.compile("https://iot\\.kooltechs\\.com/.+&code=(.+)").matcher(url);
            if (matcher.find()) {
                new JsonTask(context).execute(url);
                Log.i("==========", matcher.group(1));
            }
        }
    }
}
