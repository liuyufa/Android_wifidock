package com.hualu.wifistart;

import com.hualu.wifistart.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebSetting extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.websetting); 
		WebView webSetting = (WebView) findViewById(R.id.webSetting);
		webSetting.getSettings().setJavaScriptEnabled(true); 
		webSetting.loadUrl("http://10.10.1.1");
		webSetting.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

}
