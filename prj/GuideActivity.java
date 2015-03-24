package com.hualu.wifistart;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import jcifs.smb.SmbFile;


import com.hualu.wifistart.views.WPTextBox;
import com.hualu.wifistart.filecenter.filebrowser.Browser;
import com.hualu.wifistart.filecenter.filebrowser.FileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.ImageFileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.MusicFileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.TxtFileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.VideoFileBrowser;
import com.hualu.wifistart.filecenter.files.FileManager.FileComparatorMode;
import com.hualu.wifistart.filecenter.files.FileManager.FileFilter;
import com.hualu.wifistart.filecenter.files.FileManager.FilesFor;
import com.hualu.wifistart.filecenter.files.FileManager.OnWhichOperation;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.smbsrc.ServiceFileManager;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.smbsrc.Helper.wfDiskInfo;
import com.hualu.wifistart.views.AnimTabLayout;
import com.hualu.wifistart.views.AutoTextView;
import com.hualu.wifistart.views.PivotControl;
import com.hualu.wifistart.views.QuickAction;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import com.hualu.wifistart.R;
import com.hualu.wifistart.views.AnimTabLayout.OnTabChangeListener;

public class GuideActivity extends FragmentActivity implements OnPageChangeListener,OnWhichOperation,OnTabChangeListener{
	private int mCurrentDepth = 0;
	private float mDensity;
	//PivotControl mPivot;
	private boolean notificationDone = false;
	//private Typeface segoe;
	//private Typeface segoeBold;
	private static ArrayList<View> views;
	private ArrayList<String> tittles;
	//private ArrayList<FileInfo> files;
	ArrayList<wfDiskInfo> disks;
	private static ArrayList<MusicFileBrowser> mMusicFileBrowsers;
	private static ArrayList<VideoFileBrowser> mVideoFileBrowsers;
	private static ArrayList<ImageFileBrowser> mImageFileBrowsers;
	private static ArrayList<TxtFileBrowser> mTxtFileBrowsers;
	//private ArrayList<FileBrowser> mMusicFileBrowsers;
	private String tilesBmpDir;
	//private PivotControl mPivot;
	private static ViewPager mViewPager;
	private AnimTabLayout mAnimTab;
	
	public TextView mPasteNum;
    //private TextView mTittle;
    //private ProgressBar pb; 

	private final int MENU_FIRST = Menu.FIRST + 100;
	private final int MENU_DELETE = MENU_FIRST + 1;
	private final int MENU_RENAME = MENU_FIRST + 2;
	private final int MENU_SELECT_ALL = MENU_FIRST + 3;
	private final int MENU_REFRESH = MENU_FIRST + 4;
	private final int MENU_ADDTO_PLAYLIST = MENU_FIRST + 5;
	
    //public UIHandler mHandler = new UIHandler(this); 
    

	ImageButton btnHome,btnSearch,btnDisplay,btnStatus;
	ImageButton btnPaste;//btnBack
	//RelativeLayout rlHome,rlBack,rlSearch,rlDisplay,rlStatus;
	static String fileType="music";
	//WPTextBox mSearchBox;
	View mRoot;
	//private QuickAction popAction = null;
	private int mHeight;
	private int mWidth;
	public static int mScreenWidth;
	public SmbHelper smbHelper = new SmbHelper();
	
	final static String TAG = "ContentList";
	private SharedPreferences mPreferences;
	public static FilesFor mFilesFor = FilesFor.UNKOWN;
	
	private final static String displays[] = new String[]{"列表显示","按名称排序","按大小排序","按时间排序"};
	
	final static String status[] = new String[]{"传送列表","播放列表","正在播放"};
	final int image_guides[]=new int[]{R.layout.guide01,R.layout.guide02,R.layout.guide03,R.layout.guide04,R.layout.guide05,R.layout.guide06};
	
	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);	
		getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//getWindow().setSoftInputMode(
        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		mDensity = getResources().getDisplayMetrics().density;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		mWidth = getResources().getDisplayMetrics().widthPixels;
		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		
		views = new ArrayList<View>();
		//files = new ArrayList<FileInfo>();
		tittles = new ArrayList<String>();
		/*
		disks = new ArrayList<wfDiskInfo>(); 	
		mMusicFileBrowsers = new ArrayList<MusicFileBrowser>();
		mVideoFileBrowsers = new ArrayList<VideoFileBrowser>();
		mImageFileBrowsers = new ArrayList<ImageFileBrowser>();
		mTxtFileBrowsers = new ArrayList<TxtFileBrowser>();*/
		mRoot = ((ViewGroup)((LayoutInflater)getSystemService("layout_inflater")).inflate(R.layout.activity_guideintro, null));
		setContentView(R.layout.activity_guideintro);
		//mRoot = ((ViewGroup)((LayoutInflater)getSystemService("layout_inflater")).inflate(R.layout.activity_file, null));
		//setContentView(R.layout.activity_file);
	
		/*if (getIntent().getExtras() != null)
			fileType = getIntent().getExtras().getString("appName");
		else
			fileType = null;
		Log.i(TAG,"filetype = " + fileType);*/
		
		//mPivot = ((PivotControl)findViewById(R.id.newPivot));	
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mAnimTab = (AnimTabLayout) findViewById(R.id.animTab);
		//pb = (ProgressBar)findViewById(R.id.pbar);		
		//popAction = new QuickAction(this);	
		/*
		btnHome = (ImageButton)findViewById(R.id.btnHome);	
	
		btnHome.setOnClickListener(new btnClickListener());
	
		
		btnPaste = (ImageButton)findViewById(R.id.btnPaste);	
	
		btnPaste.setOnClickListener(new btnClickListener());
		
		
		btnSearch = (ImageButton)findViewById(R.id.btnSearch);	
		
		btnSearch.setOnClickListener(new btnClickListener());	
		
		btnDisplay = (ImageButton)findViewById(R.id.btnDisplay);	
	
		btnDisplay.setOnClickListener(new btnClickListener());
		
		btnStatus = (ImageButton)findViewById(R.id.btnStatus);	
		
		btnStatus.setOnClickListener(new btnClickListener());
		
		mPasteNum = (TextView)findViewById(R.id.downloadNum);	
		PasteCompletionListner.setView(mPasteNum);
	    if(PasteCompletionListner.mFilesPaste > 0){			
			mPasteNum.setText("" + PasteCompletionListner.mFilesPaste);
		}else{
			//getCurrBrowser().pasteComplete();
			mPasteNum.setText("");			
		}
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
*/
		initializeSettings();
		//DownloadTask dTask = new DownloadTask();
        //dTask.execute(100);         
	}
	@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			Browser browser = getCurrBrowser();	
	    	if(browser.mViewMode == ViewMode.SEARCHVIEW){	    			
				browser.toggleSearchMode();				
				browser.toggleViewMode();
	    	}
	    	else	
	    		super.onBackPressed();
		}
	class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btnHome:
				GuideActivity.this.finish();
				startActivity(new Intent(GuideActivity.this, WifiShareActivity.class));
				break;
			//case R.id.btnRefresh:
			case R.id.btnPaste:					
				//refresh();
				//browser.onBtnPaste();
				paste();				
				
				break;
			case R.id.btnSearch:{
				//if(popAction != null)
    			//	popAction.dismiss();
				//Log.i(TAG,"pivot activ = "+mPivot.mActive);
				Browser browser = getCurrBrowser();		
				browser.toggleSearchMode();				
				browser.toggleViewMode();
				/*View view = views.get(mPivot.mActive-1);
				mSearchBox = (WPTextBox)view.findViewById(R.id.search);				
				mSearchBox.setVisibility(View.VISIBLE);
				mSearchBox.requestFocus();*/
				break;
			}
			case R.id.btnDisplay:{
				//if(mSearchBox!=null)
	        	//	  mSearchBox.setVisibility(View.GONE);	       
	        	  /*int[] location = new int[2];  
	        	  btnDisplay.getLocationOnScreen(location); 
	        	  Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
	        	  QuickAction popAction = new QuickAction(ListActivity.this,displays);
	        	  popAction.show(btnDisplay,location[0]-5, location[1]);
	        	  */
	        	  int[] location = new int[2];  
		       	  btnDisplay.getLocationOnScreen(location); 
		       	  Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
		       	  final QuickAction popAction = new QuickAction(GuideActivity.this,displays);	       	
		       	  popAction.setOnActionItemClickListener(new OnItemClickListener()
		       	  {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						Log.i(TAG,"POP onItemClick " + arg2);
						Browser browser = getCurrBrowser();
						//wdx delete 1225
						/* if(arg2 == 0){
							browser.mViewMode = ViewMode.GRIDVIEW;							
							browser.toggleViewMode();
							popAction.dismiss();
						}else */if(arg2 == 0){
							browser.mViewMode = ViewMode.LISTVIEW;
							browser.toggleViewMode();
							popAction.dismiss();
						}else if(arg2 == 1){
							browser.SetFileComparator(FileComparatorMode.ComparatorByName);
							browser.QueryData();
							popAction.dismiss();
						}else if(arg2 == 2){
							browser.SetFileComparator(FileComparatorMode.ComparatorBySize);
							browser.QueryData();
							popAction.dismiss();
						}else if(arg2 == 3){
							browser.SetFileComparator(FileComparatorMode.ComparatorByTime);
							browser.QueryData();
							popAction.dismiss();
						}						 
						 SharedPreferences.Editor editor = mPreferences.edit();
						 if(fileType.equals("music")){
							 editor.putString(browser.topDir+"music", browser.mViewMode.toString());
							 Log.i(TAG,browser.topDir+"music"+ " " + browser.mViewMode.toString());
						 }
						 else if(fileType.equals("video"))
							 editor.putString(browser.topDir+"video", browser.mViewMode.toString());
						 else if(fileType.equals("photo"))
							 editor.putString(browser.topDir+"photo", browser.mViewMode.toString());
						 else if(fileType.equals("txt"))
							 editor.putString(browser.topDir+"txt", browser.mViewMode.toString());
						 editor.commit();

					}
					
		       		  
		       	  });
		       	 popAction.show(btnDisplay,location[0]-5, location[1]+10);
				break;
				}
			case R.id.btnStatus:{
				//if(mSearchBox!=null)
	        	//	  mSearchBox.setVisibility(View.GONE);
	        	  //if(popAction != null)
	    		  //		popAction.dismiss();
	        	  int[] location = new int[2];  
	        	  btnStatus.getLocationOnScreen(location); 
	        	  Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
	        	  final QuickAction popAction = new QuickAction(GuideActivity.this,status);
	        	  popAction.setOnActionItemClickListener(new OnItemClickListener()
		       	  {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if(arg2 == 0){	
							Intent intent = new Intent(GuideActivity.this, StatusActivity.class);
							intent.putExtra("listType", "transfer");	
							GuideActivity.this.startActivity(intent);  		
							popAction.dismiss();
						}else if(arg2 == 1){
							Intent intent = new Intent(GuideActivity.this, StatusActivity.class);
							intent.putExtra("listType", "play");	
							GuideActivity.this.startActivity(intent); 
							popAction.dismiss();
						}else if(arg2 == 2){	
							Intent intent = new Intent(GuideActivity.this, MusicActivity.class);								
							GuideActivity.this.startActivity(intent);
							popAction.dismiss();
						}
					}
		       	  });
	        	  popAction.show(btnStatus,location[0]-5, location[1]+10);
				 // startActivity(new Intent(ContentList.this, StatusActivity.class));				
				break;
				}
			}
		}
	}
	public void dispachBackKey() {
		Log.i(TAG,"dispachBackKey");
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
	/*
	@Override	
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.add(1, MENU_REFRESH, Menu.NONE, R.string.menu_refresh);//.setIcon(R.drawable.ic_menu_refresh);
		menu.add(1, MENU_SELECT_ALL, Menu.NONE, R.string.menu_select_all);//.setIcon(R.drawable.ic_menu_refresh);
		menu.add(1, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
		if(fileType.equals("music"))
			menu.add(1, MENU_ADDTO_PLAYLIST, Menu.NONE, R.string.addPlayList);
		// menu.add(1, MENU_HELP, Menu.NONE,
		// R.string.menu_help).setIcon(android.R.drawable.ic_menu_help);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
			case MENU_DELETE:
				break;
			case R.id.delete:
				break;
			case MENU_RENAME:
				break;
			case MENU_SELECT_ALL:
				break;
			case MENU_REFRESH:
				break;
			case MENU_ADDTO_PLAYLIST:
				break;
			
		}		
		//return super.onOptionsItemSelected(item);
		return true;
	}*/
	PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			//return 400;
			return views.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
//			 ((ViewPager)container).removeView(mViews.get(position % mViews.size()));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tittles.get(position);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			Log.i(TAG, "instantiateItem :" + position + " " + position % views.size());
		
			try {
				((ViewPager) container).addView(
						views.get(position % views.size()), 0);
			} catch (Exception e) {
			}
			return views.get(position % views.size());
		}
	};
	BaseAdapter tabAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(GuideActivity.this);
			}
			((TextView) convertView).setText(tittles.get(position));
			((TextView) convertView).setTextAppearance(GuideActivity.this,R.style.tvTitle);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return tittles.size();
		}
	};
	
	public void initializeSettings()
	{
		Log.i(TAG,"initializeSettings");
	
		//disks.clear();
		tittles.clear();
		views.clear();
		for(int i =0;i<image_guides.length;i++){
			tittles.add("image_guides0"+i);
		}
		mAnimTab.setAdapter(tabAdapter);
		mAnimTab.setOnTabChangeListener(this);
		LayoutInflater mInflater = LayoutInflater.from(this);
		for(int i = 0;i<tittles.size();i++){			
			//ImageView view = new ImageView(GuideActivity.this);	
			//view.setImageResource(image_guides[i]);
			View mView = mInflater.inflate(R.layout.guide01, null);
			//ImageView image = (ImageView) mView.findViewById(R.id.guideIntro);
			//image.setImageResource(image_guides[i]);
			views.add(i,mView);
		}
		
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);//4 * 50
		mViewPager.setOnPageChangeListener(this);
	}
	/*
	public void initializeSettings()
	{
		Log.i(TAG,"initializeSettings");
		
		//disks.clear();
		tittles.clear();
		views.clear();
		if(Singleton.SMB_ONLINE)
			Singleton.initDiskInfo();
		disks = Singleton.instance().disks;
	
		if(disks.size() > 0){
			//String diskPath = null;
			for(int i = 0;i<disks.size();i++){
				wfDiskInfo disk = disks.get(i);
				//if(diskPath!= null && !diskPath.equals(disk.path))
				tittles.add(disk.des);
			}
			
			String[] stockArr = new String[tittles.size()];
		    stockArr = tittles.toArray(stockArr);	
			//mPivot.setTitles(stockArr);
		    mAnimTab.setAdapter(tabAdapter);
			mAnimTab.setOnTabChangeListener(this);
			
			LayoutInflater localLayoutInflater = LayoutInflater.from(this);
			//if(fileType.equals("music"))
			{
				mMusicFileBrowsers.clear();
				for(int i = 0;i<tittles.size();i++){
					String mode = mPreferences.getString(disks.get(i).path+"music",ViewMode.LISTVIEW.toString());	
					Log.i(TAG,disks.get(i).path+"music" + " " + mode);
					MusicFileBrowser mFileBrowser = new MusicFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
					mFileBrowser.mFileManager.setOnWhichoperation(this);
					mMusicFileBrowsers.add(mFileBrowser);
					views.add(i,mFileBrowser.getView());
				}
			}
		}
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);//4 * 50
		mViewPager.setOnPageChangeListener(this);
	}*/
	
	/*
	public void initPage()
	{	
		for(int i =0;i<views.size();i++)
		{
			//files.clear();
			ArrayList<FileInfo> filelist = new ArrayList<FileInfo>();
			String path = disks.get(i).path;
			Log.i(TAG,"path = " + path);
			if(path.equals(Singleton.LOCAL_ROOT)){
				displayLocalFileList(filelist,path);
			}else if(path.contains("smb")){ 
				displaySmbFileList(filelist,path);				
			}
			Log.i(TAG,"file cnt = " + filelist.size());
			Message msg = Message.obtain();	
			msg.what = i;
			msg.obj = filelist;
			mHandler.sendMessage(msg);
		}

	}
	void displayLocalFileList(ArrayList<FileInfo> files, String path) {
		ArrayList<File> filesArrayList= new ArrayList<File>();
		File nowFile = new File(path);		
		if (!nowFile.exists()) {
			if (!nowFile.mkdirs()){
				
			}
		}
		try {		
			ServiceFileManager.gerAllFileBy(filesArrayList,nowFile,fileType);
		} catch (Throwable e) {
		}
//		ServiceFileManager.sortByTypeName(filesArrayList);
		Calendar   cal=Calendar.getInstance();  
		String filesize;
		for(int i = 0;i<filesArrayList.size(); i++)
		{
			File tmpfile = filesArrayList.get(i);	
			filesize = ServiceFileManager.FormetFileSize(tmpfile.length());
			cal.setTimeInMillis(tmpfile.lastModified()); 
			
			int index = tmpfile.getName().lastIndexOf(".");
			String fileName = tmpfile.getName().substring(0, index);
			String type= tmpfile.getName().substring(index+1).toUpperCase();
			SimpleDateFormat sDataFormat = new SimpleDateFormat("yyy-mm-dd");
			FileInfo tmpinfo = new FileInfo(fileName,type,sDataFormat.format(cal.getTime()),filesize,tmpfile.getPath());
			files.add(tmpinfo);
		}
	}
	void displaySmbFileList(ArrayList<FileInfo> files, String path) {
		ArrayList<SmbFile> filesArrayList;
		Log.i(TAG,"displaySmbFileList " + path + " " + fileType);
		try {		
			filesArrayList = (ArrayList<SmbFile>)smbHelper.dirby(path, true,fileType);
		} catch (Throwable e) {
			filesArrayList= new ArrayList<SmbFile>();
		}
//		ServiceFileManager.sortByTypeName(filesArrayList);
		Calendar   cal=Calendar.getInstance();  
		SimpleDateFormat sDataFormat = new SimpleDateFormat("yyy-mm-dd");
		String filesize;
		for(int i = 0;i<filesArrayList.size(); i++)
		{
			SmbFile tmpfile = filesArrayList.get(i);	
			filesize = ServiceFileManager.FormetFileSize(tmpfile.getContentLength());
			cal.setTimeInMillis(tmpfile.getLastModified()); 
			
			int index = tmpfile.getName().lastIndexOf(".");
			String fileName = tmpfile.getName().substring(0, index);
			String type= tmpfile.getName().substring(index).toUpperCase();	
			FileInfo tmpinfo = new FileInfo(fileName,type,sDataFormat.format(cal.getTime()),filesize,tmpfile.getPath());
			files.add(tmpinfo);
		}
	}*/
	/*
	class ItemAdapter extends ArrayAdapter<FileInfo>
	{
		private LayoutInflater mInflater;
		public ItemAdapter(Context context,ArrayList<FileInfo> list)
		{
			super(context,0,list);
			mInflater = LayoutInflater.from(context);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			FileInfo tmpfile = (FileInfo)getItem(position);
			if(convertView == null){
				 convertView = mInflater.inflate(R.layout.movielistitem, parent, false);				
			}
			
			ImageView icon = (ImageView)convertView.findViewById(R.id.fileIcon);
			TextView tittle = (TextView)convertView.findViewById(R.id.fileTittle);
			TextView time = (TextView)convertView.findViewById(R.id.fileTime);
			TextView size = (TextView)convertView.findViewById(R.id.fileSize);
			TextView type = (TextView)convertView.findViewById(R.id.fileType);
			if(fileType.equals("video")){
				icon.setBackgroundResource(R.drawable.videoicon);
			}else if(fileType.equals("music")){
				icon.setBackgroundResource(R.drawable.musicicon);
			}else if((fileType.equals("photo"))){
				icon.setBackgroundResource(R.drawable.pictureicon);
			}else if(fileType.equals("txt")){
				icon.setBackgroundResource(R.drawable.txticon);
			}
			
			tittle.setText(tmpfile.tittle);
			type.setText(tmpfile.type);
			time.setText(tmpfile.time);
			size.setText((tmpfile.size));
			return convertView;
		}
	}*/
	/*
	class FileInfo
	{
		String tittle;
		//String date;
		String time;
		String size;
		String path;
		String type;
		public FileInfo(String tittle,String type,String time,String size,String path)
		{
			this.tittle = tittle;
			this.type = type;
			this.time = time;
			this.size = size;
			this.path = path;
		}
		
	}
      class DownloadTask extends AsyncTask<Integer, Integer, String>{       
          @Override  
          protected void onPreExecute() {  
              //第一个执行方法  
              super.onPreExecute();  
          }  
            
          @Override  
          protected String doInBackground(Integer... params) {  
              //第二个执行方法,onPreExecute()执行完后执行            
          	//initializeSettings();
        	initPage();
        	//pb.setVisibility(View.GONE);
          	return "ok";
          } 
    
          @Override  
          protected void onPostExecute(String result) {  
              //doInBackground返回时触发，换句话说，就是doInBackground执行完后触发  
              //这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"  
			//pb.setVisibility(View.GONE);
			super.onPostExecute(result);  
          }  
            
      }*/
    /*	public void openLocalFileWithSelectOpener(String pathString) {
    		Intent intent = new Intent();
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		intent.setAction(android.content.Intent.ACTION_VIEW);
    		String fileUriString = ServiceFileManager.getFileUriString(pathString);
    		intent.setDataAndType(Uri.fromFile(new File(pathString)), fileUriString);
    		startActivity(intent);
    	}*/
    	/*
    	public void openFileWithSelectOpener(String pathString) {
    		String localFilePath = pathString;
    		String [] pathParser = pathString.split("/");
    		String localFileName = pathParser[pathParser.length-1];
    		//如果是视频，启用VideoPlayActivity
    		String fileUriString = ServiceFileManager
    				.getFileUriString(localFilePath);
    		if( pathString.toLowerCase().contains("smb:/")){
    			if (fileUriString.equals("video/*") ||fileUriString.equals("audio/*")) {
    			//if (false) {
    				Intent intent = new Intent(this,
    						VideoPlayActivity.class);
    				intent.putExtra("smbpath", localFilePath);
    				startActivityForResult(intent, 11);
    				return;
    			}else{
    				File [] tempFiles = new File(Singleton.SMB_DOWNLOAD_TEMP).listFiles();
    				if(tempFiles!=null)
    					for (File file : tempFiles) {
    						file.delete();
    					}
    				localFilePath = Singleton.SMB_DOWNLOAD_TEMP + "/"+localFileName;
    				smbHelper.smbGet(pathString,  Singleton.SMB_DOWNLOAD_TEMP);
    			}
    		}

			if(fileUriString.equals("image/*")){
				Intent intent;		         
				intent = new Intent(this, ViewImage.class);				
				intent.setData(Uri.fromFile(new File(localFilePath)));
				startActivity(intent);
			}else
			{    		
	    		//其他文档下载后再浏览 然后删除文件
	    		Intent intent = new Intent();
	    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		intent.setAction(android.content.Intent.ACTION_VIEW);
	    		intent.setDataAndType(Uri.fromFile(new File(localFilePath)), fileUriString);
	    		if (pathString.toLowerCase().contains("smb:/"))
	    			startActivityForResult(intent, 12);
	    		else
	    			startActivity(intent);
			}
    	}*/
	/*
    private int selectedPosition;	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		selectedPosition = info.position;
		//FileItemForOperation operationItem = mData.getFileItems().get(
		//		selectedPosition);
		menu.setHeaderTitle(R.string.title_menutitle);
		menu.setHeaderIcon(R.drawable.toolbar_operation);
		
		menu.add(0, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
		menu.add(0, MENU_RENAME, Menu.NONE, R.string.menu_rename);
		menu.add(0, MENU_ADDTO_PLAYLIST,Menu.NONE, R.string.addPlayList);
		//menu.add(0, MENU_READPROP, Menu.NONE, R.string.menu_read_prop);		
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			//AdapterView.AdapterContextMenuInfo localAdapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			
			switch(item.getItemId()){
				case MENU_DELETE:					
					break;
				case MENU_RENAME:	
					break;
				case MENU_ADDTO_PLAYLIST:
					break;					
				default:
					break;
			}
			return true;
		}	
	*/
	public static void pasteComplete(){
		//int curr = mViewPager.getCurrentItem()% views.size();
		if(fileType.equals("music")){
			for(int i = 0;i<mMusicFileBrowsers.size();i++)
			mMusicFileBrowsers.get(i).pasteComplete();				
		}else if(fileType.equals("video")){	
			for(int i = 0;i<mVideoFileBrowsers.size();i++)
			mVideoFileBrowsers.get(i).pasteComplete();			
		}else if(fileType.equals("photo")){
			for(int i = 0;i<mImageFileBrowsers.size();i++)
			mImageFileBrowsers.get(i).pasteComplete();		
		}else{		
			for(int i = 0;i<mTxtFileBrowsers.size();i++)
			mTxtFileBrowsers.get(i).pasteComplete();			
		}
	}
	private void paste(){
		int curr = mViewPager.getCurrentItem()% views.size();
		if(fileType.equals("music")){
			String dst = mMusicFileBrowsers.get(curr).topDir;	
			for(int i =0;i<mMusicFileBrowsers.size();i++)
			{
				mMusicFileBrowsers.get(i).onBtnPaste(dst);
			}
		}else if(fileType.equals("video")){
			String dst = mVideoFileBrowsers.get(curr).topDir;	
			for(int i =0;i<mVideoFileBrowsers.size();i++)
			{
				mVideoFileBrowsers.get(i).onBtnPaste(dst);
			}
		}else if(fileType.equals("photo")){			
			String dst = mImageFileBrowsers.get(curr).topDir;	
			for(int i =0;i<mImageFileBrowsers.size();i++)
			{
				mImageFileBrowsers.get(i).onBtnPaste(dst);
			}
		}else{
			String dst = mTxtFileBrowsers.get(curr).topDir;	
			for(int i =0;i<mTxtFileBrowsers.size();i++)
			{
				mTxtFileBrowsers.get(i).onBtnPaste(dst);
			}
		}			
		
		if(PasteCompletionListner.mFilesPaste != 0)
			mPasteNum.setText("" + PasteCompletionListner.mFilesPaste);
		else
			mPasteNum.setText("");
		mFilesFor = FilesFor.UNKOWN;
		whichOperation(mFilesFor, 0);
	}
	private String formatStr(int resId, String str) {
		String res = getText(resId).toString();
		return String.format(res, str);
	}
	public void whichOperation(FilesFor filesFor, int size) {
		Log.i(TAG,"whichOperation " + filesFor);
		if (filesFor == FilesFor.COPY || filesFor == FilesFor.CUT) {
			if (filesFor == FilesFor.COPY) {
				ViewEffect.showToastLongTime(this,
						formatStr(R.string.intent_to_copy, "" + size));
			}
			if (filesFor == FilesFor.CUT)
				ViewEffect.showToastLongTime(this,
						formatStr(R.string.intent_to_cut, "" + size));			
			btnPaste.setImageResource(R.drawable.toolbar_paste_enable);
		

		}
		if (filesFor == FilesFor.UNKOWN) {			
			btnPaste.setImageResource(R.drawable.toolbar_paste_unenable);
		}
	}
	private void refresh(){
		int curr = mViewPager.getCurrentItem()% views.size();
		
		if(fileType.equals("music"))
			mMusicFileBrowsers.get(curr).QueryData((mMusicFileBrowsers.get(curr).topDir),true, FileFilter.MUSIC);
		else if(fileType.equals("video"))
			mVideoFileBrowsers.get(curr).QueryData((mVideoFileBrowsers.get(curr).topDir),true, FileFilter.VIDEO);
		else if(fileType.equals("photo"))
			mImageFileBrowsers.get(curr).QueryData((mImageFileBrowsers.get(curr).topDir),true, FileFilter.PICTURE);
		else
			mTxtFileBrowsers.get(curr).QueryData((mTxtFileBrowsers.get(curr).topDir),true, FileFilter.TXT);
	}
	private Browser getCurrBrowser(){
		/*int index = mViewPager.getCurrentItem() % mViews.size();
		switch (index) {
		case 0:
			return mFileBrowser;
		case 1:
			return mMusicFileBrowser;
		case 2:
			return mVideoFileBrowser;
		case 3:
			return mImageFileBrowser;
		default:
			return null;
		}*/
		int curr = mViewPager.getCurrentItem()% views.size();
	
		if(fileType.equals("music"))
			return mMusicFileBrowsers.get(curr);
		else if(fileType.equals("video"))
			return mVideoFileBrowsers.get(curr);
		else if(fileType.equals("photo"))
			return mImageFileBrowsers.get(curr);
		else
			return mTxtFileBrowsers.get(curr);
	}
	@Override
		public boolean onPrepareOptionsMenu(Menu menu) {
			menu.clear();
			Browser browser = getCurrBrowser();
			
			if (browser != null) {
				browser.onPrepareOptionsMenu(menu);
			}
			//menu.add(1, MENU_ABOUT_US, Menu.NONE, R.string.menu_about_us).setIcon(android.R.drawable.ic_menu_info_details);
			return true;
		}
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			
			return true;
		}
	
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			Browser browser = getCurrBrowser();
			if (browser != null) {
				if (browser.onOptionsItemSelected(item)){
					return true;
				}
			}
			/*
			switch (item.getItemId()) {
			case MENU_ABOUT_US:
				//AboutUs.getAboutUsDialog(this).show();
				return true;
			default:
				break;
			}*/
			return true;
		}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Browser browser = getCurrBrowser();
		browser.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		Browser browser = getCurrBrowser();
		browser.onContextMenuClosed(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Browser browser = getCurrBrowser();

		return browser.onContextItemSelected(item);
	}
/*
    class UIHandler extends Handler
  	{
    	Context mContext;
    	UIHandler(Context context)
  		{
    		mContext = context;
  		}

  		public void handleMessage(Message msg)
  		{
  			super.handleMessage(msg);
  			int id = msg.what;
  			ArrayList<FileInfo> filelist = (ArrayList<FileInfo>)msg.obj;
  			View view = views.get(id);
  			//final String tittle = (String)view.getTag();
			ListView mList = (ListView)view.findViewById(R.id.list);
			ItemAdapter mAdapter = new ItemAdapter(ListActivity.this,filelist); 
			mList.setAdapter(mAdapter);		
			
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Log.i(TAG,"onItemClick");
					FileInfo file = (FileInfo)arg0.getItemAtPosition(arg2);					
					Log.i(TAG,"open file " + file.path);
					openFileWithSelectOpener(file.path);
				}				
			});
			mList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});	
			mList.setLongClickable(true);
			registerForContextMenu(mList);
  		}
  		
  	}*/
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageSelected(int index) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onPageSelected :" + index + " " +views.size());
		if(views.size()>1){
			index = index % views.size();
			mAnimTab.moveTo(index);
			/*if(fileType.equals("music"))
				mMusicFileBrowsers.get(index).onResume();
			else if(fileType.equals("video"))
				mVideoFileBrowsers.get(index).onResume();			
			else if(fileType.equals("photo"))
				mImageFileBrowsers.get(index).onResume();			
			else
				mTxtFileBrowsers.get(index).onResume();	*/
		}
	}
	@Override
	public void tabChange(int index) {
		// TODO Auto-generated method stub
		int curr = mViewPager.getCurrentItem();
		int realIndex = curr % views.size();
		int toIndex = curr + (index - realIndex);
		Log.i(TAG, "index:" + index + " curr:" + curr + " realIndex:" + realIndex + " toIndex:" + toIndex);
		mViewPager.setCurrentItem(toIndex, false);
	}
}
