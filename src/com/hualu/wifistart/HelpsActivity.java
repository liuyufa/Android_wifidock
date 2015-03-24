package com.hualu.wifistart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpsActivity extends Activity {
	ImageView imgHome, imgBack;
	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helps);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new btnClickListener());
		TextView textView = (TextView) findViewById(R.id.menuTitle);
		textView.setText(getResources().getString(R.string.setting_help));
		String option = getIntent().getExtras().getString("option");
		mWebView = (WebView) findViewById(R.id.helpswebview);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		// mWebView.loadUrl("http://weixin.qq.com/cgi-bin/readtemplate?lang=zh_CN&platform=android&t=intro_notices/notices_content#wx_60");
		// mWebView.loadUrl("http://www.baidu.com/");
		if ("connection".equals(option)) {
			mWebView.loadUrl("file:///android_asset/Connection.html");
		} else if ("readandwrite".equals(option)) {
			mWebView.loadUrl("file:///android_asset/ReadAndWrite.html");
		} else if ("toolbar".equals(option)) {
			mWebView.loadUrl("file:///android_asset/toolbar.html");
		} else if ("selectfiles".equals(option)) {
			mWebView.loadUrl("file:///android_asset/selectfiles.html");
		} else if ("copyandpaste".equals(option)) {
			mWebView.loadUrl("file:///android_asset/copyandpaste.html");
		} else if ("copyandpaste2".equals(option)) {
			mWebView.loadUrl("file:///android_asset/copyandpaste2.html");
		} else if ("deletefiles".equals(option)) {
			mWebView.loadUrl("file:///android_asset/deletefiles.html");
		} else if ("fileproperties".equals(option)) {
			mWebView.loadUrl("file:///android_asset/fileproperties.html");
		} else if ("filerename".equals(option)) {
			mWebView.loadUrl("file:///android_asset/filerename.html");
		} else if ("takephoto".equals(option)) {
			mWebView.loadUrl("file:///android_asset/takephoto.html");
		} else if ("contactsbackup".equals(option)) {
			mWebView.loadUrl("file:///android_asset/contactsbackup.html");
		} else if ("photosbackup".equals(option)) {
			mWebView.loadUrl("file:///android_asset/photosbackup.html");
		} else if ("route".equals(option)) {
			mWebView.loadUrl("file:///android_asset/route.html");
		} else if ("qanda".equals(option)) {
			mWebView.loadUrl("file:///android_asset/QandA.html");
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				HelpsActivity.this.finish();
				startActivity(new Intent(HelpsActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				setResult(RESULT_OK);
				finishActivity(1);
				finish();
				break;
			}
		}
	}

	public void dispachBackKey() {
		// Log.i(TAG,"dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
