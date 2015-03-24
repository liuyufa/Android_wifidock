package com.hualu.wifistart.filecenter.files;

import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.R;

import android.content.Context;
import android.os.Handler;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jcifs.smb.SmbFile;

public class FilePropertyAdapter {
    private Context mContext;
    public String name;
    public String type;
    public String preFolder;
    public String size;
    public String createDate;
    public String modifyDate;
    public String canWrite;
    public String canRead;
    public String isHiden;
    public FileItem fileItem;
    private int includeFiles = 0;
    private int includeFolders = 0;
    private long fileSize = 0;
    public String includeStr;
    public FilePropertyAdapter(Context context,FileItem fileItem) throws Exception{
        this.fileItem = fileItem;
        this.mContext = context;
        name = getName(fileItem);
        type = getType(fileItem);
        preFolder = getPreFolder(fileItem);
        createDate = getCreateDate(fileItem);
        modifyDate = getModifyDate(fileItem);
        canWrite = canWrite(fileItem);
        canRead = canRead(fileItem);
        isHiden = isHide(fileItem);
        
    }
    
    private String getName(FileItem fileItem){
        return fileItem.getFileName();
    }
    private String getType(FileItem fileItem){
        if(!fileItem.isDirectory()){
            return mContext.getText(R.string.file).toString();
        }
        else
            return mContext.getText(R.string.folder).toString();
    }
    private String getPreFolder(FileItem fileItem) throws Exception{
    	if(fileItem.isSmbFile()){
    		SmbFile tmpSmbFile = new SmbFile(fileItem.getFilePath());
    		return tmpSmbFile.getParent();
    	}else{
	        File tmpFile = new File(fileItem.getFilePath());
	        return tmpFile.getParent();
    	}
    }
    
    private Thread getSizeThread;
    public void getSize(final Handler handler,final Runnable runnable){
        shouldStop = false;
        getSizeThread = new Thread(){
            public void run(){
            	if(fileItem.isSmbFile()){
            		
 	                try {
 	                	SmbFile f = new SmbFile(fileItem.getFilePath());
 	                    getDirectorySize(f,handler,runnable);
 	                } catch (IOException e) {
 	                    e.printStackTrace();
 	                }
            	}else{
	                File f = new File(fileItem.getFilePath());
	                try {
	                    getDirectorySize(f,handler,runnable);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
            	}
            }
        };
        getSizeThread.start();
        
    }
    private boolean shouldStop;
    public void stopGetSize(){
        shouldStop = true;
    }
    private void getDirectorySize(File f,Handler handler,Runnable runnable) throws IOException {
        if(shouldStop) return;
        if(f.isFile()){
            fileSize = f.length();
            size = Helper.formatFromSize(fileSize);
            handler.post(runnable);
            return;
        }
        File flist[] = f.listFiles();
        if (flist == null){
            return;
        }
        int length = flist.length;
        for (int i = 0; i < length; i++) {
            if (flist[i].isDirectory()) {
                getDirectorySize(flist[i],handler,runnable);
                includeFolders++;
                includeStr = String.format(mContext.getText(R.string.include).toString(), includeFiles,includeFolders);
                handler.post(runnable);
            } else {
                fileSize = fileSize + flist[i].length();
                includeFiles++;
                includeStr = String.format(mContext.getText(R.string.include).toString(), includeFiles,includeFolders);
                size = Helper.formatFromSize(fileSize);
                handler.post(runnable);
            }
        }
    }
    
    private void getDirectorySize(SmbFile f,Handler handler,Runnable runnable) throws IOException {
        if(shouldStop) return;
        if(f.isFile()){
            fileSize = f.length();
            size = Helper.formatFromSize(fileSize);
            handler.post(runnable);
            return;
        }
        SmbFile flist[] = f.listFiles();
        if (flist == null){
            return;
        }
        int length = flist.length;
        for (int i = 0; i < length; i++) {
            if (flist[i].isDirectory()) {
                getDirectorySize(flist[i],handler,runnable);
                includeFolders++;
                includeStr = String.format(mContext.getText(R.string.include).toString(), includeFiles,includeFolders);
                handler.post(runnable);
            } else {
                fileSize = fileSize + flist[i].length();
                includeFiles++;
                includeStr = String.format(mContext.getText(R.string.include).toString(), includeFiles,includeFolders);
                size = Helper.formatFromSize(fileSize);
                handler.post(runnable);
            }
        }
    }
    private String getCreateDate(FileItem fileItem){
        return "";
    }
    private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    private String getModifyDate(FileItem fileItem){
    	if(fileItem.isSmbFile()){
    		
			try {
				SmbFile tmpFile = new SmbFile(fileItem.getFilePath());
				 long lastModifyDate = tmpFile.getLastModified();
		         if(lastModifyDate == 0)return "";
		         Date date = new Date(lastModifyDate);
		         SimpleDateFormat dateFormat = new SimpleDateFormat();
		         dateFormat.applyPattern(TIMEFORMAT);
		         return dateFormat.format(date);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
	       
    	}else{
	        File tmpFile = new File(fileItem.getFilePath());
	        long lastModifyDate = tmpFile.lastModified();
	        if(lastModifyDate == 0)return "";
	        Date date = new Date(lastModifyDate);
	        SimpleDateFormat dateFormat = new SimpleDateFormat();
	        dateFormat.applyPattern(TIMEFORMAT);
	        return dateFormat.format(date);
    	}
    }
    private String canWrite(FileItem fileItem){
        if(fileItem.isCanWrite()){
            return mContext.getText(R.string.yes).toString();
        }else
            return mContext.getText(R.string.no).toString();
    }
    private String canRead(FileItem fileItem){
        if(fileItem.isCanRead()){
            return mContext.getText(R.string.yes).toString();
        }else
            return mContext.getText(R.string.no).toString();
    }
    private String isHide(FileItem fileItem){
        if(fileItem.isHide()){
            return mContext.getText(R.string.yes).toString();
        }else
            return mContext.getText(R.string.no).toString();
    }
}
