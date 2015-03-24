package com.hualu.wifistart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hualu.wifistart.update.UpdateNew;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class WifiStarActivity extends Activity implements OnClickListener {

	private String localDir = "/data/data/com.hualu.wifistart/files";
	public ProgressDialog pBar;
	private UpdateNew mUpdateNew = new UpdateNew(WifiStarActivity.this);

	ImageView imgMusic, imgVideo, imgPicture, imgTxt, imgManage, imgSetting,
			imgCopy, imgStatus;

	public TextView mPasteNum;
	public static boolean entryFirst = true;
	public static boolean entryFromMain = true;
	public static boolean entryFromMain_phone = true;

	public static String brand = Build.BRAND;
	WifiLock mWifiLock;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifistar);
		imgMusic = (ImageView) findViewById(R.id.imgMusic);
		imgVideo = (ImageView) findViewById(R.id.imgVideo);
		imgPicture = (ImageView) findViewById(R.id.imgPicture);
		imgTxt = (ImageView) findViewById(R.id.imgTxt);
		imgManage = (ImageView) findViewById(R.id.imgManage);
		imgSetting = (ImageView) findViewById(R.id.imgSetting);
		imgCopy = (ImageView) findViewById(R.id.imgCopy);
		imgStatus = (ImageView) findViewById(R.id.imgStatus);
		mPasteNum = (TextView) findViewById(R.id.downloadNum);
		PasteCompletionListner.setView(mPasteNum);
		if (PasteCompletionListner.mFilesPaste > 0) {
			mPasteNum.setText("" + PasteCompletionListner.mFilesPaste);
		} else {
			mPasteNum.setText("");
		}
		imgMusic.setOnClickListener(this);
		imgVideo.setOnClickListener(this);
		imgPicture.setOnClickListener(this);
		imgTxt.setOnClickListener(this);
		imgManage.setOnClickListener(this);
		imgSetting.setOnClickListener(this);
		imgCopy.setOnClickListener(this);
		imgStatus.setOnClickListener(this);
		imgMusic.setClickable(true);
		imgVideo.setClickable(true);
		imgPicture.setClickable(true);
		imgTxt.setClickable(true);
		imgManage.setClickable(true);
		imgSetting.setClickable(true);
		imgCopy.setClickable(true);
		imgStatus.setClickable(true);
		SysApplication.getInstance().addActivity(this);
		Intent intent = new Intent(
				"com.hualu.wifistart.HttpService.FileHttpService");
		startService(intent);
		MApplication.isRuning = true;

		// wifilock xuw 2014/11/17
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiLock = manager.createWifiLock(
				WifiManager.WIFI_MODE_FULL_HIGH_PERF,
				"com.hualu.wifistart.WifiStarActivity");
		mWifiLock.acquire();

		// 这里来检测版本是否需要更新 //wdx 1210 s
		// 1 获得系统日期
		Date date = new Date();
		String mdate = String.valueOf(date.getDate());
		File file = new File(localDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		File dir = new File(localDir + "/date.txt");
		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch blocks
				e.printStackTrace();
			}

		}
		try {
			Context context = WifiStarActivity.this;
			// FileOutputStream outStream = this.openFileOutput("date.txt",
			// Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
			FileInputStream inStream = context.openFileInput("date.txt");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			String old_date = stream.toString();
			stream.close();
			if (!old_date.equals(mdate)) {
				FileOutputStream outStream = this.openFileOutput("date.txt",
						Context.MODE_WORLD_READABLE
								+ Context.MODE_WORLD_WRITEABLE);
				mUpdateNew.updateRun(false);
				outStream.write(mdate.getBytes());
				outStream.close();
			}
			inStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// add by Kevin.Zhang for stop http file server
		// Log.i("FileHttpService", "Stop FileHttpService");
		// Intent intent = new
		// Intent("com.hualu.wifistart.HttpService.FileHttpService");
		// stopService(intent);
		// liuyufa add 1224
		Intent intent1 = new Intent(WifiStarActivity.this,
				DownloadService.class);
		stopService(intent1);
		System.exit(0);
	}

	protected void onResume() {
		// TODO Auto-generated method stub
//		PasteCompletionListner.setView(mPasteNum);

//		if (PasteCompletionListner.mFilesPaste > 0) {
//			mPasteNum.setText("" + PasteCompletionListner.mFilesPaste);
//		} else {
//			mPasteNum.setText("");
//		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(WifiStarActivity.this, MusicService.class);
		stopService(intent);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		entryFirst = true; // wdx add 1206
		entryFromMain = true; // wdx add 1211
		entryFromMain_phone = true;// wdx add 1211
		switch (arg0.getId()) {
		case R.id.imgMusic: {
			Intent intent = new Intent(WifiStarActivity.this,
					ListActivity.class);
			intent.putExtra("appName", "music");
			// Intent intent = new Intent(WifiShareActivity.this,
			// GuideActivity.class);
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
			break;
		}
		case R.id.imgVideo: {
			Intent intent = new Intent(WifiStarActivity.this,
					ListActivity.class);
			intent.putExtra("appName", "video");
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
			break;
		}
		case R.id.imgPicture: {
			Intent intent = new Intent(WifiStarActivity.this,
					ListActivity.class);
			intent.putExtra("appName", "photo");
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
			break;
		}
		case R.id.imgTxt: {
			Intent intent = new Intent(WifiStarActivity.this,
					ListActivity.class);
			intent.putExtra("appName", "txt");
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
		}
			break;
		case R.id.imgManage: {
			Intent intent = new Intent(WifiStarActivity.this,
					FileActivity.class);
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
		}
			break;
		case R.id.imgSetting: {
			/*
			 * Intent intent = new Intent(WifiShareActivity.this,
			 * MenuActivity.class); intent.putExtra("menuType", "settings");
			 * WifiShareActivity.this.startActivity(intent);
			 * WifiShareActivity.this.overridePendingTransition(0, 0);
			 */

			Intent intent = new Intent(WifiStarActivity.this,
					SettingActivity.class);
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
		}
			break;
		case R.id.imgCopy: {
			Intent intent = new Intent(WifiStarActivity.this,
					MenuActivity.class);
			intent.putExtra("menuType", "copy");
			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
		}
			break;
		case R.id.imgStatus: {
			Intent intent = new Intent(WifiStarActivity.this, StatusActivity.class);
			intent.putExtra("listType", "transfer");	
			WifiStarActivity.this.startActivity(intent);
			
			
			
//			Intent intent = new Intent(WifiStarActivity.this,
//					MenuActivity.class);
//			intent.putExtra("menuType", "status");
//			WifiStarActivity.this.startActivity(intent);
			WifiStarActivity.this.overridePendingTransition(0, 0);
		}
			break;
		}
	}

	// wdx add 1213
	/*
	 * private boolean getServerVerCode() { try { String verjson =
	 * NetworkTool.getContent(mUpdateManager.UPDATE_SERVER +
	 * mUpdateManager.UPDATE_VERJSON); JSONArray array = new JSONArray(verjson);
	 * if (array.length() > 0) { JSONObject obj = array.getJSONObject(0); try {
	 * mUpdateManager.newVerCode = Integer.parseInt(obj.getString("verCode"));
	 * mUpdateManager.newVerName = obj.getString("verName"); } catch (Exception
	 * e) { mUpdateManager.newVerCode = -1; mUpdateManager.newVerName = "";
	 * return false; } } } catch (Exception e) { Log.e(TAG, e.getMessage());
	 * return false; } return true; }
	 */

	// wdx delete all below 1213

	// lrh add 20131216
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				/* liuyufa change for toastcolor 20140115 start */
				// Toast.makeText(getApplicationContext(), "再按一次退出程序",
				// Toast.LENGTH_SHORT).show();
				ToastBuild.toast(getApplicationContext(),
						R.string.quit_selected);
				/* liuyufa change for toastcolor 20140115 end */
				exitTime = System.currentTimeMillis();
			} else {
				// 检测文件夹是否达到设置的存储上限
				synchronized (this) {

					File file = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/wifidock/PictureImgcache");
					// File file=new File("/cache");
					if (!file.exists()) {
						file.mkdirs();
					} else {
						File[] files = file.listFiles();
						Long filesize = 0L;
						for (File file2 : files) {
							if (file2.isFile()) {
								filesize += file2.length();
							}
						}
						if (filesize > 1024 * 1024 * 200) {
							for (File file2 : files) {
								if (file2.isFile()) {
									file2.delete();
								}
							}
						}
					}
					
					File file3 = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/wifidock/VideoImgcache");
					if (!file3.exists()) {
						file3.mkdirs();
					} else {
						File[] files2 = file3.listFiles();
						Long filesize2 = 0L;
						for (File file2 : files2) {
							if (file2.isFile()) {
								filesize2 += file2.length();
							}
						}
						if (filesize2> 1024 * 1024 * 200) {
							for (File file2 : files2) {
								if (file2.isFile()) {
									file2.delete();
								}
							}
						}
					}
					
					File file4 = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/wifidock");
					if (!file4.exists()) {
						file4.mkdirs();
					} else {
						File[] files3 = file4.listFiles();
						Long filesize3 = 0L;
						for (File file2 : files3) {
							if (file2.isFile()) {
								filesize3 += file2.length();
							}
						}
						if (filesize3> 1024 * 1024 * 200) {
							for (File file2 : files3) {
								if (file2.isFile()) {
									file2.delete();
								}
							}
						}
					}
					
				}
				Intent intent1 = new Intent(WifiStarActivity.this,
						DownloadService.class);
				stopService(intent1);
				Intent intent = new Intent(
						"com.hualu.wifistart.HttpService.FileHttpService");
				stopService(intent);
				mWifiLock.release();
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// end
}
