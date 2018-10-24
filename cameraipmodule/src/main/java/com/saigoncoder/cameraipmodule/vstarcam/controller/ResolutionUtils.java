package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.utils.ProgressDialog;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;

import java.util.HashMap;
import java.util.Map;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/18/18.
 */

public class ResolutionUtils {


    public int nStreamCodecType;
    public static Map<String, Map<Object, Object>> reslutionlist = new HashMap<String, Map<Object, Object>>();



    private boolean ismax = false;
    private boolean ishigh = false;
    private boolean isp720 = false;
    private boolean ismiddle = false;
    private boolean isqvga1 = false;
    private boolean isvga1 = false;
    private boolean isqvga = false;
    private boolean isvga = false;

    private String stqvga = "qvga";
    private String stvga = "vga";
    private String stqvga1 = "qvga1";
    private String stvga1 = "vga1";
    private String stp720 = "p720";
    private String sthigh = "high";
    private String stmiddle ="middle";
    private String stmax = "max";
    public int nResolution = 0;


    String strDID;
    Activity context;


    View layout;
    View layoutJpeg;
    View layoutH264;

    Button btnJpegVGA;
    Button btnJpegQVGA;

    Button btnH264QVGA;
    Button btnH264VGA;
    Button btnH264P720;
    Button btnH264Midle;
    Button btnH264High;
    Button btnH264Max;

   public boolean isShow = false;

    public ResolutionUtils(String did, Activity c) {

        strDID = did;
        context = c;
        layout = c.findViewById(R.id.activity_play_resolution);

        layoutJpeg = layout.findViewById(R.id.camera_play_resolution_jpeg);
        layoutH264 = layout.findViewById(R.id.camera_play_resolution_h264);

        btnJpegVGA = (Button) layout.findViewById(R.id.camera_play_resolution_jpeg_vga);
        btnJpegQVGA = (Button) layout.findViewById(R.id.camera_play_resolution_jpeg_qvga);

        btnH264QVGA = (Button) layout.findViewById(R.id.camera_play_resolution_h264_qvga);
        btnH264VGA = (Button) layout.findViewById(R.id.camera_play_resolution_h264_vga);
        btnH264P720 = (Button) layout.findViewById(R.id.camera_play_resolution_h264_720p);
        btnH264Midle = (Button) layout.findViewById(R.id.camera_play_resolution_h264_middle);
        btnH264High = (Button) layout.findViewById(R.id.camera_play_resolution_h264_high);
        btnH264Max = (Button) layout.findViewById(R.id.ptz_resolution_h264_max);
        btnH264Max = (Button) layout.findViewById(R.id.ptz_resolution_h264_max);

        ImageButton btnClose = (ImageButton) layout.findViewById(R.id.ptz_resolution_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });

    }
    protected void setResolution(int Resolution) {
        Log.d("tag", "setResolution resolution:" + Resolution);
        NativeCaller.PPPPCameraControl(strDID,16, Resolution);
    }

    public void showMenu(){
        isShow = true;
        Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bottom_up);
        layout.startAnimation(bottomUp);
        layout.setVisibility(View.VISIBLE);
        showResolutionPopWindow();
    }

    public void hideMenu(){
        isShow = false;
        Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bottom_down);
        layout.startAnimation(bottomUp);
        layout.setVisibility(View.GONE);
    }

    private void showResolutionPopWindow() {

        if (reslutionlist.size() != 0) {
            getReslution();
        }
        ProgressDialog.dismiss();
        if (nStreamCodecType == ContentCommon.PPPP_STREAM_TYPE_JPEG) {
            layoutJpeg.setVisibility(View.VISIBLE);
            layoutH264.setVisibility(View.GONE);
        } else {
            layoutJpeg.setVisibility(View.GONE);
            layoutH264.setVisibility(View.VISIBLE);
        }

        if (nStreamCodecType == ContentCommon.PPPP_STREAM_TYPE_JPEG) {
            // jpeg
            if (reslutionlist.size() != 0) {
                getReslution();
            }

            btnJpegVGA.setSelected(false);
            btnJpegQVGA.setSelected(false);

            if (isvga) {
                btnJpegVGA.setSelected(false);
            }
            if (isqvga) {
                btnJpegQVGA.setSelected(false);
            }

            btnJpegQVGA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    nResolution = 1;
                    setResolution(nResolution);
                }
            });
            btnJpegVGA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    nResolution = 0;
                    setResolution(nResolution);
                }
            });



        } else {
            // h264

            btnH264Max.setSelected(false);
            btnH264High.setSelected(false);
            btnH264Midle.setSelected(false);
            btnH264QVGA.setSelected(false);
            btnH264VGA.setSelected(false);
            btnH264P720.setSelected(false);

            if (ismax) {
                btnH264Max.setSelected(true);
            }
            if (ishigh) {
                btnH264High.setSelected(true);
            }
            if (ismiddle) {
                btnH264Midle.setSelected(true);
            }
            if (isqvga1) {
                btnH264QVGA.setSelected(true);
            }
            if (isvga1) {
                btnH264VGA.setSelected(true);
            }
            if (isp720) {
                btnH264P720.setSelected(true);
            }


            btnH264High.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    ismax = false;
                    ismiddle = false;
                    ishigh = true;
                    isp720 = false;
                    isqvga1 = false;
                    isvga1 = false;
                    addReslution(sthigh, ishigh);
                    nResolution = 1;
                    setResolution(nResolution);
                }
            });
            btnH264Midle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    ismax = false;
                    ismiddle = true;
                    ishigh = false;
                    isp720 = false;
                    isqvga1 = false;
                    isvga1 = false;
                    addReslution(stmiddle, ismiddle);
                    nResolution = 2;
                    setResolution(nResolution);
                }
            });
            btnH264Max.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    ismax = true;
                    ismiddle = false;
                    ishigh = false;
                    isp720 = false;
                    isqvga1 = false;
                    isvga1 = false;
                    addReslution(stmax, ismax);
                    nResolution = 0;
                    setResolution(nResolution);
                }
            });
            btnH264QVGA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    ismax = false;
                    ismiddle = false;
                    ishigh = false;
                    isp720 = false;
                    isqvga1 = true;
                    isvga1 = false;
                    addReslution(stqvga1, isqvga1);
                    nResolution = 5;
                    setResolution(nResolution);
                }
            });
            btnH264VGA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    ismax = false;
                    ismiddle = false;
                    ishigh = false;
                    isp720 = false;
                    isqvga1 = false;
                    isvga1 = true;
                    addReslution(stvga1, isvga1);
                    nResolution = 4;
                    setResolution(nResolution);
                }
            });
            btnH264P720.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    ismax = false;
                    ismiddle = false;
                    ishigh = false;
                    isp720 = true;
                    isqvga1 = false;
                    isvga1 = false;
                    addReslution(stp720, isp720);
                    nResolution = 3;
                    setResolution(nResolution);
                }
            });
        }

    }

    private void getReslution() {
        if (reslutionlist.containsKey(strDID)) {
            Map<Object, Object> map = reslutionlist.get(strDID);
            if (map.containsKey("qvga")) {
                isqvga = true;
            } else if (map.containsKey("vga")) {
                isvga = true;
            } else if (map.containsKey("qvga1")) {
                isqvga1 = true;
            } else if (map.containsKey("vga1")) {
                isvga1 = true;
            } else if (map.containsKey("p720")) {
                isp720 = true;
            } else if (map.containsKey("high")) {
                ishigh = true;
            } else if (map.containsKey("middle")) {
                ismiddle = true;
            } else if (map.containsKey("max")) {
                ismax = true;
            }
        }
    }

    private void addReslution(String mess, boolean isfast) {
        if (reslutionlist.size() != 0) {
            if (reslutionlist.containsKey(strDID)) {
                reslutionlist.remove(strDID);
            }
        }
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(mess, isfast);
        reslutionlist.put(strDID, map);
    }

    public void handlerResolution(int what){
        switch (what) {
            case 1: // h264
            {

                if (reslutionlist.size() == 0) {
                    if (nResolution == 0) {
                        ismax = true;
                        ismiddle = false;
                        ishigh = false;
                        isp720 = false;
                        isqvga1 = false;
                        isvga1 = false;
                        addReslution(stmax, ismax);
                    } else if (nResolution == 1) {
                        ismax = false;
                        ismiddle = false;
                        ishigh = true;
                        isp720 = false;
                        isqvga1 = false;
                        isvga1 = false;
                        addReslution(sthigh, ishigh);
                    } else if (nResolution == 2) {
                        ismax = false;
                        ismiddle = true;
                        ishigh = false;
                        isp720 = false;
                        isqvga1 = false;
                        isvga1 = false;
                        addReslution(stmiddle, ismiddle);
                    } else if (nResolution == 3) {
                        ismax = false;
                        ismiddle = false;
                        ishigh = false;
                        isp720 = true;
                        isqvga1 = false;
                        isvga1 = false;
                        addReslution(stp720, isp720);
                        nResolution = 3;
                    } else if (nResolution == 4) {
                        ismax = false;
                        ismiddle = false;
                        ishigh = false;
                        isp720 = false;
                        isqvga1 = false;
                        isvga1 = true;
                        addReslution(stvga1, isvga1);
                    } else if (nResolution == 5) {
                        ismax = false;
                        ismiddle = false;
                        ishigh = false;
                        isp720 = false;
                        isqvga1 = true;
                        isvga1 = false;
                        addReslution(stqvga1, isqvga1);
                    }
                } else {
                    if (reslutionlist.containsKey(strDID)){
                        getReslution();
                    } else {
                        if (nResolution == 0) {
                            ismax = true;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmax, ismax);
                        } else if (nResolution == 1) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = true;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(sthigh, ishigh);
                        } else if (nResolution == 2) {
                            ismax = false;
                            ismiddle = true;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmiddle, ismiddle);
                        } else if (nResolution == 3) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = true;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stp720, isp720);
                            nResolution = 3;
                        } else if (nResolution == 4) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = true;
                            addReslution(stvga1, isvga1);
                        } else if (nResolution == 5) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = true;
                            isvga1 = false;
                            addReslution(stqvga1, isqvga1);
                        }
                    }

                }



                //  myRender.writeSample(videodata, nVideoWidths, nVideoHeights);
            }
            break;
            case 2: // JPEG
            {
                if (reslutionlist.size() == 0) {
                    if (nResolution == 1) {
                        isvga = true;
                        isqvga = false;
                        addReslution(stvga, isvga);
                    } else if (nResolution == 0) {
                        isqvga = true;
                        isvga = false;
                        addReslution(stqvga, isqvga);
                    }
                } else {
                    if (reslutionlist.containsKey(strDID)) {
                        getReslution();
                    } else {
                        if (nResolution == 1) {
                            isvga = true;
                            isqvga = false;
                            addReslution(stvga, isvga);
                        } else if (nResolution == 0) {
                            isqvga = true;
                            isvga = false;
                            addReslution(stqvga, isqvga);
                        }
                    }
                }


            }
        }
    }
    public Handler mHandler = new Handler()
    {

        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case 1: // h264
                {

                    if (reslutionlist.size() == 0) {
                        if (nResolution == 0) {
                            ismax = true;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmax, ismax);
                        } else if (nResolution == 1) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = true;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(sthigh, ishigh);
                        } else if (nResolution == 2) {
                            ismax = false;
                            ismiddle = true;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmiddle, ismiddle);
                        } else if (nResolution == 3) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = true;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stp720, isp720);
                            nResolution = 3;
                        } else if (nResolution == 4) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = true;
                            addReslution(stvga1, isvga1);
                        } else if (nResolution == 5) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = true;
                            isvga1 = false;
                            addReslution(stqvga1, isqvga1);
                        }
                    } else {
                        if (reslutionlist.containsKey(strDID)){
                            getReslution();
                        } else {
                            if (nResolution == 0) {
                                ismax = true;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stmax, ismax);
                            } else if (nResolution == 1) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = true;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(sthigh, ishigh);
                            } else if (nResolution == 2) {
                                ismax = false;
                                ismiddle = true;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stmiddle, ismiddle);
                            } else if (nResolution == 3) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = true;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stp720, isp720);
                                nResolution = 3;
                            } else if (nResolution == 4) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = true;
                                addReslution(stvga1, isvga1);
                            } else if (nResolution == 5) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = true;
                                isvga1 = false;
                                addReslution(stqvga1, isqvga1);
                            }
                        }

                    }



                  //  myRender.writeSample(videodata, nVideoWidths, nVideoHeights);
                }
                break;
                case 2: // JPEG
                {
                    if (reslutionlist.size() == 0) {
                        if (nResolution == 1) {
                            isvga = true;
                            isqvga = false;
                            addReslution(stvga, isvga);
                        } else if (nResolution == 0) {
                            isqvga = true;
                            isvga = false;
                            addReslution(stqvga, isqvga);
                        }
                    } else {
                        if (reslutionlist.containsKey(strDID)) {
                            getReslution();
                        } else {
                            if (nResolution == 1) {
                                isvga = true;
                                isqvga = false;
                                addReslution(stvga, isvga);
                            } else if (nResolution == 0) {
                                isqvga = true;
                                isvga = false;
                                addReslution(stqvga, isqvga);
                            }
                        }
                    }


                }
                break;
                default:
                    break;
            }
        }

    };



}
