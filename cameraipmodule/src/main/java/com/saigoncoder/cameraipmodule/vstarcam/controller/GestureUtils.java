package com.saigoncoder.cameraipmodule.vstarcam.controller;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.saigoncoder.cameraipmodule.R;
import com.saigoncoder.cameraipmodule.vstarcam.utils.ContentCommon;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by tiencao on 1/16/18.
 */

public class GestureUtils {

    private GestureDetector gt;

    public GestureUtils(String did, Context c, GLSurfaceView sf) {
        strDID = did;
        context = c;
        playSurface = sf;
        initControlDailog();
        gt = new GestureDetector(gestureListener);
    }

    public boolean onTouchEvent(MotionEvent event){
        return gt.onTouchEvent(event);
    }

    public String strDID;
    private boolean isControlDevice = false;
    private final int MINLEN = 80;
    Context context;


    private GLSurfaceView playSurface = null;
    TextView control_item;
    PopupWindow controlWindow;

    private void initControlDailog() {


        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.camera_play_gesture_layout, null);
        control_item = (TextView) view.findViewById(R.id.camera_gesture_layout_text);
        controlWindow=new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        controlWindow.setBackgroundDrawable(new ColorDrawable(0));
        controlWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                controlWindow.dismiss();
            }
        });
        controlWindow.setTouchInterceptor(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    controlWindow.dismiss();
                }
                return false;
            }
        });
    }


    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    protected float getScale() {
        return getScale(mSuppMatrix);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }
    private float spacing(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float)Math.sqrt(x * x + y * y);
        } catch (Exception e) {
        }
        return 0;
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];

    private boolean isDown = false;
    private boolean isSecondDown = false;
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;
    private Matrix savedMatrix = new Matrix();
    private float oldDist;
    private Matrix matrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mode = NONE;
    float originalScale;


    public void onTouch(MotionEvent event){
        if (!isDown) {
            x1 = event.getX();
            y1 = event.getY();
            isDown = true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                originalScale = getScale();
                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs((x1 - x2)) < 25 && Math.abs((y1 - y2)) < 25){
                    if (!isSecondDown) {
//                        if (!bProgress) {
//                            showBottom();
//                        }
                    }
                    isSecondDown = false;
                }
                x1 = 0;
                x2 = 0;
                y1 = 0;
                y2 = 0;
                isDown = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isSecondDown = true;
                oldDist = spacing(event);
                if (oldDist > 10f)
                {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();

                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 0f) {

                    }
                }
        }
    }

    GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x1 = e1.getX();
            float x2 = e2.getX();
            float y1 = e1.getY();
            float y2 = e2.getY();

            float xx = x1 > x2 ? x1 - x2 : x2 - x1;
            float yy = y1 > y2 ? y1 - y2 : y2 - y1;

            if (xx > yy)
            {
                if ((x1 > x2) && (xx > MINLEN))
                {// right
                    if(!isControlDevice)
                        new ControlDeviceTask(ContentCommon.CMD_PTZ_RIGHT).execute();

                } else if ((x1 < x2) && (xx > MINLEN)) {// left
                    if(!isControlDevice)
                        new ControlDeviceTask(ContentCommon.CMD_PTZ_LEFT).execute();
                }

            } else {
                if ((y1 > y2) && (yy > MINLEN))
                {// down
                    if(!isControlDevice)
                        new ControlDeviceTask(ContentCommon.CMD_PTZ_DOWN).execute();
                } else if ((y1 < y2) && (yy > MINLEN)) {// up
                    if(!isControlDevice)
                        new ControlDeviceTask(ContentCommon.CMD_PTZ_UP).execute();
                }

            }
            return false;
        }
    };

    private class ControlDeviceTask extends AsyncTask<Void, Void, Integer> {
        private int type;
        public ControlDeviceTask(int type) {
            this.type=type;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(type ==ContentCommon.CMD_PTZ_RIGHT) {
                control_item.setText("Move right");
            }
            else if(type ==ContentCommon.CMD_PTZ_LEFT) {
                control_item.setText("Move left");
            }
            else if(type ==ContentCommon.CMD_PTZ_UP) {
                control_item.setText("Move up");
            }
            else if(type ==ContentCommon.CMD_PTZ_DOWN) {
                control_item.setText("Move down");
            }
            if (controlWindow != null && controlWindow.isShowing())
                controlWindow.dismiss();

            if (controlWindow != null && !controlWindow.isShowing())
                controlWindow.showAtLocation(playSurface, Gravity.CENTER,0, 0);
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            isControlDevice = true;
            if(type == ContentCommon.CMD_PTZ_RIGHT) {
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_RIGHT);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_RIGHT_STOP);
            }
            else if(type ==ContentCommon.CMD_PTZ_LEFT) {
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_LEFT);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_LEFT_STOP);
            }
            else if(type ==ContentCommon.CMD_PTZ_UP) {
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_UP);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_UP_STOP);
            }
            else if(type ==ContentCommon.CMD_PTZ_DOWN) {
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_DOWN);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(strDID,ContentCommon.CMD_PTZ_DOWN_STOP);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            isControlDevice = false;
            if (controlWindow != null && controlWindow.isShowing())
                controlWindow.dismiss();
        }

    }
}
