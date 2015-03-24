
package com.hualu.wifistart.wifiset;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;

public class SetUpThreadWithLooper extends Thread {
	private static final String routercmd = "http://10.10.1.1/:.wop:smode:router";
	private static final String getmodecmd = "http://10.10.1.1/:.wop:gmode";
	private static final String setdhcpcmd = "http://10.10.1.1/:.wop:srouter:dhcp";
	private static final String getdhcpcmd = "http://10.10.1.1/:.wop:grouter";
	private Handler handler;
	private Handler MoHandler;
	public int result = 0;
	public SetUpThreadWithLooper(Handler mHandler){
		this.MoHandler = mHandler;
		handler =new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				 case WiFiSetMessages.SET_ROUTER_DHCP:
					 result = HttpForWiFiUtils.HttpForConnectCheck(routercmd);
					 switch(result){
					 case 0:
						 MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_SEND_CMD_FAIL);
						 break;
					 case 1:
						 result = HttpForWiFiUtils.HttpForGetMode(getmodecmd,1);
						 if(3 == result){							 
							 result = HttpForWiFiUtils.HttpForWiFi(setdhcpcmd);
							 if(1 == result){
								 String strFileName = Environment.getExternalStorageDirectory()+"/wifidock/xml/dhcp.xml";
								 result = HttpForWiFiUtils.HttpForWiFiXml(getdhcpcmd,strFileName);
								 if(3 == result){
									 MoHandler.sendEmptyMessage(WiFiSetMessages.SET_ROUTER_PPPOE_SUCCESS);
								 }
							 }
						 }
						 if(result!=3){
							 MoHandler.sendEmptyMessage(WiFiSetMessages.SET_ROUTER_PPPOE_FAIL);
						 }
						 break;
					 }
					 break;
				 case WiFiSetMessages.MSG_GROUTER_INFO:
						String cmd = "http://10.10.1.1/:.wop:grouter";
						result = HttpForWiFiUtils.HttpForGetMode(cmd,0);
						if(result!=0){
							String info=Integer.toString(result);
							Message rotermsg = MoHandler.obtainMessage(WiFiSetMessages.MSG_GROUTER_INFO_SUCCESS,info);
							MoHandler.sendMessage(rotermsg);
						}
						else{
							MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_GROUTER_INFO_FAIL);
						}
					 break;
				}
			}
		};
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	@Override
	public void run() {
		Looper.prepare();
		Looper.loop();
	}
}