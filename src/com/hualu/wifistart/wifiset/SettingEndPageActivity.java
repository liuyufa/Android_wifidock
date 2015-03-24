package com.hualu.wifistart.wifiset;

import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.GetSsidInfo;
import com.hualu.wifistart.wifisetting.utils.SaveData;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SettingEndPageActivity extends Activity{
	private Button back_button,save_button;
	public String pageid;
	ImageView imgHome,imgBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingendpage);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		pageid=bundle.getString("pageid");
		imgHome = (ImageView)findViewById(R.id.homebg);			
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView)findViewById(R.id.backbg);			
		imgBack.setOnClickListener(new btnClickListener());
		back_button = (Button)findViewById(R.id.back_button); 
		back_button.setOnClickListener(new btnClickListener());
		save_button = (Button)findViewById(R.id.save_button); 
		save_button.setOnClickListener(new btnClickListener());
	}
	class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.homebg:
				SettingEndPageActivity.this.finish();
				startActivity(new Intent(SettingEndPageActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:								
				//dispachBackKey();
				//break;
			case R.id.back_button:								
				SettingEndPageActivity.this.finish();
				if(pageid.contains("PPPoE")){
					startActivity(new Intent(SettingEndPageActivity.this, PPPOESettingActivity.class));
				}
				else if(pageid.contains("DHCP")){
					SettingEndPageActivity.this.finish();
		            Intent intent1=new Intent();  
		            intent1.setClass(SettingEndPageActivity.this, SetupWizardActivity.class);
		            intent1.putExtra("wanid", "static");
		            startActivity(intent1);
					//startActivity(new Intent(SettingEndPageActivity.this, SetupWizardActivity.class));
				}
				else if(pageid.contains("STATIC")){
					startActivity(new Intent(SettingEndPageActivity.this, StaticSettingActivity.class));
				}
				else if(pageid.contains("3G")){
					startActivity(new Intent(SettingEndPageActivity.this, MobileSettingActivity.class));
				}
				break;
			case R.id.save_button:
				if(pageid.contains("PPPoE")){
					String ssid=GetSsidInfo.getbssid(SettingEndPageActivity.this);
					SaveData.load(pageid,ssid+"pppoeinfo.txt",SettingEndPageActivity.this);
				}
				else if(pageid.contains("DHCP")){
					//动态暂无信息，不处理
				}
				else if(pageid.contains("STATIC")){
					//String[] info=pageid.split(",");
					String ssid=GetSsidInfo.getbssid(SettingEndPageActivity.this);
					SaveData.load(pageid,ssid+"staticinfo.txt",SettingEndPageActivity.this);
				}
				else if(pageid.contains("3G")){
					//只支持联通，暂不处理
					String ssid=GetSsidInfo.getbssid(SettingEndPageActivity.this);
					SaveData.load(pageid,ssid+"3ginfo.txt",SettingEndPageActivity.this);
				}
				ToastBuild.toast(SettingEndPageActivity.this, R.string.set_save_sucess);
				break;	
			}
		}
	}
	@Override
	public void onBackPressed() {
		SettingEndPageActivity.this.finish();
		if(pageid.contains("PPPoE")){
			startActivity(new Intent(SettingEndPageActivity.this, PPPOESettingActivity.class));
		}
		else if(pageid.contains("DHCP")){
			SettingEndPageActivity.this.finish();
            Intent intent1=new Intent();  
            intent1.setClass(SettingEndPageActivity.this, SetupWizardActivity.class);
            intent1.putExtra("wanid", "static");
            startActivity(intent1);
			//startActivity(new Intent(SettingEndPageActivity.this, SetupWizardActivity.class));
		}
		else if(pageid.contains("STATIC")){
			startActivity(new Intent(SettingEndPageActivity.this, StaticSettingActivity.class));
		}
		else if(pageid.contains("3G")){
			startActivity(new Intent(SettingEndPageActivity.this, MobileSettingActivity.class));
		}
	}
	public void dispachBackKey() {
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
