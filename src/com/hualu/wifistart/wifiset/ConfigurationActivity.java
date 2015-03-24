package com.hualu.wifistart.wifiset;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hualu.wifistart.MApplication;
import com.hualu.wifistart.R;
import com.hualu.wifistart.SettingActivity;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class ConfigurationActivity extends Activity {
	final int items[] = { R.string.set_guide, R.string.set_wifi };
	ArrayList<String> itemsList = new ArrayList<String>();
	public static int newVerCode = 0;
	public static String newVerName = "";
	public static String mode = "DHCP";
	public SetUpThreadWithLooper thread;
	public Runnable grouterRun;
	public Handler handler;
	public int select = -1;
	ImageView imgHome, imgBack;
	public static int settingItems[] = { R.string.set_guide,
			R.string.set_net_parameter, R.string.set_main_ap,
			R.string.set_hot_spot, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		ListView localListView = (ListView) findViewById(R.id.setlist);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new btnClickListener());
		 String cmdString = "http://10.10.1.1/:.wop:bw";
		 String cmdString1 = "http://10.10.1.1/:.wop:g81";
		 HttpPost httpRequest = new HttpPost(cmdString);
		 HttpPost httpRequest1 = new HttpPost(cmdString1);
		
		 BasicHttpParams httpParams = new BasicHttpParams();
		 HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
		 HttpConnectionParams.setSoTimeout(httpParams, 4000);
		 HttpClient client = new DefaultHttpClient(httpParams);
		 HttpResponse httpResponse;
		
		 for (int i = 0; i < settingItems.length; i++) {
		 itemsList.add(getResources().getString(settingItems[i]));
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
		 itemsList.remove(0);
		 itemsList.remove(0);
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
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.settingitem,
				R.id.itemTitle, itemsList);

		localListView.setAdapter(adapter);
		if ("bw".equals(MApplication.ROUTER)) {
			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							switch (arg2) {
							case 0: {
								Intent mainap = new Intent(
										ConfigurationActivity.this,
										MainApSettingActivity.class);
								ConfigurationActivity.this
										.startActivity(mainap);
							}
								break;
							case 1: {
								Intent hotset = new Intent(
										ConfigurationActivity.this,
										HotSettingActivity.class);
								ConfigurationActivity.this
										.startActivity(hotset);
							}
								break;
							}
						}

					});
		} else {
			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							switch (arg2) {
							case 0: {
								select = 0;
								getrouter();
							}
								break;
							case 1: {
								select = 1;
								getrouter();
							}
								break;
							case 2: {
								Intent mainap = new Intent(
										ConfigurationActivity.this,
										MainApSettingActivity.class);
								ConfigurationActivity.this
										.startActivity(mainap);
							}
								break;
							case 3: {
								Intent hotset = new Intent(
										ConfigurationActivity.this,
										HotSettingActivity.class);
								ConfigurationActivity.this
										.startActivity(hotset);
							}
								break;
							}
						}

					});
		}

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.MSG_GROUTER_INFO_SUCCESS:
					handler.removeCallbacks(grouterRun);
					String result = (String) msg.obj;
					int flag = Integer.parseInt(result);
					if (0 == select) {
						select = -1;
						getRouterConfigure(flag);
						ConfigurationActivity.this.finish();
						Intent intent = new Intent();
						intent.setClass(ConfigurationActivity.this,
								SetupWizardActivity.class);
						intent.putExtra("wanid", mode);
						startActivity(intent);
					} else {
						select = -1;
						getPage(flag);
					}
					break;
				case WiFiSetMessages.MSG_GROUTER_INFO_FAIL:
					handler.removeCallbacks(grouterRun);
					ToastBuild.toast(ConfigurationActivity.this,
							R.string.set_error);
					break;


				}
			}
		};

	}

	

	private void getrouter() {

		thread = new SetUpThreadWithLooper(handler);
		thread.start();
		grouterRun = new Runnable() {
			@Override
			public void run() {
				thread.getHandler().sendEmptyMessage(
						WiFiSetMessages.MSG_GROUTER_INFO);
			}
		};
		handler.post(grouterRun);
	}

	public void getRouterConfigure(int flag) {
		switch (flag) {
		case 1:
			mode = "3g";
			break;
		case 2:
			mode = "repeater";
			break;
		case 3:
			mode = "dhcp";
			break;
		case 4:
			mode = "pppoe";
			break;
		case 5:
			mode = "static";
			break;
		case 7:
			mode = "4g";
			break;
		default:
			break;
		}
	}

	public void getPage(int flag) {
		switch (flag) {
		case 1:
			ConfigurationActivity.this.finish();
			startActivity(new Intent(ConfigurationActivity.this,
					NetParaThreeGActivity.class));
			break;
		case 3:
			ConfigurationActivity.this.finish();
			startActivity(new Intent(ConfigurationActivity.this,
					NetParaDhcpActivity.class));
			break;
		case 2:
		case 4:
			ConfigurationActivity.this.finish();
			startActivity(new Intent(ConfigurationActivity.this,
					NetParaPPPoEActivity.class));
			break;
		case 5:
			ConfigurationActivity.this.finish();
			startActivity(new Intent(ConfigurationActivity.this,
					NetParaStaticActivity.class));
			break;
		default:
			ToastBuild.toast(ConfigurationActivity.this, R.string.set_error);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		ConfigurationActivity.this.finish();
		startActivity(new Intent(ConfigurationActivity.this,
				SettingActivity.class));
		// finishActivity(1);
		// finish();
	}

	class btnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				ConfigurationActivity.this.finish();
				startActivity(new Intent(ConfigurationActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				ConfigurationActivity.this.finish();
				startActivity(new Intent(ConfigurationActivity.this,
						SettingActivity.class));
				// dispachBackKey();
				break;
			}
		}
	}

	public void dispachBackKey() {
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
	/*
	 * test for datecheck public void load() { try { String
	 * path="/data/data/com.hualu.wifistart/files/data.txt"; File nowFile = new
	 * File(path); if(!nowFile.exists()){ writedata(); return ; }
	 * FileInputStream inStream=this.openFileInput("data.txt");
	 * ByteArrayOutputStream stream=new ByteArrayOutputStream(); byte[]
	 * buffer=new byte[1024]; int length=-1;
	 * while((length=inStream.read(buffer))!=-1) {
	 * stream.write(buffer,0,length); } stream.close(); inStream.close(); String
	 * datainfo=stream.toString(); String data=getdata();
	 * Log.i("dataold",datainfo); Log.i("datanew",data);
	 * if(!data.equals(datainfo)){ writedata();
	 * //Toast.makeText(ConfigurationActivity
	 * .this,datainfo+"/"+data,Toast.LENGTH_LONG).show(); } } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e){
	 * return ; } return ; } public String getdata(){ Time t=new Time();
	 * t.setToNow(); int year = t.year; int month = t.month; int date =
	 * t.monthDay; int hour=t.hour; int minute=t.minute; int second =t.second;
	 * String data=
	 * Integer.toString(year)+Integer.toString(month)+Integer.toString
	 * (date)+Integer
	 * .toString(hour)+Integer.toString(minute)+Integer.toString(second); return
	 * data; } public void writedata(){ String filename="data.txt"; try{
	 * FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
	 * fos.write(getdata().getBytes()); fos.close(); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e){
	 * return ; } return ; }
	 */
}
