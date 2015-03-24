package com.hualu.wifistart.filecenter.files;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.hualu.wifistart.MApplication;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.filecenter.PreparedResource;
import com.hualu.wifistart.filecenter.filebrowser.AppItemLoader;
import com.hualu.wifistart.filecenter.files.FileManager.FileFilter;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;


public class RefreshData {
    static final int FINISHED = 1;
    static final int QUERY_MATCH = 2;
    static final int QUERY_MATCH_FOR_FOLDER = 3;
    public static final int LOAD_APK_ICON_FINISHED = 4;
    static final int QUERY_FILES_MATCH = 5;
    static final int QUERY_FILES_ALLMATCH = 6;
    public static boolean refreshDataFinish = false;
    final String TAG = "RefreshData";
    Handler responsHandler;
	public static Hashtable<String, ArrayList<SmbFile>> mediaFileItemstable = new Hashtable<String, ArrayList<SmbFile>>();
	public static Hashtable<String, File[]> mlocalFileItemstable = new Hashtable<String, File[]>();
    private boolean isFolder;
    private boolean isSmb;
    //private WifiStarActivity MainView;
    String smbPath;
    File folder;
    Context mContext;
    FileFilter mFileFilter;

    private List<FileItemForOperation> mItems;
    private List<FileItemForOperation> mFileItems;
    private PreparedResource mPreResource;
    public static HashMap<String, String> usbIndex = new HashMap<String, String>();
    public SmbHelper smbHelper = new SmbHelper();
    
    public RefreshData(Context context, Handler handler) {
        mContext = context;
        responsHandler = handler;
        mPreResource = MApplication.getInstance().getPreparedResource();
    }

    public void setSmbPath(String path){
    	isSmb = true;
    	smbPath = path;
    }
    public void setFolder(File folder) {
        this.folder = folder;
        isSmb = false;
    }

    RefreshDataThread thread;

    public void queryData(FileFilter filter) {
    	mFileFilter = filter;
        mItems = new ArrayList<FileItemForOperation>();
        if(mFileFilter == FileFilter.SEARCH || mFileFilter == FileFilter.ALLSEARCH )
        	mFileItems = new ArrayList<FileItemForOperation>();
        thread = new RefreshDataThread();
        thread.setShouldStop(false);
        thread.start();
    }

    public List<FileItemForOperation> getItems() {
        return mItems;
    }
    
    public List<FileItemForOperation> getFileItems() {//zhaoyu	1205
        return mFileItems;
    }

    public void stopGetData() {
    	Log.i(TAG,"stopGetData");
        thread.setShouldStop(true);
    }
	//wdx add 1219    
    public void startGetData() {
    	Log.i(TAG,"sartGetData");
        thread.setShouldStop(false);
    }
    
    public boolean stopQueryStatus()
    {
    	return thread.getStopQueryStatus();
    }
    
    class RefreshDataThread extends Thread {
        private boolean shouldStop;

        private List<File> mediaFiles = new ArrayList<File>();
        public void setShouldStop(boolean b) {
            shouldStop = b;
        }
		//wdx add 1219 
        public boolean getStopQueryStatus()
        {
        	return shouldStop;
        }
        private void fetchMediaFiles(File folder){
			Log.i(TAG,"fetchMediaFiles "+folder.getAbsolutePath() + " shouldStop = " + shouldStop);
			if(folder.getPath().startsWith("/mnt/"))
			{
				if(!folder.getPath().startsWith("/mnt/sdcard"))
					return;
			}
			// wdx add 20140114
			if (folder.getPath().contains("Android")
					|| folder.getPath().contains("/.")
					|| folder.getPath().contains("Tencent")
					|| folder.getPath().contains("tencent")
					|| folder.getPath().contains("Yixin")
					|| folder.getPath().contains("iAround")
					|| folder.getPath().contains("miliao")
					|| folder.getPath().contains("xmtopic")
					|| folder.getPath().contains("UCMobileConfig")
					|| folder.getPath().contains("cache"))
				return;
        	/*if (folder.getAbsolutePath().split("/").length >= 6) {
		                return;
		            }
			*/
        	File[] files = null;
        	switch (mFileFilter) {
            case MUSIC:
                files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        File file = new File(dir + "/" + filename);
                        if (file.isDirectory()) {
                            return true;
                        }
                        return mPreResource.isAudioFile(filename.substring(
                                filename.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault()));
                    }
                });
                break;
            case VIDEO:
                files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        File file = new File(dir + "/" + filename);
                        if (file.isDirectory()) {
                            return true;
                        }
                        return mPreResource.isVideoFile(filename.substring(
                                filename.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault()));
                    }
                });
                break;
            case PICTURE:
            	/*if(folder.getPath().contains(MApplication.CACHE_IMAG))
            		return;*/
                files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        File file = new File(dir + "/" + filename);
                        if (file.isDirectory()) {
                            return true;
                        }
                        return mPreResource.isImageFile(filename.substring(
                                filename.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault()));
                    }
                });
                break;
			case TXT:
				files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        File file = new File(dir + "/" + filename);
                        if (file.isDirectory()) {
                            return true;
                        }
                        return mPreResource.isTxtFile(filename.substring(
                                filename.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault()));
                    }
                });
				break;
			case SEARCH://zhaoyu	1217
			case ALLSEARCH:
				files = folder.listFiles();
				break;
        	default:
    			break;
        	}
        	if (files != null && !shouldStop) {
	        	for (File file : files) {
					if (file.isFile()) {
						mediaFiles.add(file);
					}else {						
						if(mFileFilter == FileFilter.SEARCH||mFileFilter == FileFilter.ALLSEARCH)	//zhaoyu	1205
							mediaFiles.add(file);
						fetchMediaFiles(file);
					}
				}
        	}
        }
        public void run() {
        	Log.i(TAG,"RefreshDataThread run");
        	String keyName = getKeyName();
			if(isSmb){
				ArrayList<SmbFile> files=null;
				ArrayList<SmbFile> allFiles = null; //1205
				/*liuyufa*/
				//isSd = 0;
				//isfinish = 0;
				try {	
				//zhaoyu 1217
					if (mFileFilter == FileFilter.ALL || mFileFilter == FileFilter.ALLSEARCH) {
                    	//files = folder.listFiles();
						files	= (ArrayList<SmbFile>)smbHelper.dir(smbPath, false);						
					}
					else if(mFileFilter != FileFilter.SEARCH && mFileFilter != FileFilter.ALLSEARCH){
						String fileType;
						if(mFileFilter == FileFilter.MUSIC)
							fileType = "music";
						else if(mFileFilter == FileFilter.VIDEO){
							fileType = "video";
						}else if(mFileFilter == FileFilter.PICTURE){
							fileType = "photo";
						}else {
							fileType = "txt";
						}
						
						// wdx rewrite 1211
						if(mediaFileItemstable.get(keyName) != null && WifiStarActivity.entryFromMain)
						 {
							files = mediaFileItemstable.get(keyName);
							WifiStarActivity.entryFromMain = false;
						 }
						else
						{
							files	= (ArrayList<SmbFile>)smbHelper.dirby(smbPath, true,fileType);
						}
						//fetchMediaFiles(folder);
						//files = mediaFiles.toArray(new File[mediaFiles.size()]);
					}
					if(mFileFilter == FileFilter.SEARCH || mFileFilter == FileFilter.ALLSEARCH){
						allFiles = (ArrayList<SmbFile>)smbHelper.dirFile(smbPath);//1217
					}
					
				} catch (Throwable e) {
					files= new ArrayList<SmbFile>();
				}
				if(mFileFilter != FileFilter.SEARCH){
					if (files!= null && files.size()>0){
					SmbFile[] smbFiles = files.toArray(new SmbFile[files.size()]);
					Arrays.sort(smbFiles, FileManager.mSmbComparator);
					
					 int i;
					 Log.i(TAG,"refresh " + isSmb + " " + smbPath + " " +  files.size() + " shouldStop=" + shouldStop);
					 for (i = 0; i < smbFiles.length && !shouldStop; i++) {
						 try{
		                     if(smbFiles[i].isHidden())
		                    	 continue;
	                     }catch (SmbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						}
	                     FileItem fileOrfolder = new FileItem();
	                     SmbFile tmpfile = smbFiles[i];
	                     String fileName = tmpfile.getName();
	
						 if(fileName.endsWith("/"))
	            				fileName = fileName.substring(0, fileName.length()- 1);
	                     String extraName = fileName.substring(fileName.lastIndexOf(".") + 1);
	                     extraName = extraName.toLowerCase(Locale.getDefault());
	                     long size = tmpfile.getContentLength();
	                     String path = tmpfile.getPath();
	                     boolean isDir = false;
						 Log.i(TAG,"setFileName " + fileName + " " + path);
						 try {
							isDir = tmpfile.isDirectory();
						 } catch (SmbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						 }
	                     if (isDir) {
	                         extraName = "folder";
	                         size = -1;
	                         //path += "/";
	                     }
	  				     
	                     fileOrfolder.setDirectory(isDir);
	                     fileOrfolder.setFileName(fileName);
	                     fileOrfolder.setExtraName(extraName);
	                     fileOrfolder.setFilePath(path);
	                     fileOrfolder.setFileSize(size);
	                     try{
	                     fileOrfolder.setModifyData(getModifyDate(tmpfile.lastModified()));
	                     fileOrfolder.setCanRead(tmpfile.canRead());
	                     fileOrfolder.setCanWrite(tmpfile.canWrite());
	                     fileOrfolder.setHide(tmpfile.isHidden());                    
						} catch (SmbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                     fileOrfolder.setIconId(mPreResource.getBitMap(extraName));
	                     
	                     //FilePropertyAdapter propAdapter = new FilePropertyAdapter(mContext,fileOrfolder);
	                     FileItemForOperation fileItem = new FileItemForOperation();
	                     //fileItem.setPropAdapter(propAdapter);
	                     fileItem.setFileItem(fileOrfolder);
	                     RefreshData.this.addFileItem(fileItem);
	                     // 每搜索5个文件，刷新一下屏幕
	                     if ((i + 1) % 5 == 0 && mFileFilter != FileFilter.ALLSEARCH) {
	                         if (isFolder) {
	                             responsHandler.sendEmptyMessage(QUERY_MATCH_FOR_FOLDER);
	                         } else {
	                             responsHandler.sendEmptyMessage(QUERY_MATCH);
	                         }
	                     }
	                     /*
	                      * else{ //搜索整个文件夹完毕，刷新屏幕 if(i + 1 == files.length){
	                      * if(isFolder){ responsHandler.sendEmptyMessage(
	                      * QUERY_MATCH_FOR_FOLDER); }else{
	                      * responsHandler.sendEmptyMessage(QUERY_MATCH); } }
	                      * }
	                      */
	                 }
					}
				}
				if(mFileFilter == FileFilter.SEARCH || mFileFilter == FileFilter.ALLSEARCH){
					Log.i(TAG,"FileFilter.SEARCH " + allFiles.size());
					if(allFiles != null && allFiles.size() > 0){
						SmbFile[] smbFiles = allFiles.toArray(new SmbFile[allFiles.size()]);
						//wdx delete 1221
						//Arrays.sort(smbFiles, FileManager.mSmbComparator);				
						 int i;
						 Log.i(TAG,"refresh " + isSmb + " " + smbPath + " " +  allFiles.size() + " shouldStop=" + shouldStop);
						 for (i = 0; i < smbFiles.length && !shouldStop; i++) {
							 try{
			                     if(smbFiles[i].isHidden())
			                    	 continue;
		                     }catch (SmbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							}
							 
		                     FileItem fileOrfolder = new FileItem();
		                     SmbFile tmpfile = smbFiles[i];	
		                     String fileName = tmpfile.getName();
		                     /*liuyufa 20131223 add for search start*/
		                     Log.i(TAG,"AppItemLoader.searchcm");
		                     if(AppItemLoader.searchcm == null){
		                    	 continue;
		                     }
		                     if(tmpfile.getName().contains(AppItemLoader.searchcm)== false){
		                    	 continue;
		                     }
		                    /*liuyufa 20131223 add for search end*/
							 if(fileName.endsWith("/"))
		            				fileName = fileName.substring(0, fileName.length()- 1);
		                     String extraName = fileName.substring(fileName.lastIndexOf(".") + 1);
		                     extraName = extraName.toLowerCase(Locale.getDefault());
		                     long size = tmpfile.getContentLength();
		                     String path = tmpfile.getPath();
		                     boolean isDir = false;
							 Log.i(TAG,"setFileName " + fileName + " " + path);
							 try {
								isDir = tmpfile.isDirectory();
							 } catch (SmbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							 }
		                     if (isDir) {
		                         extraName = "folder";
		                         size = -1;
		                         //path += "/";
		                     }
		  				     
		                     fileOrfolder.setDirectory(isDir);
		                     fileOrfolder.setFileName(fileName);
		                     fileOrfolder.setExtraName(extraName);
		                     fileOrfolder.setFilePath(path);
		                     fileOrfolder.setFileSize(size);
		                     try{
		                     fileOrfolder.setModifyData(getModifyDate(tmpfile.lastModified()));
		                     fileOrfolder.setCanRead(tmpfile.canRead());
		                     fileOrfolder.setCanWrite(tmpfile.canWrite());
		                     fileOrfolder.setHide(tmpfile.isHidden());                    
							} catch (SmbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                     fileOrfolder.setIconId(mPreResource.getBitMap(extraName));
		                     
		                     //FilePropertyAdapter propAdapter = new FilePropertyAdapter(mContext,fileOrfolder);
		                     FileItemForOperation fileItem = new FileItemForOperation();
		                     //fileItem.setPropAdapter(propAdapter);
		                     fileItem.setFileItem(fileOrfolder);
							 RefreshData.this.addFilesItem(fileItem);
							 //zhaoyu	1217
							 if ((i + 1) % 5 == 0) {		                        
		                          responsHandler.sendEmptyMessage(QUERY_FILES_MATCH);		                        
		                     }
						}						 
					}
				}
				 if(!shouldStop){
				 //zhaoyu	1217
					 if(mFileFilter == FileFilter.SEARCH)
						 responsHandler.sendEmptyMessage(QUERY_FILES_MATCH);	
					 else if(mFileFilter == FileFilter.ALLSEARCH){
						 responsHandler.sendEmptyMessage(QUERY_FILES_ALLMATCH);	
					 }else{
		                 if (isFolder) {
		                     responsHandler.sendEmptyMessage(QUERY_MATCH_FOR_FOLDER);
		                 } else {
		                     responsHandler.sendEmptyMessage(QUERY_MATCH);
		                 }
					 }					
				 }			
                 responsHandler.sendEmptyMessage(FINISHED);
                 //fechRefreshData(mediaFileItemstable,keyName,files);// wdx add 1211
			}
			else if (folder.exists()) {
                if (folder.isFile()) {
                    responsHandler.sendEmptyMessage(FINISHED);
                } else if (folder.isDirectory()) {
                    File[] files = null;
                    File[] allFiles = null;
                    /*liuyufa */
                    //isSd = 1;
					//zhaoyu	1217
                    if (mFileFilter == FileFilter.ALL || mFileFilter == FileFilter.ALLSEARCH) {
                    	files = folder.listFiles();                    							
					}
                    else if(mFileFilter != FileFilter.SEARCH && mFileFilter != FileFilter.ALLSEARCH){
						// wdx rewrite 1211
						if(mlocalFileItemstable.get(keyName) != null && WifiStarActivity.entryFromMain_phone)
						 {
							files = mlocalFileItemstable.get(keyName);
							WifiStarActivity.entryFromMain_phone = false;
						 }
						else{
							fetchMediaFiles(folder);
							files = mediaFiles.toArray(new File[mediaFiles.size()]);
						}
					}
					//zhaoyu	1217
                    if(mFileFilter == FileFilter.SEARCH || mFileFilter == FileFilter.ALLSEARCH){
						fetchMediaFiles(folder);
						allFiles = mediaFiles.toArray(new File[mediaFiles.size()]);//zhaoyu	1217
					}
                    if(mFileFilter != FileFilter.SEARCH)
                    {
	                    if (files != null) {
	                        Arrays.sort(files, FileManager.mComparator);
	                        // QuickSorter.sort(files, com);
	                        int i;
	                        Log.i(TAG,"shouldStop = " + shouldStop);
	                        
	                        for (i = 0; i < files.length && !shouldStop; i++) {
	                        	if(files[i].isHidden())
	                        		continue;
	                            FileItem fileOrfolder = new FileItem();
	                            String fileName = files[i].getName();	
	                            String extraName = fileName.substring(fileName.lastIndexOf(".") + 1);
	                            extraName = extraName.toLowerCase(Locale.getDefault());
	                            long size = files[i].length();
	                            String path = files[i].getAbsolutePath();
	                            boolean isDir = files[i].isDirectory();
	                            if (isDir) {
	                                extraName = "folder";
	                                size = -1;
	                                path += "/";
	                            }
	                            fileOrfolder.setDirectory(isDir);
	                            fileOrfolder.setFileName(fileName);
	                            fileOrfolder.setExtraName(extraName);
	                            fileOrfolder.setFilePath(path);
	                            fileOrfolder.setFileSize(size);
	                            fileOrfolder.setModifyData(getModifyDate(files[i].lastModified()));
	                            
	                            fileOrfolder.setCanRead(files[i].canRead());
	                            fileOrfolder.setCanWrite(files[i].canWrite());
	                            fileOrfolder.setHide(files[i].isHidden());
	                            fileOrfolder.setIconId(mPreResource.getBitMap(extraName));
	                            
	                            //FilePropertyAdapter propAdapter = new FilePropertyAdapter(mContext,fileOrfolder);
	                            FileItemForOperation fileItem = new FileItemForOperation();
	                            //fileItem.setPropAdapter(propAdapter);
	                            fileItem.setFileItem(fileOrfolder);
	                            RefreshData.this.addFileItem(fileItem);
	                            // 每搜索5个文件，刷新一下屏幕
	                            if ((i + 1) % 5 == 0 && mFileFilter != FileFilter.ALLSEARCH) {
	                                if (isFolder) {
	                                    responsHandler.sendEmptyMessage(QUERY_MATCH_FOR_FOLDER);
	                                } else {
	                                    responsHandler.sendEmptyMessage(QUERY_MATCH);
	                                }
	                            }
	                            //musicListBack = 
	           				 	//refreshDataFinish = true;		//wdx	add 1206
	                            /*
	                             * else{ //搜索整个文件夹完毕，刷新屏幕 if(i + 1 == files.length){
	                             * if(isFolder){ responsHandler.sendEmptyMessage(
	                             * QUERY_MATCH_FOR_FOLDER); }else{
	                             * responsHandler.sendEmptyMessage(QUERY_MATCH); } }
	                             * }
	                             */
	                        }
	                        //fechRefreshData(mediaFileItemstable,keyName,files);
	                        
	                        /*
	                        if(!shouldStop){
		                        if (isFolder) {
		                            responsHandler.sendEmptyMessage(QUERY_MATCH_FOR_FOLDER);
		                        } else {
		                            responsHandler.sendEmptyMessage(QUERY_MATCH);
		                        }
	                        }
	                        responsHandler.sendEmptyMessage(FINISHED);  *///1205
	                    } else {
	                    	
		                        if (isFolder) {
		                            responsHandler.sendEmptyMessage(QUERY_MATCH_FOR_FOLDER);
		                        } else {
		                            responsHandler.sendEmptyMessage(QUERY_MATCH);
		                        } 
	                    }
                	}
                    if(mFileFilter == FileFilter.SEARCH || mFileFilter == FileFilter.ALLSEARCH){                    	
	                    if(allFiles != null)
	                    {                      
	                        int i;  
	                        Log.i(TAG,"allFiles " + allFiles.length+"  shouldStop = " + shouldStop);
	                        //	wdx delete 1221
							//Arrays.sort(allFiles, FileManager.mComparator);
	                        for (i = 0; i < allFiles.length && !shouldStop; i++) {
	                        	if(allFiles[i].isHidden())
	                        		continue;

	                            FileItem fileOrfolder = new FileItem();
	                            String fileName = allFiles[i].getName();
			                     /*liuyufa 20131223 add for search start*/
			                     Log.i(TAG,"AppItemLoader.searchcm");
			                     if(AppItemLoader.searchcm == null){
			                    	 continue;
			                     }
			                     if(allFiles[i].getName().contains(AppItemLoader.searchcm)== false){
			                    	 continue;
			                     }
			                    /*liuyufa 20131223 add for search end*/
	                            String extraName = fileName.substring(fileName.lastIndexOf(".") + 1);
	                            extraName = extraName.toLowerCase(Locale.getDefault());
	                            long size = allFiles[i].length();
	                            String path = allFiles[i].getAbsolutePath();
	                            boolean isDir = allFiles[i].isDirectory();
	                            
	                            if (isDir) {
	                                extraName = "folder";
	                                size = -1;
	                                path += "/";
	                            }
	                            fileOrfolder.setDirectory(isDir);
	                            fileOrfolder.setFileName(fileName);
	                            fileOrfolder.setExtraName(extraName);
	                            fileOrfolder.setFilePath(path);
	                            fileOrfolder.setFileSize(size);
	                            fileOrfolder.setModifyData(getModifyDate(allFiles[i].lastModified()));
	                            
	                            fileOrfolder.setCanRead(allFiles[i].canRead());
	                            fileOrfolder.setCanWrite(allFiles[i].canWrite());
	                            fileOrfolder.setHide(allFiles[i].isHidden());
	                            fileOrfolder.setIconId(mPreResource.getBitMap(extraName));                  
	                            //FilePropertyAdapter propAdapter = new FilePropertyAdapter(mContext,fileOrfolder);
	                            FileItemForOperation fileItem = new FileItemForOperation();
	                            //fileItem.setPropAdapter(propAdapter);
	                            fileItem.setFileItem(fileOrfolder);
	                            RefreshData.this.addFilesItem(fileItem);
	                            if ((i + 1) % 5 == 0) {	                                
	                                    responsHandler.sendEmptyMessage(QUERY_FILES_MATCH);	                                
	                            }
	                        }	                                                                 
	                    }
                    }
					 if(!shouldStop){
						 if(mFileFilter == FileFilter.SEARCH){
							 responsHandler.sendEmptyMessage(QUERY_FILES_MATCH);
						 }
						 else if(mFileFilter == FileFilter.ALLSEARCH){
							 responsHandler.sendEmptyMessage(QUERY_FILES_ALLMATCH);
						 }
						 else{
			                 if (isFolder) {
			                     responsHandler.sendEmptyMessage(QUERY_MATCH_FOR_FOLDER);
			                 } else {
			                     responsHandler.sendEmptyMessage(QUERY_MATCH);
			                 }
						 }
					
							 
				 	}
                    responsHandler.sendEmptyMessage(FINISHED);
                    //fechRefreshData(mlocalFileItemstable,keyName,files);// wdx add 1211
                }
            } else {
                responsHandler.sendEmptyMessage(QUERY_MATCH);
                responsHandler.sendEmptyMessage(FINISHED);
            }
        }
    }
    // wdx add 1206
    public void fechRefreshData(Hashtable<String, ArrayList<SmbFile>> mediaFileItemstable,String keyName,ArrayList<SmbFile> files)
    {
    	if(mediaFileItemstable.get(keyName) != null)
			mediaFileItemstable.remove(keyName);
		mediaFileItemstable.put(keyName,files);
		WifiStarActivity.entryFromMain = false;		//	WDX	ADD	1214
    }
    
    // wdx add 1211
    public void fechRefreshData(Hashtable<String, File[]> mediaFileItemstable,String keyName,File[] files)
    {
    	if(mlocalFileItemstable.get(keyName) != null)
    		mlocalFileItemstable.remove(keyName);
    	mlocalFileItemstable.put(keyName,files);
    	WifiStarActivity.entryFromMain_phone = false;	//	WDX	ADD	1214
    }
    /*liuyufa 20131220 add for search*/
    /*
    public int getSdSearchFinish(){
    	
    	return isfinish;
    }
    public void SetSdSearchFinish(int isfinish){
    	
    	this.isfinish =isfinish;
    }
    */
    // wdx add 1206
    private String getKeyName()
    {
    	String str = null;
    	if(!isSmb)
    	{
    		if(mFileFilter == FileFilter.MUSIC){
    			str = "music_sdcard";
    		}
    		else if(mFileFilter == FileFilter.VIDEO){
    			str = "video_sdcard";
    		}else if(mFileFilter == FileFilter.PICTURE){
    			str = "picture_sdcard";
    		}else if(mFileFilter == FileFilter.TXT){
    			str = "txt_sdcard";
    		}
    		else
    		{
    			str = "allfile_sdcard";
    		}
    	}
    	else
    	{
    		if(smbPath.contains("sda"))
    		{
    			if(mFileFilter == FileFilter.MUSIC){
    				str = "music_sda";
        		}
        		else if(mFileFilter == FileFilter.VIDEO){
        			str = "video_sda";
        		}else if(mFileFilter == FileFilter.PICTURE){
        			str = "picture_sda";
        		}else if(mFileFilter == FileFilter.TXT){
        			str = "txt_sda";
        		}
        		else
        		{
        			str = "allfile_sda";
        		}
    		}
    		if(smbPath.contains("sdb"))
    		{
    			if(mFileFilter == FileFilter.MUSIC){
    				str = "music_sdb";
        		}
        		else if(mFileFilter == FileFilter.VIDEO){
        			str = "video_sdb";
        		}else if(mFileFilter == FileFilter.PICTURE){
        			str = "picture_sdb";
        		}else if(mFileFilter == FileFilter.TXT){
        			str = "txt_sdb";
        		}
        		else
        		{
        			str = "allfile_sdb";
        		}
    		}
    		if(smbPath.contains("sdc"))
    		{
    			if(mFileFilter == FileFilter.MUSIC){
    				str = "music_sdc";
        		}
        		else if(mFileFilter == FileFilter.VIDEO){
        			str = "video_sdc";
        		}else if(mFileFilter == FileFilter.PICTURE){
        			str = "picture_sdc";
        		}else if(mFileFilter == FileFilter.TXT){
        			str = "txt_sdc";
        		}
        		else
        		{
        			str = "allfile_sdc";
        		}
    		}
			/*liuyufa add for chuangjin*/
    		if(smbPath.contains("awsd"))
    		{
    			if(mFileFilter == FileFilter.MUSIC){
    				str = "music_sdc";
        		}
        		else if(mFileFilter == FileFilter.VIDEO){
        			str = "video_sdc";
        		}else if(mFileFilter == FileFilter.PICTURE){
        			str = "picture_sdc";
        		}else if(mFileFilter == FileFilter.TXT){
        			str = "txt_sdc";
        		}
        		else
        		{
        			str = "allfile_sdc";
        		}
    		}
			/*liuyufa add for chuangjin*/
    	}
    	return str;
    }
    
    private static final String TIMEFORMAT = "yyyy-MM-dd";// HH:mm:ss
    private String getModifyDate(long lastModifyDate){
        //File tmpFile = new File(fileItem.getFilePath());
        //long lastModifyDate = tmpFile.lastModified();
        if(lastModifyDate == 0)return "";
        Date date = new Date(lastModifyDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(TIMEFORMAT);
        return dateFormat.format(date);
    }
    public void addFileItem(FileItemForOperation fileItem) {
        mItems.add(fileItem);
    }
    public void addFilesItem(FileItemForOperation fileItem) {//1205
        mFileItems.add(fileItem);
    }
}
