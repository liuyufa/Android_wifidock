package com.hualu.wifistart;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class LockCompletionListner extends BroadcastReceiver {
	public static TextView mView = null;
	public static int mFilesPaste = 0;
	private static String TAG = "LockCompletionListner";
	
	public static void setView(TextView view){
		Log.i(TAG,"setView " + view);
		mView = view;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
			String top = cn.getClassName();
			if(top.contains("FileActivity"))
				FileActivity.getCurrBrowser().pasteComplete();
			else if(top.contains("ListActivity"))
				ListActivity.pasteComplete();
	}

}
