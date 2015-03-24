package com.hualu.wifistart.filecenter;

import java.util.HashMap;

import com.hualu.wifistart.R;

import android.content.Context;
import android.graphics.Bitmap;

public class PreparedResource {
    //private final String TAG = PreparedResource.class.getCanonicalName();
	private final static String FILE_TYPE_DISK = "disk";
    private final static String FILE_TYPE_AAC = "aac";
    private final static String FILE_TYPE_BIN = "bin";
    //private final static String FILE_TYPE_BMP = "bmp";
    private final static String FILE_TYPE_DOC = "doc";
    private final static String FILE_TYPE_DOCX = "docx";
    private final static String FILE_TYPE_PDF = "pdf";
    private final static String FILE_TYPE_PPT = "ppt";
    private final static String FILE_TYPE_TXT = "txt";
    private final static String FILE_TYPE_WAV = "wav";
    private final static String FILE_TYPE_WMA = "wma";
    private final static String FILE_TYPE_MP3 = "mp3";
    private final static String FILE_TYPE_XML = "xml";
    private final static String FILE_TYPE_HTML = "html";
    private final static String FILE_TYPE_APK = "apk";
    private final static String FILE_TYPE_ZIP = "zip";
    private final static String FILE_TYPE_RAR = "rar";
    private final static String FILE_TYPE_FOLDER = "folder";
    
    
    
    private HashMap<String, Integer> fileType = new HashMap<String, Integer>();
    private HashMap<String, Bitmap> apkIcon = new HashMap<String, Bitmap>();
    private HashMap<String, String> defaultVideoType = new HashMap<String,String>();
    private HashMap<String, String> defaultAudioType = new HashMap<String,String>();
    private HashMap<String, String> defaultImageType = new HashMap<String,String>();
    private HashMap<String, String> defaultTxtType = new HashMap<String,String>();
	
	private HashMap<String, String> defaultSupportAudioType = new HashMap<String,String>();
    //private Context mContext;
	/*
    public PreparedResource(Context context){
        this.mContext = context;
        fileType.put(FILE_TYPE_DISK, R.drawable.disk);
        fileType.put(FILE_TYPE_AAC, R.drawable.aac);
        fileType.put(FILE_TYPE_BIN, R.drawable.bin);
        fileType.put(FILE_TYPE_BMP, R.drawable.picture);
        fileType.put(FILE_TYPE_DOC, R.drawable.doc);
        fileType.put(FILE_TYPE_DOCX, R.drawable.doc);
        fileType.put(FILE_TYPE_PDF, R.drawable.pdf);
        fileType.put(FILE_TYPE_PPT, R.drawable.ppt);
        fileType.put(FILE_TYPE_TXT, R.drawable.txticon);
        fileType.put(FILE_TYPE_WAV, R.drawable.wav);
        fileType.put(FILE_TYPE_WMA, R.drawable.wma);
        fileType.put(FILE_TYPE_MP3, R.drawable.mp3);
        fileType.put(FILE_TYPE_XML, R.drawable.xml);
        fileType.put(FILE_TYPE_HTML, R.drawable.all);       //html  
        fileType.put(FILE_TYPE_ZIP, R.drawable.zip);        //zip
        fileType.put(FILE_TYPE_RAR, R.drawable.zip);        //rar
        fileType.put(FILE_TYPE_FOLDER, R.drawable.foldericon);
        fileType.put(FILE_TYPE_APK, R.drawable.app_default_icon);
        loadDefaultMineType();
    }*/
        public PreparedResource(Context context){
        //this.mContext = context;
        fileType.put(FILE_TYPE_DISK, R.drawable.disk);       
        fileType.put(FILE_TYPE_BIN, R.drawable.bin);
        
		//".txt",".log",".sh",".ini","js",".xml",".pdf",".doc","doxx",".docx",".xls","xlsx","ppt"
        fileType.put(FILE_TYPE_DOC, R.drawable.docicon);
        fileType.put(FILE_TYPE_DOCX, R.drawable.docicon);       //docx
        fileType.put("doxx", R.drawable.docicon);  
		fileType.put("xls", R.drawable.xlsicon);  
		fileType.put("xlsx", R.drawable.xlsicon);  
		fileType.put("xlsx", R.drawable.xlsicon);
		fileType.put("wps", R.drawable.txticon); 
        fileType.put(FILE_TYPE_PDF, R.drawable.pdficon);
        fileType.put(FILE_TYPE_PPT, R.drawable.ppticon);
        fileType.put(FILE_TYPE_TXT, R.drawable.txticon);
		
		//".jpg",".gif",".bmp",".png",".jpeg","tif"
        
        //zhaoyu	1205
		//fileType.put(FILE_TYPE_BMP, R.drawable.pictureicon);
		//fileType.put("jpg", R.drawable.pictureicon);
		fileType.put("gif", R.drawable.pictureicon);
		//fileType.put("png", R.drawable.pictureicon);
		//fileType.put("jpeg", R.drawable.pictureicon);
		fileType.put("tif", R.drawable.pictureicon);
		
		//".wav",".wma",".mp3","aac","ogg","m4a"
        fileType.put(FILE_TYPE_WAV, R.drawable.musicicon);
        fileType.put(FILE_TYPE_WMA, R.drawable.musicicon);
        fileType.put(FILE_TYPE_MP3, R.drawable.musicicon);
		fileType.put(FILE_TYPE_AAC, R.drawable.musicicon);
		fileType.put("ogg", R.drawable.musicicon);
		//fileType.put("ape", R.drawable.musicicon);
		//fileType.put("flac", R.drawable.musicicon);
		fileType.put("m4a", R.drawable.musicicon);
		//fileType.put("mp2", R.drawable.musicicon1);
		//fileType.put("amr", R.drawable.musicicon1);
		//fileType.put("wave", R.drawable.musicicon1);
		//fileType.put("oga", R.drawable.musicicon1);
		
		
        fileType.put(FILE_TYPE_XML, R.drawable.xmlicon);		
        fileType.put(FILE_TYPE_HTML, R.drawable.all);       //html  
        fileType.put(FILE_TYPE_ZIP, R.drawable.zip);        //zip
        fileType.put(FILE_TYPE_RAR, R.drawable.zip);        //rar
        fileType.put(FILE_TYPE_FOLDER, R.drawable.foldericon);
        fileType.put(FILE_TYPE_APK, R.drawable.app_default_icon);
        loadDefaultMineType();
    }
    
    
    private void loadDefaultMineType(){
		
    	defaultVideoType.put("avi", "video/*");
    	defaultVideoType.put("flv", "video/*");
    	defaultVideoType.put("f4v", "video/*");
    	defaultVideoType.put("mpg", "video/*");
    	defaultVideoType.put("mp4", "video/*");
    	defaultVideoType.put("rmvb", "video/*");
    	defaultVideoType.put("rm", "video/*");
    	defaultVideoType.put("mkv", "video/*");
    	defaultVideoType.put("wmv", "video/*");
    	defaultVideoType.put("asf", "video/*");
    	defaultVideoType.put("3gp", "video/*");
    	defaultVideoType.put("divx", "video/*");
    	defaultVideoType.put("mpeg", "video/*");
    	defaultVideoType.put("mov", "video/*");
    	defaultVideoType.put("ram", "video/*");
    	defaultVideoType.put("vod", "video/*");
    	//defaultVideoType.put("m4v", "video/*");
    	//defaultVideoType.put("vob", "video/*");
    	//defaultVideoType.put("ts", "video/*");
    	//defaultVideoType.put("m2ts", "video/*");
    	//defaultVideoType.put("m2p", "video/*");
    	//defaultVideoType.put("d2v", "video/*");
    	//defaultVideoType.put("ogm", "video/*");
    	//defaultVideoType.put("tp", "video/*");
    	//defaultVideoType.put("iso", "video/*");
    	//defaultVideoType.put("rt", "video/*");
    	//defaultVideoType.put("qt", "video/*");
    	//defaultVideoType.put("dat", "video/*");
		//defaultVideoType.put("dv", "video/*");
		//defaultVideoType.put("pmp", "video/*");

    	
    	defaultImageType.put("png", "image/*");
    	defaultImageType.put("jpg", "image/*");
    	defaultImageType.put("jpeg", "image/*");
    	defaultImageType.put("gif", "image/*");
    	defaultImageType.put("bmp", "image/*");
		defaultImageType.put("tif", "image/*");
		
    	defaultAudioType.put("mp3", "audio/*");
    	defaultAudioType.put("wav", "audio/*");
    	defaultAudioType.put("ogg", "audio/*");
    	defaultAudioType.put("wma", "audio/*");
    	defaultAudioType.put("aac", "audio/*");
    	//defaultAudioType.put("ape", "audio/*");
    	//defaultAudioType.put("flac", "audio/*");
    	defaultAudioType.put("m4a", "audio/*");
    	//defaultAudioType.put("amr", "audio/*");
    	//defaultAudioType.put("wave", "audio/*");
    	//defaultAudioType.put("midi", "audio/*");
    	//defaultAudioType.put("mp2", "audio/*");
		//".txt",".log",".sh",".ini","js",".xml",".pdf",".doc",".docx",".xls"
		//xlsx、docx、xls、doc、txt、pdf、ppt
		
		defaultTxtType.put("txt", "txt/*");
		//defaultTxtType.put("log", "txt/*");
		//defaultTxtType.put("sh", "txt/*");
		//defaultTxtType.put("ini", "txt/*");
		//defaultTxtType.put("js", "txt/*");
		defaultTxtType.put("xml", "txt/*");
		defaultTxtType.put("pdf", "txt/*");
		defaultTxtType.put("doc", "txt/*");
		defaultTxtType.put("doxx", "txt/*");
		defaultTxtType.put("xls", "txt/*");
		
		defaultTxtType.put("xlsx", "txt/*");
		defaultTxtType.put("docx", "txt/*");
		defaultTxtType.put("wps", "txt/*");
		defaultTxtType.put("xlsx", "txt/*");
		defaultTxtType.put("ppt", "txt/*");

		defaultSupportAudioType.put("mp3", "audio/*");
		defaultSupportAudioType.put("wav", "audio/*");
		defaultSupportAudioType.put("wave", "audio/*");
		defaultSupportAudioType.put("amr", "audio/*");
		defaultSupportAudioType.put("aac", "audio/*");
    }
    
    public String getMineType(String str){
    	if(defaultAudioType.containsKey(str)){
    		return defaultAudioType.get(str);
    	} else if(defaultVideoType.containsKey(str)){
    		return defaultVideoType.get(str);
    	}else if(defaultTxtType.containsKey(str)){//zhaoyu0401
    		return defaultTxtType.get(str);
    	}else {
    		return defaultImageType.get(str);
    	}
    }
    
    public boolean isAudioFile(String key){
    	if(defaultAudioType.containsKey(key)){
    		return true;
    	}
    	return false;
    }
	 public boolean isSupportAudioFile(String key){
    	if(defaultSupportAudioType.containsKey(key)){
    		return true;
    	}
    	return false;
    }
    public boolean isVideoFile(String key){
    	if(defaultVideoType.containsKey(key))
    		return true;
    	return false;
    }
    public boolean isImageFile(String key){
    	if(defaultImageType.containsKey(key)){
    		return true;
    	}
    	return false;
    }
	public boolean isTxtFile(String key){
    	if(defaultTxtType.containsKey(key)){
    		return true;
    	}
    	return false;
    }
        
    public int getBitMap(String key){       
        if(fileType.containsKey(key)){
        	return fileType.get(key);
        }else if (isImageFile(key)) {
			return R.drawable.pictureicon;
		}else if (isVideoFile(key)) {
			return R.drawable.videoicon;
		}else{
        	return R.drawable.all;
        }
    }
    public void recycle(){
        this.apkIcon.clear();
        this.fileType.clear();
    }
}
