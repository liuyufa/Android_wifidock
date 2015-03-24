package com.hualu.wifistart;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jcifs.smb.SmbFile;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

public class RecorderActivity1 extends Activity {
	private String mPhotoPath;
	public final static int CAMERA_RESULT = 8888;
	private String currFolder = null;
	SmbFile smbPhotoFile;
	String smbPhotoPath;
	File mPhotoFile;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getExtras() != null)
			currFolder = getIntent().getExtras().getString("currFolder");
		else
			currFolder = null;
		// Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		mPhotoPath = Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera/" + getPhotoFileName();
		File mPhotoFile1 = new File(currFolder);
		if (!mPhotoFile1.exists()) {
			mPhotoFile1.mkdirs();
		}
		
		File mPhotoFile2 = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera");
		if (!mPhotoFile2.exists()) {
			mPhotoFile2.mkdirs();
		}
		
		if (currFolder.startsWith("smb")) {
			smbPhotoPath = currFolder;
//			smbPhotoPath = currFolder + "/VIDEO_" + getPhotoFileName();
//			try {
//				smbPhotoFile = new SmbFile(smbPhotoPath);
//				smbPhotoFile.connect();
//				if (!smbPhotoFile.exists()) {
//					smbPhotoFile.createNewFile();
//				}
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SmbException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		mPhotoFile = new File(mPhotoPath);
		if (!mPhotoFile.exists()) {
			try {
				mPhotoFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
		startActivityForResult(intent, CAMERA_RESULT);
	}

	/**
	 * Description: 生成文件名称
	 * 
	 * @return
	 * @see
	 */
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat;
		if ("samsung".equalsIgnoreCase(WifiStarActivity.brand)) {
			dateFormat = new SimpleDateFormat(
					"'VIDEO_samsung'_yyyyMMdd_HHmmss");
		}else{
		dateFormat = new SimpleDateFormat(
				"'VIDEO'_yyyyMMdd_HHmmss");
		}
		return dateFormat.format(date) + ".mp4";
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAMERA_RESULT) {
			if (currFolder.startsWith("smb") && mPhotoFile.length() != 0) {
//				byte[] jpgData = new byte[1024];
//				FileInputStream in = null;
//				try {
//					smbPhotoFile.connect();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				try {
//
//					in = new FileInputStream(mPhotoFile);
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				OutputStream out = null;
//				try {
//					out = new BufferedOutputStream(new SmbFileOutputStream(
//							smbPhotoFile));
//				} catch (SmbException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try {
//					while (in.read(jpgData) != -1) {
//						out.write(jpgData);
//					}
//					out.flush();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				try {
//					out.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try {
//					in.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				mPhotoFile.delete();
				
				Intent intent=new Intent(this,DownloadService.class);
				intent.putExtra("srcDir",mPhotoPath);
				intent.putExtra("toDir",smbPhotoPath);
				intent.putExtra("CopyOperation","cut");
				intent.putExtra("isCut",true);
				startService(intent);
			}

		}
		if (mPhotoFile.length() == 0) {
			mPhotoFile.delete();
		}
		// FileActivity.isCamera = true;
		// // Intent intent = new Intent(CameraActivity1.this,
		// FileActivity.class);
		// // startActivity(intent);
		// // (CameraActivity1.this).startActivityForResult(intent, 102);
		// finishActivity(1);
		// finish();
		FileActivity.isCamera = true;
		finishActivity(1);
		finish();
	}

}
