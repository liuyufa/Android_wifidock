package com.hualu.wifistart;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.hualu.wifistart.filecenter.PreparedResource;
import com.hualu.wifistart.filecenter.files.FileItemForOperation;

public class MApplication extends Application{
	/* liuyufa 20131213 add for wifiset start*/
	private boolean islive; 
	/* liuyufa 20131213 add for wifiset end*/
	private static MApplication instance = null;
	public static String CACHE_PICTURE_IMAG = "/wifidock/PictureImgcache";
	public static String CACHE_VIDEO_IMAG = "/wifidock/VideoImgcache";
	public static String CACHE_PICTURE_PATH = Environment.getExternalStorageDirectory().getPath() + CACHE_PICTURE_IMAG + File.separator;
	public static String CACHE_VIDEO_PATH = Environment.getExternalStorageDirectory().getPath() + CACHE_VIDEO_IMAG + File.separator;
	public static String VIDEO_RECORD_PATH = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
	public static String ROUTER=null;
	public static boolean isRuning;

	private static List<FileItemForOperation> mLocalitems;

	private static List<FileItemForOperation> mSmbItems;
	
	private static Map<String,List<FileItemForOperation>>PathfileItem =null;
	
	public static boolean firstSearch = false;
	public static Activity mActivity = null;
	
	
	public static Context mContext;
	
	
	
	public static Context getmContext() {
		return mContext;
	}
	public static void setmContext(Context mContext) {
		if(mContext != null){
			MApplication.mContext = null;
		}
		MApplication.mContext = mContext;
	}
	
	public static Activity getmActivity() {
		return mActivity;
	}
	public static void setmActivity(Activity mActivity) {
		if(getmActivity() == null){
			MApplication.mActivity = mActivity;
		}
	}
	
	public static boolean isFirstSearch() {
		return firstSearch;
	}
	public static void setFirstSearch(boolean firstSearch) {
		MApplication.firstSearch = firstSearch;
	}
	
	public static MApplication getInstance(){
		return instance;
	}
	private PreparedResource mResource;
	public PreparedResource getPreparedResource(){
		return mResource;
	}
	@Override
    public void onCreate() {
		instance = this;
		mResource = new PreparedResource(this);
		
	}
	/* liuyufa 20131213 add for wifiset start*/
	public boolean getState(){  
	    return this.islive;  
	}  
	
	public void setState(boolean is){  
		this.islive = is;  
	} 
	/* liuyufa 20131213 add for wifiset end*/
	
	public static List<FileItemForOperation> getmLocalitems() {
		return mLocalitems;
	}
	public static void setmLocalitems(List<FileItemForOperation> mLocalitems) {
		MApplication.mLocalitems = mLocalitems;
	}
	public static List<FileItemForOperation> getmSmbItems() {
		return mSmbItems;
	}
	public static void setmSmbItems(List<FileItemForOperation> mSmbItems) {
		MApplication.mSmbItems = mSmbItems;
	}
	public static Map<String, List<FileItemForOperation>> getPathfileItem() {
		return PathfileItem;
	}
	public static void setPathfileItem(
			Map<String, List<FileItemForOperation>> pathfileItem) {
		PathfileItem = pathfileItem;
	}
	
	
}
