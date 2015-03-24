package com.hualu.wifistart.smbsrc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import android.os.Environment;
import android.util.Log;

import com.hualu.wifistart.GetConfigInfo;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.smbsrc.Helper.wfDiskInfo;
import com.hualu.wifistart.wifisetting.utils.LanguageCheck;

public class Singleton {
	static private Singleton vThis = null;
	static public String LOCAL_ROOT_WIFIDOCK = Environment.getExternalStorageDirectory().getPath() +"/WifiDock/";
	static public String LOCAL_ROOT = Environment.getExternalStorageDirectory().getPath() + File.separator;	
	static public String SMB_DOWNLOAD_TEMP = Environment.getExternalStorageDirectory().getPath() + "/.temp";
	public final static String SMB_ROOT_ONE = "smb://Hualu:123456@10.10.1.1/Hualu/";
	public final static String SMB_ROOT_AIRDISK = "smb://Hualu:123456@10.10.1.1/Hualu/";
	static public String SMB_ROOT = SMB_ROOT_ONE;
	static public boolean SMB_ONLINE = false;
	static public int screenW ,screenH;
	static public String localDir = "/data/data/com.hualu.wifistart/files";
	static public Map<String,String[]> fileKinds = new HashMap<String,String[]>();
	static {
		fileKinds.put("music",new String[]{".wav",".wma",".mp3",".aac",".ogg",".m4a"});
		fileKinds.put("video",new String[]{".avi",".flv",".f4v",".mpg",".mp4",".rmvb",
				".rm",".mkv",".wmv",".asf",".3gp",".divx",".mpeg","mov","ram","vod"});
		fileKinds.put("photo",new String[]{".jpg",".gif",".bmp",".png",".jpeg",".tif"});
		fileKinds.put("txt",new String[]{".txt",".xml",".pdf",".doc","doxx",".docx",".wps",".xls","xlsx","xlsx","ppt"});	
		fileKinds.put("all",null);
	}
	
	public static final String SELECTED_SETTING = "setting";
	public static final String SELECTED_LOCAL 	= "localdisk";
	public static final String SELECTED_SDCARD1 = "sdcard1";
	public static final String SELECTED_SDCARD2 = "sdcard2";
	

	public ArrayList<String> pasteFiles = new ArrayList<String>();

	public Map<String, String> appContext = new HashMap<String, String>();

	public ArrayList<wfDiskInfo> disks = new ArrayList<wfDiskInfo>();
	
	public List<HashMap<String, Object>> selectedItems = new ArrayList<HashMap<String, Object>>();
	
	private final static String TAG = "Singleton";
	protected Singleton() {
		
	}

	public static Singleton instance() {
		if (vThis == null) {
			return vThis = new Singleton();
		} else {
			return vThis;
		}
	}
	
	public static boolean isBelong(String absolutePath, String[] suffixes) {
		if(suffixes==null) return true; 
		for (String aString:suffixes)
			if (absolutePath.toLowerCase(Locale.getDefault()).endsWith(aString))	return true;
		return false;
	}
	public static boolean isWifiShareConnected(){
		SmbHelper smbHelper = new SmbHelper();
		SMB_ROOT = SMB_ROOT_ONE;
		Log.i("SMB","isWifiShareConnected " + SMB_ROOT);
		try {
			SMB_ONLINE = smbHelper.isSmbConnect(SMB_ROOT);
			Log.i("SMB","isSmbConnect " + SMB_ONLINE + " " + SMB_ROOT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(SMB_ONLINE)
			return SMB_ONLINE;
		SMB_ROOT = SMB_ROOT_AIRDISK;
		try {
			SMB_ONLINE = smbHelper.isSmbConnect(SMB_ROOT);
			Log.i("SMB","isSmbConnect2 " + SMB_ONLINE + " " + SMB_ROOT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SMB_ONLINE;
	}
	static public void offlineDiskInfo(){
		Singleton.instance().disks.clear();
		if(LOCAL_ROOT.startsWith("/mnt") || (LOCAL_ROOT.startsWith("/storage") && !LOCAL_ROOT.startsWith("/storage/emulated")))
			LOCAL_ROOT = Environment.getExternalStorageDirectory().getParent() + File.separator;
		if(LanguageCheck.isZh()){
			Singleton.instance().disks.add(new wfDiskInfo(Singleton.LOCAL_ROOT,"本地"));
		}
		else{
			Singleton.instance().disks.add(new wfDiskInfo(Singleton.LOCAL_ROOT,"Local"));
		}
		 File file = new File(Singleton.LOCAL_ROOT_WIFIDOCK);
         if (!file.exists()) 
             file.mkdirs();
	}
	static public void initDiskInfo(){
		Singleton.instance().disks.clear();
		String smbRoot = Singleton.SMB_ROOT;
		if(LOCAL_ROOT.startsWith("/mnt") || (LOCAL_ROOT.startsWith("/storage") && !LOCAL_ROOT.startsWith("/storage/emulated")))
			LOCAL_ROOT = Environment.getExternalStorageDirectory().getParent() + File.separator;
		String localRoot =Singleton.LOCAL_ROOT;
		SmbHelper smbHelper = new SmbHelper();
		try {
			List<SmbFile> allFile = smbHelper.dir(smbRoot,false);
	         File file = new File(localDir); 
             if (!file.exists())  
             {  
               file.mkdirs();  
             }
			for (SmbFile smbFile : allFile) {
				wfDiskInfo info = new wfDiskInfo();
				info.path = smbFile.getPath();		
				Log.i(TAG,"liuyufa" + info.path);
				if(info.path.equals("smb://Hualu:123456@10.10.1.1/Hualu/.config")) {
					Log.d(TAG, "find .config file");
					info.des = ".config";
					continue;
				}
				if(info.path.contains("smb://Hualu:123456@10.10.1.1/Hualu/sd")) {
					GetConfigInfo.deleteConfig(localDir +File.separator+".config");
					String uri = info.path;
					GetConfigInfo.smbGet(uri+File.separator+".config", localDir);
					info.des = GetConfigInfo.getConfiginfo(GetConfigInfo.readFileSdcardFile(localDir +File.separator+".config"));
				}
				if(info.path.contains("smb://Hualu:123456@10.10.1.1/Hualu/awsd")){
					info.des = "扩展坞";
				}
				Singleton.instance().disks.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(LanguageCheck.isZh()){
			Singleton.instance().disks.add(new wfDiskInfo(localRoot,"本地"));
		}
		else{
			Singleton.instance().disks.add(new wfDiskInfo(localRoot,"Local"));
		}
		 File file = new File(Singleton.LOCAL_ROOT);
         if (!file.exists())
             file.mkdirs();
	}
}
