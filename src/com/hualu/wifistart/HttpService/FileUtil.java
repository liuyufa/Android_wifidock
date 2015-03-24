package com.hualu.wifistart.HttpService;

import java.io.File;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * 根据后缀名返回文件类型
 * 
 * @author Kevin.Zhang
 *
 */
public class FileUtil
{
	public static final String TAG = "HttpService.FileUtil";
	private static String type = "*/*";
	public static String ip = "127.0.0.1";
	public static String deviceDMRUDN = "0";
	public static String deviceDMSUDN = "0";
	public static int port = 0;
	
	
	public static String getFileType(String uri)
	{
		if (uri == null)
		{
			return type; 
		}
		
		if (uri.endsWith(".mp3"))
		{
			return "audio/mpeg"; 
		}
		
		if (uri.endsWith(".mp4"))
		{
			return "video/mp4"; 
		} 
		
		return type; 
	}
	
	public static String getDeviceDMRUDN()
	{
		return deviceDMRUDN;
	}
	
	public static String getDeviceDMSUDN()
	{
		return deviceDMSUDN;
	}
	
	/**
	 * @param path 文件名
	 * @return 创建成功返回true。否则false。
	 */
	public static boolean mkdir(String name)
	{
		boolean bool = false;
		
		boolean state = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); 
		if (state)
		{
			File f = Environment.getExternalStorageDirectory();
			String path = f.getPath(); 
			String dir = path+"/"+name+"/";
			File file = new File(dir);
			if (!file.exists())
			{
				  bool = file.mkdir(); 
			}
			else
			{
				Log.i(TAG, "-----------"+dir+"已存在----------------");
			} 

		}
		else
		{
			Log.e(TAG, "-----------------外部存储器不可用----------------");
		}
		
		
		return bool; 
	}
	
	
	

}
