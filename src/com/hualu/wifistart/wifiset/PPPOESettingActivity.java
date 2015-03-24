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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PPPOESettingActivity extends Activity {
	public static Handler handler;
	public SetUpThreadWithLooper thread;
	public Runnable Runable;
	private Button next_button,back_button;
	private ImageView imgHome,imgBack;
	private EditText edit_account,edit_pwd;
	private String account="user";
	private String pwd="password";
	private String routercmd="http://10.10.1.1/:.wop:smode:router";
	private String pppoecmd ="http://10.10.1.1/:.wop:srouter:pppoe:";
	public String saveinfo="";
	private static int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingpppoe);
		imgHome = (ImageView)findViewById(R.id.homebg);			
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView)findViewById(R.id.backbg);			
		imgBack.setOnClickListener(new btnClickListener());
		back_button = (Button)findViewById(R.id.back_button);
		back_button.setOnClickListener(new btnClickListener());
		next_button = (Button)findViewById(R.id.next_button); 
		next_button.setOnClickListener(new btnClickListener());
		edit_account = (EditText)findViewById(R.id.edit_account);
		edit_pwd = (EditText)findViewById(R.id.edit_pwd);
		edit_account.setHorizontallyScrolling(true);
		edit_pwd.setHorizontallyScrolling(true);
		String ssid=GetSsidInfo.getbssid(this);
		String result=SaveData.load("getinfo", ssid+"pppoeinfo.txt", this);
		if(!result.equals("NG")&&result.length()!=0){
			String[] info=result.split(",");
			account=info[1];
			pwd=info[2];
		}
		edit_account.setText(account.toCharArray(), 0, account.length());
		edit_pwd.setText(pwd.toCharArray(), 0, pwd.length());
		edit_account.setOnFocusChangeListener(new OnFocusChangeListener() {
             @Override
             public void onFocusChange(View arg0, boolean arg1) {
                 // TODO Auto-generated method stub
                 //获取触发事件的EditText
                 EditText clickEditText = (EditText)arg0;
                 //如果失去焦点
                 account =clickEditText.getText().toString().trim();

             }
         });
		edit_account.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   account =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
         //监听控件有新字符输入
		edit_account.setOnKeyListener(new OnKeyListener() {
             @Override
             public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                 // TODO Auto-generated method stub    
                 if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                     return true;
                 }
                 EditText clickEditText = (EditText)arg0;
                 account =clickEditText.getText().toString().trim();
                 return false;
             }
         });
		edit_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //获取触发事件的EditText
                EditText clickEditText = (EditText)arg0;
                pwd =clickEditText.getText().toString().trim();
            }
        });
		edit_pwd.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   pwd =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
        //监听控件有新字符输入
		edit_pwd.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub    
                if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                    return true;
                }
                EditText clickEditText = (EditText)arg0;
                pwd =clickEditText.getText().toString().trim();
                return false;
            }
        });
	}
	class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.homebg:
				PPPOESettingActivity.this.finish();
				startActivity(new Intent(PPPOESettingActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:
				PPPOESettingActivity.this.finish();
	            Intent intent=new Intent();  
	            intent.setClass(PPPOESettingActivity.this, SetupWizardActivity.class);
	            intent.putExtra("wanid", "pppoe");
	            startActivity(intent);
				//dispachBackKey();
				break;
			case R.id.back_button:								
				PPPOESettingActivity.this.finish();
	            Intent intent1=new Intent();  
	            intent1.setClass(PPPOESettingActivity.this, SetupWizardActivity.class);
	            intent1.putExtra("wanid", "pppoe");
	            startActivity(intent1);  
				//startActivity(new Intent(PPPOESettingActivity.this, SetupWizardActivity.class));
				break;
			case R.id.next_button:
				if(account.length()==0){
					ToastBuild.toast(PPPOESettingActivity.this, R.string.set_account_empty);
					break;
				}
				if(pwd.length()==0){
					ToastBuild.toast(PPPOESettingActivity.this, R.string.set_pwd_empty);
					break;
				}
				flag = HttpForWiFiUtils.HttpForWiFi(routercmd);
				if(1==flag){
					pppoecmd=pppoecmd+account+":"+pwd;
					saveinfo=account+","+pwd;
					flag = HttpForWiFiUtils.HttpForWiFi(pppoecmd);
					if(1==flag){
						PPPOESettingActivity.this.finish();
			            Intent intent2=new Intent();  
			            intent2.setClass(PPPOESettingActivity.this, SettingEndPageActivity.class);
			            intent2.putExtra("pageid", "PPPoE"+","+saveinfo);
			            startActivity(intent2);  
					}
				}
				if(0==flag){
					ToastBuild.toast(PPPOESettingActivity.this, R.string.set_send_error);
				}
				break;
			}
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		PPPOESettingActivity.this.finish();
        Intent intent1=new Intent();  
        intent1.setClass(PPPOESettingActivity.this, SetupWizardActivity.class);
        intent1.putExtra("wanid", "pppoe");
        startActivity(intent1);
//		finishActivity(1);
//		finish();
	}
	public void dispachBackKey() {
		//Log.i(TAG,"dispachBackKey");
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
