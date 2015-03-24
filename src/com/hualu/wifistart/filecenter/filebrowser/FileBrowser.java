package com.hualu.wifistart.filecenter.filebrowser;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jcifs.smb.SmbFile;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hualu.wifistart.FileActivity;
import com.hualu.wifistart.FileEnDecryptManager;
import com.hualu.wifistart.ListActivity;
import com.hualu.wifistart.R;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.PreparedResource;
import com.hualu.wifistart.filecenter.files.FileItem;
import com.hualu.wifistart.filecenter.files.FileItemForOperation;
import com.hualu.wifistart.filecenter.files.FileManager.FileComparatorMode;
import com.hualu.wifistart.filecenter.files.FileManager.FileFilter;
import com.hualu.wifistart.filecenter.files.FileManager.FilesFor;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.files.FileOperationThreadManager;
import com.hualu.wifistart.filecenter.files.FileOperationThreadManager.CopyOperation;
import com.hualu.wifistart.filecenter.files.FilePropertyAdapter;
import com.hualu.wifistart.filecenter.files.RefreshData;
import com.hualu.wifistart.filecenter.files.SmbFileOperationThreadManager;
import com.hualu.wifistart.filecenter.utils.CustomListener;
import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.music.MusicList;
import com.hualu.wifistart.views.QuickAction;
import com.hualu.wifistart.views.WPTextBox;
import com.hualu.wifistart.wifisetting.utils.LanguageCheck;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class FileBrowser extends Browser implements
		LoaderManager.LoaderCallbacks<List<FileItemForOperation>>,
		OnScrollListener {// implements OnTouchListener
	final boolean DEBUG = false;
	static {
		TAG = FileBrowser.class.getCanonicalName();
	}
	private final String SDCARD = Environment.getExternalStorageDirectory()
			.getPath();
	private final String MNT_SDCARD = "/mnt/sdcard/WifiShare";
	private final String KEY_PATH = "com.hualu.wifistart.filebrowser.path";

	private GridView mGridView;
	private ListView mListView;
	/* liuyufa 20131223 add for search start */
	private Button btnSearch2;
	/* liuyufa 20131223 add for search end */
	public File externalStorageDirectory = Environment
			.getExternalStorageDirectory();
	private HorizontalLayout filePathLayout;
	// RefreshData fileRefreshData; //wdx add 1216
	// private boolean cutFlag = true;
	// private String topDir;
	// private final static String displays[] = new
	// String[]{"图标显示","列表显示","按名称排序","按大小排序","按时间排序"};
	// lrh mdf 1218
	// private static String displays[] = new
	// String[]{"列表显示","按名称排序","按大小排序","按时间排序"};
	private static String displays[] = new String[] { "按名称排序", "按大小排序", "按时间排序" };
	// end
	/**
	 * 当前浏览的文件夹
	 */
	// public String currFolder =
	// externalStorageDirectory.getParentFile().getAbsolutePath();
	public String currFolder;// = externalStorageDirectory + "/WifiShare";
								// //zhaoyu
	public String searchFolder = null;
	// public String pastDir = null;
	// public FileBrowser pastBrowser;
	// Button btnHome,btnUp,btnNew,btnDisplay,btnPaste;
	// RelativeLayout rlHome,rlUp,rlNew,rlDisplay,rlPaste;

	private LinearLayout ivEmptyFolder;
	public FragmentActivity activity;
	IAppLoader mLoader;
	/* liuyufa change for dialog start */
	/*
	 * private ProgressBar pb;//zhaoyu 1205
	 */
	// private static Dialog mydialog;
	private static SearchDialog mydialog;
	private static int isshow = 0;
	/* liuyufa change for dialog end */
	// private CopyOperation operationType = CopyOperation.APPEND2;

	private HuzAlertDialog passwordDialog;
	private HuzAlertDialog passwordDialog2;
	private HuzAlertDialog alertDialog;
	String pw;
	public final int LOCK_START=400;
	public final int LOCK_INCREACE=500;
	public final int LOCK_END=600;

	List<Button> btns = new ArrayList<Button>();
	private final int MENU_FIRST = Menu.FIRST + 100;
	private final int MENU_COPY = MENU_FIRST;
	private final int MENU_CUT = MENU_FIRST + 1;
	private final int MENU_DELETE = MENU_FIRST + 2;
	private final int MENU_RENAME = MENU_FIRST + 3;
	private final int MENU_READPROP = MENU_FIRST + 4;
	private final int MENU_SELECT_ALL = MENU_FIRST + 5;
	private final int MENU_LOCK = MENU_FIRST + 6;
	private final int MENU_UNLOCK = MENU_FIRST + 12;
	private final int MENU_REFRESH = MENU_FIRST + 7;
	private final int MENU_OPEN_AS = MENU_FIRST + 9;
	private final int MENU_ADDTO_PLAYLIST = MENU_FIRST + 10;
	private final int MENU_NEW = MENU_FIRST + 11;

	// private final int MENU_PHOTO = MENU_FIRST + 12;
	// private final int MENU_EXIT = MENU_FIRST + 12;
	public PreparedResource getPreparedResource() {
		return preResource;
	}

	/**
	 * 在后台执行操作
	 */
	private boolean backgroundOperation = false;

	/*
	 * public FileBrowser(Context context) { super(context); nm =
	 * (NotificationManager) context
	 * .getSystemService(Context.NOTIFICATION_SERVICE); initView(); mViewMode =
	 * ViewMode.GRIDVIEW; Log.i(TAG,"QueryData " + currFolder); //QueryData(new
	 * File(currFolder)); QueryData( currFolder); }
	 */
	public FileBrowser(Context context, String path, FragmentActivity activity,
			ViewMode mode) {
		super(context);
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		this.activity = activity;
		// mViewMode = ViewMode.GRIDVIEW;
		mViewMode = mode;// ViewMode.LISTVIEW;
		initView();
		Log.i(TAG, "QueryData " + path);
		currFolder = path;
		// if(path.equals(Singleton.LOCAL_ROOT))
		// QueryData(new File(currFolder));
		// else
		topDir = path;
		QueryData(path);
	}

	/*
	 * private void QueryData(File preFile) { QueryData(preFile, true,
	 * FileFilter.ALL); }
	 */

	public void QueryData() {
		super.QueryData(currFolder, true, FileFilter.ALL);
		/* liuyufa change for dialog start */
		/*
		 * pb.setVisibility(View.VISIBLE);//zhaoyu 1205
		 */
		Searchdialog();
		isshow = 1;
		/* liuyufa change for dialog end */

		/*
		 * pb.setVisibility(View.VISIBLE); if(mViewMode == ViewMode.LISTVIEW)
		 * mListView.setAdapter(mItemsAdapter); else
		 * mGridView.setAdapter(mItemsAdapter);
		 */
	}

	public void QueryData(String path) {
		// wdx rewrite 1224
		if (mViewMode == ViewMode.SEARCHVIEW) {
			AppItemLoader.issearch2 = 1;
			StartQueryData(); // wdx add 1219
			Log.i(TAG, "" + searchFolder + " " + currFolder);
			searchFolder = currFolder;
			QueryData(searchFolder, false, FileFilter.SEARCH);
			mSearchAdapter.updatSearchData(mFileData); // zhaoyu 1205
			mSearchList.setAdapter(mSearchAdapter);
			AppItemLoader.issearch2 = 0;
		} else {
			QueryData(path, true, FileFilter.ALL);
		}
	}

	protected void QueryData(File preFile, boolean clear, FileFilter filter) {
		Log.i(TAG, "QueryData" + preFile.getAbsolutePath());
		super.QueryData(preFile, clear, filter);
		/* liuyufa delete for dialog start */
		/*
		 * pb.setVisibility(View.VISIBLE);//zhaoyu 1205
		 */
		Searchdialog();
		isshow = 1;
		/* liuyufa delete for dialog end */
		toggleViewMode();
		SetFilePath(preFile.getAbsolutePath().equals("/") ? "/" : preFile
				.getAbsolutePath() + "/");
		toggleBtnEnable(false);
		selectedAll = false;
		toggleOperatingView(false);
	}

	protected void QueryData(String path, boolean clear, FileFilter filter) {
		Log.i(TAG, "QueryData " + path + " " + filter);
		// super.QueryData(preFile, clear, filter);
		super.QueryData(path, clear, filter);
		/* liuyufa change for dialog start */
		/*
		 * pb.setVisibility(View.VISIBLE);//zhaoyu 1205
		 */
		// displaySearchDialog();
		Searchdialog();
		isshow = 1;
		/* liuyufa change for dialog end */
		if (filter != FileFilter.SEARCH && filter != FileFilter.ALLSEARCH)
			toggleViewMode();
		// SetFilePath(preFile.getAbsolutePath().equals("/") ? "/" : preFile
		// .getAbsolutePath() + "/");
		SetFilePath(path);
		toggleBtnEnable(false);
		selectedAll = false;
		toggleOperatingView(false);
	}

	public String getCurrFolder() {
		return currFolder;
	}

	/**
	 * switch viewmode
	 */
	/*
	 * public void toggleViewMode(){
	 * 
	 * switch (mViewMode) { case LISTVIEW:
	 * mItemsAdapter.setViewMode(ViewMode.LISTVIEW);
	 * mListView.setVisibility(View.VISIBLE);
	 * mListView.setAdapter(mItemsAdapter); mGridView.setVisibility(View.GONE);
	 * mGridView.setAdapter(null); break; case GRIDVIEW:
	 * mItemsAdapter.setViewMode(ViewMode.GRIDVIEW);
	 * mGridView.setVisibility(View.VISIBLE);
	 * mGridView.setAdapter(mItemsAdapter); mListView.setVisibility(View.GONE);
	 * mListView.setAdapter(null); break; default:
	 * 
	 * break; } }
	 */
	// wdx add 1211
	public void onBtnCopy() {
		if (backgroundOperation) {
			ViewEffect.showToast(mContext, R.string.toast_please_waite);
			return;
		}
		mFileManager.resetDataForOperation();
		// mFileManager.addFileItem(fileItemForOperation);
		addSelectedItemToApp(FilesFor.COPY);
		// test
		List<FileItemForOperation> list = mFileManager.getDataForOperation()
				.getFileItems();

		for (int i = 0; i < list.size(); i++)
			Log.i(TAG, "choose copy file "
					+ list.get(i).getFileItem().getFilePath());
		//
		mFileManager.setFilesFor(FilesFor.COPY);
		toggleOperatingView(false);
		/*
		 * if (backgroundOperation) { ViewEffect.showToast(mContext,
		 * R.string.toast_please_waite); return; } final FileItemForOperation
		 * fileItemForOperation ;//= mData.getFileItems().get(selectedPosition);
		 * if(mViewMode == ViewMode.SEARCHVIEW){ fileItemForOperation =
		 * mSearchAdapter.mSearchData.getFileItems().get(selectedPosition);
		 * }else fileItemForOperation =
		 * mData.getFileItems().get(selectedPosition);
		 * mFileManager.resetDataForOperation();
		 * //mFileManager.addFileItem(fileItemForOperation);
		 * addSelectedItemToApp(FilesFor.COPY); //test
		 * List<FileItemForOperation> list =
		 * mFileManager.getDataForOperation().getFileItems();
		 * 
		 * for(int i = 0;i<list.size();i++ ) Log.i(TAG,"choose copy file " +
		 * list.get(i).getFileItem().getFilePath()); //
		 * mFileManager.setFilesFor(FilesFor.COPY); toggleOperatingView(false);
		 */
	}

	public void onResume() {
		if (mViewMode == ViewMode.SEARCHVIEW) {
			if (!mSearchBox.getText().equals(""))
				mLoader.updateList(mSearchBox.getText().toString(),
						mSearchAdapter.mdata);
			else {
				mSearchAdapter.updatSearchData(mFileData);// zhaoyu 1205
				mSearchAdapter.notifyDataSetChanged();
			}
		} else {
			nm.cancelAll();
			// zhaoyu 1217
			InputMethodManager imm = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
			mFileManager.resetDataForOperation();
			// String pastDir = null;
			QueryData(currFolder);
		}
	}

	public void onDestroy() {
		preResource.recycle();
		mFileManager.resetDataForOperation();
	}

	public void onPause() {

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		Log.i(TAG, "initView");
		mView = (View) mInflater.inflate(R.layout.file_browser, null);
		mGridView = (GridView) mView.findViewById(R.id.filesGridView);
		mListView = (ListView) mView.findViewById(R.id.filesListView);
		/* liuyufa delete for dialog start */
		// pb = (ProgressBar)mView.findViewById(R.id.pbar);//zhaoyu 1205
		/* liuyufa delete for dialog end */

		/* liuyufa 20131223 add for search start */
		btnSearch2 = (Button) mView.findViewById(R.id.btnSearch2);
		btnSearch2.setOnClickListener(new btnClickListener());
		/* liuyufa 20131223 add for search end */

		/*
		 * btnHome = (Button)mView.findViewById(R.id.btnHome); rlHome =
		 * (RelativeLayout)mView.findViewById(R.id.rlHome);
		 * btnHome.setOnClickListener(this); btnHome.setOnTouchListener(this);
		 * 
		 * btnUp = (Button)mView.findViewById(R.id.btnUp); rlUp =
		 * (RelativeLayout)mView.findViewById(R.id.rlUp);
		 * btnUp.setOnClickListener(this); btnUp.setOnTouchListener(this);
		 * 
		 * btnNew = (Button)mView.findViewById(R.id.btnNew); rlNew =
		 * (RelativeLayout)mView.findViewById(R.id.rlNew);
		 * btnNew.setOnClickListener(this); btnNew.setOnTouchListener(this);
		 * 
		 * btnDisplay = (Button)mView.findViewById(R.id.btnDisplay); rlDisplay =
		 * (RelativeLayout)mView.findViewById(R.id.rlDisplay);
		 * btnDisplay.setOnClickListener(this);
		 * btnDisplay.setOnTouchListener(this);
		 * 
		 * btnPaste = (Button)mView.findViewById(R.id.btnPaste); rlPaste =
		 * (RelativeLayout)mView.findViewById(R.id.rlPaste);
		 * btnPaste.setOnClickListener(this); btnPaste.setOnTouchListener(this);
		 */

		ivEmptyFolder = (LinearLayout) mView.findViewById(R.id.empty_folder);

		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this); // wdx 20140212 add
		((Activity) mContext).registerForContextMenu(mListView);
		mGridView.setOnItemClickListener(this);
		((Activity) mContext).registerForContextMenu(mGridView);

		filePathLayout = (HorizontalLayout) mView
				.findViewById(R.id.filePathLayout);

		mSearchView = (LinearLayout) mView.findViewById(R.id.search);
		mSearchBox = (WPTextBox) mView.findViewById(R.id.searchbox);
		mSearchMode = SearchMode.NORMALVIEW;
		mSearchView.setVisibility(View.GONE);

		mSearchList = (ListView) mView.findViewById(R.id.lvSearchList);
		mSearchList.setOnItemClickListener(this);
		mSearchList.setOnScrollListener(this); // wdx 20140212 add
		mSearchList.setVisibility(View.GONE);

		((Activity) mContext).registerForContextMenu(mSearchList);
		/*
		 * filePathLayout.setOnItemClickListener(new OnTVItemClickListener() {
		 * 
		 * @Override public void onItemClick(TextView v) { willExit = false;
		 * currFolder = filePathLayout.GetPathByTv(v);
		 * //QueryData((currFolder)); } });
		 */
		setViewWidth();

		mSearchBox
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView paramTextView,
							int paramInt, KeyEvent paramKeyEvent) {
						if ((paramKeyEvent != null)
								&& (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER))
							((InputMethodManager) mContext
									.getSystemService("input_method"))
									.hideSoftInputFromWindow(mSearchBox
											.getApplicationWindowToken(), 2);
						return false;
					}
				});
		activity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.mSearchBox.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable paramEditable) {
			}

			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
			}

			public void onTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
				mLoader.updateList(paramCharSequence.toString(),
						mSearchAdapter.mdata);// mSearchAdapter
			}
		});

		Loader<?> loader = ((FragmentActivity) activity)
				.getSupportLoaderManager().initLoader(0, null, this);
		loader.forceLoad();
		mLoader = ((IAppLoader) loader);

	}

	/**
	 * 计算并设置按钮的宽度
	 */
	private void setViewWidth() {

		mGridView.setNumColumns(FileActivity.mScreenWidth / 160);
	}

	/**
	 * 切换浏览模式
	 */
	public void toggleViewMode() {
		Log.i(TAG, "" + mViewMode);
		if (mViewMode == null) {
			mViewMode = ViewMode.LISTVIEW;
		}
		switch (mViewMode) {
		case LISTVIEW:
			mItemsAdapter.setViewMode(ViewMode.LISTVIEW);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(mItemsAdapter);
			mGridView.setVisibility(View.GONE);
			mGridView.setAdapter(null);
			mSearchList.setVisibility(View.GONE);
			mSearchView.setVisibility(View.GONE);
			break;
		case GRIDVIEW:
			mItemsAdapter.setViewMode(ViewMode.GRIDVIEW);
			mGridView.setVisibility(View.VISIBLE);
			mGridView.setAdapter(mItemsAdapter);
			mListView.setVisibility(View.GONE);
			mListView.setAdapter(null);
			mSearchList.setVisibility(View.GONE);
			mSearchView.setVisibility(View.GONE);
			break;
		case SEARCHVIEW:
			mItemsAdapter.setViewMode(ViewMode.SEARCHVIEW);
			mGridView.setVisibility(View.GONE);
			mGridView.setAdapter(null);
			mListView.setVisibility(View.GONE);
			mListView.setAdapter(null);
			mSearchView.setVisibility(View.VISIBLE);
			mSearchList.setVisibility(View.VISIBLE);
			if (0 == AppItemLoader.issearch2) {
				break;
			}
			StartQueryData();
			Log.i(TAG, "" + searchFolder + " " + currFolder);
			searchFolder = currFolder;
			QueryData(searchFolder, false, FileFilter.SEARCH);
			mSearchAdapter.updatSearchData(mFileData);
			mSearchList.setAdapter(mSearchAdapter);
		default:
			break;
		}
	}

	private void toggleBtnEnable(boolean finished) {
	}

	/**
	 * 更新地址栏显示的路径
	 */
	private void SetFilePath(String currFolder) {
		filePathLayout.SetFilePath(currFolder);
	}

	/**
	 * 当前目录是否可操作（粘贴，新建文件夹，重命名，删除）
	 * 
	 * @param justBrowser
	 *            是否只是进入当前目录
	 * @return
	 */
	public boolean currFolderCanOperate(boolean justBrowser) {
		if (!mFileManager.getSdcardState().equals(Environment.MEDIA_MOUNTED)) {
			if ((currFolder.equals(SDCARD) || currFolder.equals(MNT_SDCARD))
					&& justBrowser) {
				return false;
			} else if ((currFolder.startsWith(SDCARD) || currFolder
					.startsWith(MNT_SDCARD)) && !justBrowser) {
				return false;
			}
		}
		return true;
	}

	private boolean isOperating;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileItemForOperation fileItem;// = mData.getFileItems().get(position);
		itemClickPosition = position;
		if (mViewMode == ViewMode.SEARCHVIEW) {
			fileItem = mSearchAdapter.mSearchData.getFileItems().get(position);
		} else
			fileItem = mData.getFileItems().get(position);

		if (!isOperating) {
			if (!fileItem.getFileItem().isDirectory()) {
				clickFileItem(fileItem);
				return;
			}
			if (!mData.getFileItems().get(position).getFileItem()
				.getFileName().contains("lockfile")) {
				
				currFolder = fileItem.getFileItem().getFilePath();
				currFolderCanOperate(true);
				// QueryData(new File(currFolder));
				// zhaoyu 1217
				if (mViewMode == ViewMode.SEARCHVIEW) {
					searchFolder = currFolder;
					QueryData(searchFolder, true, FileFilter.ALLSEARCH);
				} else
					QueryData((currFolder));
			}else {
				//解密
				final int selectposition=position;
				passwordDialog = ViewEffect.createRenameDialog(mContext,
						R.string.set_pwd_input, "", new CustomListener() {

							@Override
							public void onListener() {
								passwordDialog.dismiss();
								// 密码
								EditText et = (EditText) passwordDialog
										.findViewById(R.id.rename);
								String passwordString = et.getText().toString();
								SharedPreferences sh = mContext
										.getSharedPreferences("lockedfile", 0);
								String pString = sh.getString(mData.getFileItems().get(selectposition)
										.getFileItem().getFilePath(), null);
								Log.i(mData.getFileItems().get(selectposition)
										.getFileItem().getFilePath(), pString+"==");
								if (pString != null
										&& !pString.equals(passwordString)) {
									alertDialog = ViewEffect.createComfirDialog(
											mContext, R.string.menu_read_unlock,
											R.string.menu_read_passwordfail,
											new CustomListener() {

												@Override
												public void onListener() {
													alertDialog.dismiss();
													passwordDialog.show();
												}
											}, null);
									alertDialog.show();
									return;
								}
								//这里写解密逻辑
								
//								FileEnDecryptManager fed = FileEnDecryptManager
//										.getInstance();
//								boolean flag = fed.InitEncrypt(fileItemForOperation
//										.getFileItem().getFilePath(),
//										passwordString, "txt");
								if (true) {
									alertDialog = ViewEffect.createComfirDialog(
											mContext, R.string.menu_read_unlock,
											R.string.menu_read_unlocksucess,
											new CustomListener() {

												@Override
												public void onListener() {
													alertDialog.dismiss();
												}
											}, null);
									alertDialog.show();
									Editor editor = sh.edit();
									editor.remove(mData.getFileItems().get(selectposition)
											.getFileItem().getFilePath());
									editor.commit();
									File file = new File(mData.getFileItems().get(selectposition)
											.getFileItem().getFilePath());
									if (file.canWrite()) {
										String filename = mData.getFileItems().get(selectposition)
												.getFileItem().getFileName();

										String[] pathAndName = Helper.reName(
												mData.getFileItems().get(selectposition).getFileItem(),
												filename.substring(
														0,
														filename.length()
																- 10
																));
										String newPath = pathAndName[0];
										File tmp = new File(newPath);
										if (tmp.exists()) {
											return;
										}
										file.renameTo(new File(newPath));
										mData.getFileItems().get(selectposition)
												.getFileItem()
												.setFileName(
														filename.substring(
																0,
																filename.length()
																		- 10
																		));
										mData.getFileItems().get(selectposition).getFileItem()
												.setFilePath(newPath);
									}
									
									QueryData(currFolder);
								} else {
									alertDialog = ViewEffect.createComfirDialog(
											mContext, R.string.menu_read_unlock,
											R.string.menu_read_unlockfail,
											new CustomListener() {

												@Override
												public void onListener() {
													alertDialog.dismiss();
												}
											}, null);
									alertDialog.show();
								}

							}

						}, new CustomListener() {

							@Override
							public void onListener() {
								passwordDialog.dismiss();
							}
						});
				passwordDialog.show();
				
			}
		}/*
		 * else { int selState = fileItem.getSelectState(); if (selState == 0) {
		 * fileItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL); }
		 * else if (selState == 1) {
		 * fileItem.setSelectState(FileItemForOperation.SELECT_STATE_NOR); }
		 * refreshData(); }
		 */
	}

	/**
	 * 将选中的Item保存起来
	 */
	private void addSelectedItemToApp(FilesFor filesFor) {
		// wdx rewrite 1224
		if (mViewMode == ViewMode.SEARCHVIEW) {
			for (FileItemForOperation operationFile : mFileData.getFileItems()) {
				if (operationFile.getSelectState() == FileItemForOperation.SELECT_STATE_SEL) {
					mFileManager.addFileItem(operationFile);
					if (filesFor == FilesFor.CUT) {
						operationFile
								.setSelectState(FileItemForOperation.SELECT_STATE_CUT);
						refreshData();
					}
				}
			}
		} else {
			for (FileItemForOperation operationFile : mData.getFileItems()) {
				if (operationFile.getSelectState() == FileItemForOperation.SELECT_STATE_SEL) {
					mFileManager.addFileItem(operationFile);
					if (filesFor == FilesFor.CUT) {
						operationFile
								.setSelectState(FileItemForOperation.SELECT_STATE_CUT);
						refreshData();
					}
				}
			}
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (DEBUG)
			Log.i(TAG, "newConfig========>" + newConfig);
		setViewWidth();
		switch (newConfig.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			if (mViewMode == ViewMode.LISTVIEW) {
				mViewMode = ViewMode.GRIDVIEW;
				toggleViewMode();
			}
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			if (mViewMode == ViewMode.GRIDVIEW) {
				mViewMode = ViewMode.LISTVIEW;
				toggleViewMode();
			}
			break;
		default:
			break;
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// menu.add(1, MENU_REFRESH, Menu.NONE,
		// R.string.menu_refresh);//.setIcon(R.drawable.ic_menu_refresh);
		// //menu.add(1, MENU_EXIT, Menu.NONE, R.string.menu_exit);
		// menu.add(1, MENU_NEW, Menu.NONE, R.string.title_newFolder);
		// menu.add(1, MENU_SELECT_ALL, Menu.NONE,
		// R.string.menu_select_all);//.setIcon(R.drawable.ic_menu_refresh);
		// menu.add(1, MENU_COPY, Menu.NONE, R.string.menu_copy_selected);
		// menu.add(1, MENU_CUT, Menu.NONE, R.string.menu_cut_selected);
		// menu.add(1, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
		// //menu.add(1, MENU_PHOTO, Menu.NONE, R.string.menu_photo);
		// // menu.add(1, MENU_HELP, Menu.NONE,
		// // R.string.menu_help).setIcon(android.R.drawable.ic_menu_help);
		return true;
	}

	public boolean onClickPopMenu(int item) {
		switch (item) {
		case MENU_NEW:
			onBtnNew();
			break;
		case MENU_SELECT_ALL:
			addSelectedItemToApp(FilesFor.COPY);
			if (!selectedAll) {
				SelectAll();
			} else {
				SelectNothing();
			}
			toggleOperatingView(selectedAll);
			break;
		case MENU_CUT:
			if (backgroundOperation) {
				ViewEffect.showToast(mContext, R.string.toast_please_waite);
				break;
			}
			mFileManager.resetDataForOperation();
			addSelectedItemToApp(FilesFor.CUT);
			mFileManager.setFilesFor(FilesFor.CUT);
			pastDir = currFolder;
			toggleOperatingView(false);
			break;
		case MENU_DELETE:
			if (backgroundOperation) {
				ViewEffect.showToast(mContext, R.string.toast_please_waite);
				break;
			}
			if (!currFolderCanOperate(false))
				return false;
			comfirDialog = ViewEffect.createComfirDialog(mContext,
					R.string.title_comfir_delete,
					R.string.dialog_msg_comfir_delete, new CustomListener() {
						@Override
						public void onListener() {
							mFileManager.resetDataForOperation();
							// mFileManager.addFileItem(fileItemForOperation);
							addSelectedItemToApp(FilesFor.DELETE);
							mFileManager.setFilesFor(FilesFor.DELETE);
							List<FileItemForOperation> list = mFileManager
									.getDataForOperation().getFileItems();
							boolean deletflag=false;
							for(int i=0;i<list.size();i++){
								if (list.get(i).getFileItem().getFileName().contains("lockfile")) {
									list.remove(i);
									i--;
									deletflag=true;
								}
							}
							if (deletflag) {
								ToastBuild.toast(mContext, R.string.toast_lockfile_undelete);
							}
							if (list.size()==0) {
								QueryData(currFolder);
								return;
							}
							showOperationProgressDialog(
									R.string.title_deleting, list.size(), true);
							if (currFolder.startsWith("smb")) {
								SmbFileOperationThreadManager manager = new SmbFileOperationThreadManager(
										list, mHandler);
								manager.beginDelete();
							} else {
								FileOperationThreadManager manager = new FileOperationThreadManager(
										list, mHandler);
								manager.beginDelete();
							}

						}
					}, new CustomListener() {
						@Override
						public void onListener() {
							comfirDialog.dismiss();
						}
					});
			RefreshData.mediaFileItemstable.clear();
			RefreshData.mlocalFileItemstable.clear();
			comfirDialog.show();
			break;
		case MENU_REFRESH:
			searchFolder = null;
			QueryData(currFolder);
			return true;
		default:
			break;
		}
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// /*case MENU_EXIT:
		// Intent intent = new Intent(mContext, MusicService.class);
		// mContext.stopService(intent);
		// SysApplication.getInstance().exit();
		// break;*/
		// case MENU_NEW:
		// onBtnNew();
		// break;
		// case MENU_SELECT_ALL:
		// addSelectedItemToApp(FilesFor.COPY);
		// if (!selectedAll) {
		// SelectAll();
		// } else {
		// SelectNothing();
		// }
		// toggleOperatingView(selectedAll);
		// break;
		// case MENU_COPY:
		// if (backgroundOperation) {
		// ViewEffect.showToast(mContext, R.string.toast_please_waite);
		// break;
		// }
		// mFileManager.resetDataForOperation();
		// //mFileManager.addFileItem(fileItemForOperation);
		// addSelectedItemToApp(FilesFor.COPY);
		// //test
		// List<FileItemForOperation> list =
		// mFileManager.getDataForOperation().getFileItems();
		//
		// for(int i = 0;i<list.size();i++ )
		// Log.i(TAG,"choose copy file " +
		// list.get(i).getFileItem().getFilePath());
		// //
		// mFileManager.setFilesFor(FilesFor.COPY);
		// toggleOperatingView(false);
		// break;
		// case MENU_CUT:
		// if (backgroundOperation) {
		// ViewEffect.showToast(mContext, R.string.toast_please_waite);
		// break;
		// }
		// mFileManager.resetDataForOperation();
		// //mFileManager.addFileItem(fileItemForOperation);
		// addSelectedItemToApp(FilesFor.CUT);
		// mFileManager.setFilesFor(FilesFor.CUT);
		// pastDir = currFolder;
		// toggleOperatingView(false);
		// break;
		// case MENU_DELETE:
		// if (backgroundOperation) {
		// ViewEffect.showToast(mContext, R.string.toast_please_waite);
		// break;
		// }
		// //addSelectedItemToApp(FilesFor.COPY);
		// if (!currFolderCanOperate(false))
		// return false;
		// comfirDialog = ViewEffect.createComfirDialog(mContext,
		// R.string.title_comfir_delete,
		// R.string.dialog_msg_comfir_delete, new CustomListener() {
		// @Override
		// public void onListener() {
		// mFileManager.resetDataForOperation();
		// //mFileManager.addFileItem(fileItemForOperation);
		// addSelectedItemToApp(FilesFor.DELETE);
		// mFileManager.setFilesFor(FilesFor.DELETE);
		// List<FileItemForOperation> list = mFileManager
		// .getDataForOperation().getFileItems();
		// showOperationProgressDialog(
		// R.string.title_deleting, list.size(), true);
		// if(currFolder.startsWith("smb")){
		// SmbFileOperationThreadManager manager = new
		// SmbFileOperationThreadManager(
		// list, mHandler);
		// manager.beginDelete();
		// }
		// else{
		// FileOperationThreadManager manager = new FileOperationThreadManager(
		// list, mHandler);
		// manager.beginDelete();
		// }
		//
		// }
		// }, new CustomListener() {
		// @Override
		// public void onListener() {
		// comfirDialog.dismiss();
		// }
		// });
		// //zhaoyu delete 1217
		// RefreshData.mediaFileItemstable.clear();
		// RefreshData.mlocalFileItemstable.clear();
		// comfirDialog.show();
		// break;
		// case MENU_REFRESH:
		// //QueryData(new File(currFolder));
		// //zhaoyu add 1217
		// searchFolder = null;
		// QueryData(currFolder);
		// return true;
		// /*case MENU_PHOTO:
		// Intent intent = new Intent(mContext, CameraActivity.class);
		// intent.putExtra("currFolder",currFolder);
		// ((Activity)mContext).startActivityForResult(intent,FileActivity.ACTIVITY_RESULT_CAPTUREPICTURE);
		// break;*/
		// default:
		// break;
		// }
		return false;
	}

	/**
	 * 长按选择的数据的位置
	 */
	private int selectedPosition;

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		selectedPosition = info.position;

		// FileItemForOperation operationItem =
		// mData.getFileItems().get(selectedPosition);
		FileItemForOperation operationItem;
		if (mViewMode == ViewMode.SEARCHVIEW) {
			operationItem = mSearchAdapter.mSearchData.getFileItems().get(
					selectedPosition);
		} else
			operationItem = mData.getFileItems().get(selectedPosition);
		/* liuyufa delete for menu 20140116 start */
		/*
		 * menu.setHeaderTitle(R.string.title_menutitle);
		 * menu.setHeaderIcon(R.drawable.toolbar_operation);
		 */
		/* liuyufa delete for menu 20140116 end */
		/*
		 * if (selectedAll) menu.add(0, MENU_SELECT_ALL, Menu.NONE,
		 * R.string.menu_unselect_all); else menu.add(0, MENU_SELECT_ALL,
		 * Menu.NONE, R.string.menu_select_all);
		 */
		/*
		 * if(operationItem.getFileItem().isDirectory()){ menu.add(0,
		 * MENU_OPEN_AS, Menu.NONE, R.string.menu_open); }else{ SubMenu subMenu
		 * = menu.addSubMenu(1, MENU_OPEN_AS, Menu.NONE, R.string.menu_open_as);
		 * subMenu.add(1, SUB_MENU_TXT, Menu.NONE, R.string.sub_menu_txt);
		 * subMenu.add(1, SUB_MENU_AUDIO, Menu.NONE, R.string.sub_menu_audio);
		 * subMenu.add(1, SUB_MENU_VIDEO, Menu.NONE, R.string.sub_menu_video);
		 * subMenu.add(1, SUB_MENU_PIC, Menu.NONE, R.string.sub_menu_pic); }
		 */
		// menu.add(0, MENU_OPEN_AS, Menu.NONE, R.string.menu_open);
		// xuw updata 2014 12 18
		if (!mData.getFileItems().get(selectedPosition).getFileItem()
				.getFileName().contains("lockfile")) {
			menu.add(0, MENU_COPY, Menu.NONE, R.string.menu_copy_selected);
			menu.add(0, MENU_CUT, Menu.NONE, R.string.menu_cut_selected);
			menu.add(0, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
			menu.add(0, MENU_RENAME, Menu.NONE, R.string.menu_rename);
		}else {
				menu.add(0, MENU_UNLOCK, Menu.NONE, R.string.menu_read_unlock);
		}
		menu.add(0, MENU_READPROP, Menu.NONE, R.string.menu_read_prop);
		// menu.add(0, MENU_READPROP, Menu.NONE, R.string.menu_read_prop);
		String filetype = operationItem.getFileItem().getExtraName()
				.toLowerCase(Locale.getDefault());

		// xuw updata2014/10/09 去除是否显示属性的判断
		// if(!topDir.startsWith("smb")&&(preResource.isSupportAudioFile(filetype)))

		// lrh add 1219
		// menu.add(0, MENU_ADDTO_PLAYLIST, Menu.NONE, R.string.addPlayList);
		// end

		// xuw add 2014/12/05 for lockfile
		
//		if (!mData.getFileItems().get(selectedPosition).getFileItem()
//				.getFilePath().startsWith("smb")) {
			
//			if (!mData.getFileItems().get(selectedPosition).getFileItem()
//					.getFileName().contains("lockfile")) {
////				menu.add(0, MENU_LOCK, Menu.NONE, R.string.menu_read_lock);
//			} else {
//				menu.add(0, MENU_UNLOCK, Menu.NONE, R.string.menu_read_unlock);
//			}
//		}

		if (isOperating)
			operationItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
		else {
			SelectNothing();
			operationItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
		}
		refreshData();
	}

	public void onContextMenuClosed(Menu menu) {
		if (!hasContextItemSelected) {
			// wdx rewrite 1224
			FileItemForOperation fileItemForOperation = new FileItemForOperation();
			if (mViewMode == ViewMode.SEARCHVIEW)
				fileItemForOperation = mFileData.getFileItems().get(
						selectedPosition);
			else
				fileItemForOperation = mData.getFileItems().get(
						selectedPosition);
			fileItemForOperation
					.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
			refreshData();
		}
		hasContextItemSelected = false;
	}

	boolean hasContextItemSelected = false;

	public boolean onContextItemSelected(MenuItem item) {

		hasContextItemSelected = true;
		// final FileItemForOperation fileItemForOperation =
		// mData.getFileItems()
		// .get(selectedPosition);
		final FileItemForOperation fileItemForOperation;// =
														// mData.getFileItems().get(selectedPosition);
		if (mViewMode == ViewMode.SEARCHVIEW) {
			fileItemForOperation = mSearchAdapter.mSearchData.getFileItems()
					.get(selectedPosition);
		} else
			fileItemForOperation = mData.getFileItems().get(selectedPosition);
		switch (item.getItemId()) {
		/*
		 * case SUB_MENU_TXT : case SUB_MENU_PIC: case SUB_MENU_AUDIO: case
		 * SUB_MENU_VIDEO: openAs(item.getItemId(),
		 * fileItemForOperation.getFileItem()); break;
		 */
		case MENU_ADDTO_PLAYLIST:
			FileItem file = fileItemForOperation.getFileItem();
			String fileName = file.getFileName();
			// String fileName =
			// file.getName().substring(0,file.getName().indexOf("."));
			Log.i(TAG, "add to playlist " + fileName);
			// if (MusicList.query(mContext,fileName))
			{
				MusicList.insertMusic(mContext, file);// 添加音乐
			}
			break;
		case MENU_OPEN_AS:
			if (fileItemForOperation.getFileItem().isDirectory()) {
				currFolder = fileItemForOperation.getFileItem().getFilePath();
				currFolderCanOperate(true);
				// QueryData(new File(currFolder));
				QueryData((currFolder));
			} else {
				openAsDialog(fileItemForOperation.getFileItem()).show();
			}
			break;
		case MENU_SELECT_ALL:
			if (!selectedAll) {
				SelectAll();
			} else {
				SelectNothing();
			}
			toggleOperatingView(selectedAll);
			break;
		case MENU_COPY:
			if (backgroundOperation) {
				// ViewEffect.showToast(mContext, R.string.toast_please_waite);
				break;
			}
			mFileManager.resetDataForOperation();
			mFileManager.addFileItem(fileItemForOperation);
			// addSelectedItemToApp(FilesFor.COPY);
			// test
			List<FileItemForOperation> list = mFileManager
					.getDataForOperation().getFileItems();

			for (int i = 0; i < list.size(); i++)
				Log.i(TAG, "choose copy file "
						+ list.get(i).getFileItem().getFilePath());
			//
			mFileManager.setFilesFor(FilesFor.COPY);
			toggleOperatingView(false);
			break;
		case MENU_CUT:
			if (backgroundOperation) {
				// ViewEffect.showToast(mContext, R.string.toast_please_waite);
				break;
			}
			mFileManager.resetDataForOperation();
			mFileManager.addFileItem(fileItemForOperation);
			// addSelectedItemToApp(FilesFor.CUT);
			mFileManager.setFilesFor(FilesFor.CUT);
			toggleOperatingView(false);
			// cutFlag = true;
			break;
		case MENU_DELETE:
			if (backgroundOperation) {
				// ViewEffect.showToast(mContext, R.string.toast_please_waite);
				break;
			}
			if (!currFolderCanOperate(false))
				return false;
			comfirDialog = ViewEffect.createComfirDialog(mContext,
					R.string.title_comfir_delete,
					R.string.dialog_msg_comfir_delete, new CustomListener() {
						@Override
						public void onListener() {
							mFileManager.resetDataForOperation();
							mFileManager.addFileItem(fileItemForOperation);
							addSelectedItemToApp(FilesFor.DELETE);
							mFileManager.setFilesFor(FilesFor.DELETE);
							List<FileItemForOperation> list = mFileManager
									.getDataForOperation().getFileItems();
							showOperationProgressDialog(
									R.string.title_deleting, list.size(), true);
							if (currFolder.startsWith("smb")) {
								SmbFileOperationThreadManager manager = new SmbFileOperationThreadManager(
										list, mHandler);
								manager.beginDelete();
							} else {
								FileOperationThreadManager manager = new FileOperationThreadManager(
										list, mHandler);
								manager.beginDelete();
							}
						}
					}, new CustomListener() {
						@Override
						public void onListener() {
							comfirDialog.dismiss();
						}
					});
			comfirDialog.show();
			break;
		case MENU_RENAME:
			if (!currFolderCanOperate(false))
				return false;
			FileItem fileItem = fileItemForOperation.getFileItem();
			renameDialog = ViewEffect.createRenameDialog(mContext,
					R.string.title_rename, fileItem.getFileName(),
					new CustomListener() {
						@Override
						public void onListener() {
							EditText et = (EditText) renameDialog
									.findViewById(R.id.rename);
							String newName = et.getText().toString();
							if (currFolder.startsWith("smb")) {
								SmbFileOperationThreadManager manager = new SmbFileOperationThreadManager(
										fileItemForOperation, mHandler);
								try {
									manager.rename(newName);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								FileOperationThreadManager manager = new FileOperationThreadManager(
										fileItemForOperation, mHandler);
								manager.rename(newName);
							}
						}
					}, new CustomListener() {
						@Override
						public void onListener() {
							renameDialog.dismiss();
						}
					});

			renameDialog.show();
			break;
		case MENU_READPROP:
			FilePropertyAdapter adapter = null;
			if (currFolder.startsWith("smb")) {
				SmbFileOperationThreadManager manager = new SmbFileOperationThreadManager();
				try {
					adapter = manager.readProp(mContext, fileItemForOperation);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				FileOperationThreadManager manager = new FileOperationThreadManager();
				try {
					adapter = manager.readProp(mContext, fileItemForOperation);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// FilePropertyAdapter adapter =
			// manager.readProp(mContext,fileItemForOperation);
			HuzAlertDialog propertyDialog = ViewEffect.createPropertyDialog(
					mContext, R.string.title_read_property, adapter);
			propertyDialog.show();
			break;

		case MENU_LOCK:
			if (mData.getFileItems().get(selectedPosition).getFileItem().getFilePath().startsWith("smb")) {
				alertDialog = ViewEffect.createComfirDialog(mContext,
						R.string.menu_read_lock,
						R.string.menu_read_smblock, new CustomListener() {

							@Override
							public void onListener() {
								alertDialog.dismiss();
							}
						}, null);
				alertDialog.show();
				break;
			}
			SharedPreferences sh = mContext.getSharedPreferences("lockedfile",
					0);
			String prepassword = sh.getString("rememberpassword", null);
			passwordDialog = ViewEffect.createPassWordDialog(mContext,
					R.string.set_pwd_input, prepassword, new CustomListener() {

						@Override
						public void onListener() {
							passwordDialog.dismiss();
							// 密码
							EditText et1 = (EditText) passwordDialog
									.findViewById(R.id.inputpassword1);
							String passwordString1 = et1.getText().toString();
							EditText et2 = (EditText) passwordDialog
									.findViewById(R.id.inputpassword2);
							String passwordString2 = et2.getText().toString();
							if ("".equals(passwordString1)
									|| "".equals(passwordString2)) {
								alertDialog = ViewEffect.createComfirDialog(
										mContext, R.string.menu_read_lock,
										R.string.set_password_empty,
										new CustomListener() {

											@Override
											public void onListener() {
												alertDialog.dismiss();
											}
										}, null);
								alertDialog.show();
								return;
							}
							if (passwordString1.length()<6) {
								alertDialog = ViewEffect.createComfirDialog(
										mContext, R.string.menu_read_lock,
										R.string.set_pwd_input,
										new CustomListener() {
											
											@Override
											public void onListener() {
												alertDialog.dismiss();
											}
										}, null);
								alertDialog.show();
								return;
							}
							if (!passwordString1.equals(passwordString2)) {
								alertDialog = ViewEffect.createComfirDialog(
										mContext, R.string.menu_read_lock,
										R.string.menu_read_passfail,
										new CustomListener() {

											@Override
											public void onListener() {
												alertDialog.dismiss();
											}
										}, null);
								alertDialog.show();
								return;
							} else {
								FileEnDecryptManager fed = FileEnDecryptManager
										.getInstance();
								boolean flag = fed.InitEncrypt(
										fileItemForOperation.getFileItem()
												.getFilePath(),
										passwordString2, "file",null);
								if (flag) {
									alertDialog = ViewEffect
											.createComfirDialog(
													mContext,
													R.string.menu_read_lock,
													R.string.menu_read_locksucess,
													new CustomListener() {

														@Override
														public void onListener() {
															alertDialog
																	.dismiss();
														}
													}, null);
									alertDialog.show();
									File file = new File(fileItemForOperation
											.getFileItem().getFilePath());
									if (file.canWrite()) {
										String[] pathAndName = Helper.reName(
												fileItemForOperation
														.getFileItem(),
												fileItemForOperation
														.getFileItem()
														.getFileName()
														+ ".lockfile."
														+ fileItemForOperation
																.getFileItem()
																.getExtraName());
										String newPath = pathAndName[0];
										File tmp = new File(newPath);
										if (tmp.exists()) {
											return;
										}
										file.renameTo(new File(newPath));
										SharedPreferences sh = mContext
												.getSharedPreferences("lockedfile",
														0);
										Editor editor = sh.edit();
										editor.putString(tmp.getName(),
												passwordString2);
										editor.commit();
										fileItemForOperation
												.getFileItem()
												.setFileName(
														fileItemForOperation
																.getFileItem()
																.getFileName()
																+ ".lockfile."
																+ fileItemForOperation
																		.getFileItem()
																		.getExtraName());
										fileItemForOperation.getFileItem()
												.setFilePath(newPath);
									}
									QueryData(currFolder);
								} else {
									alertDialog = ViewEffect
											.createComfirDialog(
													mContext,
													R.string.menu_read_lock,
													R.string.menu_read_lockfail,
													new CustomListener() {

														@Override
														public void onListener() {
															alertDialog
																	.dismiss();
														}
													}, null);
									alertDialog.show();
								}
							}

							// 记住密码
							CheckBox cb = (CheckBox) passwordDialog
									.findViewById(R.id.passwordcheckBox);
							boolean check = cb.isChecked();
							if (check) {
								SharedPreferences sh = mContext
										.getSharedPreferences("lockedfile", 0);
								Editor editor = sh.edit();
								editor.putString("rememberpassword",
										passwordString2);
								editor.commit();
							}
						}

					}, new CustomListener() {

						@Override
						public void onListener() {
							passwordDialog.dismiss();
						}
					});
			passwordDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			passwordDialog.show();
			break;
		case MENU_UNLOCK:
			staticfileItem=mData.getFileItems().get(selectedPosition).getFileItem();
			String type1= preResource.getMineType(fileItemForOperation.getFileItem().getExtraName().toLowerCase(
					Locale.getDefault()));
			if (type1 == null) {
				type1 = staticfileItem.getExtraName();
			}
			if (type1.startsWith("txt")) {
				type1="txt";
			}else if (type1.startsWith("video")) {
				type1="video";
			}else if (type1.startsWith("image")) {
				type1="image";
			}else {
				type1="music";
			}
			final String type=type1;
			if (fileItemForOperation
					.getFileItem().getFilePath().startsWith("smb")) {
				alertDialog = ViewEffect.createComfirDialog(mContext,
						R.string.menu_read_unlock,
						R.string.menu_read_smbunlock, new CustomListener() {

							@Override
							public void onListener() {
								alertDialog.dismiss();
							}
						}, null);
				alertDialog.show();
				break;
			}else {
			passwordDialog = ViewEffect.createRenameDialog(mContext,
					R.string.set_pwd_input, "", new CustomListener() {

						@Override
						public void onListener() {
							passwordDialog.dismiss();
							// 密码
							EditText et = (EditText) passwordDialog
									.findViewById(R.id.rename);
							String passwordString = et.getText().toString();
							FileEnDecryptManager fed=FileEnDecryptManager.getInstance();
							String pString = fed.getStringOf(new File(staticfileItem.getFilePath()),2);
							int paslength=Integer.parseInt(pString)+2;
							 pString=fed.getStringOf(new File(staticfileItem.getFilePath()),Integer.parseInt(pString)+2);
							 pString=pString.substring(0, pString.length()-2);
							pString = fed.decryptPassWord(pString);
							if (pString != null
									&& !pString.equals(passwordString)) {
								alertDialog = ViewEffect.createComfirDialog(
										mContext, R.string.menu_read_unlock,
										R.string.menu_read_passwordfail,
										new CustomListener() {
											@Override
											public void onListener() {
												alertDialog.dismiss();
												passwordDialog.show();
											}
										}, null);
								alertDialog.show();
								return;
							}
							fed.deletePassWord(new File(staticfileItem.getFilePath()), paslength);
							boolean flag;
							if (type.contains("txt")) {
							flag= fed.InitEncrypt(
									fileItemForOperation
									.getFileItem().getFilePath(), passwordString,
									type,mHandler);
							}else {
								flag= fed.InitEncrypt(
										fileItemForOperation
										.getFileItem().getFilePath(), passwordString,
										type,null);
							}
							if (flag) {
								File file = new File(fileItemForOperation
										.getFileItem().getFilePath());
								if (file.canWrite()) {
									String filename = fileItemForOperation
											.getFileItem().getFileName();

									String[] pathAndName = Helper.reName(
											fileItemForOperation
											.getFileItem(),
											filename.substring(
													0,
													filename.indexOf(".lockfile")));
									String newPath = pathAndName[0];
									File tmp = new File(newPath);
									if (tmp.exists()) {
										Log.i("解密后存在同名文件", "进入=====================");
										if (tmp.isFile()) {
											tmp.delete();
											Log.i("解密后存在同名文件", "删除=====================");
										}
									}
									tmp = new File(newPath);
									file.renameTo(new File(newPath));
									fileItemForOperation
									.getFileItem().setFileName(filename.substring(0,
											filename.indexOf(".lockfile")));
									fileItemForOperation
									.getFileItem().setFilePath(newPath);
								}
								QueryData(currFolder);
								alertDialog = ViewEffect.createComfirDialog(
										mContext, R.string.menu_read_unlock,
										R.string.menu_read_unlocksucess,
										new CustomListener() {

											@Override
											public void onListener() {
												alertDialog.dismiss();
											}
										}, null);
								alertDialog.show();
							} else {
								alertDialog = ViewEffect.createComfirDialog(
										mContext, R.string.menu_read_unlock,
										R.string.menu_read_unlockfail,
										new CustomListener() {

											@Override
											public void onListener() {
												alertDialog.dismiss();
											}
										}, null);
								alertDialog.show();
							}

						}

					}, new CustomListener() {

						@Override
						public void onListener() {
							passwordDialog.dismiss();
						}
					});
			passwordDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			passwordDialog.show();
			break;
		}
			
//			passwordDialog = ViewEffect.createRenameDialog(mContext,
//					R.string.set_pwd_input, "", new CustomListener() {
//
//						@Override
//						public void onListener() {
//							passwordDialog.dismiss();
//							// 密码
//							EditText et = (EditText) passwordDialog
//									.findViewById(R.id.rename);
//							String passwordString = et.getText().toString();
//							SharedPreferences sh = mContext
//									.getSharedPreferences("lockedfile", 0);
//							String pString = sh.getString(fileItemForOperation
//									.getFileItem().getFilePath(), null);
//							if (pString != null
//									&& !pString.equals(passwordString)) {
//								alertDialog = ViewEffect.createComfirDialog(
//										mContext, R.string.menu_read_unlock,
//										R.string.menu_read_passwordfail,
//										new CustomListener() {
//
//											@Override
//											public void onListener() {
//												alertDialog.dismiss();
//												passwordDialog.show();
//											}
//										}, null);
//								alertDialog.show();
//								return;
//							}
//							//这里写解密逻辑
//							
////							FileEnDecryptManager fed = FileEnDecryptManager
////									.getInstance();
////							boolean flag = fed.InitEncrypt(fileItemForOperation
////									.getFileItem().getFilePath(),
////									passwordString, "txt");
//							if (true) {
//								alertDialog = ViewEffect.createComfirDialog(
//										mContext, R.string.menu_read_unlock,
//										R.string.menu_read_unlocksucess,
//										new CustomListener() {
//
//											@Override
//											public void onListener() {
//												alertDialog.dismiss();
//											}
//										}, null);
//								alertDialog.show();
//								Editor editor = sh.edit();
//								editor.remove(fileItemForOperation
//										.getFileItem().getFilePath());
//								editor.commit();
//								File file = new File(fileItemForOperation
//										.getFileItem().getFilePath());
//								if (file.canWrite()) {
//									String filename = fileItemForOperation
//											.getFileItem().getFileName();
//
//									String[] pathAndName = Helper.reName(
//											fileItemForOperation.getFileItem(),
//											filename.substring(
//													0,
//													filename.length()
//															- 10
//															));
//									String newPath = pathAndName[0];
//									File tmp = new File(newPath);
//									if (tmp.exists()) {
//										return;
//									}
//									file.renameTo(new File(newPath));
//									fileItemForOperation
//											.getFileItem()
//											.setFileName(
//													filename.substring(
//															0,
//															filename.length()
//																	- 10
//																	));
//									fileItemForOperation.getFileItem()
//											.setFilePath(newPath);
//								}
//								
//								QueryData(currFolder);
//							} else {
//								alertDialog = ViewEffect.createComfirDialog(
//										mContext, R.string.menu_read_unlock,
//										R.string.menu_read_unlockfail,
//										new CustomListener() {
//
//											@Override
//											public void onListener() {
//												alertDialog.dismiss();
//											}
//										}, null);
//								alertDialog.show();
//							}
//
//						}
//
//					}, new CustomListener() {
//
//						@Override
//						public void onListener() {
//							passwordDialog.dismiss();
//						}
//					});
//			passwordDialog.show();
//			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 文件重命名对话框
	 */
	private HuzAlertDialog renameDialog;
	/**
	 * 新建文件夹对话框
	 */
	private HuzAlertDialog newFolderDialog;

	// boolean willExit = false;

	/**
	 * 是否已经回退到了根目录
	 * 
	 * @return
	 */
	boolean isRoot() {
		// return currFolder.equals("/");
		return currFolder.equals(topDir);
	}

	/**
	 * 返回上一级文件夹
	 */
	private void goBack() {
		Log.i(TAG, "goBack " + isRoot() + " " + topDir + " " + currFolder);
		if (isRoot()) {
			// willExit = true;
			// ViewEffect.showToast(mContext,
			// R.string.toast_press_one_more_to_exit);
			return;
		}
		// willExit = false;
		String parentPath = null;
		if (currFolder.contains("smb")) {
			SmbFile smb;
			try {
				smb = new SmbFile(currFolder);
				parentPath = smb.getParent();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (parentPath != null) {
				mData.getFileItems().clear();
				// QueryData(parentFile);
				QueryData(parentPath);
				refreshData();
				currFolder = parentPath.equals("/") ? "/" : parentPath;
			}

		} else {
			File file = new File(currFolder);
			File parentFile = file.getParentFile();
			parentPath = parentFile.getAbsolutePath();
			if (parentPath != null) {
				mData.getFileItems().clear();
				// QueryData(parentFile);
				QueryData(parentPath);
				refreshData();
				currFolder = parentPath.equals("/") ? "/" : parentPath + "/";
			}
		}

	}

	/**
	 * 接收并处理各种操作的结果消息
	 */
	public void pasteComplete() {
		mHandler.sendEmptyMessage(FileOperationThreadManager.PASTE_COMPLETED);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case RefreshData.LOAD_APK_ICON_FINISHED:
			// refreshData();
			// return;
			case FileOperationThreadManager.NEWFOLDER_FAILED:
				newFolderDialog.dismiss();
				handleFailed(msg);
				break;
			case FileOperationThreadManager.NEWFOLDER_SUCCEED:
				newFolderDialog.dismiss();
				handleSucceed(msg);

				Bundle bb = msg.getData();
				if (bb != null) {
					FileItem fileItem = new FileItem();
					String newName = bb
							.getString(FileOperationThreadManager.KEY_NEW_NAME);
					String newPath = bb
							.getString(FileOperationThreadManager.KEY_NEW_PATH);
					fileItem.setFileName(newName);
					if (!newPath.endsWith(File.separator))
						newPath += "/";
					fileItem.setFilePath(newPath);
					fileItem.setDirectory(true);
					fileItem.setFileSize(-1);
					fileItem.setExtraName("folder");
					fileItem.setIconId(preResource.getBitMap("folder"));

					// 将新建成功的文件夹放入到 显示队列中
					List<FileItemForOperation> fileItems = mData.getFileItems();
					FileItemForOperation operationFile = new FileItemForOperation();
					operationFile.setFileItem(fileItem);
					mData.insertAt(fileItems.size(), operationFile);
					refreshData();
					// QueryData(currFolder);
				}
				ivEmptyFolder.setVisibility(View.GONE);
				break;
			case FileOperationThreadManager.RENAME_FAILED:
				renameDialog.dismiss();
				handleFailed(msg);
				break;
			case FileOperationThreadManager.RENAME_SUCCEED:
				renameDialog.dismiss();
				if (mViewMode == ViewMode.SEARCHVIEW)
					mSearchAdapter.notifyDataSetChanged();
				else
					refreshData();
				handleSucceed(msg);
				break;
			case FileOperationThreadManager.GETTOTALNUM_COMPLETED:
				ProgressBar del_progress1 = (ProgressBar) operationProgressDialog
						.findViewById(R.id.progressBar);
				TextView del_tvNum1 = (TextView) operationProgressDialog
						.findViewById(R.id.tvNumber);
				TextView del_tvPercent1 = (TextView) operationProgressDialog
						.findViewById(R.id.tvPercent);
				del_progress1.setProgress(0);
				del_tvPercent1.setText(0 + "%");
				del_tvNum1.setText(0 + "/" + msg.arg1);
				break;
			case FileOperationThreadManager.GETTOTALNUM_ERROR:
				operationProgressDialog.dismiss();
				// ViewEffect.showToast(mContext,
				// R.string.toast_getfilenumber_error);
				break;
			case FileOperationThreadManager.DELETE_COMPLETED:
				handleSucceed(msg);
				// QueryData(new File(currFolder));
				// wdx delete
				/*
				 * if(mViewMode == ViewMode.SEARCHVIEW){
				 * //mLoader.updateList(mSearchBox
				 * .getText().toString(),mSearchAdapter.mdata);//mSearchAdapter
				 * mSearchAdapter
				 * .mSearchData.getFileItems().remove(selectedPosition);
				 * mSearchAdapter.notifyDataSetChanged(); } else
				 */
				QueryData((currFolder));
				mFileManager.resetDataForOperation();// zhaoyu
				operationProgressDialog.dismiss();
				break;
			case FileOperationThreadManager.DELETE_PROGRESS_CHANGE:
				ProgressBar del_progress = (ProgressBar) operationProgressDialog
						.findViewById(R.id.progressBar);
				TextView del_tvNum = (TextView) operationProgressDialog
						.findViewById(R.id.tvNumber);
				TextView del_tvPercent = (TextView) operationProgressDialog
						.findViewById(R.id.tvPercent);
				del_progress.setProgress(msg.arg1);
				del_tvPercent.setText(msg.arg1 + "%");
				String[] del_old = del_tvNum.getText().toString().split("/");
				del_tvNum.setText(msg.arg2 + "/" + del_old[1]);
				break;
			case FileOperationThreadManager.DELETE_FAILED:
				operationProgressDialog.dismiss();
				handleFailed(msg);
				break;
			case FileOperationThreadManager.DELETE_CANCEL:
				// ViewEffect.showToast(mContext,
				// R.string.toast_operation_canceled);
				break;
			case FileOperationThreadManager.GETSIZE_COMPLETED:
				if (operationProgressDialog != null) {
					TextView tv = (TextView) operationProgressDialog
							.findViewById(R.id.tvNumber);
					tv.setText("0/" + msg.arg1);
				}
				if (currFolder.startsWith("smb")) {
					if (smbpasteThreadManager != null) {
						try {
							smbpasteThreadManager
									.beginPaste(CopyOperation.UNKOWN);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					if (pasteThreadManager != null) {
						try {
							pasteThreadManager.beginPaste(CopyOperation.UNKOWN);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
			case FileOperationThreadManager.PASTE_FAILED:
				operationProgressDialog.dismiss();
				pasteThreadManager = null;
				smbpasteThreadManager = null;
				handleFailed(msg);
				break;
			case FileOperationThreadManager.PASTE_COMPLETED:

				// operationProgressDialog.dismiss();
				handleSucceed(msg);
				QueryData(currFolder);

				/*
				 * if(cutFlag){ cutFlag = false;
				 * QueryData((currFolder));//zhaoyu }else{ if(pastDir != null){
				 * Log.i(TAG,"" + currFolder + " " + pastDir);
				 * if(!currFolder.equals(pastDir))
				 * pastBrowser.QueryData((pastDir)); else
				 * QueryData((currFolder));//zhaoyu
				 * mFileManager.resetDataForOperation(); } }
				 */
				break;
			case FileOperationThreadManager.PASTE_PROGRESS_CHANGE:
				ProgressBar paste_progress = (ProgressBar) operationProgressDialog
						.findViewById(R.id.progressBar);
				TextView paste_tvNum = (TextView) operationProgressDialog
						.findViewById(R.id.tvNumber);
				TextView paste_tvPercent = (TextView) operationProgressDialog
						.findViewById(R.id.tvPercent);
				paste_progress.setProgress(msg.arg1);
				if (msg.arg2 != 0) {
					paste_tvNum.setText(msg.arg2 + "");
				}
				Bundle bundle = (Bundle) msg.obj;

				if (bundle != null) {
					paste_tvNum.setText(bundle.getString("currPos"));
					paste_tvPercent.setText(bundle.getString("percentage"));
				}
				break;
			case FileOperationThreadManager.PASTE_PAUSE:
				handlePaused(msg);
				break;
			case FileOperationThreadManager.PASTE_CANCEL:
				// QueryData(new File(currFolder));
				QueryData((currFolder));
				// ViewEffect.showToast(mContext,
				// R.string.toast_operation_canceled);
				operationProgressDialog.dismiss();
				pasteThreadManager = null;
				smbpasteThreadManager = null;
				break;
			// case FileOperationThreadManager.LOADCAPACITY:
			// mCapacityAdapater.notifyDataSetChanged();
			// return;
			// case FileOperationThreadManager.LOADCAPACITYOK:
			// if (msg.arg1 == rand) {
			// mCapacityAdapater.notifyDataSetChanged();
			// View view = capacityDialog.findViewById(R.id.loading);
			// view.setVisibility(View.GONE);
			// }
			// return;
				
			case FileOperationThreadManager.DELETE_LOCKFILE:
				ToastBuild.toast(mContext, R.string.toast_lockfile_undelete);
				break;
				
			case LOCK_START :
			  	   mLockProgressDialog = new ProgressDialog(mContext);
						mLockProgressDialog.setTitle(R.string.menu_read_lock);
						mLockProgressDialog.setMessage(mContext.getString(R.string.locking));
						mLockProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						mLockProgressDialog.setMax(msg.arg1);
						mLockProgressDialog.setProgress(msg.arg2);
						mLockProgressDialog.show();
			  	   break;
			     case LOCK_INCREACE :
			  	   mLockProgressDialog.setProgress(msg.arg1);
			  	   break;
			     case LOCK_END :
			  	   mLockProgressDialog.dismiss();
			  	   break;
			default:
				break;
			}
			/**
			 * 操作结束后不重置application，可以多次粘贴(剪切除外)
			 */
			if (msg.what == FileOperationThreadManager.PASTE_CANCEL
					|| msg.what == FileOperationThreadManager.PASTE_COMPLETED
					|| msg.what == FileOperationThreadManager.PASTE_SUCCEED
					|| msg.what == FileOperationThreadManager.PASTE_FAILED
					|| msg.what == FileOperationThreadManager.DELETE_COMPLETED
					|| msg.what == FileOperationThreadManager.DELETE_FAILED) {
				if (mFileManager.getFilesFor() == FilesFor.CUT
						|| mFileManager.getFilesFor() == FilesFor.DELETE)
					mFileManager.resetDataForOperation();
				if (backgroundOperation) {
					backgroundOperation = false;
					setMood(R.drawable.smallicon,
							R.string.operating_background_complete);
				}
			}
			finishOperation();
		}
	};

	/**
	 * 操作完成之后 回到初始状态
	 */
	public void finishOperation() {
		isOperating = false;
		// item = null;
		// showCurLoc();
		// // toogleImageButton();
		// toggleOperatingView(false, false);
		// drawPopup(false);
		// setFocus();
	}

	private void handleSucceed(Message msg) {
		Bundle b = msg.getData();
		String currName = "";
		if (b != null) {
			currName = b.getString(FileOperationThreadManager.KEY_CURR_NAME);
			if (currName == null)
				currName = "";
		}
		switch (msg.what) {
		case FileOperationThreadManager.PASTE_COMPLETED:
			ViewEffect.showToast(mContext, R.string.toast_paste_complete);
			break;
		case FileOperationThreadManager.DELETE_COMPLETED:
			ViewEffect.showToast(mContext,
					formatStr(R.string.toast_delete_complete, currName));
			break;
		case FileOperationThreadManager.NEWFOLDER_SUCCEED:
			ViewEffect.showToast(mContext, R.string.toast_new_folder_succeed);
			break;
		case FileOperationThreadManager.RENAME_SUCCEED:
			ViewEffect.showToast(mContext, R.string.toast_rename_succeed);
			break;
		default:
			break;
		}
	}

	/**
	 * handle operation failed
	 * 
	 * @param msg
	 */
	private void handleFailed(Message msg) {
		Bundle b = msg.getData();
		String currName = "";
		if (b != null) {
			currName = b.getString(FileOperationThreadManager.KEY_CURR_NAME);
			if (currName == null)
				currName = "";
		}
		switch (msg.arg1) {
		case FileOperationThreadManager.FAILED_REASON_UNKOWN:
			ViewEffect.showToast(mContext, R.string.toast_operation_failed);
			break;
		case FileOperationThreadManager.FAILED_REASON_INVALNAME:
			ViewEffect.showToast(mContext, R.string.toast_inval_filename);
			break;
		case FileOperationThreadManager.FAILED_REASON_FROM_FILE_NOT_EXIST:
			ViewEffect.showToast(mContext, R.string.toast_file_not_find);
			break;
		case FileOperationThreadManager.FAILED_REASON_READ_ONLY_FILE_SYSTEM:
			ViewEffect.showToast(mContext,
					formatStr(R.string.toast_read_only_file_system, currName));
			break;
		case FileOperationThreadManager.FAILED_REASON_FOLDER_HAS_EXIST:
			ViewEffect.showToast(mContext,
					R.string.toast_rename_or_new_folder_failed_folder_exist);
			break;
		case FileOperationThreadManager.FAILED_REASON_FOLDER_LIMIT:
			ViewEffect.showToast(mContext, R.string.toast_folder_limit);
			break;
		case FileOperationThreadManager.FAILED_REASON_SAME_FOLDER:
			ViewEffect.showToast(mContext,
					R.string.toast_cont_move_in_same_folder);
			break;
		case FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT:
			ViewEffect.showToast(mContext, R.string.toast_no_space_left);
			break;
		default:
			break;
		}
	}

	/*
	 * private String formatStr(int resId, String str) { String res =
	 * mContext.getText(resId).toString(); return String.format(res, str); }
	 */

	/**
	 * handle operation paused
	 * 
	 * @param msg
	 */
	private void handlePaused(Message msg) {
		switch (msg.arg1) {
		case FileOperationThreadManager.PAUSE_REASON_TO_FOLDER_HAS_EXIST:
			// showChooseOperationDialog();
			break;
		case FileOperationThreadManager.PAUSE_REASON_CANNOT_COVER:
			// ViewEffect.showToast(mContext, R.string.toast_cant_cover);
			// showChooseOperationDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 是否全选了
	 */
	private boolean selectedAll = false;

	/**
	 * 切换多选模式/单击打开模式
	 */
	/*
	 * private void toggleOperating() { toggleOperatingView(); SelectNothing();
	 * // 选择了文件，但是没有下任何命令 if (mFileManager.getFilesFor() == FilesFor.UNKOWN)
	 * mFileManager.resetDataForOperation(); }
	 */

	/*
	 * private void toggleOperatingView() { isOperating = !isOperating; if
	 * (!isOperating) { ViewEffect.cancelToast();
	 * //btn_operating.setImageResource(R.drawable.toolbar_single); } else {
	 * //ViewEffect.showToast(mContext, R.string.toast_multi_operating);
	 * //btn_operating.setImageResource(R.drawable.toolbar_multi); } }
	 */

	private void toggleOperatingView(boolean b) {
		isOperating = b;
		/*
		 * if (!isOperating) {
		 * btn_operating.setImageResource(R.drawable.toolbar_single); } else
		 * btn_operating.setImageResource(R.drawable.toolbar_multi);
		 */
	}

	@Override
	public boolean onLongClick(View v) {
		/*
		 * if (v.getId() == btn_operating.getId()) { // 切换单击和多选 if
		 * (!selectedAll) { SelectAll(); } else { SelectNothing(); }
		 * toggleOperatingView(selectedAll); return true; }
		 */
		return false;
	}

	public void onBtnUp() {
		if (!isRoot()) {
			goBack();
		}
	}

	public void onBtnNew() {
		newFolderDialog = ViewEffect.newFolderDialog(mContext,
				R.string.title_newFolder, new CustomListener() {
					@Override
					public void onListener() {
						EditText et = (EditText) newFolderDialog
								.findViewById(R.id.rename);
						String newFolder = et.getText().toString();

						if (currFolder.startsWith("smb")) {
							SmbFileOperationThreadManager manager = new SmbFileOperationThreadManager(
									mHandler);
							try {
								manager.newFolder(currFolder, newFolder);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							FileOperationThreadManager manager = new FileOperationThreadManager(
									mHandler);
							manager.newFolder(currFolder, newFolder);
						}
					}
				}, new CustomListener() {
					@Override
					public void onListener() {
						newFolderDialog.dismiss();
					}
				});

		newFolderDialog.show();
	}

	/*
	 * public void onBtnPaste(FileBrowser browser,String dir){ pastDir = dir;
	 * pastBrowser = browser; if (!currFolderCanOperate(false)) { return; }
	 * 
	 * List<FileItemForOperation> list =
	 * mFileManager.getDataForOperation().getFileItems();
	 * //showOperationProgressDialog(R.string.title_copying, list.size(),true);
	 * Log.i(TAG,"past files " + list.size() + " " + currFolder);
	 * 
	 * if(list.size() > 0){
	 * 
	 * for(int i=0;i<list.size();i++){ FileItem item =
	 * list.get(i).getFileItem(); if(pastDir.startsWith(item.getFilePath())){
	 * continue;//responseHandler.sendMessage(responseMsg(PASTE_FAILED,
	 * FAILED_REASON_PASTE_NOT_ALLOWED)); } String FileName =
	 * item.getFileName(); String newFilePath = dir + FileName;
	 * PasteCompletionListner.mFilesPaste++;
	 * 
	 * if(newFilePath.startsWith("smb")){ try { SmbFile smbFile = new
	 * SmbFile(newFilePath); if(smbFile.exists()){
	 * showChooseOperationDialog(item.getFilePath(),dir); continue; }
	 * 
	 * } catch (MalformedURLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }catch (SmbException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 * 
	 * }else{ File newFile = new File(newFilePath); if(newFile.exists()){
	 * showChooseOperationDialog(item.getFilePath(),dir); continue; } }
	 * 
	 * Intent intent = new Intent(mContext, DownloadService.class);
	 * intent.putExtra("srcDir", item.getFilePath());
	 * intent.putExtra("toDir",dir); Log.i(TAG,"" + operationType);
	 * if(operationType == CopyOperation.COVER)
	 * //COVER,JUMP,APPEND2,CANCEL,UNKOWN intent.putExtra("CopyOperation",
	 * "COVER"); else intent.putExtra("CopyOperation", "APPEND2");
	 * 
	 * if( mFileManager.getFilesFor() == FilesFor.CUT) intent.putExtra("isCut",
	 * true); else intent.putExtra("isCut", false);
	 * //FileActivity.mFilesPaste++; mContext.startService(intent); }
	 * ViewEffect.showToastLongTime(mContext,
	 * formatStr(R.string.intent_to_paste, "" + list.size())); }
	 * mFileManager.resetDataForOperation(); }
	 */
	public void onBtnDisplay(View v, int posx, int posy) {
		// int[] location = new int[2];
		// btnDisplay.getLocationOnScreen(location);
		// Log.i(TAG,"getlocation x = " + location[0] + " y= " + location[1]);
		if (!LanguageCheck.isZh()) {
			// displays[0] = "display by list";
			displays[0] = "sort by name";
			displays[1] = "sort by size";
			displays[2] = "sort by time";
		}
		final QuickAction popAction = new QuickAction(mContext, displays);
		popAction.setOnActionItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG, "POP onItemClick " + arg2);
				// lrh mdf 1218
				/*
				 * if(arg2 == 0){ mViewMode = ViewMode.GRIDVIEW;
				 * toggleViewMode(); popAction.dismiss(); }
				 */
				/* liuyufa change for listview 20140123 start */
				/*
				 * if(arg2 == 0){ mViewMode = ViewMode.LISTVIEW;
				 * toggleViewMode(); popAction.dismiss(); }else if(arg2 == 1){
				 * SetFileComparator(FileComparatorMode.ComparatorByName);
				 * QueryData((currFolder)); popAction.dismiss(); }else if(arg2
				 * == 2){
				 * SetFileComparator(FileComparatorMode.ComparatorBySize);
				 * QueryData((currFolder)); popAction.dismiss(); }else if(arg2
				 * == 3){ //mFileManager.mComparator = mFileManager.comp_update;
				 * SetFileComparator(FileComparatorMode.ComparatorByTime);
				 * QueryData((currFolder)); popAction.dismiss(); }
				 */
				if (arg2 == 0) {
					SetFileComparator(FileComparatorMode.ComparatorByName);
					QueryData((currFolder));
					popAction.dismiss();
				} else if (arg2 == 1) {
					SetFileComparator(FileComparatorMode.ComparatorBySize);
					QueryData((currFolder));
					popAction.dismiss();
				} else if (arg2 == 2) {
					SetFileComparator(FileComparatorMode.ComparatorByTime);
					QueryData((currFolder));
					popAction.dismiss();
				}
				/* liuyufa change for listview 20140123 end */
				SharedPreferences.Editor editor = FileActivity.mPreferences
						.edit();
				editor.putString(topDir + "file", mViewMode.toString());
				editor.commit();
			}
			// end

		});
		// popAction.show(btnDisplay,location[0]-5, location[1]);
		popAction.showPop(v, posx - 5, posy);
	}

	/*
	 * @Override public void onClick(View v) { if(v.getId() == R.id.btnHome){
	 * mContext.startActivity(new Intent(mContext, WifiShareActivity.class));
	 * }else if(v.getId() == R.id.btnUp){ if(!isRoot()){ goBack(); } }else
	 * if(v.getId() == R.id.btnNew){ newFolderDialog =
	 * ViewEffect.newFolderDialog(mContext, R.string.title_newFolder, new
	 * CustomListener() {
	 * 
	 * @Override public void onListener() { EditText et = (EditText)
	 * newFolderDialog .findViewById(R.id.rename); String newFolder =
	 * et.getText().toString();
	 * 
	 * if(currFolder.startsWith("smb")){ SmbFileOperationThreadManager manager =
	 * new SmbFileOperationThreadManager( mHandler); try {
	 * manager.newFolder(currFolder, newFolder); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }else{
	 * FileOperationThreadManager manager = new FileOperationThreadManager(
	 * mHandler); manager.newFolder(currFolder, newFolder); } } }, new
	 * CustomListener() {
	 * 
	 * @Override public void onListener() { newFolderDialog.dismiss(); } });
	 * 
	 * newFolderDialog.show(); }if(v.getId() == R.id.btnPaste){ if
	 * (!currFolderCanOperate(false)) { return; } if
	 * (!mFileManager.canOperation()) { ViewEffect.showToast(mContext,
	 * R.string.toast_no_file_to_paste); return; }
	 * 
	 * List<FileItemForOperation> list =
	 * mFileManager.getDataForOperation().getFileItems();
	 * showOperationProgressDialog(R.string.title_copying, list.size(),true);
	 * Log.i(TAG,"past files " + list.size() + " " + currFolder); try {
	 * createPasteThread(list); if(currFolder.startsWith("smb"))
	 * smbpasteThreadManager.setFilesFor(mFileManager.getFilesFor()); else
	 * pasteThreadManager.setFilesFor(mFileManager.getFilesFor()); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }if(v.getId() == R.id.btnDisplay){ int[] location = new int[2];
	 * btnDisplay.getLocationOnScreen(location); Log.i(TAG,"getlocation x = " +
	 * location[0] + " y= " + location[1]); final QuickAction popAction = new
	 * QuickAction(mContext,displays);
	 * popAction.setOnActionItemClickListener(new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * arg2, long arg3) { // TODO Auto-generated method stub
	 * Log.i(TAG,"POP onItemClick " + arg2); if(arg2 == 0){ mViewMode =
	 * ViewMode.GRIDVIEW; toggleViewMode(); popAction.dismiss(); }else if(arg2
	 * == 1){ mViewMode = ViewMode.LISTVIEW; toggleViewMode();
	 * popAction.dismiss(); }else if(arg2 == 2){ mFileManager.mComparator =
	 * mFileManager.comp_name; QueryData((currFolder)); popAction.dismiss();
	 * }else if(arg2 == 3){ mFileManager.mComparator = mFileManager.comp_size;
	 * QueryData((currFolder)); popAction.dismiss(); }else if(arg2 == 4){
	 * mFileManager.mComparator = mFileManager.comp_update;
	 * QueryData((currFolder)); popAction.dismiss(); } }
	 * 
	 * 
	 * }); popAction.show(btnDisplay,location[0]-5, location[1]); }
	 * 
	 * @Override public boolean onTouch(View arg0, MotionEvent arg1) { // TODO
	 * Auto-generated method stub return false; } /** 复制文件用的Manager
	 */
	FileOperationThreadManager pasteThreadManager;
	SmbFileOperationThreadManager smbpasteThreadManager;

	/*
	 * private void createPasteThread(List<FileItemForOperation> list) throws
	 * Exception{ if(currFolder.startsWith("smb")){ if (smbpasteThreadManager ==
	 * null) { smbpasteThreadManager = new SmbFileOperationThreadManager(list,
	 * pastDir, mHandler, mFileManager.getFilesFor(),mContext);//currFolder }
	 * else { smbpasteThreadManager.currPosition = 0;
	 * smbpasteThreadManager.setToFolder(pastDir);//currFolder
	 * smbpasteThreadManager.setOperatingFiles(list); } }else{ if
	 * (pasteThreadManager == null) { pasteThreadManager = new
	 * FileOperationThreadManager(list, pastDir, mHandler,
	 * mFileManager.getFilesFor(),mContext);//currFolder } else {
	 * pasteThreadManager.currPosition = 0;
	 * pasteThreadManager.setToFolder(pastDir);//currFolder
	 * pasteThreadManager.setOperatingFiles(list); } }
	 * 
	 * }
	 */
	/* liuyufa 20131221 add for dialog start */
	private void Searchdialog() {
		if (1 == isshow) {
			mydialog.dismiss();
		}
		mydialog = new SearchDialog(mContext, R.layout.searching_dialog,
				R.style.Theme_dialog);
		mydialog.show();
	}

	/* liuyufa 20131221 add for dialog end */
	/**
	 * 删除确认对话框
	 */
	HuzAlertDialog comfirDialog;

	/**
	 * 处理进度条
	 */
	HuzAlertDialog operationProgressDialog;

	private void showOperationProgressDialog(int titleId, int count,
			boolean reCreat) {
		if (operationProgressDialog == null || reCreat) {
			operationProgressDialog = ViewEffect.createCustProgressDialog(
					mContext, titleId, count, hideOperationListener,
					negativeListener, cancelListener);
		}
		operationProgressDialog.show();
	}

	/**
	 * 隐藏处理进度条
	 */
	private CustomListener hideOperationListener = new CustomListener() {
		@Override
		public void onListener() {
			backgroundOperation = true;
			setMood(R.drawable.smallicon, R.string.operating_background);
		}
	};
	/**
	 * 取消处理
	 */
	private CustomListener negativeListener = new CustomListener() {
		@Override
		public void onListener() {
			pasteThreadManager.setCanceled(true);
		}
	};
	private OnCancelListener cancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			pasteThreadManager.setCanceled(true);
		}
	};
	PendingIntent contentIntent;

	private PendingIntent makeMoodIntent() {
		if (contentIntent == null)
			contentIntent = PendingIntent.getActivity(
					mContext,
					0,
					new Intent(mContext, FileBrowser.class).setFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(
							KEY_PATH, currFolder),
					PendingIntent.FLAG_UPDATE_CURRENT);
		return contentIntent;
	}

	private NotificationManager nm;

	@SuppressWarnings("deprecation")
	private void setMood(int moodId, int contentId) {
		String notiContent = mContext.getText(contentId).toString();
		Notification notification = new Notification(moodId, notiContent,
				System.currentTimeMillis());
		notification.setLatestEventInfo(mContext,
				mContext.getText(R.string.app_name).toString(), notiContent,
				makeMoodIntent());
		nm.notify(R.layout.file_browser, notification);
	}

	/**
	 * 全选
	 */
	private void SelectAll() {
		selectedAll = true;
		if (mViewMode == ViewMode.SEARCHVIEW)
			for (FileItemForOperation fileItem : mFileData.getFileItems()) {
				fileItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
			}
		else
			for (FileItemForOperation fileItem : mData.getFileItems()) {
				fileItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
			}
		if (mViewMode == ViewMode.SEARCHVIEW) {
			// mSearchAdapter.updatSearchData(mFileData);
			mSearchAdapter.notifyDataSetChanged();
		} else
			refreshData();
	}

	/**
	 * 全不选
	 */
	private void SelectNothing() {
		selectedAll = false;
		if (mViewMode == ViewMode.SEARCHVIEW)
			for (FileItemForOperation fileItem : mFileData.getFileItems()) {
				fileItem.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
			}
		else
			for (FileItemForOperation fileItem : mData.getFileItems()) {
				fileItem.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
			}
		if (mViewMode == ViewMode.SEARCHVIEW) {
			// mSearchAdapter.updatSearchData(mFileData);
			mSearchAdapter.notifyDataSetChanged();
		} else
			refreshData();
	}

	/*
	 * @Override public void whichOperation(FilesFor filesFor, int size) {
	 * Log.i(TAG,"whichOperation " + filesFor); if (filesFor == FilesFor.COPY ||
	 * filesFor == FilesFor.CUT) { if (filesFor == FilesFor.COPY) {
	 * ViewEffect.showToastLongTime(mContext, formatStr(R.string.intent_to_copy,
	 * "" + size)); } if (filesFor == FilesFor.CUT)
	 * ViewEffect.showToastLongTime(mContext, formatStr(R.string.intent_to_cut,
	 * "" + size)); //btnPaste.setBackgroundDrawable(mContext.getResources() //
	 * .getDrawable(R.drawable.toolbar_item_bg_highlight));
	 * FileActivity.btnPaste.setImageResource(R.drawable.toolbar_paste_enable);
	 * /
	 * /FileActivity.btnPaste.setBackgroundResource(R.drawable.toolbar_paste_enable
	 * ); //btnPaste.setImageResource(R.drawable.toolbar_paste_enable);
	 * 
	 * } if (filesFor == FilesFor.UNKOWN) { nm.cancelAll();
	 * //btnPaste.setBackgroundDrawable(mContext.getResources() //
	 * .getDrawable(R.drawable.toolbar_item_bg_unenable));
	 * //btnPaste.setImageResource(R.drawable.toolbar_paste_unenable);
	 * //FileActivity
	 * .btnPaste.setBackgroundResource(R.drawable.toolbar_paste_unenable);
	 * FileActivity
	 * .btnPaste.setImageResource(R.drawable.toolbar_paste_unenable); } }
	 */
	@Override
	public void queryFinished() {
		selectedAll = false;
		/* liuyufa 20131220 change for dialog start */
		/*
		 * pb.setVisibility(View.GONE);//zhaoyu 1205
		 */
		mydialog.dismiss();
		isshow = 0;
		/* liuyufa 20131220 change for dialog end */
		/*
		 * if (mData.getFileItems().size() == 0) {
		 * ivEmptyFolder.setVisibility(View.VISIBLE); } else {
		 * ivEmptyFolder.setVisibility(View.GONE); } toggleBtnEnable(true);
		 */
	}

	@Override
	public void queryMatched() {
		Log.i(TAG, "queryMatched " + mData.getFileItems().size() + " "
				+ mFileData.getFileItems().size() + " " + mViewMode + " "
				+ currFolder);
		if (mViewMode == ViewMode.SEARCHVIEW) {
			mSearchAdapter.updatSearchData(mFileData);
			mSearchAdapter.notifyDataSetChanged();
		} else
			refreshData();
	}

	@Override
	public boolean onBackPressed() {
		StopQueryData(); // zhaoyu 1217
		if (isRoot()) {// willExit
			// ViewEffect.cancelToast();
			return false;
		}
		goBack();
		return true;
	}

	@Override
	public Loader<List<FileItemForOperation>> onCreateLoader(int arg0,
			Bundle arg1) {
		// TODO Auto-generated method stub
		return new AppItemLoader(mContext, mSearchAdapter);
	}

	@Override
	public void onLoadFinished(Loader<List<FileItemForOperation>> arg0,
			List<FileItemForOperation> arg1) {
		// TODO Auto-generated method stub
		mSearchAdapter.mSearchData.clear();
		List<FileItemForOperation> items = new ArrayList<FileItemForOperation>();
		Iterator<FileItemForOperation> iterator = arg1.iterator();
		while (iterator.hasNext()) {
			FileItemForOperation item = (FileItemForOperation) iterator.next();
			items.add(item);// localBaseListItem);
		}
		mSearchAdapter.mSearchData.setFileItems(items);
		Log.i(TAG, "onLoadFinished " + mSearchAdapter.getCount());
		mSearchAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<FileItemForOperation>> arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onLoaderReset ");
		mSearchAdapter.notifyDataSetChanged();
	}

	/* liuyufa 20131223 add for search start */
	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnSearch2) {
				AppItemLoader.issearch2 = 1;
				StartQueryData(); // wdx add 1219
				Log.i(TAG, "" + searchFolder + " " + currFolder);
				// wdx rewrite 1219
				// if(searchFolder==null || !searchFolder.equals(currFolder)){
				// if(searchFolder==null || !searchFolder.equals(currFolder)){
				searchFolder = currFolder;
				QueryData(searchFolder, false, FileFilter.SEARCH);
				// }
				mSearchAdapter.updatSearchData(mFileData); // zhaoyu 1205
				mSearchList.setAdapter(mSearchAdapter);
				AppItemLoader.issearch2 = 0;
			}
		}
	}

	/* liuyufa 20131223 add for search start */

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE: // Idle态，进行实际数据的加载显示
			ListActivity.mBusy = false;
			this.refreshData();
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			ListActivity.mBusy = true;
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			ListActivity.mBusy = true;
			break;
		default:
			break;
		}
	}

}
