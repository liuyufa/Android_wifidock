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
import android.util.Log;

public class AudioActivity extends Activity {
	private String mAudioPath;
	private String currFolder = null;
	SmbFile smbAudioFile;
	String smbAudioPath;
	File mAudioFile;
	public final static int Audio_Result=9999;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getExtras() != null)
			currFolder = getIntent().getExtras().getString("currFolder");
		else
			currFolder = null;

		Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		
		File mPhotoFile1 = new File(currFolder);
		if (!mPhotoFile1.exists()) {
			mPhotoFile1.mkdirs();
		}
		if (currFolder.startsWith("smb")) {
			smbAudioPath = currFolder;
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
		

		startActivityForResult(intent, Audio_Result);
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
				"'Audio'_yyyyMMdd_HHmmss");
		// "yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".m4a";
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//
//		FileActivity.isCamera = true;
//		finishActivity(1);
//		finish();
        Log.i("resultcode=========================", String.valueOf(resultCode));
		if (requestCode == Audio_Result) {
			
			Uri uri=data.getData();
			mAudioPath=uri.getPath();
			mAudioFile=new File(mAudioPath);
			Log.i("mypath==============================",mAudioPath);
			if (currFolder.startsWith("smb") && mAudioFile.length() != 0) {
				
				Intent intent=new Intent(this,DownloadService.class);
				intent.putExtra("srcDir",mAudioPath);
				intent.putExtra("toDir",smbAudioPath);
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
		if (mAudioFile.length() == 0) {
			mAudioFile.delete();
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
