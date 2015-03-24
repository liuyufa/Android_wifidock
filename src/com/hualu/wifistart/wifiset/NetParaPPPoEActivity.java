package com.hualu.wifistart.wifiset;

import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.GetSsidInfo;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;
import com.hualu.wifistart.wifisetting.utils.SaveData;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class NetParaPPPoEActivity extends Activity {
	public String pageid;
	public Spinner spinner_adr_type;
	private Button re_button, save_button;
	private ImageView imgHome, imgBack;
	//public String[] adr_type = new String[] { "动态IP", "静态IP", "拨号网络", "3G拨号上网" };
	public ArrayAdapter<String> adr_adapter;
	public EditText edit_account, edit_pwd;
	public String account = "user", pwd = "password", newaccount = "",
			newpwd = "";
	public static int result;
	private String routercmd = "http://10.10.1.1/:.wop:smode:router";
	private String pppoecmd = "http://10.10.1.1/:.wop:srouter:pppoe:";
	//public String[] area = new String[] { "中国" };
	//public String[] isp = new String[] { "中国电信", "中国移动", "中国联通" };
	//public ArrayAdapter<String> area_adapter;
	//public ArrayAdapter<String> isp_adapter;
	//public Spinner spinner_area;
	//public Spinner spinner_isp;
	public int flag = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.netpara_pppoe);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new pppoebtnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new pppoebtnClickListener());
		save_button = (Button) findViewById(R.id.save_button);
		save_button.setOnClickListener(new pppoebtnClickListener());
		re_button = (Button) findViewById(R.id.re_button);
		re_button.setOnClickListener(new pppoebtnClickListener());
		edit_account = (EditText) findViewById(R.id.edit_account);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);
		edit_account.setHorizontallyScrolling(true);
		edit_pwd.setHorizontallyScrolling(true);
		spinner_adr_type = (Spinner) findViewById(R.id.spinner_adr_type);
		adr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.address_type));
		adr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_adr_type.setAdapter(adr_adapter);
		spinner_adr_type
				.setOnItemSelectedListener(new pppoeAdrSpinnerSelectedListener());
		spinner_adr_type.setVisibility(View.VISIBLE);
		spinner_adr_type.setSelection(2);
		String ssid = GetSsidInfo.getbssid(this);
		String result = SaveData.load("getinfo", ssid + "pppoeinfo.txt", this);
		if (!result.equals("NG") && result.length() != 0) {
			String[] info = result.split(",");
			account = info[1];
			pwd = info[2];
		}
		newaccount = account;
		newpwd = pwd;
		/*
		 * if(HttpForWiFiUtils.pppoeinfo!=null){
		 * prasePPPoE(HttpForWiFiUtils.pppoeinfo); }
		 */
		edit_account.setText(account.toCharArray(), 0, account.length());
		edit_pwd.setText(pwd.toCharArray(), 0, pwd.length());
		edit_account.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				// 获取触发事件的EditText
				EditText clickEditText = (EditText) arg0;
				// 如果失去焦点
				newaccount = clickEditText.getText().toString().trim();

			}
		});
		edit_account.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newaccount = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_account.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newaccount = clickEditText.getText().toString().trim();
				return false;
			}
		});
		edit_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				// 获取触发事件的EditText
				EditText clickEditText = (EditText) arg0;
				newpwd = clickEditText.getText().toString().trim();
			}
		});
		edit_pwd.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
				newpwd = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		// 监听控件有新字符输入
		edit_pwd.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == arg1
						&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				EditText clickEditText = (EditText) arg0;
				newpwd = clickEditText.getText().toString().trim();
				return false;
			}
		});
	}

	public void prasePPPoE(String info) {
		int len = info.length();
		int accountstart = 0, accountend = 0;
		int pwdstart = 0, pwdend = 0;
		for (int i = 0; i < len; i++) {
			if ('<' == info.charAt(i) && 'U' == info.charAt(i + 1)) {
				accountstart = i + 10;
				i = i + 8;
			}
			if ('/' == info.charAt(i) && 'U' == info.charAt(i + 1)) {
				accountend = i - 1;
				i = i + 9;
			}
			if ('<' == info.charAt(i) && 'P' == info.charAt(i + 1)) {
				pwdstart = i + 8;
				i = i + 7;
			}
			if ('/' == info.charAt(i) && 'P' == info.charAt(i + 1)) {
				pwdend = i - 1;
				break;
			}
		}
		account = info.substring(accountstart, accountend);
		pwd = info.substring(pwdstart, pwdend);
		newaccount = account;
		newpwd = pwd;
	}

	class pppoeAdrSpinnerSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int index = spinner_adr_type.getSelectedItemPosition();
			switch (index) {
			case 0:
				NetParaPPPoEActivity.this.finish();
				startActivity(new Intent(NetParaPPPoEActivity.this,
						NetParaDhcpActivity.class));
				break;
			case 1:
				NetParaPPPoEActivity.this.finish();
				startActivity(new Intent(NetParaPPPoEActivity.this,
						NetParaStaticActivity.class));
				break;
			case 2:
				break;
			case 3:
				NetParaPPPoEActivity.this.finish();
				startActivity(new Intent(NetParaPPPoEActivity.this,
						NetParaThreeGActivity.class));
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	class pppoebtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				NetParaPPPoEActivity.this.finish();
				startActivity(new Intent(NetParaPPPoEActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				NetParaPPPoEActivity.this.finish();
				startActivity(new Intent(NetParaPPPoEActivity.this,
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
		if (newaccount.length() == 0) {
			ToastBuild.toast(NetParaPPPoEActivity.this,
					R.string.set_account_empty);
			return;
		}
		if (newpwd.length() == 0) {
			ToastBuild.toast(NetParaPPPoEActivity.this, R.string.set_pwd_empty);
			return;
		}
		result = HttpForWiFiUtils.HttpForWiFi(routercmd);
		if (1 == result) {
			pppoecmd = pppoecmd + newaccount + ":" + newpwd;
			result = HttpForWiFiUtils.HttpForWiFi(pppoecmd);
			if (1 == result) {
				account = newaccount;
				pwd = newpwd;
				String info = "PPPoE" + "," + account + "," + pwd;
				String ssid = GetSsidInfo.getbssid(NetParaPPPoEActivity.this);
				SaveData.load(info, ssid + "pppoeinfo.txt",
						NetParaPPPoEActivity.this);
				ToastBuild.toast(NetParaPPPoEActivity.this,
						R.string.set_save_sucess);
			}
		}
		if (0 == result) {
			ToastBuild.toast(NetParaPPPoEActivity.this, R.string.set_save_fail);
		}
	}

	private void restoreforall() {
		edit_account.setText(account.toCharArray(), 0, account.length());
		edit_pwd.setText(pwd.toCharArray(), 0, pwd.length());
		ToastBuild.toast(NetParaPPPoEActivity.this, R.string.set_re_sucess);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		NetParaPPPoEActivity.this.finish();
		startActivity(new Intent(NetParaPPPoEActivity.this,ConfigurationActivity.class));
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
