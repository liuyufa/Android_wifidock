package com.hualu.wifistart.filecenter.files;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.hualu.wifistart.FileActivity;

import jcifs.smb.SmbFile;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FileManager {
	public static final String HOME = "/mnt/";
    public static final String USB = HOME + "usbDisk";
    public static final String SD = HOME + "sdcard/";
    public static final String EXTSD = HOME + "ext_sd/";
    public static final String MEMORY = HOME + "innerDisk/";
    //zhaoyu	rewrite		1217
	public enum FileFilter {MUSIC,VIDEO,PICTURE,TXT,ALL,SEARCH,ALLSEARCH};
	private FileItemSet mData;
	private FileItemSet mFileData;	//zhaoyu	1205
	private FileItemSet mDataForOperation;
	public enum FilesFor {COPY,CUT,DELETE,UNKOWN};
    //private FilesFor mFilesFor = FilesFor.UNKOWN;
	
	private RefreshData queryThread;
	public static Comparator<File> mComparator; 
	public static Comparator<SmbFile> mSmbComparator;
	public FileComparatorByName comp_name;
	public FileComparatorBySize comp_size;
	public FileComparatorByUpdateTime comp_update;
	
	public SmbFileComparatorByName smb_comp_name;
	public SmbFileComparatorBySize smb_comp_size;
	public SmbFileComparatorByUpdateTime smb_comp_update;

    public enum ViewMode {
        LISTVIEW, GRIDVIEW,SEARCHVIEW;
        public static ViewMode toViewMode (String mode) {
            try {
                return valueOf(mode);
            } catch (Exception ex) {
                    // For error cases
                return LISTVIEW;
            }
        }

    };

    public enum FileComparatorMode {
    	ComparatorByName, ComparatorBySize,ComparatorByTime
    };

    private Context mContext;
    private String sdcardState = "";
    
    private OnFileSetUpdated mOnFileSetUpdated;
    private OnWhichOperation mOnWhichOperation;
    
    public static final boolean IS_SIMSDCARD = getSystemPro();

    static boolean getSystemPro() {
        String value = "";
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[] {
                String.class
            });
            value = (String) getMethod.invoke(classType, new Object[] {
                "ro.uc13x.sim_sdcard"
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
        return "simsdcard".equals(value);
    }
	

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RefreshData.FINISHED:
                    if (mOnFileSetUpdated != null) {
						mOnFileSetUpdated.queryFinished();
					}
                    break;
                case RefreshData.QUERY_MATCH:
                    // System.out.println(System.currentTimeMillis());
                    List<FileItemForOperation> items = new ArrayList<FileItemForOperation>();
                    items.addAll(queryThread.getItems());
                    // System.out.println(m_items.size());
                    // System.out.println("===================");
                    mData.setFileItems(items);
					// Here	some problems
                    for(int i = 0;i<items.size();i++)
					{
            			Log.i("FileManager","filename " + items.get(i).getFileItem().getFileName());
						// wdx	add 1204
            			if(items.get(i).FileIsHide())
            			{
            				items.remove(i);
            			}
					}
                    if (mOnFileSetUpdated != null) {
						mOnFileSetUpdated.queryMatched();
					}
                    return;
				case RefreshData.QUERY_FILES_MATCH: //zxhaoyu	1205					
					List<FileItemForOperation> fileItems = new ArrayList<FileItemForOperation>();
                	fileItems.addAll(queryThread.getFileItems());                	
                	mFileData.setFileItems(fileItems); 
                	Log.i("FileManager","QUERY_FILES_MATCH");
                	if (mOnFileSetUpdated != null) {
						mOnFileSetUpdated.queryMatched();
					}
                	//for(int i = 0;i<fileItems.size();i++)
            		//	Log.i("FileManager","filename " + fileItems.get(i).getFileItem().getFileName());
					break;
				case RefreshData.QUERY_FILES_ALLMATCH: //zxhaoyu	1217					
					List<FileItemForOperation> files = new ArrayList<FileItemForOperation>();
					files.addAll(queryThread.getFileItems());                	
                	mFileData.setFileItems(files); 
                	
                	List<FileItemForOperation> allItems = new ArrayList<FileItemForOperation>();
                	allItems.addAll(queryThread.getItems());
                    // System.out.println(m_items.size());
                    // System.out.println("===================");
                    mData.setFileItems(allItems);
                    
                    Log.i("FileManager","QUERY_FILES_ALLMATCH " +files.size() + " " + allItems.size()); 
                    if (mOnFileSetUpdated != null) {
						mOnFileSetUpdated.queryMatched();
					}
                	//for(int i = 0;i<fileItems.size();i++)
            		//	Log.i("FileManager","filename " + fileItems.get(i).getFileItem().getFileName());
					break;	
				
                case RefreshData.LOAD_APK_ICON_FINISHED:

                    return;
                default:
                    break;
            }
        }
    };
    
	public FileManager(Context context,FileItemSet data){
		mContext = context;
		initComparator();
		setSdcardState(Environment.getExternalStorageState());
		
        mData = data;
        mDataForOperation = new FileItemSet();
	}
	
	public void setFileBrowser(FileItemSet data){//zhaoyu	1205
		mFileData = data;	
	}
	
	public void query(String path,FileFilter fileFilter){
        if(queryThread != null)
            queryThread.stopGetData();
        queryThread = new RefreshData(mContext,mHandler);
        if(path.contains("smb"))
        	queryThread.setSmbPath(path);
        else
        {
        	queryThread.setFolder(new File(path));
        }
        queryThread.queryData(fileFilter);
	}
	public void StopQueryData(){
		if(queryThread != null)
            queryThread.stopGetData();
	}
	//wdx add 1219
	public void SartQueryData(){
		if(queryThread != null)
            queryThread.startGetData();
	}
	public boolean StopQueryStatus()
	{
		return queryThread.stopQueryStatus();
	}
	public void setSdcardState(String sdcardState) {
        this.sdcardState = sdcardState;
    }
    public String getSdcardState() {
        return sdcardState;
    }
	private void initComparator() {
        comp_name = new FileComparatorByName();
        comp_size = new FileComparatorBySize();
        comp_update = new FileComparatorByUpdateTime();
        smb_comp_name = new SmbFileComparatorByName();
        smb_comp_size = new SmbFileComparatorBySize();
        smb_comp_update = new SmbFileComparatorByUpdateTime();
        mComparator = comp_name;
        mSmbComparator = smb_comp_name;
    }
	
	public FileItemSet getDataForOperation(){
        return this.mDataForOperation;
    }
    public void resetDataForOperation(){
        mDataForOperation.clear();
        //mFilesFor = FilesFor.UNKOWN;
        //FileActivity.whichOperation(FileActivity.mFilesFor,0);
        //mOnWhichOperation.whichOperation(mFilesFor,0);		
    }
    public void addFileItem(FileItemForOperation fileItem){
        if(!mDataForOperation.contains(fileItem)){
        	mDataForOperation.Add(fileItem);
        }
    }
    public void removeFileItem(FileItemForOperation fileItem){
        if(mDataForOperation.contains(fileItem)){
        	mDataForOperation.remove(fileItem);
        }
    }

    public void setFilesFor(FilesFor filesFor) {
        //mFilesFor = filesFor;
        FileActivity.mFilesFor = filesFor;
        if(filesFor == FilesFor.COPY || filesFor == FilesFor.CUT){
        	mOnWhichOperation.whichOperation(filesFor,this.mDataForOperation.size());
        }
    }
    public FilesFor getFilesFor() {
        return FileActivity.mFilesFor;
    }

    public boolean canOperation(){
        if(mDataForOperation.size() > 0 && FileActivity.mFilesFor != FilesFor.UNKOWN)
            return true;
        return false;
    }
   
    public void setOnWhichoperation(OnWhichOperation iWhichoperation) {
        mOnWhichOperation = iWhichoperation;
    }
    public OnWhichOperation getWhichoperation() {
        return mOnWhichOperation;
    }
    public interface OnWhichOperation{
        public void whichOperation(FilesFor filesFor,int size);
    }
	
	public void setOnFileSetUpdated(OnFileSetUpdated fileSetUpdated){
		mOnFileSetUpdated = fileSetUpdated;
	}
    
    public interface OnFileSetUpdated{
    	public void queryFinished();
    	public void queryMatched();
    }
    
}
