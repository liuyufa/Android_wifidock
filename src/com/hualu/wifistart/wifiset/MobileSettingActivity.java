package com.hualu.wifistart.wifiset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.Intents;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hualu.wifistart.R;
import com.hualu.wifistart.R.array;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.GetSsidInfo;
import com.hualu.wifistart.wifisetting.utils.SaveData;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class MobileSettingActivity extends Activity {
	private Handler Mobilehandler;
	private Runnable Runable;
	private MoThreadWithLooper thread;
	public Spinner spinner_area;
	public Spinner spinner_isp;
	public Button button_next;
	public Button button_back;
	private ImageView imgHome, imgBack;
	private TextView tView;
	// public String[] area = new String[] { "中国" };
	// public String[] isp = new String[] { "中国电信", "中国移动", "中国联通" };
	public ArrayAdapter<String> area_adapter;
	public ArrayAdapter<String> isp_adapter;
	public static int isoperator = 0;
	public static String saveinfo = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String modeString = getIntent().getExtras().getString("mode");
		setContentView(R.layout.mobile_setting);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new btnClickListener());
		spinner_area = (Spinner) findViewById(R.id.spinner_area);
		spinner_isp = (Spinner) findViewById(R.id.spinner_isp);
		button_next = (Button) findViewById(R.id.next_button);
		button_back = (Button) findViewById(R.id.back_button);
		tView = (TextView) findViewById(R.id.set_guidetv);
		if ("4g".equals(modeString)) {
			tView.setText(getResources().getString(R.string.set_guide_4isp));
		}
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
		String ssid = GetSsidInfo.getbssid(this);
		String result = SaveData.load("getinfo", ssid + "3ginfo.txt", this);
		if (!result.equals("NG") && result.length() != 0) {
			String[] info = result.split(",");
			spinner_isp.setSelection(Integer.parseInt(info[1]));
		}
		Mobilehandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.MSG_SEND_3G_SUCCESS:
					Mobilehandler.removeCallbacks(Runable);
					Runable = new Runnable() {
						@Override
						public void run() {
							thread.getHandler().sendEmptyMessage(
									WiFiSetMessages.MSG_GET_3G_DETAILS);
						}
					};
					Mobilehandler.post(Runable);
					break;
				case WiFiSetMessages.MSG_SEND_3G_FAIL:
					Mobilehandler.removeCallbacks(Runable);
					ToastBuild.toast(MobileSettingActivity.this,
							R.string.set_send_error);
					break;
				case WiFiSetMessages.MSG_GET_3G_DETAIL_SUCCESS:
					Mobilehandler.removeCallbacks(Runable);
					MobileSettingActivity.this.finish();
					saveinfo = "";
					saveinfo = "3G" + "," + isoperator;
					Intent intent = new Intent();
					intent.setClass(MobileSettingActivity.this,
							SettingEndPageActivity.class);
					intent.putExtra("pageid", saveinfo);
					startActivity(intent);
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

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				MobileSettingActivity.this.finish();
				startActivity(new Intent(MobileSettingActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				MobileSettingActivity.this.finish();
				Intent intent1 = new Intent();
				intent1.setClass(MobileSettingActivity.this,
						SetupWizardActivity.class);
				intent1.putExtra("wanid", "3g");
				startActivity(intent1);
				// dispachBackKey();
				break;
			}
		}
	}

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.next_button:
			// if (isoperator == 2) {
			thread = new MoThreadWithLooper(Mobilehandler);
			thread.start();
			Runable = new Runnable() {
				@Override
				public void run() {
					thread.getHandler().sendEmptyMessage(
							WiFiSetMessages.MSG_SEND_3G);
				}
			};
			Mobilehandler.post(Runable);
			// } else {
			/* 移动，电信暂不支持 */
			// ToastBuild.toast(MobileSettingActivity.this,
			// R.string.set_isp_error);
			// }
			break;
		case R.id.back_button:
			MobileSettingActivity.this.finish();
			Intent intent1 = new Intent();
			intent1.setClass(MobileSettingActivity.this,
					SetupWizardActivity.class);
			intent1.putExtra("wanid", "3g");
			startActivity(intent1);
			// startActivity(new Intent(MobileSettingActivity.this,
			// SetupWizardActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		MobileSettingActivity.this.finish();
		Intent intent1 = new Intent();
		intent1.setClass(MobileSettingActivity.this, SetupWizardActivity.class);
		intent1.putExtra("wanid", "3g");
		startActivity(intent1);
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
