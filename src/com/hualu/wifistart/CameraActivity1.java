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

public class CameraActivity1 extends Activity {
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

		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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
//			smbPhotoPath = currFolder + "IMAGE_" + getPhotoFileName();
//			try {
//				smbPhotoFile = new SmbFile(smbPhotoPath);
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
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		// "yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//
//		FileActivity.isCamera = true;
//		finishActivity(1);
//		finish();

		if (requestCode == CAMERA_RESULT) {
			if (currFolder.startsWith("smb") && mPhotoFile.length() != 0) {
				
				Intent intent=new Intent(this,DownloadService.class);
				intent.putExtra("srcDir",mPhotoPath);
				intent.putExtra("toDir",smbPhotoPath);
				intent.putExtra("CopyOperation","cut");
				intent.putExtra("isCut",true);
				startService(intent);
//				byte[] jpgData = new byte[1024 * 10];
//				InputStream in = null;
//				
//				try {
//					in = new BufferedInputStream(
//							new FileInputStream(mPhotoFile));
//					Log.i("in+++++++++++++++===================", in.toString());
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				int byteread = 0;
//
//				try {
//					smbPhotoFile = new SmbFile(smbPhotoPath);
//					Log.i("smbphotopath============================",
//							smbPhotoPath);
//					if (smbPhotoFile.exists()) {
//						Log.i("smbfile", "smbfile  is  exist");
//						OutputStream out = new BufferedOutputStream(
//								new SmbFileOutputStream(smbPhotoFile));
//						if (out != null) {
//							Log.i("======out", "=================");
//							while ((byteread = in.read(jpgData)) != -1) {
//								out.write(jpgData, 0, byteread);
//								// out.write(jpgData);
//							}
//							out.flush();
//							out.close();
//						}
//					}
//					in.close();
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SmbException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
              
				
				//copyFileJavaCommand(mPhotoFile, smbPhotoFile);
				//mPhotoFile.delete();
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
