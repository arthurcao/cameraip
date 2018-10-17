package com.saigoncoder.cameraipmodule.nestcam;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saigoncoder.cameraipmodule.R;

public class CameraViewActivity extends AppCompatActivity {
    WebView webView;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_webview);
        webView = findViewById(R.id.camWebview);

//      String token = getIntent().getStringExtra("TOKEN");
//      String device = getIntent().getStringExtra("DEVICE");//
        String name = getIntent().getStringExtra("NAME");
        setTitle(name);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.nest_cam_orange), PorterDuff.Mode.SRC_ATOP);

            }
            getSupportActionBar().setHomeAsUpIndicator(drawable);

            int color_orange = getResources().getColor(R.color.nest_cam_orange);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + color_orange + "\">" + name + "</font>"));
        }

        url = getIntent().getStringExtra("WEBURL");

//      GetCameraUrlPresenter presenter = new GetCameraUrlPresenter(this);
//      presenter.setPresenterListener(new iView<String>() {
//            @Override
//            public void response(String s) {
//                String url = s.replaceAll("\"", "");
//                Log.e("nestcamera", "url: " + url);
//                webView.loadUrl(url);
//            }
//
//            @Override
//            public void error(int error) {
//
//            }
//      });
//      presenter.get(token, device);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        client();
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

    void client(){
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }
        });
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);

        webView.loadUrl(url);
    }
}
