package com.hualu.wifistart.wifiset;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class StaticSettingActivity extends Activity {
	//private Handler handler;
	//private SetUpThreadWithLooper thread;
	//private Runnable Runable;
	private Button next_button,back_button;
	private ImageView imgHome,imgBack;
	public EditText edit_ipadr,edit_subnet,edit_gateway,edit_primary_domain,edit_backup_domain;
	public String ipadr="";
	public String subnet="";
	public String gateway="";
	public String primary_domain="";
	public String backup_domain="";
	public String saveinfo="";
	final int emptyItems[]={			
			R.string.set_ip_empty,
			R.string.set_subnet_empty,
			R.string.set_gateway_empty,
			R.string.set_primary_empty,};
	final int errorItems[]={
			R.string.set_ip_error,
			R.string.set_subnet_error,
			R.string.set_gateway_error,
			R.string.set_primary_error,
			R.string.set_backup_error,};
	public String ipinfo[]= {
			"10.10.2.2",
			"255.255.255.0",
			"192.168.1.1",
			"6.6.6.6",
			"8.8.8.8"	
	};
	public boolean isright=false;
	private String routercmd="http://10.10.1.1/:.wop:smode:router";
	private String staticcmd ="http://10.10.1.1/:.wop:srouter:static";
	private static int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingstatic);
		imgHome = (ImageView)findViewById(R.id.homebg);			
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView)findViewById(R.id.backbg);			
		imgBack.setOnClickListener(new btnClickListener());
		back_button = (Button)findViewById(R.id.back_button);
		back_button.setOnClickListener(new btnClickListener());
		next_button = (Button)findViewById(R.id.next_button); 
		next_button.setOnClickListener(new btnClickListener());
		edit_ipadr = (EditText)findViewById(R.id.edit_ipadr);
		edit_subnet = (EditText)findViewById(R.id.edit_subnet);
		edit_gateway = (EditText)findViewById(R.id.edit_gateway);
		edit_primary_domain = (EditText)findViewById(R.id.edit_primary_domain);
		edit_backup_domain = (EditText)findViewById(R.id.edit_backup_domain);
		edit_ipadr.setHorizontallyScrolling(true);
		edit_subnet.setHorizontallyScrolling(true);
		edit_gateway.setHorizontallyScrolling(true);
		edit_primary_domain.setHorizontallyScrolling(true);
		edit_backup_domain.setHorizontallyScrolling(true);
		String ssid=GetSsidInfo.getbssid(this);
		String result=SaveData.load("getinfo", ssid+"staticinfo.txt", this);
		if(!result.equals("NG")&&result.length()!=0){
			String[] info=result.split(",");
			for(int i=0;i<info.length-1;i++){
				ipinfo[i]=info[i+1];
			}
			if(info.length==4){
				ipinfo[4]="";
			}
		}
		edit_ipadr.setText(ipinfo[0].toCharArray(), 0, ipinfo[0].length());
		edit_subnet.setText(ipinfo[1].toCharArray(), 0, ipinfo[1].length());
		edit_gateway.setText(ipinfo[2].toCharArray(), 0, ipinfo[2].length());
		edit_primary_domain.setText(ipinfo[3].toCharArray(), 0, ipinfo[3].length());
		edit_backup_domain.setText(ipinfo[4].toCharArray(), 0, ipinfo[4].length());
		edit_ipadr.setOnFocusChangeListener(new OnFocusChangeListener() {
             @Override
             public void onFocusChange(View arg0, boolean arg1) {
                 // TODO Auto-generated method stub
                 //获取触发事件的EditText
                 EditText clickEditText = (EditText)arg0;
                 //如果失去焦点
                 ipinfo[0] =clickEditText.getText().toString().trim();

             }
         });
		edit_ipadr.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   ipinfo[0] =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
         //监听控件有新字符输入
		edit_ipadr.setOnKeyListener(new OnKeyListener() {
             @Override
             public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                 // TODO Auto-generated method stub    
                 if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                     return true;
                 }
                 EditText clickEditText = (EditText)arg0;
                 ipinfo[0] =clickEditText.getText().toString().trim();
                 return false;
             }
         });
		edit_subnet.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //获取触发事件的EditText
                EditText clickEditText = (EditText)arg0;
                ipinfo[1] =clickEditText.getText().toString().trim();
            }
        });
		edit_subnet.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   ipinfo[1] =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
        //监听控件有新字符输入
		edit_subnet.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub    
                if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                    return true;
                }
                EditText clickEditText = (EditText)arg0;
                ipinfo[1] =clickEditText.getText().toString().trim();
                return false;
            }
        });
		edit_gateway.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //获取触发事件的EditText
                EditText clickEditText = (EditText)arg0;
                ipinfo[2] =clickEditText.getText().toString().trim();
            }
        });
		edit_gateway.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   ipinfo[2] =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
        //监听控件有新字符输入
		edit_gateway.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub    
                if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                    return true;
                }
                EditText clickEditText = (EditText)arg0;
                ipinfo[2] =clickEditText.getText().toString().trim();
                return false;
            }
        });
		edit_primary_domain.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //获取触发事件的EditText
                EditText clickEditText = (EditText)arg0;
                ipinfo[3] =clickEditText.getText().toString().trim();
            }
        });
		edit_primary_domain.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   ipinfo[3] =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
        //监听控件有新字符输入
		edit_primary_domain.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub    
                if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                    return true;
                }
                EditText clickEditText = (EditText)arg0;
                ipinfo[3] =clickEditText.getText().toString().trim();
                return false;
            }
        });
		edit_backup_domain.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //获取触发事件的EditText
                EditText clickEditText = (EditText)arg0;
                ipinfo[4] =clickEditText.getText().toString().trim();
            }
        });
		edit_backup_domain.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   ipinfo[4] =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
        //监听控件有新字符输入
		edit_backup_domain.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub    
                if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                    return true;
                }
                EditText clickEditText = (EditText)arg0;
                ipinfo[4] =clickEditText.getText().toString().trim();
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
				StaticSettingActivity.this.finish();
				startActivity(new Intent(StaticSettingActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:	
				StaticSettingActivity.this.finish();
	            Intent intent1=new Intent();  
	            intent1.setClass(StaticSettingActivity.this, SetupWizardActivity.class);
	            intent1.putExtra("wanid", "static");
	            startActivity(intent1); 
				//dispachBackKey();
				break;
			case R.id.back_button:								
				StaticSettingActivity.this.finish();
	            Intent intent=new Intent();  
	            intent.setClass(StaticSettingActivity.this, SetupWizardActivity.class);
	            intent.putExtra("wanid", "static");
	            startActivity(intent); 
				//startActivity(new Intent(StaticSettingActivity.this, SetupWizardActivity.class));
				break;
			case R.id.next_button:
				if(checkAllString()){
					flag = HttpForWiFiUtils.HttpForWiFi(routercmd);
					if(1==flag){
						if(ipinfo[0].contains("10.10.1.")){
							ToastBuild.toast(StaticSettingActivity.this, R.string.set_ip_crash_error);
							break;
						}
						for(int i=0;i<4;i++){
							staticcmd=staticcmd+":"+ipinfo[i];
							saveinfo=saveinfo+","+ipinfo[i];
						}
						if(0!=ipinfo[4].length()){
							staticcmd=staticcmd+","+ipinfo[4];
							saveinfo=saveinfo+","+ipinfo[4];
						}
						flag = HttpForWiFiUtils.HttpForWiFi(staticcmd);
						if(1==flag){
				            Intent intent2=new Intent();
				            intent2.setClass(StaticSettingActivity.this, SettingEndPageActivity.class);
				            intent2.putExtra("pageid", "STATIC"+saveinfo);
				            startActivity(intent2);  
						}
					}
					if(0==flag){
						ToastBuild.toast(StaticSettingActivity.this, R.string.set_send_error);
					}
					break;
				}
				break;
			}
		}
	}
	private boolean checkAllString(){
		if(checkempty()){
			if(checkvalidity()){
				return true;
			}
		}
		return false;
	}
	private boolean checkempty(){
		for(int i=0;i<4;i++){
			if(0==ipinfo[i].length()){
				ToastBuild.toast(StaticSettingActivity.this, emptyItems[i]);
				return false;
			}
		}
		return true;
	}
	private boolean checkvalidity(){
		for(int i=0;i<5;i++){
			if(!IPInfoValidityUtils.IPInfoValidity(ipinfo[i],i)){
				ToastBuild.toast(StaticSettingActivity.this, errorItems[i]);
				return false;
			}
		}
		return true;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		StaticSettingActivity.this.finish();
        Intent intent1=new Intent();  
        intent1.setClass(StaticSettingActivity.this, SetupWizardActivity.class);
        intent1.putExtra("wanid", "static");
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
