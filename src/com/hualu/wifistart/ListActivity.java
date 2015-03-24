package com.hualu.wifistart;

import java.io.File;
import java.util.ArrayList;

import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.filebrowser.Browser;
import com.hualu.wifistart.filecenter.filebrowser.ImageFileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.MusicFileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.TxtFileBrowser;
import com.hualu.wifistart.filecenter.filebrowser.VideoFileBrowser;
import com.hualu.wifistart.filecenter.files.FileManager;
import com.hualu.wifistart.filecenter.files.FileManager.FileComparatorMode;
import com.hualu.wifistart.filecenter.files.FileManager.FileFilter;
import com.hualu.wifistart.filecenter.files.FileManager.FilesFor;
import com.hualu.wifistart.filecenter.files.FileManager.OnWhichOperation;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.files.FilesAdapter;
import com.hualu.wifistart.filecenter.files.SearchAdapter;
import com.hualu.wifistart.filecenter.utils.CustomListener;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.smbsrc.Helper.wfDiskInfo;
import com.hualu.wifistart.views.AnimTabLayout;
import com.hualu.wifistart.views.QuickAction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.hualu.wifistart.views.AnimTabLayout.OnTabChangeListener;
import com.hualu.wifistart.wifisetting.utils.LanguageCheck;
import com.hualu.wifistart.R;

public class ListActivity extends FragmentActivity implements OnPageChangeListener,OnWhichOperation,OnTabChangeListener{
	//private int mCurrentDepth = 0;
	public float mDensity;
	//PivotControl mPivot;
	//private boolean notificationDone = false;
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
	//private String tilesBmpDir;
	//private PivotControl mPivot;
	private static ViewPager mViewPager;
	private AnimTabLayout mAnimTab;
	private boolean loadingMyPhone = false;		//wdx add 1205
	//private WifiStarActivity mainPage;		//wdx add 1205
	
	public static boolean mBusy = false; //标识是否存在滚屏操作
	//public TextView mPasteNum;
	
    //private TextView mTittle;
    //private ProgressBar pb; 

	private final int MENU_FIRST = Menu.FIRST + 100;
	//private final int MENU_DELETE = MENU_FIRST + 1;
	//private final int MENU_RENAME = MENU_FIRST + 2;
	private final int MENU_SELECT_ALL = MENU_FIRST + 3;
	//private final int MENU_REFRESH = MENU_FIRST + 4;
	//private final int MENU_ADDTO_PLAYLIST = MENU_FIRST + 5;
	private final int MENU_CUT = MENU_FIRST + 9;
	
	//private final int MENU_DISPLAY = MENU_FIRST + 15;
	//private final int MENU_STATUS = MENU_FIRST + 16;
    //public UIHandler mHandler = new UIHandler(this); 
	
	//luoronghui增加 imgHome,imgBack
	ImageView imgHome,imgBack;
    

	//ImageButton btnHome,btnBack;//btnDisplay,btnStatus;
	//luoronghui 增加textView的动态显示页面主题
	TextView menuTitle;
	
	ImageButton btnCopy,btnPaste,btnDelete,btnRefresh,btnSearch;//btnBack
	//RelativeLayout rlHome,rlBack,rlSearch,rlDisplay,rlStatus;
	static String fileType;
	//WPTextBox mSearchBox;
	View mRoot;
	//private QuickAction popAction = null;
	public int mHeight;
	public int mWidth;
	public static int mScreenWidth;
	public static int mScreenHight;
	private static boolean buildVoid = false;
	public SmbHelper smbHelper = new SmbHelper();
	
	final static String TAG = "ContentList";
	private SharedPreferences mPreferences;
	public static FilesFor mFilesFor = FilesFor.UNKOWN;
	/*
	private final static String displays[] = new String[]{"列表显示","按名称排序","按大小排序","按时间排序"};
	private final static String musicSearch[] = new String[]{"文件名","WMA","WAV","MP3","AAC","OGG","M4A"};
	private final static String videoSearch[] = new String[]{"文件名","AVI","FLV","F4V","MPG","MP4","RMVB",
		"RM","MKV","WMV","ASF","3GP","DIVX","MPEG","MOV","RAM","VOD"};
	private final static String photoSearch[] = new String[]{"文件名","PNG","JPG","GIF","BMP","JPEG","TIF"};
	final static String defaultSearch = "文件名";
	private final static String txtSearch[] = new String[]{"文件名","TXT","DOCX","PDF","XLS","WPS","DOC","PPT","DOXX","XLSX"};
	//final static String status[] = new String[]{"传送列表","播放列表","正在播放"};	// WDX	DELETE	1214
	final static String status[] = new String[]{"传送列表"};	// WDX	ADD	1214
	*/
	private static String displays[] = new String[]{"按名称排序","按大小排序","按时间排序"};
//	private static String musicSearch[] = new String[]{"文件名","WMA","WAV","MP3","AAC","OGG","M4A"};
	private static String musicSearch[] = new String[]{"文件名"};
//	private static String videoSearch[] = new String[]{"文件名","AVI","FLV","F4V","MPG","MP4","RMVB",
//		"RM","MKV","WMV","ASF","3GP","MPEG","MOV","VOD"};
		private static String videoSearch[] = new String[]{"文件名"};
//	private static String photoSearch[] = new String[]{"文件名","PNG","JPG","GIF","BMP","JPEG","TIF"};
	private static String photoSearch[] = new String[]{"文件名"};
	static String defaultSearch = "文件名";
//	private static String txtSearch[] = new String[]{"文件名","TXT","DOCX","PDF","XLS","WPS","DOC","PPT","DOXX","XLSX"};
	private static String txtSearch[] = new String[]{"文件名"};
	static String status[] = new String[]{"传送列表"};	// WDX	ADD	1214
	private PopupWindow pop;
	private View view;
	
	private HuzAlertDialog alertDialog;
	Context mContext;
    /*liuyufa add for vedio 20140318*/
	public static int isTrue=0;
	/*liuyufa add for vedio 20140318*/
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);	
		getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mDensity = getResources().getDisplayMetrics().density;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		mWidth = getResources().getDisplayMetrics().widthPixels;
		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		mScreenHight = getWindowManager().getDefaultDisplay().getHeight();
		
		views = new ArrayList<View>();
		tittles = new ArrayList<String>();
		disks = new ArrayList<wfDiskInfo>(); 	
		mMusicFileBrowsers = new ArrayList<MusicFileBrowser>();
		mVideoFileBrowsers = new ArrayList<VideoFileBrowser>();
		mImageFileBrowsers = new ArrayList<ImageFileBrowser>();
		mTxtFileBrowsers = new ArrayList<TxtFileBrowser>();
		mRoot = ((ViewGroup)((LayoutInflater)getSystemService("layout_inflater")).inflate(R.layout.activity_contentlist, null));
		setContentView(R.layout.activity_contentlist);
		/*liuyufa add for english mode 20140123 start*/
		if(!LanguageCheck.isZh()){
			displays[0] = "sort by name";
			displays[1] = "sort by size";
			displays[2] = "sort by time";
			musicSearch[0]="Filename";
			videoSearch[0]="Filename";
			photoSearch[0]="Filename";
			txtSearch[0]="Filename";
			defaultSearch = "Filename";
			status[0]="TransferList";
		}
		/*liuyufa add for english mode 20140123 end*/
		if (getIntent().getExtras() != null)
			fileType = getIntent().getExtras().getString("appName");
		else
			fileType = null;
		Log.i(TAG,"filetype = " + fileType);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mAnimTab = (AnimTabLayout) findViewById(R.id.animTab);
		imgHome=(ImageView)findViewById(R.id.homebg);
		imgBack=(ImageView)findViewById(R.id.backbg);	
		imgHome.setOnClickListener(new btnClickListener());
		imgBack.setOnClickListener(new btnClickListener());

		//增加引用
		menuTitle=(TextView)findViewById(R.id.menuTitle);
		
		btnPaste = (ImageButton)findViewById(R.id.btnPaste);
		btnPaste.setOnClickListener(new btnClickListener());
		
		
		btnSearch = (ImageButton)findViewById(R.id.btnSearch);			
		btnSearch.setOnClickListener(new btnClickListener());	
		
		btnCopy = (ImageButton)findViewById(R.id.btnCopy);
		btnCopy.setOnClickListener(new btnClickListener());		
		btnDelete = (ImageButton)findViewById(R.id.btnDelete);			
		btnDelete.setOnClickListener(new btnClickListener());
//		btnRefresh = (ImageButton)findViewById(R.id.btnRefresh);			
//		btnRefresh.setOnClickListener(new btnClickListener());
		
	    /*liuyufa add for vedio 20140318*/
		isTrue=1;
		FileActivity.isTrue=0;
		/*liuyufa add for vedio 20140318*/
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		initializeSettings();
		
		SysApplication.getInstance().addActivity(this); 
		
		initPopupWindow();
        setupViews();
        mContext=this;
	}
	@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			Browser browser = getCurrBrowser();	
			if(browser == null)
			{
				this.finish();
				return;
			}
	    	if(browser.mViewMode == ViewMode.SEARCHVIEW){	    			
				browser.toggleSearchMode(defaultSearch);				
				browser.toggleViewMode();
	    	}else{
				if(fileType.equals("music")){
					for(int i =0;i<mMusicFileBrowsers.size();i++){
						mMusicFileBrowsers.get(i).StopQueryData();
					}
				}			
				else if(fileType.equals("video")){
					for(int i =0;i<mVideoFileBrowsers.size();i++){
						mVideoFileBrowsers.get(i).StopQueryData();
					}
				}
				else if(fileType.equals("photo")){					
					for(int i =0;i<mImageFileBrowsers.size();i++){
						mImageFileBrowsers.get(i).StopQueryData();
					}
				}
				else{
					for(int i =0;i<mTxtFileBrowsers.size();i++){
						mTxtFileBrowsers.get(i).StopQueryData();
					}				
				}    	
				super.onBackPressed();
				this.finish();
		}
		}

	class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(FilesAdapter.Asynclist!=null&&FilesAdapter.filelist!=null){
				FilesAdapter.Asynclist.clear();
				FilesAdapter.filelist.clear();
			}
			if(SearchAdapter.Asynclist!=null&&SearchAdapter.filelist!=null){
				SearchAdapter.Asynclist.clear();
				SearchAdapter.filelist.clear();
			}
/*			if(Browser.mMemoryCache!=null){
				Browser.clearCache();
			}*/
			switch(v.getId()){
			case R.id.homebg:
				ListActivity.this.finish();
				startActivity(new Intent(ListActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:
				ListActivity.this.finish();
				startActivity(new Intent(ListActivity.this, WifiStarActivity.class));
				break;
			case R.id.btnPaste:					
				onBtnPaste();
				break;
			case R.id.btnSearch:{		
				  final Browser browser = getCurrBrowser();
				  if(browser.mViewMode == ViewMode.SEARCHVIEW){
					  browser.toggleSearchMode(defaultSearch);				
					  browser.toggleViewMode();	
				  }else{
					  int[] location = new int[2];  
					  btnSearch.getLocationOnScreen(location); 
			       	  Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
			       	  final QuickAction popAction ;
				      if(fileType.equals("music"))
				    	  popAction = new QuickAction(ListActivity.this,musicSearch);
					  else if(fileType.equals("video"))
						  popAction = new QuickAction(ListActivity.this,videoSearch);
					  else if(fileType.equals("photo"))
						  popAction = new QuickAction(ListActivity.this,photoSearch);
					  else
						  popAction = new QuickAction(ListActivity.this,txtSearch);	       	
			       	  popAction.setOnActionItemClickListener(new OnItemClickListener()
			       	  {
	
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Log.i(TAG,"POP onItemClick " + arg2);
							if(fileType.equals("music"))
								browser.toggleSearchMode(musicSearch[arg2]);
							else if(fileType.equals("video"))
								browser.toggleSearchMode(videoSearch[arg2]);
							else if(fileType.equals("photo"))	
								browser.toggleSearchMode(photoSearch[arg2]);
							else
								browser.toggleSearchMode(txtSearch[arg2]);
										
							browser.toggleViewMode();
							popAction.dismiss();
						} 
			       	  });
			       	 popAction.showPop(btnSearch,location[0]-5, location[1]+10); 
				  }
				break;
			}
			case R.id.btnCopy:
				onBtnCopy();
				break;
			case R.id.btnDelete:
				onBtnDelete();
				break;
//			case R.id.btnRefresh:
//				onBtnRefresh();
//				break;
			}
		}
	}
	
	/**
	 * 处理键盘事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			if(!pop.isShowing()){
			    pop.showAtLocation(this.view, Gravity.BOTTOM, 0, 0);
			}else{
				pop.dismiss();
			}
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	/**
	 * 初始话自定义Menu
	 */
	private void initPopupWindow(){
		view = this.getLayoutInflater().inflate(R.layout.popup_menu, null);
		pop = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
	}
	
	/**
	 * 控件初始化
	 */
	private void setupViews(){
		Button mDisplay = (Button)view.findViewById(R.id.btn_Display);
//		Button mStatus = (Button)view.findViewById(R.id.btn_Status);
		Button mSelectAll = (Button)view.findViewById(R.id.btn_SelectAll);
		Button mCut = (Button)view.findViewById(R.id.btn_Cut);
		
		mDisplay.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				popUpDisplayWindow();
			}
		});
		
//		mStatus.setOnClickListener(new View.OnClickListener() {
//			
//			public void onClick(View v) {
//				Intent intent = new Intent(ListActivity.this, StatusActivity.class);
//				intent.putExtra("listType", "transfer");	
//				ListActivity.this.startActivity(intent);
////				popUpStatusWindow();
//			}
//		});
		
		mSelectAll.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int curr = mViewPager.getCurrentItem()% views.size();
				if(fileType.equals("music"))
					mMusicFileBrowsers.get(curr).onClickPopMenu(MENU_SELECT_ALL);
				else if(fileType.equals("video"))
					mVideoFileBrowsers.get(curr).onClickPopMenu(MENU_SELECT_ALL);
				else if(fileType.equals("photo"))
					mImageFileBrowsers.get(curr).onClickPopMenu(MENU_SELECT_ALL);
				else
					mTxtFileBrowsers.get(curr).onClickPopMenu(MENU_SELECT_ALL);
			}
		});
		
		mCut.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				int curr = mViewPager.getCurrentItem()% views.size();
				if(fileType.equals("music"))
					mMusicFileBrowsers.get(curr).onClickPopMenu(MENU_CUT);
				else if(fileType.equals("video"))
					mVideoFileBrowsers.get(curr).onClickPopMenu(MENU_CUT);
				else if(fileType.equals("photo"))
					mImageFileBrowsers.get(curr).onClickPopMenu(MENU_CUT);
				else
					mTxtFileBrowsers.get(curr).onClickPopMenu(MENU_CUT);
			}
		});
 
}
	
	public void popUpDisplayWindow(){		
		 int[] location = new int[2];  
	  	 btnCopy.getLocationOnScreen(location); 
      	//  Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
      	  final QuickAction popAction = new QuickAction(this,displays);	       	
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
				}else */
				/*liuyufa change for listview 20140123 start*/
				/*
				if(arg2 == 0){
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
				*/
				if(arg2 == 0){
					browser.SetFileComparator(FileComparatorMode.ComparatorByName);
					browser.QueryData();
					popAction.dismiss();
				}else if(arg2 == 1){
					browser.SetFileComparator(FileComparatorMode.ComparatorBySize);
					browser.QueryData();
					popAction.dismiss();
				}else if(arg2 == 2){
					browser.SetFileComparator(FileComparatorMode.ComparatorByTime);
					browser.QueryData();
					popAction.dismiss();
				}
				/*liuyufa change for listview 20140123 end*/
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
      	 //int pos = mViewPager.getCurrentItem()% views.size(); 
      	 View view = views.get(mViewPager.getCurrentItem()% views.size());
      	 //popAction.show(view,view.getMeasuredWidth(), view.getMeasuredHeight());
      	popAction.show(view,0,location[1], view.getMeasuredHeight());
	}
	public void popUpStatusWindow(){		
	  int[] location = new int[2];  
  	  //int[]location2=new int[2];
	  btnCopy.getLocationOnScreen(location); 
  	  //mStatus.getLocationOnScreen(location2);
	 // getLocationInWindow(location2);
  	  //Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
  	  final QuickAction popAction = new QuickAction(this,status);
  	  popAction.setOnActionItemClickListener(new OnItemClickListener()
     	  {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2 == 0){	
					Intent intent = new Intent(ListActivity.this, StatusActivity.class);
					intent.putExtra("listType", "transfer");	
					ListActivity.this.startActivity(intent);  		
					popAction.dismiss();
				}/*else if(arg2 == 1){
					Intent intent = new Intent(ListActivity.this, StatusActivity.class);
					intent.putExtra("listType", "play");	
					ListActivity.this.startActivity(intent); 
					popAction.dismiss();
				}else if(arg2 == 2){	
					Intent intent = new Intent(ListActivity.this, MusicActivity.class);								
					ListActivity.this.startActivity(intent);
					popAction.dismiss();
				}*/
			}
     	  });
  	  View view = views.get(mViewPager.getCurrentItem()% views.size());
  	  //popAction.show(view,view.getMeasuredWidth(), view.getMeasuredHeight());
  	  Log.i("location  =============","location_x:"+location[0]+"   mScreenwith:  "+mScreenWidth+"   location/2:  " );
  	  popAction.show(view,mScreenWidth/4,location[1], view.getMeasuredHeight());
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
				convertView = new TextView(ListActivity.this);
			}
			((TextView) convertView).setText(tittles.get(position));
			((TextView) convertView).setTextAppearance(ListActivity.this,R.style.tvPage_Title);
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
		if(Singleton.SMB_ONLINE)
			Singleton.initDiskInfo();
		disks = Singleton.instance().disks;
	
		if(disks.size() > 0){
			//String diskPath = null;
			for(int i = 0;i<disks.size();i++){
				wfDiskInfo disk = disks.get(i);
				if(disk.des.equals(".config")) {
					Log.d(TAG, "find .config file");
					continue;
				}
				//if(diskPath!= null && !diskPath.equals(disk.path))
				tittles.add(disk.des);
			}
			
			String[] stockArr = new String[tittles.size()];
		    stockArr = tittles.toArray(stockArr);	
			//mPivot.setTitles(stockArr);
		    mAnimTab.setAdapter(tabAdapter);
			mAnimTab.setOnTabChangeListener(this);

			if(fileType.equals("music"))
			{
				mMusicFileBrowsers.clear();
				for(int i = 0;i<tittles.size();i++){	
					String mode = mPreferences.getString(disks.get(i).path+"music",ViewMode.LISTVIEW.toString());	
					Log.i(TAG,disks.get(i).path+"music" + " " + mode);
					if(i == tittles.size() - 1){
						buildVoid = true;
					}
					MusicFileBrowser mFileBrowser = new MusicFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
					mFileBrowser.mFileManager.setOnWhichoperation(this);
					mMusicFileBrowsers.add(mFileBrowser);
					views.add(i,mFileBrowser.getView());
					menuTitle.setText(getString(R.string.musicsTittle));
					buildVoid = false;
				}
			}else if(fileType.equals("video")){			
				mVideoFileBrowsers.clear();
				MApplication.setmContext(this);
				for(int i = 0;i<tittles.size();i++){
					String mode = mPreferences.getString(disks.get(i).path+"video",ViewMode.LISTVIEW.toString());
					if(i == tittles.size() - 1){
						buildVoid = true;
					}
					VideoFileBrowser mFileBrowser = new VideoFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb //disks.get(i).path
					mFileBrowser.mFileManager.setOnWhichoperation(this);
					mVideoFileBrowsers.add(mFileBrowser);
					views.add(i,mFileBrowser.getView());
					menuTitle.setText(getString(R.string.moviesTittle));
					buildVoid = false;
				}
			}else if(fileType.equals("photo")){		
				mImageFileBrowsers.clear();
				for(int i = 0;i<tittles.size();i++){
					String mode = mPreferences.getString(disks.get(i).path+"photo",ViewMode.LISTVIEW.toString());
					if(i == tittles.size() - 1){
						buildVoid = true;
					}
					ImageFileBrowser mFileBrowser = new ImageFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
					mFileBrowser.mFileManager.setOnWhichoperation(this);
					mImageFileBrowsers.add(mFileBrowser);
					views.add(i,mFileBrowser.getView());
					menuTitle.setText(getString(R.string.photosTittle));
					buildVoid = false;
				}
			}else if(fileType.equals("txt")){		
				mTxtFileBrowsers.clear();
				for(int i = 0;i<tittles.size();i++){
					String mode = mPreferences.getString(disks.get(i).path+"txt",ViewMode.LISTVIEW.toString());	
					if(i == tittles.size() - 1){
						buildVoid = true;
					}
					TxtFileBrowser mFileBrowser = new TxtFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
					mFileBrowser.mFileManager.setOnWhichoperation(this);
					mTxtFileBrowsers.add(mFileBrowser);
					views.add(i,mFileBrowser.getView());
					menuTitle.setText(getString(R.string.filesTittle));
					buildVoid = false;
				}
			}
			
			//wdx add 1206
			if(tittles.size() == 1)
			{
				loadingPhone();
			}
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setCurrentItem(0);//4 * 50
			mViewPager.setOnPageChangeListener(this);
//			final Browser browser = getCurrBrowser();
//			browser.mListView.setOnScrollListener(this);//wdx 20140211	
			buildVoid = false;
			/*
			for(int i = 0;i<views.size();i++){
				View view = (View)views.get(i);
				view.setTag(tittles.get(i));
				mPivot.addPage(view);
			}*/
			//initPage();
		}
	}
	@Override
	protected void onResume() {
		Log.i(TAG,"onResume");
		super.onResume();
		if(views.size()==0){
			this.finish();
			System.exit(0);
		}
		//registerSDCardChangeReceiver();
		//IntentFilter filter=new IntentFilter("com.hualu.wifishare.paste.complete");
		//registerReceiver(completionListner, filter);
		
	}
	@Override
	protected void onPause() {
		Log.i(TAG,"onPause");
		super.onPause();
	}
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
	private void onBtnPaste(){
		int curr = mViewPager.getCurrentItem()% views.size();
		if(fileType.equals("music")){
			String dst = mMusicFileBrowsers.get(curr).topDir;
			if(!dst.startsWith("smb"))
				dst = Environment.getExternalStorageDirectory().getPath() + File.separator;
			for(int i =0;i<mMusicFileBrowsers.size();i++)
			{
				mMusicFileBrowsers.get(i).onBtnPaste(dst);
			}
		}else if(fileType.equals("video")){
			String dst = mVideoFileBrowsers.get(curr).topDir;
			if(!dst.startsWith("smb"))
				dst = Environment.getExternalStorageDirectory().getPath() + File.separator;
			for(int i =0;i<mVideoFileBrowsers.size();i++)
			{
				mVideoFileBrowsers.get(i).onBtnPaste(dst);
			}
		}else if(fileType.equals("photo")){
			String dst = mImageFileBrowsers.get(curr).topDir;
			if(!dst.startsWith("smb"))
				dst = Environment.getExternalStorageDirectory().getPath() + File.separator;
			for(int i =0;i<mImageFileBrowsers.size();i++)
			{
				mImageFileBrowsers.get(i).onBtnPaste(dst);
			}
		}else{
			String dst = mTxtFileBrowsers.get(curr).topDir;
			if(!dst.startsWith("smb"))
				dst = Environment.getExternalStorageDirectory().getPath() + File.separator;
			for(int i =0;i<mTxtFileBrowsers.size();i++)
			{
				mTxtFileBrowsers.get(i).onBtnPaste(dst);
			}
		}			
		/*
		if(PasteCompletionListner.mFilesPaste != 0)
			mPasteNum.setText("" + PasteCompletionListner.mFilesPaste);
		else
			mPasteNum.setText("");*/
		mFilesFor = FilesFor.UNKOWN;
		whichOperation(mFilesFor, 0);
	}
	private void onBtnCopy(){
		int curr = mViewPager.getCurrentItem()% views.size();
		if(fileType.equals("music"))
			mMusicFileBrowsers.get(curr).onBtnCopy();
		else if(fileType.equals("video"))
			mVideoFileBrowsers.get(curr).onBtnCopy();
		else if(fileType.equals("photo"))
			mImageFileBrowsers.get(curr).onBtnCopy();
		else
			mTxtFileBrowsers.get(curr).onBtnCopy();
	}
	private void onBtnDelete(){
		int curr = mViewPager.getCurrentItem()% views.size();
		
		if(fileType.equals("music"))
			mMusicFileBrowsers.get(curr).onBtnDelete();
		else if(fileType.equals("video"))
			mVideoFileBrowsers.get(curr).onBtnDelete();
		else if(fileType.equals("photo"))
			mImageFileBrowsers.get(curr).onBtnDelete();
		else
			mTxtFileBrowsers.get(curr).onBtnDelete();
	}
	public static void onBtnRefresh(){
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
		int curr=0;
		if(views.size()!=0){
		 curr= mViewPager.getCurrentItem()% views.size();
		
		if(fileType.equals("music"))
			return mMusicFileBrowsers.get(curr);
		else if(fileType.equals("video"))
			return mVideoFileBrowsers.get(curr);
		else if(fileType.equals("photo"))
			return mImageFileBrowsers.get(curr);
		else
			return mTxtFileBrowsers.get(curr);
	}else//wdx add 1219
	{
		this.finish();
		return null;
	}
}
	 /*protected void setMenuBackground() {
	        ListActivity.this.getLayoutInflater().setFactory(new android.view.LayoutInflater.Factory() {     
	            /**   
	             * name - Tag name to be inflated.<br/>   
	             * context - The context the view is being created in.<br/>   
	             * attrs - Inflation attributes as specified in XML file.<br/>   
	             *     
	            public View onCreateView(String name, Context context, AttributeSet attrs) {     
	                // 指定自定义inflate的对象     
	                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {     
	                    try {     
	                        LayoutInflater f = getLayoutInflater();     
	                        final View view = f.createView(name, null, attrs);     
	                        new Handler().post(new Runnable() {     
	                            public void run() {     
	                                // 设置背景图片     
	                                view.setBackgroundColor(R.color.color_white);;     
	                            }     
	                        });     
	                        return view;
	                    } catch (InflateException e) {     
	                        e.printStackTrace();
	                    } catch (ClassNotFoundException e) {     
	                        e.printStackTrace();
	                    }     
	                }     
	                return null;
	            }
	        });
	    } */
//	@Override
//		public boolean onPrepareOptionsMenu(Menu menu) {
//			
//			//if(!fileType.equals("music")
//		   {	
//				menu.clear();
//				Browser browser = getCurrBrowser();
//				menu.add(1, MENU_DISPLAY, Menu.NONE, R.string.display);
//				menu.add(1, MENU_STATUS, Menu.NONE, R.string.status);
//				menu.add(1, MENU_SELECT_ALL, Menu.NONE, R.string.menu_select_all);
//				menu.add(1, MENU_CUT, Menu.NONE, R.string.menu_cut_selected);
//				if (browser != null) {
//					browser.onPrepareOptionsMenu(menu);
//				}
//				return true;
//				//menu.add(1, MENU_ABOUT_US, Menu.NONE, R.string.menu_about_us).setIcon(android.R.drawable.ic_menu_info_details);
//			}
//			//else
//			//	return super.onPrepareOptionsMenu(menu);
//			
//		}
//		@Override
//		public boolean onCreateOptionsMenu(Menu menu) {	
//			/*
//			if(fileType.equals("music")){
//				Browser browser = getCurrBrowser();
//				
//				if(browser.topDir.startsWith("smb")){
//					Log.i(TAG,"onCreateOptionsMenu " + browser.topDir);
//					getMenuInflater().inflate(R.menu.musicmenu, menu); 
//				}
//				else{
//					Log.i(TAG,"onCreateOptionsMenu " + browser.topDir);
//					getMenuInflater().inflate(R.menu.musicmenuall, menu); 
//				}
//					
//			}*/
//			//setMenuBackground();
//			return true;
//		}
//	
//		@Override
//		public boolean onOptionsItemSelected(MenuItem item) {
//		if(item.getItemId() == MENU_DISPLAY)
//			popUpDisplayWindow();
//		else if(item.getItemId() == MENU_STATUS)		
//			popUpStatusWindow();
//		else{	
//			Browser browser = getCurrBrowser();
//			if (browser != null) {
//				if (browser.onOptionsItemSelected(item)){
//					return true;
//				}
//			}
//		}
//			/*
//			switch (item.getItemId()) {
//			case MENU_ABOUT_US:
//				//AboutUs.getAboutUsDialog(this).show();
//				return true;
//			default:
//				break;
//			}*/
//			return true;
//		}
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
		int curr = mViewPager.getCurrentItem();
		int realIndex = curr % views.size();
		int toIndex = curr + (index - realIndex);
		Log.i(TAG, "onPageSelected :" + index + " " +views.size());
		if(views.size()>1){
			//index = index % views.size();
			mAnimTab.moveTo(realIndex);
			if(fileType.equals("music"))
			{
				mMusicFileBrowsers.get(realIndex).onResume();
			}
			else if(fileType.equals("video"))
				mVideoFileBrowsers.get(realIndex).onResume();			
			else if(fileType.equals("photo"))
				mImageFileBrowsers.get(realIndex).onResume();			
			else
				mTxtFileBrowsers.get(realIndex).onResume();	
		}
		
		//wdx add 1216
		if((toIndex == views.size() - 1) && WifiStarActivity.entryFirst)
		{
			loadingMyPhone = true;
			WifiStarActivity.entryFirst = false;
		}
		else
		{
			loadingMyPhone = false;
		}
		if(shoudLoadMyPhone())
		{
			loadingPhone();
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setCurrentItem(0);//4 * 50
			mViewPager.setOnPageChangeListener(this);
		}
		mViewPager.setCurrentItem(toIndex, false);
	}
	@Override
	public void tabChange(int index) {
		// TODO Auto-generated method stub
		int curr = mViewPager.getCurrentItem();
		int realIndex = curr % views.size();
		int toIndex = curr + (index - realIndex);
		Log.i(TAG, "index:" + index + " curr:" + curr + " realIndex:" + realIndex + " toIndex:" + toIndex);
		
		//wdx rewrite 1216
		if((toIndex == views.size() - 1) && WifiStarActivity.entryFirst)
		{
			loadingMyPhone = true;
			WifiStarActivity.entryFirst = false;
		}
		else
		{
			loadingMyPhone = false;
		}
		if(shoudLoadMyPhone())
		{
			loadingPhone();
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setCurrentItem(0);//4 * 50
			mViewPager.setOnPageChangeListener(this);
		}
		mViewPager.setCurrentItem(toIndex, false);
	}
	//wdx	add	1216
	public static boolean buildVoidFileBrowser()
	{
		return buildVoid;
	}
	public boolean shoudLoadMyPhone()
	{
		return loadingMyPhone;
	}
	
	//wdx rewrite 1206
	public void loadingPhone()
	{
		int i = views.size() - 1;//wdx	rewrite	1216
		views.remove(i);	//wdx	add	1216
		if(fileType.equals("music"))
		{
			String mode = mPreferences.getString(disks.get(i).path+"music",ViewMode.LISTVIEW.toString());	
			Log.i(TAG,disks.get(i).path+"music" + " " + mode);
			MusicFileBrowser mFileBrowser = new MusicFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
			mFileBrowser.mFileManager.setOnWhichoperation(this);
			mMusicFileBrowsers.remove(i);//wdx	add	1216
			mMusicFileBrowsers.add(mFileBrowser);
			views.add(i,mFileBrowser.getView());
			menuTitle.setText(getString(R.string.musicsTittle));
		}
		else if(fileType.equals("video"))
		{
			String mode = mPreferences.getString(disks.get(i).path+"music",ViewMode.LISTVIEW.toString());	
			Log.i(TAG,disks.get(i).path+"music" + " " + mode);
			VideoFileBrowser mFileBrowser = new VideoFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
			mFileBrowser.mFileManager.setOnWhichoperation(this);
			mVideoFileBrowsers.remove(i);//wdx	add	1216
			mVideoFileBrowsers.add(mFileBrowser);
			views.add(i,mFileBrowser.getView());
			menuTitle.setText(getString(R.string.moviesTittle));
		}
		else if(fileType.equals("photo"))
		{
			String mode = mPreferences.getString(disks.get(i).path+"music",ViewMode.LISTVIEW.toString());	
			Log.i(TAG,disks.get(i).path+"music" + " " + mode);
			ImageFileBrowser mFileBrowser = new ImageFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
			mFileBrowser.mFileManager.setOnWhichoperation(this);
			mImageFileBrowsers.remove(i);//wdx	add	1216
			mImageFileBrowsers.add(mFileBrowser);
			views.add(i,mFileBrowser.getView());
			menuTitle.setText(getString(R.string.photosTittle));
		}
		else if(fileType.equals("txt"))
		{
			String mode = mPreferences.getString(disks.get(i).path+"music",ViewMode.LISTVIEW.toString());	
			Log.i(TAG,disks.get(i).path+"music" + " " + mode);
			TxtFileBrowser mFileBrowser = new TxtFileBrowser(this,disks.get(i).path,this,ViewMode.toViewMode(mode));//isSmb
			mFileBrowser.mFileManager.setOnWhichoperation(this);
			mTxtFileBrowsers.remove(i);//wdx	add	1216
			mTxtFileBrowsers.add(mFileBrowser);
			views.add(i,mFileBrowser.getView());
			menuTitle.setText(getString(R.string.filesTittle));
		}
	}
}
