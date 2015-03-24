package com.hualu.wifistart;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hualu.wifistart.filecenter.filebrowser.AppItemLoader;
import com.hualu.wifistart.filecenter.filebrowser.Browser;
import com.hualu.wifistart.filecenter.filebrowser.FileBrowser;
import com.hualu.wifistart.filecenter.files.FileManager;
import com.hualu.wifistart.filecenter.files.FileManager.FilesFor;
import com.hualu.wifistart.filecenter.files.FileManager.OnWhichOperation;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.files.FilesAdapter;
import com.hualu.wifistart.filecenter.files.RefreshData;
import com.hualu.wifistart.filecenter.files.SearchAdapter;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.smbsrc.Helper.wfDiskInfo;
import com.hualu.wifistart.views.AnimTabLayout;
import com.hualu.wifistart.views.AnimTabLayout.OnTabChangeListener;
import com.hualu.wifistart.views.WPTextBox;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class FileActivity extends FragmentActivity implements
		OnPageChangeListener, OnWhichOperation, OnTabChangeListener,
		UncaughtExceptionHandler {

	public static FileActivity instance1 = null;
	
	public FileManager mFileManager;
	private static String TAG = "FileActivity";
	private static List<FileBrowser> mFileBrowsers = new ArrayList<FileBrowser>();
	public static int mScreenWidth;
	private static ArrayList<View> views = new ArrayList<View>();
	private ArrayList<String> tittles;
	ArrayList<wfDiskInfo> disks;
	private static ViewPager mViewPager;
	private AnimTabLayout mAnimTab;
	public ProgressBar pb;
	public UIHandler mHandler = new UIHandler(this);
	ImageButton btnTakePhoto, btnRecord, btnSearch;
	ImageView imgHome, imgBack;
	TextView menuTitle;
	public static ImageButton btnCopy, btnPaste;
	WPTextBox mSearchBox;
	View mRoot;
	public SmbHelper smbHelper = new SmbHelper();
	private final int MENU_FIRST = Menu.FIRST + 100;
	private final int MENU_REFRESH = MENU_FIRST + 7;
	private final int MENU_DELETE = MENU_FIRST + 2;
	private final int MENU_NEW = MENU_FIRST + 11;
	private final int MENU_SELECT_ALL = MENU_FIRST + 5;
	private final int MENU_CUT = MENU_FIRST + 1;
	public final static int ACTIVITY_RESULT_CAPTUREPICTURE = 102;
	final static String defaultSearch = "文件名";
	public static FilesFor mFilesFor = FilesFor.UNKOWN;
	public static SharedPreferences mPreferences;
	private PopupWindow pop;
	private View view;
	public static int isTrue = 0;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		tittles = new ArrayList<String>();
		disks = new ArrayList<wfDiskInfo>();
		setContentView(R.layout.activity_file);
		MApplication.setmContext(this.getApplication());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mAnimTab = (AnimTabLayout) findViewById(R.id.animTab);
		pb = (ProgressBar) findViewById(R.id.pbar);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack.setOnClickListener(new btnClickListener());
		menuTitle = (TextView) findViewById(R.id.menuTitle);
		menuTitle.setText(R.string.managementTittle);
		btnTakePhoto = (ImageButton) findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(new btnClickListener());
		btnCopy = (ImageButton) findViewById(R.id.btnCopy);
		btnCopy.setOnClickListener(new btnClickListener());
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new btnClickListener());
		btnRecord = (ImageButton) findViewById(R.id.btnRecord);
		btnRecord.setOnClickListener(new btnClickListener());
		btnPaste = (ImageButton) findViewById(R.id.btnPaste);
		btnPaste.setOnClickListener(new btnClickListener());
		isTrue = 1;
		ListActivity.isTrue = 0;
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		initializeSettings();
		SysApplication.getInstance().addActivity(this);
		initPopupWindow();
		setupViews();
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		//2014/11/03
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		
		if (arg1 == RESULT_OK) {
			if (arg0 == ACTIVITY_RESULT_CAPTUREPICTURE) {
				FileBrowser browser = getCurrBrowser();
				browser.QueryData(browser.currFolder);
			}
			if (arg0==130) {
				ToastBuild.toast(this, "通讯录导入成功");
			}
		} else if (arg1 == RESULT_CANCELED) {
			if (arg0==130) {
				ToastBuild.toast(this, "通讯录导入失败");
			}
//			if (arg0 == 100) {
//				final FileBrowser browser = getCurrBrowser();
//				final int position = browser.itemClickPosition;
//				Dialog dialog = new HuzAlertDialog.Builder(this)
//						.setTitle(R.string.title_comfir_delete)
//						.setMessage(
//								(getResources()
//										.getString(R.string.fileDownloadMsg)))
//						.setPositiveButton(R.string.set_done,
//								new DialogInterface.OnClickListener() {
//									public void onClick(
//											DialogInterface paramDialogInterface,
//											int paramInt) {
//										String dst = Singleton.LOCAL_ROOT_WIFIDOCK;
//
//										FileItemForOperation fileItem;
//										if (browser.mViewMode == ViewMode.SEARCHVIEW) {
//											fileItem = browser.mSearchAdapter.mSearchData
//													.getFileItems().get(
//															position);
//										} else
//											fileItem = browser.mData
//													.getFileItems().get(
//															position);
//										mFileBrowsers.get(0).mFileManager
//												.resetDataForOperation();
//										mFileBrowsers.get(0).mFileManager
//												.addFileItem(fileItem);
//
//										mFileBrowsers.get(0).onBtnPaste(dst);
//										paramDialogInterface.dismiss();
//									}
//								})
//						.setNegativeButton(R.string.set_cancel,
//								new DialogInterface.OnClickListener() {
//									public void onClick(
//											DialogInterface paramDialogInterface,
//											int paramInt) {
//										paramDialogInterface.dismiss();
//									}
//								}).create();
//				dialog.show();
//			}
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStart");
		super.onStart();

	}

	private String formatStr(int resId, String str) {
		String res = getText(resId).toString();
		return String.format(res, str);
	}

	public void whichOperation(FilesFor filesFor, int size) {
		Log.i(TAG, "whichOperation " + filesFor);
		if (filesFor == FilesFor.COPY || filesFor == FilesFor.CUT) {
			if (filesFor == FilesFor.COPY) {
				ViewEffect.showToastLongTime(this,
						formatStr(R.string.intent_to_copy, "" + size));
			}
			if (filesFor == FilesFor.CUT)
				ViewEffect.showToastLongTime(this,
						formatStr(R.string.intent_to_cut, "" + size));
			FileActivity.btnPaste
					.setImageResource(R.drawable.toolbar_paste_enable);
		}
		if (filesFor == FilesFor.UNKOWN) {
			FileActivity.btnPaste
					.setImageResource(R.drawable.toolbar_paste_unenable);
		}
	}

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			isCamera = false;
			if (FilesAdapter.Asynclist != null && FilesAdapter.filelist != null) {
				FilesAdapter.Asynclist.clear();
				FilesAdapter.filelist.clear();
			}
			if (SearchAdapter.Asynclist != null
					&& SearchAdapter.filelist != null) {
				SearchAdapter.Asynclist.clear();
				SearchAdapter.filelist.clear();
			}
			/*
			 * if(Browser.mMemoryCache!=null){ Browser.clearCache(); }
			 */
			// TODO Auto-generated method stub
			FileBrowser browser = getCurrBrowser();
			if (v.getId() == R.id.homebg) {
				FileActivity.this.finish();
				startActivity(new Intent(FileActivity.this,
						WifiStarActivity.class));
			} else if (v.getId() == R.id.backbg) {
				dispachBackKey();
			} else if (v.getId() == R.id.btnTakePhoto) {
				int curr = mViewPager.getCurrentItem() % views.size();
				String curDir = mFileBrowsers.get(curr).currFolder;
				if ("/storage/".equals(curDir)) {
					Toast.makeText(getBaseContext(), R.string.choosedisk,
							Toast.LENGTH_SHORT).show();
					return;
				} else if (curDir.startsWith("smb")) {
					try {
						SmbFile file = new SmbFile(curDir);
						file.connect();
						if (!file.canWrite()) {
							Toast.makeText(getBaseContext(),
									R.string.effectivedisk, Toast.LENGTH_SHORT)
									.show();
							return;
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SmbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					File file = new File(curDir);
					if (!file.canWrite()) {
						Toast.makeText(getBaseContext(),
								R.string.effectivedisk, Toast.LENGTH_SHORT)
								.show();
						return;
					}
				}
				Intent intent = new Intent(FileActivity.this,
						CameraActivity1.class);
				intent.putExtra("currFolder",
						mFileBrowsers.get(curr).currFolder);
				// (FileActivity.this).startActivityForResult(intent,
				// FileActivity.ACTIVITY_RESULT_CAPTUREPICTURE);
				startActivity(intent);
			} else if (v.getId() == R.id.btnSearch) {
//				int curr = mViewPager.getCurrentItem() % views.size();
//				String curDir = mFileBrowsers.get(curr).currFolder;
//				if ("/storage/".equals(curDir)) {
//					Toast.makeText(getBaseContext(), R.string.choosedisk,
//							Toast.LENGTH_SHORT).show();
//					return;
//				} else if (curDir.startsWith("smb")) {
//					try {
//						SmbFile file = new SmbFile(curDir);
//						file.connect();
//						if (!file.canWrite()) {
//							Toast.makeText(getBaseContext(),
//									R.string.effectivedisk, Toast.LENGTH_SHORT)
//									.show();
//							return;
//						}
//					} catch (MalformedURLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (SmbException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} else {
//					File file = new File(curDir);
//					if (!file.canWrite()) {
//						Toast.makeText(getBaseContext(),
//								R.string.effectivedisk, Toast.LENGTH_SHORT)
//								.show();
//						return;
//					}
//				}
//				Intent intent = new Intent(FileActivity.this,
//						AudioActivity.class);
//				intent.putExtra("currFolder",
//						mFileBrowsers.get(curr).currFolder);
//				startActivity(intent);
				
				
				
				
				
				browser.toggleSearchMode(defaultSearch);
				browser.toggleViewMode();
			} else if (v.getId() == R.id.btnCopy) {
				int curr = mViewPager.getCurrentItem() % views.size();
				mFileBrowsers.get(curr).onBtnCopy();

			} else if (v.getId() == R.id.btnPaste) {
				int curr = mViewPager.getCurrentItem() % views.size();
				String dst = mFileBrowsers.get(curr).getCurrFolder();
				for (int i = 0; i < mFileBrowsers.size(); i++) {
					mFileBrowsers.get(i).onBtnPaste(dst);// mFileBrowsers.get(curr),
				}
				mFilesFor = FilesFor.UNKOWN;
				RefreshData.mediaFileItemstable.clear();
				RefreshData.mlocalFileItemstable.clear();
				whichOperation(mFilesFor, 0);
			}

			else if (v.getId() == R.id.btnRecord) {
				int curr = mViewPager.getCurrentItem() % views.size();
				String curDir = mFileBrowsers.get(curr).currFolder;
				if ("/storage/".equals(curDir)) {
					Toast.makeText(getBaseContext(), R.string.choosedisk,
							Toast.LENGTH_SHORT).show();
					return;
				} else if (curDir.startsWith("smb")) {
					try {
						SmbFile file = new SmbFile(curDir);
						file.connect();
						if (!file.canWrite()) {
							Toast.makeText(getBaseContext(),
									R.string.effectivedisk, Toast.LENGTH_SHORT)
									.show();
							return;
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SmbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					File file = new File(curDir);
					if (!file.canWrite()) {
						Toast.makeText(getBaseContext(),
								R.string.effectivedisk, Toast.LENGTH_SHORT)
								.show();
						return;
					}
				}
				Intent intent = new Intent(FileActivity.this,
						RecorderActivity1.class);
				intent.putExtra("currFolder",
						mFileBrowsers.get(curr).currFolder);
//				(FileActivity.this).startActivityForResult(intent,
//						FileActivity.ACTIVITY_RESULT_CAPTUREPICTURE);
				startActivity(intent);
			}
		}
	}

	/**
	 * 处理键盘事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			isCamera = false;
			if (!pop.isShowing()) {
				pop.showAtLocation(this.view, Gravity.BOTTOM, 0, 0);
			} else {
				pop.dismiss();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 初始话自定义Menu
	 */
	private void initPopupWindow() {
		view = this.getLayoutInflater()
				.inflate(R.layout.pop_menu_5button, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
	}
	

	/**
	 * 控件初始化
	 */
	
	private void setupViews() {
//		Button mRefresh = (Button) view.findViewById(R.id.btn_Refresh);
		Button mDelete = (Button) view.findViewById(R.id.btn_Delete);
		Button mNewfold = (Button) view.findViewById(R.id.btn_Newfolde);
		Button mSelectAll = (Button) view.findViewById(R.id.btn_SelectAll);
		Button mCut = (Button) view.findViewById(R.id.btn_Cut);
//		mRefresh.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				int curr = mViewPager.getCurrentItem() % views.size();
//				mFileBrowsers.get(curr).onClickPopMenu(MENU_REFRESH);
//			}
//		});
		mDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int curr = mViewPager.getCurrentItem() % views.size();
				mFileBrowsers.get(curr).onClickPopMenu(MENU_DELETE);
			}
		});
		mNewfold.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int curr = mViewPager.getCurrentItem() % views.size();
				mFileBrowsers.get(curr).onClickPopMenu(MENU_NEW);
			}
		});
		mSelectAll.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int curr = mViewPager.getCurrentItem() % views.size();
				mFileBrowsers.get(curr).onClickPopMenu(MENU_SELECT_ALL);
			}
		});
		mCut.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int curr = mViewPager.getCurrentItem() % views.size();
				mFileBrowsers.get(curr).onClickPopMenu(MENU_CUT);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (views.size() == 0) {
			this.finish();
			System.exit(0);
		}
		int curr = mViewPager.getCurrentItem() % views.size();
		mFileBrowsers.get(curr).onClickPopMenu(MENU_REFRESH);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppItemLoader.isfileactivity = 0;
	}

	static boolean isCamera = false;

	@Override
	public void onBackPressed() {
		// if (isCamera) {
		// isCamera=false;
		// Intent intent = new Intent(FileActivity.this,
		// WifiStarActivity.class);
		// startActivity(intent);
		// }
		Browser browser = getCurrBrowser();

		// int curr = mViewPager.getCurrentItem() % views.size();
		// String curDir = mFileBrowsers.get(curr).currFolder;
		// if (!curDir.endsWith(Environment.getExternalStorageDirectory()
		// .toString() + "/")) {
		// isCamera = false;
		// }
		if (browser.mViewMode == ViewMode.SEARCHVIEW) {
			browser.toggleSearchMode(defaultSearch);
			browser.toggleViewMode();
		} else if (isCamera) {
			isCamera = false;
			// Intent intent = new Intent(FileActivity.this,
			// WifiStarActivity.class);
			// startActivity(intent);
			super.onBackPressed();
			finishActivity(1);
			this.finish();
		} else if (browser != null) {
			if (!browser.onBackPressed()) {
				super.onBackPressed();
				this.finish();
			}
		}
		// else {
		// Intent intent = new Intent(FileActivity.this,
		// WifiStarActivity.class);
		// startActivity(intent);
		// }

	}

	/*
	 * private void registerSDCardChangeReceiver(){ IntentFilter filter = new
	 * IntentFilter(); filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
	 * filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	 * filter.addDataScheme("file"); registerReceiver(mReceiver, filter); }
	 */
	PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tittles.get(position);
		}

		@Override
		public Object instantiateItem(View container, int position) {
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
				convertView = new TextView(FileActivity.this);
			}
			((TextView) convertView).setText(tittles.get(position));
			((TextView) convertView).setTextAppearance(FileActivity.this,
					R.style.tvPage_Title);
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

		/*
		 * public void tabchang() {
		 * 
		 * }
		 */
	};

	public void initializeSettings() {
		AppItemLoader.isfileactivity = 1;
		Log.i(TAG, "initializeSettings");
		tittles.clear();
		mFileBrowsers.clear();
		views.clear();
		if (Singleton.SMB_ONLINE)
			Singleton.initDiskInfo();
		disks = Singleton.instance().disks;

		if (disks.size() > 0) {

			for (int i = 0; i < disks.size(); i++) {
				wfDiskInfo disk = disks.get(i);
				if (disk.des.equals(".config")) {
					Log.d(TAG, "find .config file");
					continue;
				}
				tittles.add(disk.des);
			}

			String[] stockArr = new String[tittles.size()];
			stockArr = tittles.toArray(stockArr);
			mAnimTab.setAdapter(tabAdapter);
			mAnimTab.setOnTabChangeListener(this);
			for (int i = 0; i < tittles.size() - 1; i++) {
				String smbmode = mPreferences.getString(disks.get(i).path
						+ "file", ViewMode.LISTVIEW.toString());
				FileBrowser mFileBrowser = new FileBrowser(this,
						disks.get(i).path, this, ViewMode.toViewMode(smbmode));// isSmb
				mFileBrowser.mFileManager.setOnWhichoperation(this);
				mFileBrowsers.add(mFileBrowser);
				views.add(i, mFileBrowser.getView());

			}
			String sdpath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator;
			if (sdpath.startsWith("/mnt")
					|| (sdpath.startsWith("/storage") && !sdpath
							.startsWith("/storage/emulated")))
				sdpath = Environment.getExternalStorageDirectory().getParent()
						+ File.separator;
			String mode = mPreferences.getString(sdpath + "file",
					ViewMode.LISTVIEW.toString());
			FileBrowser fileBrowser = new FileBrowser(this, sdpath, this,
					ViewMode.toViewMode(mode));// isSmb
			fileBrowser.mFileManager.setOnWhichoperation(this);
			mFileBrowsers.add(fileBrowser);
			views.add(tittles.size() - 1, fileBrowser.getView());
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setCurrentItem(0);// 4 * 50
			mViewPager.setOnPageChangeListener(this);
		}
	}

	public void dispachBackKey() {
		Log.i(TAG, "dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "newConfig========>" + newConfig);
		super.onConfigurationChanged(newConfig);
	}

	public static FileBrowser getCurrBrowser() {
		int curr = mViewPager.getCurrentItem() % views.size();
		return mFileBrowsers.get(curr);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		Browser browser = getCurrBrowser();

		if (browser != null) {
			browser.onPrepareOptionsMenu(menu);
		}
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
			if (browser.onOptionsItemSelected(item)) {
				return true;
			}
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int curr = mViewPager.getCurrentItem() % views.size();
		mFileBrowsers.get(curr).onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		int curr = mViewPager.getCurrentItem() % views.size();
		mFileBrowsers.get(curr).onContextMenuClosed(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int curr = mViewPager.getCurrentItem() % views.size();
		return mFileBrowsers.get(curr).onContextItemSelected(item);
	}

	class UIHandler extends Handler {
		Context mContext;

		UIHandler(Context context) {
			mContext = context;
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	}

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
		if (views.size() > 1) {
			index = index % views.size();
			mAnimTab.moveTo(index);
			mFileBrowsers.get(index).onResume();
		}
	}

	@Override
	public void tabChange(int index) {
		// TODO Auto-generated method stub
		Browser browser = getCurrBrowser();
		browser.mViewMode = ViewMode.LISTVIEW;
		mFileBrowsers.get(index).StopQueryData();
		int curr = mViewPager.getCurrentItem();
		int realIndex = curr % views.size();
		int toIndex = curr + (index - realIndex);
		Log.i(TAG, "index:" + index + " curr:" + curr + " realIndex:"
				+ realIndex + " toIndex:" + toIndex);
		mViewPager.setCurrentItem(toIndex, false);
	}

	private UncaughtExceptionHandler mDefaultUEH;

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		mDefaultUEH.uncaughtException(thread, ex);
		// System.exit(0);

	}
}
