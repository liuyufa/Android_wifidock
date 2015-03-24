package com.hualu.wifistart.wifiset;

import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.GetSsidInfo;
import com.hualu.wifistart.wifisetting.utils.SaveData;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class NetParaThreeGActivity extends Activity {
	public String pageid;
	public Spinner spinner_adr_type;
	private Button re_button, save_button;
	private ImageView imgHome, imgBack;
	// public String[] adr_type=new String[]{"动态IP","静态IP","拨号网络","3G拨号上网"};
	public ArrayAdapter<String> adr_adapter;
	public static int saveid, result;
	// public String[] area = new String[] { "中国" };
	// public String[] isp = new String[] { "中国电信", "中国移动", "中国联通" };
	public ArrayAdapter<String> area_adapter;
	public ArrayAdapter<String> isp_adapter;
	public Spinner spinner_area;
	public Spinner spinner_isp;
	private Handler mobilehandler;
	private Runnable Runable;
	private MoThreadWithLooper thread;
	public int flag = 0;
	public static int isoperator = 0;
	public static String saveinfo = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.netpara_3g);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new threegbtnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new threegbtnClickListener());
		save_button = (Button) findViewById(R.id.save_button);
		save_button.setOnClickListener(new threegbtnClickListener());
		re_button = (Button) findViewById(R.id.re_button);
		re_button.setOnClickListener(new threegbtnClickListener());
		spinner_adr_type = (Spinner) findViewById(R.id.spinner_adr_type);
		spinner_area = (Spinner) findViewById(R.id.spinner_area);
		spinner_isp = (Spinner) findViewById(R.id.spinner_isp);
		area_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.area));
		isp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.isp));
		area_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		isp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_area.setAdapter(area_adapter);
		spinner_isp.setAdapter(isp_adapter);
		spinner_area
				.setOnItemSelectedListener(new AreaSpinnerSelectedListener());
		spinner_area.setVisibility(View.VISIBLE);
		spinner_isp.setOnItemSelectedListener(new IspSpinnerSelectedListener());
		spinner_isp.setVisibility(View.VISIBLE);
		adr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.address_type));
		adr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_adr_type.setAdapter(adr_adapter);
		spinner_adr_type
				.setOnItemSelectedListener(new threegAdrSpinnerSelectedListener());
		spinner_adr_type.setVisibility(View.VISIBLE);
		spinner_adr_type.setSelection(3);
		saveid = 3;
		String ssid = GetSsidInfo.getbssid(this);
		String result = SaveData.load("getinfo", ssid + "3ginfo.txt", this);
		if (!result.equals("NG") && result.length() != 0) {
			String[] info = result.split(",");
			int sel = Integer.parseInt(info[1]);
			spinner_isp.setSelection(sel);
		}
		mobilehandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.MSG_SEND_3G_SUCCESS:
					mobilehandler.removeCallbacks(Runable);
					Runable = new Runnable() {
						@Override
						public void run() {
							thread.getHandler().sendEmptyMessage(
									WiFiSetMessages.MSG_GET_3G_DETAILS);
						}
					};
					mobilehandler.post(Runable);
					break;
				case WiFiSetMessages.MSG_SEND_3G_FAIL:
					mobilehandler.removeCallbacks(Runable);
					ToastBuild.toast(NetParaThreeGActivity.this,
							R.string.set_save_fail);
					break;
				case WiFiSetMessages.MSG_GET_3G_DETAIL_SUCCESS:
					mobilehandler.removeCallbacks(Runable);
					Intent intent = new Intent();
					intent.setClass(NetParaThreeGActivity.this,
							SettingEndPageActivity.class);
					saveinfo = "";
					saveinfo = "3G" + "," + isoperator;
					String ssid = GetSsidInfo
							.getbssid(NetParaThreeGActivity.this);
					SaveData.load(saveinfo, ssid + "3ginfo.txt",
							NetParaThreeGActivity.this);
					ToastBuild.toast(NetParaThreeGActivity.this,
							R.string.set_save_sucess);
					break;
				}
			}
		};
	}

	class AreaSpinnerSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	class IspSpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int index = spinner_isp.getSelectedItemPosition();
			switch (index) {
			case 0:
				isoperator = 0;
				break;
			case 1:
				isoperator = 1;
				break;
			case 2:
				isoperator = 2;
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	}

	class threegAdrSpinnerSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int index = spinner_adr_type.getSelectedItemPosition();
			switch (index) {
			case 0:
				NetParaThreeGActivity.this.finish();
				startActivity(new Intent(NetParaThreeGActivity.this,
						NetParaDhcpActivity.class));
				break;
			case 1:
				NetParaThreeGActivity.this.finish();
				startActivity(new Intent(NetParaThreeGActivity.this,
						NetParaStaticActivity.class));
				break;
			case 2:
				NetParaThreeGActivity.this.finish();
				startActivity(new Intent(NetParaThreeGActivity.this,
						NetParaPPPoEActivity.class));
				break;
			case 3:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	class threegbtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				NetParaThreeGActivity.this.finish();
				startActivity(new Intent(NetParaThreeGActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				NetParaThreeGActivity.this.finish();
				startActivity(new Intent(NetParaThreeGActivity.this,
						ConfigurationActivity.class));
				// dispachBackKey();
				break;
			case R.id.save_button:
				saveforall();
				break;
			case R.id.re_button:
				restoreforall();
				break;
			}
		}
	}

	private void saveforall() {
		switch (isoperator) {
		case 0:
			thread = new MoThreadWithLooper(mobilehandler);
			thread.start();
			Runable = new Runnable() {
				@Override
				public void run() {
					thread.getHandler().sendEmptyMessage(
							WiFiSetMessages.MSG_SEND_3G);
				}
			};
			mobilehandler.post(Runable);
//			ToastBuild
//					.toast(NetParaThreeGActivity.this, R.string.set_isp_error);
			break;
		case 1:
			thread = new MoThreadWithLooper(mobilehandler);
			thread.start();
			Runable = new Runnable() {
				@Override
				public void run() {
					thread.getHandler().sendEmptyMessage(
							WiFiSetMessages.MSG_SEND_3G);
				}
			};
			mobilehandler.post(Runable);
			
//			ToastBuild
//					.toast(NetParaThreeGActivity.this, R.string.set_isp_error);
			break;
		case 2:
			thread = new MoThreadWithLooper(mobilehandler);
			thread.start();
			Runable = new Runnable() {
				@Override
				public void run() {
					thread.getHandler().sendEmptyMessage(
							WiFiSetMessages.MSG_SEND_3G);
				}
			};
			mobilehandler.post(Runable);
			break;
		}
	}

	private void restoreforall() {
		ToastBuild.toast(NetParaThreeGActivity.this, R.string.set_re_sucess);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		NetParaThreeGActivity.this.finish();
		startActivity(new Intent(NetParaThreeGActivity.this,
				ConfigurationActivity.class));
		// finishActivity(1);
		// finish();
	}

	public void dispachBackKey() {
		// Log.i(TAG,"dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
