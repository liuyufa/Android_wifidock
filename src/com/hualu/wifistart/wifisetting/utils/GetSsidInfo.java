package com.hualu.wifistart.wifisetting.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class GetSsidInfo {
	public static String connectssid="";
	public static String bssid="";
	public static String pwd="";
	public static String getbssid(Context context){
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()){
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			connectssid = wifiInfo.getBSSID();
			String cmd="http://10.10.1.1/:.wop:ability";
			int flag = HttpForWiFiUtils.HttpForConnect(cmd);
			if(flag==1){
				String[] str=connectssid.split(":");
				bssid="";
				for(int i=0;i<str.length;i++){
					bssid=bssid+str[i];
				}
				return bssid;
			}
		}
		return "NG";
	}
	public static String getpwd(){
		try {
			File policyFile = new File("/data/misc/wifi/wpa_supplicant.conf");
			FileInputStream fis=null;
			fis = new FileInputStream(policyFile);
			ByteArrayOutputStream stream=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int length=-1;
			while((length=fis.read(buffer))!=-1)   {
				stream.write(buffer,0,length);
			}
			stream.close();
			fis.close();
			pwd=stream.toString();
	        } 
			catch (FileNotFoundException e) {
				return "NG";
			}catch (Exception e) {
	            return "NG";
	        }
		return pwd;
	}
}


