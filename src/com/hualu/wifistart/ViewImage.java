package com.hualu.wifistart;


import com.hualu.wifistart.imagePicker.Util;
import com.hualu.wifistart.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class ViewImage extends Activity {
	private Uri mSavedUri;
	private ImageView mImageView;
	final static String TAG = "viewimage";
	private Bitmap mBitmapShow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewimage);
		mImageView = (ImageView) findViewById(R.id.image);
		Intent intent = getIntent();
		mSavedUri = (Uri)intent.getData();
		Log.i(TAG,"uri = " + mSavedUri);
		setImage(mSavedUri);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	void setImage(Uri path){
		if((mBitmapShow!=null)&&(mBitmapShow.isRecycled()))
			mBitmapShow.recycle();
		mBitmapShow = Util.makeBitmap(getResources().getDisplayMetrics().widthPixels, 3 * 1024 * 1024,
				path,this.getContentResolver(), false);
		mImageView.setImageBitmap(mBitmapShow);
			
	}


}
