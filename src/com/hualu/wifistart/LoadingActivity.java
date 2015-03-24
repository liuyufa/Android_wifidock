package com.hualu.wifistart;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;

public class LoadingActivity extends Activity {

	private String versioncode = "";
	private String codeNumber="";
	private boolean isFirstUse;
	private SharedPreferences shared;

	private ProgressBar net_ProgressBar;
	public Handler handler;
	private String result;
	private boolean ishualu=true;
	private String connectssid;
	private ImageView view;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		net_ProgressBar = (ProgressBar) findViewById(R.id.smbProgressBar);
		net_ProgressBar.setVisibility(View.VISIBLE);
		net_ProgressBar.setProgress(0);
		Bitmap bm = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.loadingbg1);
		BitmapDrawable bd = new BitmapDrawable(this.getResources(), bm);
		view = (ImageView) findViewById(R.id.tittle);
		view.setBackgroundDrawable(bd);
		
		shared = new SharedConfig(this).GetConfig();
		
//		preferences = getSharedPreferences("isFirstUse",MODE_WORLD_READABLE);
        isFirstUse = shared.getBoolean("isFirstUse", true);
        versioncode = shared.getString("versioncode", "0");
        codeNumber = getVersionName();
        
        SysApplication.getInstance().addActivity(this); 
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				net_ProgressBar.setVisibility(View.GONE);
				if (msg.what == 1) {
					int disksCnt = (int) (msg.arg1);
					if (disksCnt == -1) {
						enterWifiSetting();
					} else if (disksCnt > 1) {
//						Intent intent = new Intent();
//						intent.setClass(LoadingActivity.this,
//								WifiStarActivity.class);
//						startActivity(intent);
						
						if (isFirstUse || !versioncode.equals(codeNumber)) {
							startActivity(new Intent(LoadingActivity.this,
									GuideViewPagerActivity.class));
						} else {
							startActivity(new Intent(LoadingActivity.this,
									WifiStarActivity.class));
						}
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						LoadingActivity.this.finish();
					} else {
						if (Singleton.isWifiShareConnected()) {
							enterWifiShare();
						} else
							enterWifiSetting();
					}
				}
			}
		};
		WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()) {
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			connectssid = wifiInfo.getSSID();
			int ipAddress=wifiInfo.getIpAddress();
			String ipdr=((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."  
	                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));  
			Log.i("==============Ipadr=================", ipdr);
            if (ipdr.startsWith("10.10.1")) {
				ishualu=true;
			}
			result = "ok";
		}
		mWifiManager = null;
		Thread t = new smbThread(handler, result, connectssid);
		t.start();
	}

	public class smbThread extends Thread {
		private Handler uihandler;
		private String mresult;
		private String connectSSID;

		public smbThread(Handler handler, String result, String ssid) {
			uihandler = handler;
			mresult = result;
			connectSSID = ssid;
		}

		public void run() {
			super.run();
			Singleton.SMB_ONLINE = false;
			if (mresult != null && mresult.equals("ok")&&ishualu) {
				String cmdString = "http://10.10.1.1/:.wop:bw";
				String cmdString1 = "http://10.10.1.1/:.wop:g81";
				String cmdString2 = "http://10.10.1.1/:.wop:el976";
				HttpPost httpRequest = new HttpPost(cmdString);
				HttpPost httpRequest1 = new HttpPost(cmdString1);
				HttpPost httpRequest2 = new HttpPost(cmdString2);

				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
				HttpConnectionParams.setSoTimeout(httpParams, 4000);
				HttpClient client = new DefaultHttpClient(httpParams);
				HttpResponse httpResponse;
                
				try {
					httpResponse = client.execute(httpRequest2);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String strResult2 = EntityUtils
								.toString(httpResponse.getEntity());
						if (strResult2.contains("EL976")) {
							MApplication.ROUTER = "EL976";
						}
					}
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					httpResponse = client.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String strResult = EntityUtils.toString(httpResponse
								.getEntity());
						if (!"bw".equals(strResult)) {
							httpResponse = client.execute(httpRequest1);
							if (httpResponse.getStatusLine().getStatusCode() == 200) {
								String strResult1 = EntityUtils.toString(httpResponse
										.getEntity());
								if (strResult1.contains("G81")) {
									MApplication.ROUTER = "G81";
								}
								Log.e("strResult=====================", strResult1
										+ "==" + MApplication.ROUTER);
							}
						}
						if (strResult.contains("bw")) {
							MApplication.ROUTER = "bw";
						}
						Log.e("strResult=====================", strResult + "=="
								+ MApplication.ROUTER);
					}

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				String cmd = "http://10.10.1.1/:.wop:ability";
				int flag = HttpForWiFiUtils.HttpForConnect(cmd);
				Log.i("===============connectssid", connectSSID);
				Log.i("===============connectflag", String.valueOf(flag));
				if (flag == 1 || connectSSID.contains("StrongRising")) {
					int disksCnt = 0;
					for (int i = 0; i < 800; i++) {
						{
							Singleton.isWifiShareConnected();
							Singleton.initDiskInfo();
							disksCnt = Singleton.instance().disks.size();
							if (disksCnt >= 1) {
								HttpForWiFiUtils.connectssid = connectSSID;
								break;
							}
							if (disksCnt > 1)
								break;
							SystemClock.sleep(100);

						}
					}
					Log.i("test", "disksCnt = " + disksCnt);
					Message sendmsg = Message.obtain();
					sendmsg.what = 1;
					sendmsg.arg1 = disksCnt;
					uihandler.sendMessage(sendmsg);
					return;
				}
			}
			Singleton.SMB_ONLINE = false;
			Message sendmsg = Message.obtain();
			sendmsg.what = 1;
			sendmsg.arg1 = -1;
			uihandler.sendMessage(sendmsg);
		}
	}

	private void enterWifiSetting() {
		final Dialog dialog = new HuzAlertDialog.Builder(LoadingActivity.this)
				.setTitle(R.string.title_comfir_delete)
				.setMessage(
						Html.fromHtml(getResources().getString(
								R.string.shareConnectFailMsg)))
				.setPositiveButton(R.string.set_done,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_WIFI_SETTINGS));
								paramDialogInterface.dismiss();
								overridePendingTransition(R.anim.in_from_right,
										R.anim.out_to_left);
								LoadingActivity.this.finish();

							}
						})
				.setNegativeButton(R.string.set_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								Singleton.offlineDiskInfo();
//								Intent intent = new Intent();
//								intent.setClass(LoadingActivity.this,
//										WifiStarActivity.class);
//								startActivity(intent);
								if (isFirstUse || !versioncode.equals(codeNumber)) {
									startActivity(new Intent(LoadingActivity.this,
											GuideViewPagerActivity.class));
								} else {
									startActivity(new Intent(LoadingActivity.this,
											WifiStarActivity.class));
								}
								paramDialogInterface.dismiss();
								overridePendingTransition(R.anim.in_from_right,
										R.anim.out_to_left);
								LoadingActivity.this.finish();
							}
						}).create();
		dialog.show();

	}

	private void enterWifiShare() {
		final Dialog dialog = new HuzAlertDialog.Builder(LoadingActivity.this)
				.setTitle(R.string.title_comfir_delete)
				.setMessage(
						Html.fromHtml(getResources().getString(
								R.string.noMediaMountedMsg)))
				.setPositiveButton(R.string.set_done,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
//								Intent intent = new Intent();
//								intent.setClass(LoadingActivity.this,
//										WifiStarActivity.class);
//								startActivity(intent);
								if (isFirstUse || !versioncode.equals(codeNumber)) {
									startActivity(new Intent(LoadingActivity.this,
											GuideViewPagerActivity.class));
								} else {
									startActivity(new Intent(LoadingActivity.this,
											WifiStarActivity.class));
								}
								paramDialogInterface.dismiss();
								overridePendingTransition(R.anim.in_from_right,
										R.anim.out_to_left);
								LoadingActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.set_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
								LoadingActivity.this.finish();
							}
						}).create();
		dialog.show();

	}

	@Override
	protected void onDestroy() {
		BitmapDrawable bd = (BitmapDrawable) view.getBackground();
		view.setBackgroundResource(0);// 别忘了把背景设为null，避免onDraw刷新背景时候出现used a
										// recycled bitmap错误
		bd.setCallback(null);
		bd.getBitmap().recycle();
		// 实例化Editor对象
//		Editor editor = preferences.edit();
//		// 存入数据
//		editor.putBoolean("isFirstUse", false);
//		// 提交修改
//		editor.commit();
		super.onDestroy();
	}
	
	private String getVersionName() {
		
    	try{
    		 PackageManager manager = this.getPackageManager();
    		 PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
    		 String version = info.versionName;
    		 return version;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    	
        
    }
}
