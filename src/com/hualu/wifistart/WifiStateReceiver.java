package com.hualu.wifistart;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateReceiver extends BroadcastReceiver {
	private final String TAG = "WifiStateReceiver";
	private String connectSSID="";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
		 ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  


		 connectSSID =  ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID();
		 Log.i(TAG, "network state changed DISCONNECTED=" +  connectSSID);
		 if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			 try{
				 Log.i(TAG, "wifi state changed to " + intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN));
			 }catch(Exception e){
				 e.printStackTrace();
			 }        
             
         } else 
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
        	 WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        	 connectSSID = mWifiManager.getConnectionInfo().getSSID();  
        	 Log.i("connectSSID", "network state changed CONNECTED" + connectSSID);
        	 NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//        	 ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
//			 ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        	 if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
        		 Log.i(TAG, "network state changed DISCONNECTED=" +   mWifiManager.getConnectionInfo().getSSID() +"=="+cn.getPackageName()+"="+connectSSID);
//        		 String connectSSID1 = mWifiManager.getConnectionInfo().getSSID();
//        		 Log.e("connectSSID", connectSSID);
//        		 if(connectSSID.contains("airdisk")||connectSSID.contains("Hualu")||connectSSID.contains("internet"))
        		 if(connectSSID != null){
        			 if(cn.getPackageName().equals("com.hualu.wifistart")) {
		        		 Intent newintent = new Intent(context, AlertActivity.class); 
		        		 newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        		 context.startActivity(newintent);
        			 
        			 } 
        		 }
        			else if(connectSSID == null){
        				 if(cn.getPackageName().equals("com.hualu.wifistart")) {
    		        		 Intent newintent = new Intent(context, AlertActivity.class); 
    		        		 newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		        		 context.startActivity(newintent);
            			 
            			 }
        				  if(MApplication.isRuning){
        					 Log.e("cn.getPackageName()", cn.getPackageName());
        					 Intent newintent = new Intent(context, AlertActivity.class); 
    		        		 newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		        		 context.startActivity(newintent);
        				 }
	        		 }
//        		 }
        	 }else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
       		 
        		Log.i(TAG, "network state changed CONNECTED" + connectSSID);
     			Intent intent1 = new Intent("com.hualu.wifistart.HttpService.FileHttpService");
     			context.stopService(intent1);
     	        Intent intent2 = new Intent("com.hualu.wifistart.HttpService.FileHttpService");
     	        context.startService(intent2);
        		if(cn.getPackageName().equals("com.hualu.wifistart")){
        			if(!connectSSID.contains("airdisk")&&!connectSSID.contains("Hualu")&&!connectSSID.contains("internet")){        				
			        		 Intent newintent = new Intent(context, AlertActivity.class); 
			        		 newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        		 context.startActivity(newintent); 
        			}
        		}
        	 }            
         }      
		 else {
             Log.e(TAG, "Received an unknown Wifi Intent");
         }
	}	
}
