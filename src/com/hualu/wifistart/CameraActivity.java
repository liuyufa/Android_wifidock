package com.hualu.wifistart;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;

import com.hualu.wifistart.R.color;

import jcifs.smb.SmbFileOutputStream;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener { // zhaoyu0402,
																			// PictureCallback
	private CameraSurfacePreview mCameraSurPreview = null;
	private ImageButton btnCapture, btnCancel, btnConfirm;
	private RelativeLayout rlyConfirm;
	// private ImageView photoView;
	private TextView txtCapture;
	private String TAG = "CAMERA";
	private long btime;
	private long endtime;

	private enum CaptureStatus {
		CAPTURE, RECAPTURE
	};

	private CaptureStatus captureStatus = CaptureStatus.CAPTURE;
	private String currFolder = null;
	private byte[] jpgData;
//	private SVDraw mSVDraw = null;
//	private ImageView focusfailed;
//	private ImageView focusing;
//	private ImageView focused;

	// zhaoyu0402 private Camera mCamera;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		// Create our Preview view and set it as the content of our activity.
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		mCameraSurPreview = new CameraSurfacePreview(this, pictureCallback);
		preview.addView(mCameraSurPreview);
		// mSVDraw = (com.hualu.wifistart.SVDraw) findViewById(R.id.mDraw);
//		focusfailed = (ImageView) findViewById(R.id.focusfailed);
//		focusing = (ImageView) findViewById(R.id.focusing);
//		focused = (ImageView) findViewById(R.id.focused);
//		focusing.bringToFront();
		if (getIntent().getExtras() != null)
			currFolder = getIntent().getExtras().getString("currFolder");
		else
			currFolder = null;

		// Add a listener to the Capture button
		btnCapture = (ImageButton) findViewById(R.id.btnCapture);
		btnCancel = (ImageButton) findViewById(R.id.btnCancel);
		btnConfirm = (ImageButton) findViewById(R.id.btnConfirm);
		btnCapture.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);

		rlyConfirm = (RelativeLayout) findViewById(R.id.rlyConfirm);
		// photoView = (ImageView)findViewById(R.id.photoView);
		txtCapture = (TextView) findViewById(R.id.txtCapture);
	}

	/*
	 * zhaoyu0402
	 * 
	 * @Override public void onPictureTaken(byte[] data, Camera camera) {
	 * //Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	 * mCamera = camera; jpgData = null; jpgData = new byte[data.length];
	 * System.arraycopy(data,0,jpgData,0,data.length);
	 * //photoView.setImageBitmap(mBitmap);
	 * rlyConfirm.setVisibility(View.VISIBLE);
	 * txtCapture.setText(R.string.set_shoot); captureStatus =
	 * CaptureStatus.RECAPTURE; }
	 */
	public boolean saveCapture() {
		boolean ret = false;
		if (currFolder != null) {
			Toast.makeText(this,R.string.saving, Toast.LENGTH_SHORT).show();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			String path = currFolder + "IMAGE_" + timeStamp + ".jpg";
			Log.i(TAG, "filedir = " + path);
			if (currFolder.startsWith("smb")) {
				try {
					OutputStream out = new BufferedOutputStream(
							new SmbFileOutputStream(path));
					out.write(jpgData);
					out.close();
					ret = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {

					File pictureFile = new File(currFolder + "IMAGE_"
							+ timeStamp + ".jpg");// + File.separator
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(jpgData);
					fos.close();
					ret = true;
				} catch (FileNotFoundException e) {
					Log.d(TAG, "File not found: " + e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, "Error accessing file: " + e.getMessage());
				}
			}
			Toast.makeText(this, R.string.succeed, Toast.LENGTH_SHORT).show();
		}
		return ret;
	}

	@Override
	public void onBackPressed() {
		System.gc();
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirm:
			saveCapture();
			// photoView.setVisibility(View.GONE);
			rlyConfirm.setVisibility(View.GONE);
			captureStatus = CaptureStatus.CAPTURE;
			mCameraSurPreview.mCamera.startPreview();
			txtCapture.setText(R.string.start_camer);
			btnCapture.setEnabled(true);
			break;
		case R.id.btnCancel:
			System.gc();
			CameraActivity.this.finish();
			break;
		case R.id.btnCapture:
			if (captureStatus == CaptureStatus.CAPTURE) {
				// btnCapture.setEnabled(false);
				captureStatus = CaptureStatus.RECAPTURE;
				// get an image from the camera
				// mCameraSurPreview.takePicture(this);// zhaoyu0402

//				btime = System.currentTimeMillis();
//				focusing.setVisibility(View.INVISIBLE);
//				focused.bringToFront();
//				focused.setVisibility(View.VISIBLE);
				mCameraSurPreview.AutoFocusAndPreviewCallback();
				// focusfailed.setVisibility(View.INVISIBLE);
				// mSVDraw.setVisibility(mSVDraw.VISIBLE);
				// mSVDraw.drawLine();
//				endtime = System.currentTimeMillis();
//				while (endtime - btime < 2000) {
//					endtime = System.currentTimeMillis();
//					Log.i("==============", String.valueOf(endtime - btime));
//				}
//				focused.setVisibility(View.INVISIBLE);
//				Log.i("focus",
//						"======================================================");
			} else {

				rlyConfirm.setVisibility(View.GONE);
				mCameraSurPreview.mCamera.startPreview();
				txtCapture.setText(R.string.start_camer);
				captureStatus = CaptureStatus.CAPTURE;
				btnCapture.setEnabled(true);
			}
			break;
		}
	}

	/*
	 * private File getOutputMediaFile(){ //get the mobile Pictures directory
	 * File picDir =
	 * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES
	 * );
	 * 
	 * //get the current time String timeStamp = new
	 * SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	 * 
	 * return new File(picDir.getPath() + File.separator + "IMAGE_"+ timeStamp +
	 * ".jpg"); }
	 */
	// zhaoyu0402
	private Camera.PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0,
			// data.length);
			// mCamera = camera;
			jpgData = null;
			jpgData = new byte[data.length];
			System.arraycopy(data, 0, jpgData, 0, data.length);
			// photoView.setImageBitmap(mBitmap);
			rlyConfirm.setVisibility(View.VISIBLE);
			txtCapture.setText(R.string.re_camer);
			captureStatus = CaptureStatus.RECAPTURE;
		}
	};
}