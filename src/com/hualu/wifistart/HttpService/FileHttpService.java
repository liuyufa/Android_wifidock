package com.hualu.wifistart.HttpService;

import org.cybergarage.http.HTTPServerList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FileHttpService extends Service
{
	private String TAG = "FileHttpService";
	
	private FileServer fileServer = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	} 
	
	@Override
	public void onCreate()
	{
		super.onCreate(); 
		fileServer = new FileServer();
		fileServer.start();
		Log.i(TAG, "FileHttpService is start!");
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		HTTPServerList httpServerList = fileServer.getHttpServerList();
		httpServerList.stop(); 
		httpServerList.close(); 
		httpServerList.clear(); 
		fileServer.interrupt(); 
		
		Log.i(TAG, "FileHttpService is stop!");
		
	}

}
