package com.saigoncoder.cameraipmodule.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.saigoncoder.cameraipmodule.R;


public class ProgressDialog {

	private static Dialog progress;
	public static void showProgressDialog(Context context, boolean isCancelable){
		dismiss();
		ProgressBar mSpinner = new ProgressBar(context);
		mSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		mSpinner.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.cam_spinner_ios));

		progress = new Dialog(context);
		progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progress.setContentView(mSpinner);
		progress.setCancelable(isCancelable);
		progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		progress.getWindow().setDimAmount((float) 0.5);
		progress.show();
	}


	public static void showProgressDialogNoCancel(Context context){
		dismiss();
		ProgressBar mSpinner = new ProgressBar(context);
		mSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		mSpinner.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.cam_spinner_ios));

		progress = new Dialog(context);
		progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progress.setContentView(mSpinner);
		progress.setCancelable(false);
		progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		progress.getWindow().setDimAmount((float) 0.5);
		progress.show();
	}

	public static void dismiss(){
		if(progress != null){
			progress.dismiss();
			progress = null;
		}
	}
}