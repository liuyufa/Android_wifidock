package com.hualu.wifistart.wifiset;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class SetupWizardActivity extends Activity {
	public int id = 1;
	private static final int PPPOE = 1;
	private static final int DHCP = 2;
	private static final int STATIC = 3;
	private static final int THREEG = 4;
	private static final int FOURG = 5;
	private Handler handler;
	private SetUpThreadWithLooper thread;
	private Runnable Runable;
	public String mode;
	RadioButton pppoe_radio, dhcp_radio, static_radio, threeg_radio;
	Button next_button;
	ImageView imgHome, imgBack;
	TextView _4Gtextview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupwizard);
		Bundle bunde = this.getIntent().getExtras();
		mode = bunde.getString("wanid");
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new btnClickListener());
		next_button = (Button) findViewById(R.id.next_button);
		next_button.setOnClickListener(new btnClickListener());
		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup1);
		group.setOnCheckedChangeListener(new RadioGroupListener());
		pppoe_radio = (RadioButton) findViewById(R.id.radio_pppoe);
		dhcp_radio = (RadioButton) findViewById(R.id.radio_dhcp);
		static_radio = (RadioButton) findViewById(R.id.radio_static);
		threeg_radio = (RadioButton) findViewById(R.id.radio_3g);
		_4Gtextview = (TextView) findViewById(R.id.textView3);
		String cmdString = "http://10.10.1.1/:.wop:g81";
		HttpPost httpRequest = new HttpPost(cmdString);
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				if (strResult.contains("G81")) {
					Log.i("myHttpMessage", "4g");
					_4Gtextview.setText(this.getResources().getString(
							R.string.set_4gnet));
				}
			}
		}

		catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		initRadio();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.SET_ROUTER_PPPOE_SUCCESS:
					SetupWizardActivity.this.finish();
					Intent intent = new Intent();
					intent.setClass(SetupWizardActivity.this,
							SettingEndPageActivity.class);
					intent.putExtra("pageid", "DHCP");
					startActivity(intent);
					break;
				case WiFiSetMessages.SET_ROUTER_PPPOE_FAIL:
				case WiFiSetMessages.MSG_SEND_CMD_FAIL:
					ToastBuild.toast(SetupWizardActivity.this,
							R.string.set_send_error);
					break;
				}
			}
		};
	}

	private void initRadio() {
		if (mode.contains("3g")) {
			threeg_radio.setChecked(true);
		} else if (mode.contains("dhcp")) {
			dhcp_radio.setChecked(true);
		} else if (mode.contains("pppoe") || mode.contains("repeater")) {
			pppoe_radio.setChecked(true);
		} else if (mode.contains("static")) {
			static_radio.setChecked(true);
		}
	}

	class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == pppoe_radio.getId()) {
				id = PPPOE;
			} else if (checkedId == dhcp_radio.getId()) {
				id = DHCP;
			} else if (checkedId == static_radio.getId()) {
				id = STATIC;
			} else if (checkedId == threeg_radio.getId()) {
				id = THREEG;
				if (_4Gtextview.getText().toString().contains("4G")) {
					id=FOURG;
				}
			}
		}
	}

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				SetupWizardActivity.this.finish();
				startActivity(new Intent(SetupWizardActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				SetupWizardActivity.this.finish();
				startActivity(new Intent(SetupWizardActivity.this,
						ConfigurationActivity.class));
				// dispachBackKey();
				break;
			case R.id.next_button:
				setNextPage(id);
				break;
			}
		}
	}

	public void setNextPage(int id) {
		thread = new SetUpThreadWithLooper(handler);
		thread.start();
		switch (id) {
		case PPPOE:
			SetupWizardActivity.this.finish();
			Intent pppoe = new Intent(SetupWizardActivity.this,
					PPPOESettingActivity.class);
			SetupWizardActivity.this.startActivity(pppoe);
			break;
		case DHCP:
			Runable = new Runnable() {
				@Override
				public void run() {
					thread.getHandler().sendEmptyMessage(
							WiFiSetMessages.SET_ROUTER_DHCP);
				}
			};
			handler.post(Runable);
			break;
		case STATIC:
			SetupWizardActivity.this.finish();
			Intent sta = new Intent(SetupWizardActivity.this,
					StaticSettingActivity.class);
			SetupWizardActivity.this.startActivity(sta);
			break;
		case THREEG:
			SetupWizardActivity.this.finish();
			Intent threeg = new Intent(SetupWizardActivity.this,
					MobileSettingActivity.class);
			SetupWizardActivity.this.startActivity(threeg);
			break;
		case FOURG:
			SetupWizardActivity.this.finish();
			Intent fourg = new Intent(SetupWizardActivity.this,
					MobileSettingActivity.class);
			fourg.putExtra("mode", "4g");
			SetupWizardActivity.this.startActivity(fourg);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		SetupWizardActivity.this.finish();
		startActivity(new Intent(SetupWizardActivity.this,
				ConfigurationActivity.class));
		// finishActivity(1);
		// finish();
	}

	public void dispachBackKey() {
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
