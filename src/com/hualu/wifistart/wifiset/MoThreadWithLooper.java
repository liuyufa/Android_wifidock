package com.hualu.wifistart.wifiset;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;

public class MoThreadWithLooper extends Thread {
	private static final String threegmodecmd ="http://10.10.1.1/:.wop:smode:3g";
	private static final String getmodecmd ="http://10.10.1.1/:.wop:gmode";
	private static final String connectcmd ="http://10.10.1.1/:.wop:3g-connect";
	private static final String gresultcmd ="http://10.10.1.1/:.wop:3gresult";
	public static String localDir ="/data/data/com.hualu.wifistart/";
	private Handler handler;
	private Handler MoHandler;
	public int result = 0;
	public MoThreadWithLooper(Handler mHandler){
		this.MoHandler = mHandler;
		handler =new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				 case WiFiSetMessages.MSG_SEND_3G:
					 result = HttpForWiFiUtils.HttpForWiFi(threegmodecmd);
					 switch(result){
					 case 0:
						 MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_SEND_3G_FAIL);
						 break;
					 case 1:
						 result = HttpForWiFiUtils.HttpForGetMode(getmodecmd,1);
						 if(1 == result){
							 result = HttpForWiFiUtils.HttpForWiFi(connectcmd);
							 if(1 == result){
								MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_SEND_3G_SUCCESS);
							 }
						 }
						 if(0==result){
							 MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_SEND_3G_FAIL);
						 }
						 break;
					 }
					 break;
				 case WiFiSetMessages.MSG_GET_3G_DETAILS:
					 String strFileName =localDir +"3g_result.xml";
					 result = HttpForWiFiUtils.HttpForWiFiXml(gresultcmd,strFileName);
					 switch(result){
					 case 0:
						 MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_SEND_3G_FAIL);
						 break;
					 case 2:
						 MoHandler.sendEmptyMessage(WiFiSetMessages.MSG_GET_3G_DETAIL_SUCCESS);
						 break;
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