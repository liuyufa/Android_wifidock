package com.hualu.wifistart.wifiset;

import java.util.StringTokenizer;

import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.GetSsidInfo;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;
import com.hualu.wifistart.wifisetting.utils.IPInfoValidityUtils;
import com.hualu.wifistart.wifisetting.utils.SaveData;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class NetParaStaticActivity extends Activity {
	public String pageid;
	public Spinner spinner_adr_type;
	private Button re_button, save_button;
	private ImageView imgHome, imgBack;
	// public String[] adr_type=new String[]{"动态IP","静态IP","拨号网络","3G拨号上网"};
	public ArrayAdapter<String> adr_adapter;
	public EditText edit_account, edit_pwd;
	public EditText edit_ipadr, edit_subnet, edit_gateway, edit_primary_domain,
			edit_backup_domain;
	public String account, pwd, newaccount = "", newpwd = "";
	public static int result;
	private String routercmd = "http://10.10.1.1/:.wop:smode:router";
	public ArrayAdapter<String> area_adapter;
	public ArrayAdapter<String> isp_adapter;
	public Spinner spinner_area;
	public Spinner spinner_isp;
	final int emptyItems[] = { R.string.set_ip_empty,
			R.string.set_subnet_empty, R.string.set_gateway_empty,
			R.string.set_primary_empty, };
	final int errorItems[] = { R.string.set_ip_error,
			R.string.set_subnet_error, R.string.set_gateway_error,
			R.string.set_primary_error, R.string.set_backup_error, };
	public String ipinfo[] = { "10.10.2.2", "255.255.255.0", "192.168.1.1",
			"6.6.6.6", "8.8.8.8" };
	public String newipinfo[] = { "", "", "", "", "" };
	public static String staticinfo = "";
	private String staticcmd = "http://10.10.1.1/:.wop:srouter:static";
	public int flag = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.netpara_static);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new staticbtnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new staticbtnClickListener());
		save_button = (Button) findViewById(R.id.save_button);
		save_button.setOnClickListener(new staticbtnClickListener());
		re_button = (Button) findViewById(R.id.re_button);
		re_button.setOnClickListener(new staticbtnClickListener());
		adr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.address_type));
		adr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_adr_type = (Spinner) findViewById(R.id.spinner_adr_type);
		spinner_adr_type.setAdapter(adr_adapter);
		spinner_adr_type
				.setOnItemSelectedListener(new staticAdrSpinnerSelectedListener());
		spinner_adr_type.setVisibility(View.VISIBLE);
		spinner_adr_type.setSelection(1);
		edit_ipadr = (EditText) findViewById(R.id.edit_ipadr);
		edit_subnet = (EditText) findViewById(R.id.edit_subnet);
		edit_gateway = (EditText) findViewById(R.id.edit_gateway);
		edit_primary_domain = (EditText) findViewById(R.id.edit_primary_domain);
		edit_backup_domain = (EditText) findViewById(R.id.edit_backup_domain);
		edit_ipadr.setHorizontallyScrolling(true);
		edit_subnet.setHorizontallyScrolling(true);
		edit_gateway.setHorizontallyScrolling(true);
		edit_primary_domain.setHorizontallyScrolling(true);
		edit_backup_domain.setHorizontallyScrolling(true);
		/*
		 * String ssid=GetSsidInfo.getbssid(this); String
		 * result=SaveData.load("getinfo", ssid+"staticinfo.txt", this);
		 * if(!result.equals("NG")){ String[] info=result.split(","); for(int
		 * i=1;i<info.length-1;i++){ newipinfo[i]=info[i+1]; }
		 * if(info.length==4){ newipinfo[4]=""; } }
		 */
		if (HttpForWiFiUtils.staticinfo != null) {
			prasestatic(HttpForWiFiUtils.staticinfo);
		} else {
			for (int i = 0; i < 5; i++) {
				newipinfo[i] = ipinfo[i];
			}
		}
		edit_ipadr
				.setText(newipinfo[0].toCharArray(), 0, newipinfo[0].length());
		edit_subnet.setText(newipinfo[1].toCharArray(), 0,
				newipinfo[1].length());
		edit_gateway.setText(newipinfo[2].toCharArray(), 0,
				newipinfo[2].length());
		edit_primary_domain.setText(newipinfo[3].toCharArray(), 0,
				newipinfo[3].length());
		edit_backup_domain.setText(newipinfo[4].toCharArray(), 0,
				newipinfo[4].length());
		edit_ipadr.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				// 获取触发事件的EditText
				EditText clickEditText = (EditText) arg0;
				// 如果失去焦点
				newipinfo[0] = clickEditText.getText().toString().trim();

			}
		});
		edit_ipadr.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newipinfo[0] = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_ipadr.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newipinfo[0] = clickEditText.getText().toString().trim();
				return false;
			}
		});
		edit_subnet.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				// 获取触发事件的EditText
				EditText clickEditText = (EditText) arg0;
				newipinfo[1] = clickEditText.getText().toString().trim();
			}
		});
		edit_subnet.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newipinfo[1] = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_subnet.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newipinfo[1] = clickEditText.getText().toString().trim();
				return false;
			}
		});
		edit_gateway.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				// 获取触发事件的EditText
				EditText clickEditText = (EditText) arg0;
				newipinfo[2] = clickEditText.getText().toString().trim();
			}
		});
		edit_gateway.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newipinfo[2] = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_gateway.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newipinfo[2] = clickEditText.getText().toString().trim();
				return false;
			}
		});
		edit_primary_domain
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						// 获取触发事件的EditText
						EditText clickEditText = (EditText) arg0;
						newipinfo[3] = clickEditText.getText().toString()
								.trim();
					}
				});
		edit_primary_domain.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newipinfo[3] = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_primary_domain.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newipinfo[3] = clickEditText.getText().toString().trim();
				return false;
			}
		});
		edit_backup_domain
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						// 获取触发事件的EditText
						EditText clickEditText = (EditText) arg0;
						newipinfo[4] = clickEditText.getText().toString()
								.trim();
					}
				});
		edit_backup_domain.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newipinfo[4] = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_backup_domain.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newipinfo[4] = clickEditText.getText().toString().trim();
				return false;
			}
		});
	}

	class staticAdrSpinnerSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int index = spinner_adr_type.getSelectedItemPosition();
			switch (index) {
			case 0:
				NetParaStaticActivity.this.finish();
				startActivity(new Intent(NetParaStaticActivity.this,
						NetParaDhcpActivity.class));
				break;
			case 1:
				break;
			case 2:
				NetParaStaticActivity.this.finish();
				startActivity(new Intent(NetParaStaticActivity.this,
						NetParaPPPoEActivity.class));
				break;
			case 3:
				NetParaStaticActivity.this.finish();
				startActivity(new Intent(NetParaStaticActivity.this,
						NetParaThreeGActivity.class));
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	class staticbtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				NetParaStaticActivity.this.finish();
				startActivity(new Intent(NetParaStaticActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				NetParaStaticActivity.this.finish();
				startActivity(new Intent(NetParaStaticActivity.this,
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
		if (checkAllString()) {
			flag = HttpForWiFiUtils.HttpForWiFi(routercmd);
			if (1 == flag) {
				if (newipinfo[0].contains("10.10.1.")) {
					ToastBuild.toast(NetParaStaticActivity.this,
							R.string.set_ip_crash_error);
					return;
				}
				for (int i = 0; i < 4; i++) {
					staticcmd = staticcmd + ":" + newipinfo[i];
					staticinfo = staticinfo + "," + newipinfo[i];
				}
				if (0 != newipinfo[4].length()) {
					staticcmd = staticcmd + "," + newipinfo[4];
					staticinfo = staticinfo + "," + newipinfo[4];
				}
				flag = HttpForWiFiUtils.HttpForWiFi(staticcmd);
				if (1 == flag) {
					for (int i = 0; i < 5; i++) {
						ipinfo[i] = newipinfo[i];
					}
					staticinfo = "STATIC" + staticinfo;
					String ssid = GetSsidInfo.getbssid(this);
					SaveData.load(staticinfo, ssid + "staticinfo.txt",
							NetParaStaticActivity.this);
					staticinfo = "";
					ToastBuild.toast(NetParaStaticActivity.this,
							R.string.set_save_sucess);
				}
			}
			if (0 == flag) {
				ToastBuild.toast(NetParaStaticActivity.this,
						R.string.set_save_fail);
			}
			return;
		}
	}

	private void restoreforall() {
		edit_ipadr.setText(ipinfo[0].toCharArray(), 0, ipinfo[0].length());
		edit_subnet.setText(ipinfo[1].toCharArray(), 0, ipinfo[1].length());
		edit_gateway.setText(ipinfo[2].toCharArray(), 0, ipinfo[2].length());
		edit_primary_domain.setText(ipinfo[3].toCharArray(), 0,
				ipinfo[3].length());
		edit_backup_domain.setText(ipinfo[4].toCharArray(), 0,
				ipinfo[4].length());
		ToastBuild.toast(NetParaStaticActivity.this, R.string.set_re_sucess);
	}

	private boolean checkAllString() {
		if (checkempty()) {
			if (checkvalidity()) {
				return true;
			}
		}
		return false;
	}

	private boolean checkempty() {
		for (int i = 0; i < 4; i++) {
			if (0 == newipinfo[i].length()) {
				ToastBuild.toast(NetParaStaticActivity.this, emptyItems[i]);
				return false;
			}
		}
		return true;
	}

	private boolean checkvalidity() {
		for (int i = 0; i < 5; i++) {
			if (!IPInfoValidityUtils.IPInfoValidity(newipinfo[i], i)) {
				ToastBuild.toast(NetParaStaticActivity.this, errorItems[i]);
				return false;
			}
		}
		return true;
	}

	public void prasestatic(String info) {
		int len = info.length();
		int ipadrstart = 0, ipadrend = 0;
		int subnetstart = 0, subnetend = 0;
		int gatewaystart = 0, gatewayend = 0;
		int primarystart = 0, primaryend = 0;
		Log.i("ipinfo", info);
		for (int i = 94; i < len; i++) {
			if ('<' == info.charAt(i) && 'I' == info.charAt(i + 1)) {
				ipadrstart = i + 4;
				i = i + 1;
			}
			if ('/' == info.charAt(i) && 'I' == info.charAt(i + 1)) {
				ipadrend = i - 1;
				i = i + 1;
			}
			if ('<' == info.charAt(i) && 'M' == info.charAt(i + 1)) {
				subnetstart = i + 6;
				i = i + 3;
			}
			if ('/' == info.charAt(i) && 'M' == info.charAt(i + 1)) {
				subnetend = i - 1;
				i = i + 5;
			}
			if ('<' == info.charAt(i) && 'g' == info.charAt(i + 1)) {
				gatewaystart = i + 9;
				i = i + 7;
			}
			if ('/' == info.charAt(i) && 'g' == info.charAt(i + 1)) {
				gatewayend = i - 1;
				i = i + 8;
			}
			if ('<' == info.charAt(i) && 'D' == info.charAt(i + 1)) {
				primarystart = i + 5;
				i = i + 3;
			}
			if ('/' == info.charAt(i) && 'D' == info.charAt(i + 1)) {
				primaryend = i - 1;
				break;
			}
		}
		ipinfo[0] = info.substring(ipadrstart, ipadrend);
		ipinfo[1] = info.substring(subnetstart, subnetend);
		ipinfo[2] = info.substring(gatewaystart, gatewayend);
		ipinfo[3] = info.substring(primarystart, primaryend);
		if (ipinfo[3].contains(",")) {
			StringTokenizer str = new StringTokenizer(ipinfo[3], ",");
			ipinfo[3] = str.nextToken();
			ipinfo[4] = str.nextToken();
		}
		for (int i = 0; i < 4; i++) {
			Log.i("ipinfo", ipinfo[i]);
		}
		for (int i = 0; i < 5; i++) {
			newipinfo[i] = ipinfo[i];
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		NetParaStaticActivity.this.finish();
		startActivity(new Intent(NetParaStaticActivity.this,ConfigurationActivity.class));
//		finishActivity(1);
//		finish();
	}

	public void dispachBackKey() {
		// Log.i(TAG,"dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
