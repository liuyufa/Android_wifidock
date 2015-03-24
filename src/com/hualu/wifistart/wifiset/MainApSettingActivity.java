package com.hualu.wifistart.wifiset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.wifisetting.utils.GetSsidInfo;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;
import com.hualu.wifistart.wifisetting.utils.SaveData;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainApSettingActivity extends Activity {
	public static Handler handler;
	public SetUpThreadWithLooper thread;
	public Runnable Runable;
	private Button save_button,re_button;
	private ImageView imgHome,imgBack;
	private EditText edit_ssid,edit_wpacode;
	private static String ssid="Hualu", wpacode="12345678";
	private String newssid="",newwpacode="";
	private String routercmd="http://10.10.1.1/:.wop:smode:router";
	private String apcmd ="http://10.10.1.1/:.wop:ssid:";
	private static int flag=0;
	private static int isssidng=0;
	public String bssid="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setmainap);
		imgHome = (ImageView)findViewById(R.id.homebg);			
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView)findViewById(R.id.backbg);			
		imgBack.setOnClickListener(new btnClickListener());
		save_button = (Button)findViewById(R.id.save_button);
		save_button.setOnClickListener(new btnClickListener());
		re_button = (Button)findViewById(R.id.re_button); 
		re_button.setOnClickListener(new btnClickListener());
		edit_ssid = (EditText)findViewById(R.id.edit_ssid);
		edit_wpacode = (EditText)findViewById(R.id.edit_wpacode);
		edit_ssid.setHorizontallyScrolling(true);
		edit_wpacode.setHorizontallyScrolling(true);
		WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		try{
			restoreContacts();
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (mWifiManager.isWifiEnabled()){
        	WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
    		String connectSSID = wifiInfo.getSSID();
    		bssid = GetSsidInfo.getbssid(this);
    		String cmd="http://10.10.1.1/:.wop:ability";
    		int flag = HttpForWiFiUtils.HttpForConnect(cmd);
    		if(flag==1){
    			ssid=connectSSID;
    			Log.i("ssid",ssid);
    			if(ssid.charAt(0) == '\"'){
    				int len=ssid.length();
    				ssid=ssid.substring(1, len-1);
    				Log.i("ssid",ssid);
    			}
    		}
    		/*
            String filename =bssid+"info.txt";
    		String result=SaveData.load("getinfo", filename, this);
    		if(!result.equals("NG")&&result.length()!=0){
    			String[] info=result.split(",");
    			wpacode=info[1];
    		}
    		else {
    			wpacode="12345678";
    		}
    		*/
    		String result=GetSsidInfo.getpwd();
    		Log.i("result",result);
        }
		edit_ssid.setText(ssid.toCharArray(), 0, ssid.length());
		edit_wpacode.setText(wpacode.toCharArray(), 0, wpacode.length());
		newssid=ssid;
		newwpacode=wpacode;
		edit_ssid.setOnFocusChangeListener(new OnFocusChangeListener() {
             @Override
             public void onFocusChange(View arg0, boolean arg1) {
                 // TODO Auto-generated method stub
                 //获取触发事件的EditText
                 EditText clickEditText = (EditText)arg0;
                 //如果失去焦点
                 newssid =clickEditText.getText().toString().trim();

             }
         });
		edit_ssid.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   newssid =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
         //监听控件有新字符输入
		edit_ssid.setOnKeyListener(new OnKeyListener() {
             @Override
             public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                 // TODO Auto-generated method stub    
                 if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                     return true;
                 }
                 EditText clickEditText = (EditText)arg0;
                 newssid =clickEditText.getText().toString().trim();
                 return false;
             }
         });
		edit_wpacode.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //获取触发事件的EditText
                EditText clickEditText = (EditText)arg0;
                newwpacode =clickEditText.getText().toString().trim();
            }
        });
		edit_wpacode.addTextChangedListener(new TextWatcher(){
			   public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				   newwpacode =s.toString();
			   }
			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
			   }	         
		});
        //监听控件有新字符输入
		edit_wpacode.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub    
                if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {  
                    return true;
                }
                EditText clickEditText = (EditText)arg0;
                newwpacode =clickEditText.getText().toString().trim();
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
				MainApSettingActivity.this.finish();
				startActivity(new Intent(MainApSettingActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:								
				dispachBackKey();
				break;
			case R.id.re_button:
				edit_ssid.setText(ssid.toCharArray(), 0, ssid.length());
				edit_wpacode.setText(wpacode.toCharArray(), 0, wpacode.length());
				ToastBuild.toast(MainApSettingActivity.this, R.string.set_re_sucess);
				break;
			case R.id.save_button:
				int len=newssid.length();
				if(len==0){
					ToastBuild.toast(MainApSettingActivity.this, R.string.set_account_empty);
					break;
				}
				for(int i=0;i<len;i++){
					if(newssid.charAt(i)=='-'||newssid.charAt(i)=='_'||('a'<=newssid.charAt(i)&&newssid.charAt(i)<='z')||('A'<=newssid.charAt(i)&&newssid.charAt(i)<='Z')||('0'<=newssid.charAt(i)&&newssid.charAt(i)<='9')){
						continue;
					}
					else{
						isssidng=1;
						break;
					}
				}
				if(isssidng==1){
					isssidng=0;
					ToastBuild.toast(MainApSettingActivity.this, R.string.set_account_error);
					break;
				}
				if(newwpacode.length()==0){
					ToastBuild.toast(MainApSettingActivity.this, R.string.set_pwd_empty);
					break;
				}
				if(newwpacode.length()<8){
					ToastBuild.toast(MainApSettingActivity.this, R.string.set_pwd_error);
					break;
				}
				flag = HttpForWiFiUtils.HttpForWiFi(routercmd);
				if(1==flag){
					apcmd=apcmd+newssid+":"+newwpacode;
					flag = HttpForWiFiUtils.HttpForWiFi(apcmd);
					if(1==flag){
						ssid=newssid;
						wpacode=newwpacode;
						String info=ssid+","+wpacode;
						String filename=bssid+"info.txt";
						SaveData.load(info,filename,MainApSettingActivity.this);
						//SaveWiFiInfo(filename, info);
						ToastBuild.toast(MainApSettingActivity.this, R.string.set_save_mainap);
					}
				}
				if(0==flag){
					ToastBuild.toast(MainApSettingActivity.this, R.string.set_send_error);
				}
				break;
			}
		}
	}
/*	private void SaveWiFiInfo(String filename,String info){
		try{
			FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(info.getBytes());
			fos.close();
		}
		catch(ClientProtocolException e){
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}
	public void dispachBackKey() {
		//Log.i(TAG,"dispachBackKey");
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
	/**          
	 * * 获取vCard文件中的联系人信息 
	          * @return 
	          */
	         public void restoreContacts() throws Exception {
	             //List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
	             
	             //VCardParser parse = new VCardParser();
	             //VDataBuilder builder = new VDataBuilder();
	            String file = Environment.getExternalStorageDirectory() + "/Contacts.vcf";
	        	 //String file = Environment.getExternalStorageDirectory() + "/contacts.vcf";
	             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	             
	             String vcardString = "";
	             String line;
	             while((line = reader.readLine()) != null) {
	                 vcardString += line + "\n";
	             }
	             reader.close();
	             //vcardString =vcardString.toString();
	             Log.i("vCard", vcardString);
	}
    
}
