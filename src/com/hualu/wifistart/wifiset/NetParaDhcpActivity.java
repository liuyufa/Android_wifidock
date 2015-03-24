package com.hualu.wifistart.wifiset;

import com.hualu.wifistart.R;
import com.hualu.wifistart.SettingActivity;
import com.hualu.wifistart.WifiStarActivity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class NetParaDhcpActivity extends Activity {
	public Spinner spinner_adr_type;
	private Button re_button, save_button;
	private ImageView imgHome, imgBack;
	// public String[] adr_type=new String[]{"动态IP","静态IP","拨号网络","3G拨号上网"};
	public ArrayAdapter<String> adr_adapter;
	public EditText edit_ipadr, edit_subnet, edit_gateway, edit_primary_domain,
			edit_backup_domain;
	public String[] area = new String[] { "中国" };
	public String[] isp = new String[] { "中国电信", "中国移动", "中国联通" };
	public ArrayAdapter<String> area_adapter;
	public ArrayAdapter<String> isp_adapter;
	public Spinner spinner_area;
	public Spinner spinner_isp;
	private Runnable Runable;
	private Handler dhcphandler;
	private SetUpThreadWithLooper dhcpthread;
	public int flag = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.netpara_dhcp);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgBack = (ImageView) findViewById(R.id.backbg);
		save_button = (Button) findViewById(R.id.save_button);
		re_button = (Button) findViewById(R.id.re_button);
		edit_primary_domain = (EditText) findViewById(R.id.edit_primary_domain);
		edit_backup_domain = (EditText) findViewById(R.id.edit_backup_domain);
		spinner_adr_type = (Spinner) findViewById(R.id.spinner_adr_type);
		save_button.setOnClickListener(new btnClickListener());
		re_button.setOnClickListener(new btnClickListener());
		imgBack.setOnClickListener(new btnClickListener());
		imgHome.setOnClickListener(new btnClickListener());
		adr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.address_type));
		adr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_adr_type.setAdapter(adr_adapter);
		spinner_adr_type
				.setOnItemSelectedListener(new dhcpAdrSpinnerSelectedListener());
		spinner_adr_type.setVisibility(View.VISIBLE);
		spinner_adr_type.setSelection(0);
		dhcphandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.SET_ROUTER_PPPOE_SUCCESS:
					dhcphandler.removeCallbacks(Runable);
					ToastBuild.toast(NetParaDhcpActivity.this,
							R.string.set_save_sucess);
					break;
				}
			}
		};
	}

	class dhcpAdrSpinnerSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int index = spinner_adr_type.getSelectedItemPosition();
			switch (index) {
			case 0:

				break;
			case 1:
				NetParaDhcpActivity.this.finish();
				startActivity(new Intent(NetParaDhcpActivity.this,
						NetParaStaticActivity.class));
				break;
			case 2:
				NetParaDhcpActivity.this.finish();
				startActivity(new Intent(NetParaDhcpActivity.this,
						NetParaPPPoEActivity.class));
				break;
			case 3:
				NetParaDhcpActivity.this.finish();
				startActivity(new Intent(NetParaDhcpActivity.this,
						NetParaThreeGActivity.class));
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
				NetParaDhcpActivity.this.finish();
				startActivity(new Intent(NetParaDhcpActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				NetParaDhcpActivity.this.finish();
				startActivity(new Intent(NetParaDhcpActivity.this,
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
		dhcpthread = new SetUpThreadWithLooper(dhcphandler);
		dhcpthread.start();
		Runable = new Runnable() {
			@Override
			public void run() {
				dhcpthread.getHandler().sendEmptyMessage(
						WiFiSetMessages.SET_ROUTER_DHCP);
			}
		};
		dhcphandler.post(Runable);
	}

	private void restoreforall() {
		ToastBuild.toast(NetParaDhcpActivity.this, R.string.set_re_sucess);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		NetParaDhcpActivity.this.finish();
		startActivity(new Intent(NetParaDhcpActivity.this,ConfigurationActivity.class));
		//finishActivity(1);
		//finish();
	}

	public void dispachBackKey() {
		// Log.i(TAG,"dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
