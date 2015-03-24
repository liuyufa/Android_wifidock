package com.hualu.wifistart.filecenter.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

import com.hualu.wifistart.filecenter.files.FileOperationThreadManager.CopyOperation;
import com.hualu.wifistart.filecenter.utils.Helper;

public class PasteFileThread extends Thread {
    protected CopyOperation operationType = CopyOperation.APPEND2;//CopyOperation.UNKOWN;
    FileItemForOperation mFileForOperation;
    Handler mHandler;
    //Handler responseHandler;
    public double totalSize = 0;
    public double hasPasted = 0;
    public boolean isCut;
    int totalFileNum = 0;
    public int pasted_rate;
    String srcDir;
    String dstFolder;
    public String tittle;
    public boolean cancelFlag = false;
    Hashtable<Integer, FileOperation> mSrcToDir;// = new Hashtable<Integer, FileOperation>();
    protected final String TAG = "PasteFileThread";
    final boolean DEBUG = true;
    
    public PasteFileThread(String src,String to,Handler handler,boolean isCut,CopyOperation operation){
        //this.mFileForOperation = fileForOperation;
    	//Log.i(TAG,"PasteFileThread " + src + " " + to);
        this.mHandler = handler;
        this.isCut = isCut;
        operationType = operation;
        //responseHandler = response;
    	srcDir = src;
    	dstFolder = to;
    	tittle = Helper.getFolderNameOfPath(src);
    	FileOperation item = new FileOperation(srcDir,dstFolder);
    	mSrcToDir = new Hashtable<Integer, FileOperation>();
    	mSrcToDir.put(0, item);
    }
    public PasteFileThread(){
    	
    }
    public void setCancel(){
    	cancelFlag = true;
    }
    public void getSize() throws Exception{    	
    	File f = new File(srcDir);
    	getDirectorySize(f);
    	//if(DEBUG)
    	//	Log.i(TAG,"getSize " + totalSize + " " + totalFileNum);
    }
    public String getSrc(){
    	return srcDir;
    }
    public String getTo(){
    	return dstFolder;
    }
    private void getDirectorySize(File f){
        if(f.isFile()){
            totalSize += f.length();
            totalFileNum += 1;
            return;
        }
        totalFileNum += 1;
        File flist[] = f.listFiles();
        if (flist == null){
            return;
        }
        int length = flist.length;
        for (int i = 0; i < length ; i++) {
            if (flist[i].isDirectory()) {
                getDirectorySize(flist[i]);
            } else {
                totalSize = totalSize + flist[i].length();
                totalFileNum = totalFileNum + 1;
            }
        }
    }
    private boolean addFilesToTable(FileOperation folder)throws Exception{
    	 String path = folder.src;
         String folderName = Helper.getFolderNameOfPath(path);
         
        //String path = folder.getAbsolutePath();         
        //String folderName = Helper.getFolderNameOfPath(path);
        //if(DEBUG)
        //	Log.i(TAG,"addFilesToTable " + path + " " + folderName + " " + folder.dst); 
        File folderDir = new File(folder.src);
        File files[] = folderDir.listFiles();
        //List<FileItemForOperation> files = Helper.GetData(new File(path));
        int size = files.length;
        int tableSize = mSrcToDir.size();
        
        if(folder.dst.startsWith("smb")){
        	SmbFile tmpFile = new SmbFile(folder.dst + folderName + "/");
            
            //检查目标路径是否存在,不存在就建立路径
            int tmp = 1;
            while (tmpFile.exists()) {
                tmp++;
                tmpFile = new SmbFile(folder.dst + folderName + "(" + tmp + ")");
            }
            try{
            tmpFile.mkdir();
            }catch(SmbException se){
            	se.printStackTrace();
            }
            for(int i = 0; i < size; i++){
               // FileItemForOperation fileItemForOperation = files.get(i);
               // fileItemForOperation.setDirFolder(tmpFile.getPath());
            	FileOperation item = new FileOperation(files[i].getAbsolutePath(),tmpFile.getPath());
                mSrcToDir.put(tableSize + i, item);
            }
        }else{
        	File tmpFile = new File(folder.dst + folderName + "/");
            
            //检查目标路径是否存在,不存在就建立路径
            int tmp = 1;
            while (tmpFile.exists()) {
                tmp++;
                tmpFile = new File(folder.dst + folderName + "(" + tmp + ")");
            }
            tmpFile.mkdir();
           
            for(int i = 0; i < size; i++){
            	FileOperation item = new FileOperation(files[i].getAbsolutePath(),tmpFile.getAbsolutePath()+"/");
                mSrcToDir.put(tableSize + i, item);
            }
        }  
        
        return true;
    }
    public int pasteFile(FileOperation from)throws Exception{
    	//if(DEBUG)
    	//	Log.i(TAG,"toFolder = " + from.dst + " from " + from.src);
    	File fromFile = new File(from.src);
    	int copyResult = FileOperationThreadManager.PASTE_FAILED;
    	if(!fromFile.exists()){
            return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_FROM_FILE_NOT_EXIST;
        }
        //检查被复制的文件是否被限制无法拷贝出
        if(!canPasteFrom(fromFile)){
            return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_FOLDER_LIMIT;
        }
        //zhaoyu 
        if(from.dst.startsWith("smb")){
        	SmbFile tmpFile = new SmbFile(from.dst);
        	 //检查目标路径是否存在,不存在就建立路径
            if(!tmpFile.exists())	          
            	try{
                	tmpFile.mkdir();
                	}catch(SmbException se){
                		return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT;
                	}
            //检查目标文件夹是否可写
            if(!tmpFile.canWrite()){
                return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_READ_ONLY_FILE_SYSTEM;
            }
            tmpFile = new SmbFile(from.dst + fromFile.getName());
            double fileSize = fromFile.length();
          //检查粘贴的目录是否已经有同名的文件
            if(tmpFile.exists()){
            	 //Log.i(TAG,"=============>"+"存在同名的");
                if(operationType == CopyOperation.COVER){
                    if(tmpFile.getPath().equals(fromFile.getAbsolutePath())){
                        return FileOperationThreadManager.PASTE_PAUSE + FileOperationThreadManager.PAUSE_REASON_CANNOT_COVER;
                    }else{
                    	tmpFile.delete();//System.out.println(tmpFile.delete());
                        Log.e(TAG,"===========temp.exist" + tmpFile.exists());
                    }	
                }
                else if(operationType == CopyOperation.JUMP){
                    //responseHandler.sendEmptyMessage(PASTE_PROGRESS_CHANGE);
                    pasted_rate = (int)((hasPasted + fileSize) / totalSize * 100.0);
                    mHandler.sendMessage(getProgressChangeMsg());
                    return FileOperationThreadManager.PASTE_SUCCEED;
                }
                else if(operationType == CopyOperation.APPEND2){
                	if (tmpFile.getName().contains("lockfile")) {
                		Message msg = new Message();
                		msg.what=FileOperationThreadManager.PASTE_LOCKFILEEXIST;
                		 mHandler.sendMessage(msg);
                		 pasted_rate = (int)((hasPasted + fileSize) / totalSize * 100.0);
                         mHandler.sendMessage(getProgressChangeMsg());
                         return FileOperationThreadManager.PASTE_SUCCEED;
					}else{
                    int tmp = 2;
                    tmpFile = new SmbFile(from.dst + Helper.getNameAppendStr(fromFile.getName(),"(" + tmp + ")"));
                    while (tmpFile.exists()) {
                        tmp++;
                        tmpFile = new SmbFile(from.dst + Helper.getNameAppendStr(fromFile.getName(),"(" + tmp + ")"));
                    }
					}
                }
                else if(operationType == CopyOperation.CANCEL){
                    return FileOperationThreadManager.PASTE_CANCEL;
                }
                else{
                    return FileOperationThreadManager.PASTE_PAUSE + FileOperationThreadManager.PAUSE_REASON_TO_FOLDER_HAS_EXIST;
                }
            }
            //copyFileLinuxCommand(fileItem.getFilePath(), toFolder);
            copyResult = copyFileJavaCommand(fromFile, tmpFile);
            return copyResult;
        }else{
            File tmpFile = new File(from.dst);
          //检查目标路径是否存在,不存在就建立路径
            if(!tmpFile.exists())
          
                if(!tmpFile.mkdir()){
                	//空间不够，经历路径失败
                	return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT;
                }
          //检查目标文件夹是否可写
            if(!tmpFile.canWrite()){
                return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_READ_ONLY_FILE_SYSTEM;
            }
            tmpFile = new File(from.dst + fromFile.getName());
            double fileSize = fromFile.length();
          //检查粘贴的目录是否已经有同名的文件
            if(tmpFile.exists()){
            	//Log.i(TAG,"=============>"+"存在同名的");
                if(operationType == CopyOperation.COVER){
                    if(tmpFile.getAbsolutePath().equals(fromFile.getAbsolutePath())){
                        return FileOperationThreadManager.PASTE_PAUSE + FileOperationThreadManager.PAUSE_REASON_CANNOT_COVER;
                    }else{
                    	System.out.println(tmpFile.delete());
                        Log.e(TAG,"===========temp.exist" + tmpFile.exists());
                    }	
                }
                else if(operationType == CopyOperation.JUMP){
                    //responseHandler.sendEmptyMessage(PASTE_PROGRESS_CHANGE);
                    pasted_rate = (int)((hasPasted + fileSize) / totalSize * 100.0);
                    mHandler.sendMessage(getProgressChangeMsg());
                    return FileOperationThreadManager.PASTE_SUCCEED;
                }
                else if(operationType == CopyOperation.APPEND2){
                	if (tmpFile.getName().contains("lockfile")) {
                		Message msg = new Message();
                		msg.what=FileOperationThreadManager.PASTE_LOCKFILEEXIST;
                		 mHandler.sendMessage(msg);
                		 pasted_rate = (int)((hasPasted + fileSize) / totalSize * 100.0);
                         mHandler.sendMessage(getProgressChangeMsg());
                         return FileOperationThreadManager.PASTE_SUCCEED;
					}else{
                    int tmp = 2;
                    tmpFile = new File(from.dst + Helper.getNameAppendStr(fromFile.getName(),"(" + tmp + ")")); 
                    while (tmpFile.exists()) {
                        tmp++;
                        tmpFile = new File(from.dst + Helper.getNameAppendStr(fromFile.getName(),"(" + tmp + ")"));
                    }
					}
                }
                else if(operationType == CopyOperation.CANCEL){
                    return FileOperationThreadManager.PASTE_CANCEL;
                }
                else{
                    return FileOperationThreadManager.PASTE_PAUSE + FileOperationThreadManager.PAUSE_REASON_TO_FOLDER_HAS_EXIST;
                }
            }
            //copyFileLinuxCommand(fileItem.getFilePath(), toFolder);
            copyResult = copyFileJavaCommand(fromFile, tmpFile);
            return copyResult;
    	}
    }
    public double getTotalSize(){
    	return totalSize;
    }
    
    @Override
    public void run(){
    	//if(DEBUG)
    	//Log.i(TAG,"run");
    	try {
			getSize();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e(TAG,e1.getMessage());
		}    	
    	int tablecnt = mSrcToDir.size();
    	//if(DEBUG)
    	//	Log.i(TAG," " + tablecnt);
    	for(int i = 0;i<tablecnt;i++){
    		File fromFile = new File(mSrcToDir.get(i).src);
	    	if(fromFile.isDirectory()){
	    		try {
					if(!addFilesToTable(mSrcToDir.get(i)))
					    return;
					tablecnt = mSrcToDir.size();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG," " + e.getMessage());
				}
	    	}
	    	else{
	    	//paste file
	    	try {
	    		pasteFile(mSrcToDir.get(i));
				/*int res = pasteFile(mSrcToDir.get(i));
				if(res ==FileOperationThreadManager.PASTE_FAILED){
					mHandler.sendMessage(getFailedMsg());
				}*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG," " + e.getMessage());
			}
	    	}
    	}
    	mHandler.sendMessage(getCompleteMsg());
    }
    public void setOperationType(CopyOperation operationType) {
        this.operationType = operationType;
    }
    public CopyOperation getOperationType() {
        return operationType;
    }
    
    private int copyFileJavaCommand(File srcFile,File dirFile){
    	if(DEBUG)
    	Log.i(TAG,"===============>"+"开始复制A" + " " + srcFile.getPath() + " " + dirFile.getAbsolutePath());
        int res = 0;
        int byteread = 0;
        try{
    //    	Log.i(TAG, "dir file==========>" + dirFile.getParent());
            StatFs stat = null;
            if(dirFile.getAbsolutePath().startsWith("/mnt/sdcard")){
            	stat = new StatFs("/mnt/sdcard");
            } else if(dirFile.getAbsolutePath().startsWith("/mnt/ext_sd")){
                stat = new StatFs("/mnt/ext_sd");
            } else if(dirFile.getAbsolutePath().startsWith("/storage/sdcard0")){
            	stat = new StatFs("/storage/sdcard0");
            }else if(dirFile.getAbsolutePath().startsWith("/storage/emulated/0")){
            	stat = new StatFs("/storage/emulated/0");
            }else {
            	stat = new StatFs("/mnt/innerDisk");
            }
            long left = (((long)stat.getAvailableBlocks())*stat.getBlockSize());
            if(DEBUG)
            	Log.i(TAG, "left space============>" + left);
            if(srcFile.length() > left){
                return FileOperationThreadManager.PASTE_FAILED + FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT;
            }
            // 使用".tmp"做临时后缀		2014.12.26 wdx
            dirFile.getName().replace(dirFile.getName(), dirFile.getName() + ".tmp");
            InputStream inStream = new FileInputStream(srcFile); // 读入原文件
            FileOutputStream outStream = new FileOutputStream(dirFile);
            byte[] buffer = new byte[1024 * 10];
            int tmpRate = 0;
            while (((byteread = inStream.read(buffer)) != -1)&& !cancelFlag) {
                hasPasted += byteread; // 字节数 文件大小
                outStream.write(buffer, 0, byteread);
                pasted_rate = (int)(hasPasted / totalSize * 100.0);
                if(pasted_rate >= tmpRate + 1){
                	mHandler.sendMessage(getProgressChangeMsg());
                    tmpRate = pasted_rate;
                }
            }
            inStream.close();
           /* if(canceled){
                res = FileOperationThreadManager.PASTE_CANCEL;
                dirFile.delete();
                responseHandler.sendEmptyMessage(PASTE_CANCEL);
            }else*/{
            	// 去掉".tmp"的临时后缀		2014.12.26 wdx
            	dirFile.getName().replace(".tmp", "");
                /*liuyufa add 20140221*/
                if(outStream!=null)
                	outStream.close();
                /*liuyufa add 20140221*/
                res = FileOperationThreadManager.PASTE_SUCCEED;
             //	responseHandler.sendMessage(getProgressChangeMsg(0,1));
            }
        }catch(Exception ex){
        	Log.i(TAG,"EXCEPTION===>"+ex.getMessage());
            res = FileOperationThreadManager.PASTE_FAILED;
            mHandler.sendMessage(getFailedMsg());
            //canceled = true;
            ex.printStackTrace();
        }
        operationType = CopyOperation.UNKOWN;
        return res;
    }
    private int copyFileJavaCommand(File srcFile,SmbFile dirFile){
    	if(DEBUG)
    		Log.i(TAG,"===============>"+"开始复制B" + " " + srcFile.getPath() + " " + dirFile.getPath());
        int res = 0;
        int byteread = 0;
        try{
    //    	Log.i(TAG, "dir file==========>" + dirFile.getParent());
         /*   StatFs stat = null;
            if(dirFile.getAbsolutePath().startsWith("/mnt/sdcard")){
            	stat = new StatFs("/mnt/sdcard");
            } else if(dirFile.getAbsolutePath().startsWith("/mnt/ext_sd")){
                stat = new StatFs("/mnt/ext_sd");
            } else{
            	stat = new StatFs("/mnt/innerDisk");
            }
            long left = (((long)stat.getAvailableBlocks())*stat.getBlockSize());
            Log.i(TAG, "left space============>" + left);
            if(srcFile.length() > left){
                return PASTE_FAILED + FAILED_REASON_NO_SPACE_LEFT;
            }*/
            // 使用".tmp"做临时后缀		2014.12.26 wdx
            dirFile.getName().replace(dirFile.getName(), dirFile.getName() + ".tmp");
            InputStream inStream = new BufferedInputStream(new FileInputStream(srcFile)); // 读入原文件
            OutputStream outStream = new BufferedOutputStream(new SmbFileOutputStream(dirFile));
            byte[] buffer = new byte[1024 * 10];
            int tmpRate = 0;
            while (((byteread = inStream.read(buffer)) != -1)&& !cancelFlag) {//&& !canceled
            	Log.i("=================================", "copyfile");
                hasPasted += byteread; // 字节数 文件大小
                outStream.write(buffer, 0, byteread);
                pasted_rate = (int)(hasPasted / totalSize * 100.0);
                if(pasted_rate >= tmpRate + 1){
                	mHandler.sendMessage(getProgressChangeMsg());
                    tmpRate = pasted_rate;
                }
            }
            inStream.close();
        	// 去掉".tmp"的临时后缀		2014.12.26 wdx
        	dirFile.getName().replace(".tmp", "");
            if(outStream!=null)
            	outStream.close();
           /* if(canceled){
                res = FileOperationThreadManager.PASTE_CANCEL;
                dirFile.delete();
                responseHandler.sendEmptyMessage(FileOperationThreadManager.PASTE_CANCEL);
            }else*/{
                res = FileOperationThreadManager.PASTE_SUCCEED;
             //	responseHandler.sendMessage(getProgressChangeMsg(0,1));
            }
        }catch(Exception ex){
        	Log.e(TAG,"EXCEPTION===>"+ex.getMessage());
            res = FileOperationThreadManager.PASTE_FAILED;
            mHandler.sendMessage(getFailedMsg());
            //Message msg = new Message();
	       // msg.what = FileOperationThreadManager.PASTE_FAILED;
           // mHandler.sendMessage(msg);
           // ViewEffect.showToast(mContext, R.string.toast_operation_failed);
           // canceled = true;
            ex.printStackTrace();
        }
        operationType = CopyOperation.UNKOWN;
        return res;
    }
    private boolean canPasteFrom(File fromFile){
//        String fromPath = fromFile.getAbsolutePath();
//        String[] limitFolders = MyApplication.limitFolders;
//        if(limitFolders.length == 0)return true;
//        for (String string : limitFolders) {
//            if(fromPath.startsWith(string)){
//                return false;
//            }
//        }
        return true;
    }
	 private Message getProgressChangeMsg(){
			//	System.out.println(pasted_rate);
		         Message msg = new Message();
		         msg.what = FileOperationThreadManager.PASTE_PROGRESS_CHANGE;
		         msg.arg1 = pasted_rate;
		         Bundle bundle = new Bundle();
			    	//String currPos = currPosition+"/"+totalFileNum;
			    	String percentage = pasted_rate+"%";
			    
			    	//bundle.putString("currPos", currPos);
			    	bundle.putString("percentage", percentage);
			    	msg.obj = bundle;
			    	return msg;
		     }
	 protected Message getFailedMsg(){
  		Message msg = new Message();
	         msg.what = FileOperationThreadManager.PASTE_FAILED;
	         Bundle bundle = new Bundle();
	         bundle.putString("srcDir", srcDir);
	         bundle.putString("toFolder", dstFolder);
	         msg.obj = bundle;
	         return msg;
  }
	 protected Message getCompleteMsg(){
     		Message msg = new Message();
	         msg.what = FileOperationThreadManager.PASTE_COMPLETED;
	         Bundle bundle = new Bundle();
	         bundle.putString("srcDir", srcDir);
	         bundle.putString("toFolder", dstFolder);
	         msg.obj = bundle;
	         return msg;
     }
	 class FileOperation{
		 String src;
		 String dst;
		 public FileOperation(String src,String dst){
			 this.src = src;
			 this.dst = dst;
		 }
	 }
}
