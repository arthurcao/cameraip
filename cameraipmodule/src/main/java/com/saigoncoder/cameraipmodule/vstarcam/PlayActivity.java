package com.saigoncoder.cameraipmodule.vstarcam;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saigoncoder.cameraipmodule.LOG;
import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.vstarcam.controller.CruiseUtils;
import com.saigoncoder.cameraipmodule.vstarcam.controller.DisplayUtils;
import com.saigoncoder.cameraipmodule.vstarcam.controller.GestureUtils;
import com.saigoncoder.cameraipmodule.vstarcam.controller.MenuBrightness;
import com.saigoncoder.cameraipmodule.vstarcam.controller.ResolutionUtils;
import com.saigoncoder.cameraipmodule.vstarcam.controller.TakePitureUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.AudioPlayer;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;
import com.saigoncoder.cameraipmodule.vstarcam.utils.CustomAudioRecorder;
import com.saigoncoder.cameraipmodule.vstarcam.utils.CustomBuffer;
import com.saigoncoder.cameraipmodule.vstarcam.utils.CustomBufferData;
import com.saigoncoder.cameraipmodule.vstarcam.utils.CustomBufferHead;
import com.saigoncoder.cameraipmodule.vstarcam.utils.CustomVideoRecord;
import com.saigoncoder.cameraipmodule.vstarcam.utils.MyRender;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SDCardUtils;
import com.saigoncoder.cameraipmodule.vstarcam.utils.SystemValue;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;

import vstc2.nativecaller.NativeCaller;

public class PlayActivity extends AppCompatActivity implements View.OnTouchListener
{

    private static final String LOG_TAG = "PlayActivity";
    private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;
    Context context;
    //surfaceView
    private GLSurfaceView playSurface = null;


    private byte[] videodata = null;
    private int videoDataLen = 0;
    public int nVideoWidths = 0;
    public int nVideoHeights = 0;

    private View progressView = null;
    private boolean bProgress = true;


    private final int IR_STATE = 14;//IR(夜视)开关


    private boolean bInitCameraParam = false;

//    private TextView textosd = null;
    private String strName = null;
    private String strDID = null;

   // private View osdView = null;

    private boolean bDisplayFinished = true;
    private CustomBuffer AudioBuffer = null;
    private AudioPlayer audioPlayer = null;
    private boolean bAudioStart = false;


    private ImageView videoViewPortrait;
    private ImageView videoViewStandard;

    private HorizontalScrollView bottomView;
    private TextView ptzAudio,ptztalk,ptzTake_vodeo;
    private int nStreamCodecType;







    private Animation showAnim;
    private boolean isTakepic = false;
    private boolean isTalking = false;
    private boolean isMcriophone = false;

    private CustomVideoRecord myvideoRecorder;
    public boolean isH264 = false;
    public boolean isJpeg=false;
    private boolean isTakeVideo = false;
    private long videotime = 0;

    private Animation dismissAnim;


    private BitmapDrawable drawable = null;
    private boolean bAudioRecordStart = false;

    private CustomAudioRecorder customAudioRecorder;

    private MyRender myRender;




    private void defaultVideoParams() {
        DisplayUtils.getInstance().nBrightness = 1;
        DisplayUtils.getInstance().nBrightness = 128;
        NativeCaller.PPPPCameraControl(strDID, 1, 0);
        NativeCaller.PPPPCameraControl(strDID, 2, 128);

    }

    private void setViewVisible() {
        if (bProgress)
        {
            bProgress = false;
            progressView.setVisibility(View.INVISIBLE);
            //osdView.setVisibility(View.VISIBLE);
            getCameraParams();
        }
    }

    int disPlaywidth;
    private Bitmap mBmp;


    private void getCameraParams() {

        NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
    }

    private Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d("tag", "Camera disconnected");
                Toast.makeText(getApplicationContext(),                  R.string.pppp_status_disconnect, Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    };

    MenuBrightness mMenuBrightness;
    GestureUtils mGestureUtils;
    TakePitureUtils mTakePitureUtils;
    ResolutionUtils mResolutionUtils;
    CruiseUtils mCruiseUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        context = this;

        strName = SystemValue.deviceName;
        strDID = SystemValue.deviceId;
       // setTitle(strDID);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Drawable drawable = ResourcesCompat.getDrawable(getResources(),   R.drawable.ic_back, this.getTheme());
            getSupportActionBar().setHomeAsUpIndicator(drawable);
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#ff9800'>" + strDID +" </font>"));
        }

        disPlaywidth = getWindowManager().getDefaultDisplay().getWidth();
        findView();
        AudioBuffer = new CustomBuffer();
        audioPlayer = new AudioPlayer(AudioBuffer);
        customAudioRecorder=new CustomAudioRecorder(new CustomAudioRecorder.AudioRecordResult() {
            @Override
            public void AudioRecordData(byte[] data, int len) {
                // TODO Auto-generated method stub
                if (bAudioRecordStart && len > 0) {
                    NativeCaller.PPPPTalkAudioData(strDID, data, len);
                }
            }

            @Override
            public void AudioError(final int code) {
                if(code == CustomAudioRecorder.ERROR_NOT_INIT){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"startRecording() called on an uninitialized AudioRecord",Toast.LENGTH_SHORT).show();
                            isMcriophone = false;
                            bAudioRecordStart = false;
                            ptztalk.setSelected(false);
                            StopTalk();
                        }
                    });
                }
            }
        });
        BridgeService.setPlayInterface(new BridgeService.PlayInterface() {
            @Override
            public void callBackCameraParamNotify(String did, int resolution,
                                                  int brightness, int contrast, int hue, int saturation, int flip, int mode) {
                Log.e("callback", resolution+","+brightness+","+contrast+","+hue+","+saturation+","+flip+","+mode);

                mResolutionUtils.nResolution = resolution;
                bInitCameraParam = true;
                mCruiseUtils.initFlip(flip);
                mMenuBrightness.nBrightness = brightness;
                mMenuBrightness.nBrightness = contrast;

            }

            @Override
            public void callBackVideoData(byte[] videobuf, int h264Data, int len,int width, int height) {
                // Log.e("video", "videobuf:"+videobuf+"--"+"h264Data"+h264Data+"len"+len+"width"+width+"height"+height);
                if (!bDisplayFinished)
                    return;
                bDisplayFinished = false;
                videodata = videobuf;
                videoDataLen = len;
                Message msg = new Message();
                if (h264Data == 1)
                { // H264
                    nVideoWidths = width;
                    nVideoHeights = height;
                    if (isTakepic) {
                        isTakepic = false;
                        byte[] rgb = new byte[width * height * 2];
                        NativeCaller.YUV4202RGB565(videobuf, rgb, width, height);
                        ByteBuffer buffer = ByteBuffer.wrap(rgb);
                        mBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        mBmp.copyPixelsFromBuffer(buffer);
                        mTakePitureUtils.takePicture(mBmp);
                    }
                    isH264 = true;
                    msg.what = 1;
                } else { // MJPEG
                    isJpeg=true;
                    msg.what = 2;
                }
                mResolutionUtils.handlerResolution(msg.what);
                mHandler.sendMessage(msg);
                //
                if (isTakeVideo) {

                    Date date = new Date();
                    long times = date.getTime();
                    int tspan = (int) (times - videotime);
                    Log.d("tag", "play  tspan:" + tspan);
                    videotime = times;
                    if (videoRecorder != null) {
                        if (isJpeg) {

                            videoRecorder.VideoRecordData(2, videobuf, width, height,tspan);
                        }
                    }
                }
            }

            @Override
            public void callBackMessageNotify(String did, int msgType, int param) {
                //Log.e("callback", "MessageNotify did: " + did + " msgType: " + msgType              + " param: " + param);

                if (msgType == ContentCommon.PPPP_MSG_TYPE_STREAM) {
                    nStreamCodecType = param;
                    nStreamCodecType = param;
                    mResolutionUtils.nStreamCodecType = param;
                    return;
                }

                if (msgType != ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS)
                {
                    return;
                }

                if (!did.equals(strDID)) {
                    return;
                }

                Message msg = new Message();
                msg.what = 1;
                msgHandler.sendMessage(msg);
            }

            @Override
            public void callBackAudioData(byte[] pcm, int len) {
                //  Log.e("callback", "AudioData: len :+ " + len);
                if (!audioPlayer.isAudioPlaying()) {
                    return;
                }
                CustomBufferHead head = new CustomBufferHead();
                CustomBufferData data = new CustomBufferData();
                head.length = len;
                head.startcode = AUDIO_BUFFER_START_CODE;
                data.head = head;
                data.data = pcm;
                AudioBuffer.addData(data);
            }

            @Override
            public void callBackH264Data(byte[] h264, int type, int size) {
                // Log.e("callback", "CallBack_H264Data" + " type:" + type + " size:" + size);
                if (isTakeVideo)
                {
                    Date date = new Date();
                    long time = date.getTime();
                    int tspan = (int) (time - videotime);
                    Log.d("tag", "play  tspan:" + tspan);
                    videotime = time;
                    if (videoRecorder != null) {
                        videoRecorder.VideoRecordData(type, h264, size, 0, tspan);
                    }
                }
            }
        });

        myvideoRecorder = new CustomVideoRecord(this, strDID);


        NativeCaller.StartPPPPLivestream(strDID, 10, 1);

        getCameraParams();
        showAnim = AnimationUtils.loadAnimation(this,
                R.anim.ptz_otherset_anim_show);
        dismissAnim = AnimationUtils.loadAnimation(this,
                R.anim.ptz_otherset_anim_dismiss);

        myRender = new MyRender(playSurface);
        playSurface.setRenderer(myRender);


        mMenuBrightness = new MenuBrightness(this,strDID);
        mGestureUtils = new GestureUtils(strDID,this,playSurface);
        mTakePitureUtils = new TakePitureUtils(strDID,this);
        mTakePitureUtils.requestPermissionSaveSDCard();
        mTakePitureUtils.setListener(new TakePitureUtils.TakePitureUtilsListener() {
            @Override
            public void takePictureSuccess(final String path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LOG.e("Take picture success");
                        galleryAddPic(path);
                    }
                });

            }

            @Override
            public void takePictureError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LOG.e("Take picture error");
                    }
                });
            }
        });

        mResolutionUtils = new ResolutionUtils(strDID,this);
        mCruiseUtils = new CruiseUtils(strDID,this);

    }

    public void galleryAddPic(String path) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
//        String mCurrentPhotoPath = "file:" + path; // image is the created file image
//        File file = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(file);
//        mediaScanIntent.setData(contentUri);
//        context.sendBroadcast(mediaScanIntent);
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



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mTakePitureUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playing_menu, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.nest_cam_orange), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK){

            if(mResolutionUtils.isShow){
                mResolutionUtils.hideMenu();
                return  true;
            }

            if(mCruiseUtils.isShow){
                mCruiseUtils.hideMenu();
                return  true;
            }


            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void findView() {

        playSurface = (GLSurfaceView) findViewById(R.id.mysurfaceview);
        playSurface.setOnTouchListener(this);
        playSurface.setLongClickable(true);//确保手势识别正确工作

        videoViewPortrait = (ImageView) findViewById(R.id.vedioview);
        videoViewStandard = (ImageView) findViewById(R.id.vedioview_standard);
        progressView = (View) findViewById(R.id.progressLayout);
        bottomView=(HorizontalScrollView) findViewById(R.id.bottom_view);

        ptztalk= (TextView) findViewById(R.id.ptz_talk);
        ptzAudio = (TextView) findViewById(R.id.ptz_audio);
        ptzTake_vodeo= (TextView)findViewById(R.id.ptz_take_videos);

        ptztalk.setTextColor(getResources().getColor(R.color.btn_text_colorc));


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.top_bg);
        drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        drawable.setDither(true);

        bottomView.setBackgroundDrawable(drawable);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_UP:
                if(mResolutionUtils.isShow){
                    mResolutionUtils.hideMenu();
                    return  true;
                }

                if(mCruiseUtils.isShow){
                    mCruiseUtils.hideMenu();
                    return  true;
                }
                break;

        }

        mGestureUtils.onTouch(event);
        return mGestureUtils.onTouchEvent(event);
    }



    private boolean isPTZPrompt;

    public void pressSettings(View view){
        Intent i = new Intent(this,SettingsActivity.class);
        startActivity(i);
    }

    public void pressTakePicture(View view){
        if (SDCardUtils.getInstance().existSdcard()) {
            mTakePitureUtils.takePicture(mBmp);
            isTakepic = true;
        } else {
            LOG.e("SDCard not existed");
        }
    }

    //TODO onlick
    public void pressBrightness(View v){
        mMenuBrightness.showMenu();
    }

    public void pressResolution(View v){
        mResolutionUtils.showMenu();
    }

    public void pressTakeVideo(View view){
        goTakeVideo();
    }

    public void pressListen(View view){
        goAudio();
    }

    public void pressTalk(View view){
        goMicroPhone();
    }

    public void pressReset(View view){

        NativeCaller.PPPPCameraControl(strDID, 1, 0);
        NativeCaller.PPPPCameraControl(strDID, 2, 128);
        mMenuBrightness.nBrightness = 0;
        mMenuBrightness.nBrightness = 128;

    }

    public void pressPanTilt(View view){
        mCruiseUtils.showMenu();
    }

    /*
     * 录像
     */
    private void goTakeVideo() {

        if (isTakeVideo) {
            LOG.e("Stop record video");
            String strRecord = "";
            NativeCaller.RecordLocal(strDID,strRecord,0);
            isTakeVideo = false;
            ptzTake_vodeo.setSelected(false);
            myvideoRecorder.stopRecordVideo();
        } else {
            isTakeVideo = true;
            LOG.e("Start record video");
            videotime = (new Date()).getTime();
            ptzTake_vodeo.setSelected(true);
            String strRecord = "/mnt/sdcard/Video/test5.mp4";
            NativeCaller.RecordLocal(strDID,strRecord,1);
        }
    }
    private void stopTakevideo() {
        if (isTakeVideo) {
            LOG.e("Stop record video");
            isTakeVideo = false;
            // cameratakevideo.stopRecordVideo(strDID);
            myvideoRecorder.stopRecordVideo();
        }
    }
    //StartTalk
    private void StartTalk() {
        if (customAudioRecorder != null) {
            Log.i("info", "startTalk");
            customAudioRecorder.StartRecord();
            NativeCaller.PPPPStartTalk(strDID);
        }
    }
    //StopTalk
    private void StopTalk() {
        if (customAudioRecorder != null) {
            Log.i("info", "stopTalk");
            customAudioRecorder.StopRecord();
            NativeCaller.PPPPStopTalk(strDID);
        }
    }
    //StartAudio
    private void StartAudio() {
        synchronized (this) {
            AudioBuffer.ClearAll();
            audioPlayer.AudioPlayStart();
            NativeCaller.PPPPStartAudio(strDID);
        }
    }
    //StopAudio
    private void StopAudio() {
        synchronized (this) {
            audioPlayer.AudioPlayStop();
            AudioBuffer.ClearAll();
            NativeCaller.PPPPStopAudio(strDID);
        }
    }
    /*
     * 监听
     */
    private void goAudio() {

        if (!isMcriophone) {
            if (bAudioStart) {
                Log.d("info", "StopAudio");
                isTalking = false;
                bAudioStart = false;
                ptzAudio.setSelected(false);
                StopAudio();
            } else {
                Log.d("info", "StartAudio");
                isTalking = true;
                bAudioStart = true;
                ptzAudio.setSelected(true);
                StartAudio();
            }

        } else {
            isMcriophone = false;
            bAudioRecordStart = false;
            ptztalk.setSelected(false);
            StopTalk();
            isTalking = true;
            bAudioStart = true;
            ptzAudio.setSelected(true);
            StartAudio();
        }

    }

    /*
     * 对讲
     */
    private void goMicroPhone() {

        if (!isTalking) {
            if (bAudioRecordStart)
            {
                Log.e("listent", "goMicroPhone StopTalk");
                isMcriophone = false;
                bAudioRecordStart = false;
                ptztalk.setSelected(false);
                StopTalk();
            } else {
                Log.e("listent", "goMicroPhone StartTalk");
                isMcriophone = true;
                bAudioRecordStart = true;
               ptztalk.setSelected(true);
                StartTalk();
            }
        } else {
            Log.e("listent", "goMicroPhone stop and start");
            isTalking = false;
            bAudioStart = false;
            ptzAudio.setSelected(false);
            StopAudio();
            isMcriophone = true;
            bAudioRecordStart = true;
            ptztalk.setSelected(true);
            StartTalk();
        }

    }




    @Override
    protected void onDestroy() {
        NativeCaller.StopPPPPLivestream(strDID);
        StopAudio();
        StopTalk();
        stopTakevideo();
        NativeCaller.StopPPPP(strDID);

        super.onDestroy();
    }





    public void setVideoRecord(VideoRecorder videoRecorder)
    {
        this.videoRecorder = videoRecorder;
    }

    public VideoRecorder videoRecorder;

    public interface VideoRecorder
    {
        abstract public void VideoRecordData(int type, byte[] videodata, int width, int height, int time);
    }


    private Handler mHandler = new Handler()
    {

        public void handleMessage(Message msg)
        {
            if (msg.what == 1 || msg.what == 2) {
                setViewVisible();
            }
            if (!isPTZPrompt)
            {
                isPTZPrompt = true;
                // showToast(请按菜单键,进行云台控制);
            }
            int width = getWindowManager().getDefaultDisplay().getWidth();
            int height = getWindowManager().getDefaultDisplay().getHeight();
            switch (msg.what) {
                case 1: // h264
                {

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    {
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                width, width * 3 / 4);
                        lp.gravity = Gravity.CENTER;
                        playSurface.setLayoutParams(lp);
                    }
                    else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    {
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                width, height);
                        lp.gravity = Gravity.CENTER;
                        playSurface.setLayoutParams(lp);
                    }
                    myRender.writeSample(videodata, nVideoWidths, nVideoHeights);
                }
                break;
                case 2: // JPEG
                {

                    mBmp = BitmapFactory.decodeByteArray(videodata, 0,
                            videoDataLen);
                    if (mBmp == null) {
                        bDisplayFinished = true;
                        return;
                    }
                    if (isTakepic) {
                        mTakePitureUtils.takePicture(mBmp);
                        isTakepic = false;
                    }
                    nVideoWidths = mBmp.getWidth();
                    nVideoHeights = mBmp.getHeight();

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Bitmap
                        Bitmap bitmap = Bitmap.createScaledBitmap(mBmp, width,
                                width * 3 / 4, true);
                        //videoViewLandscape.setVisibility(View.GONE);
                        videoViewPortrait.setVisibility(View.VISIBLE);
                        videoViewPortrait.setImageBitmap(bitmap);

                    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Bitmap bitmap = Bitmap.createScaledBitmap(mBmp, width,height, true);
                        videoViewPortrait.setVisibility(View.GONE);
                        //videoViewLandscape.setVisibility(View.VISIBLE);
                        //videoViewLandscape.setImageBitmap(bitmap);
                    }

                }
                break;
                default:
                    break;
            }
            if (msg.what == 1 || msg.what == 2)
            {
                bDisplayFinished = true;
            }
        }

    };
}

