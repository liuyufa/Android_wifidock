package com.hualu.wifistart.filecenter.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hualu.wifistart.FileEnDecryptManager;
import com.hualu.wifistart.ListActivity;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.files.FileItem;
import com.hualu.wifistart.filecenter.files.FileItemForOperation;
import com.hualu.wifistart.filecenter.files.FileOperationThreadManager;
import com.hualu.wifistart.filecenter.files.FilePropertyAdapter;
import com.hualu.wifistart.filecenter.files.SmbFileOperationThreadManager;
import com.hualu.wifistart.filecenter.files.FileManager.FileFilter;
import com.hualu.wifistart.filecenter.files.FileManager.FilesFor;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.utils.CustomListener;
import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.music.MusicList;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.views.WPTextBox;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;
import com.hualu.wifistart.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.FragmentActivity;

public class MusicFileBrowser extends Browser implements
		LoaderManager.LoaderCallbacks<List<FileItemForOperation>> {
	final boolean DEBUG = false;
	ListActivity listActivity;
	static {
		TAG = MusicFileBrowser.class.getCanonicalName();

	}
	// private ListView mListView;
	// private GridView mGridView;

	private boolean onResume = false;
	// private String topDir = null;
	private ProgressBar pb;

	// file operation =========================zhaoyu
	// private final int MENU_FIRST = Menu.FIRST + 300;
	// private final int MENU_SWITCH_MODE = MENU_FIRST;

	private final int MENU_FIRST = Menu.FIRST + 100;
	private final int MENU_DELETE = MENU_FIRST + 1;
	private final int MENU_RENAME = MENU_FIRST + 2;
	private final int MENU_SELECT_ALL = MENU_FIRST + 3;
	// private final int MENU_REFRESH = MENU_FIRST + 4;
	private final int MENU_LOCK = MENU_FIRST + 4;
	private final int MENU_UNLOCK = MENU_FIRST + 6;
	private final int MENU_ADDTO_PLAYLIST = MENU_FIRST + 5;
	// private final int MENU_OPEN_AS = MENU_FIRST + 6;
	private final int MENU_READPROP = MENU_FIRST + 7;

	// private final int MENU_COPY = MENU_FIRST + 8;
	private final int MENU_CUT = MENU_FIRST + 9;
	// private final int MENU_DISPLAY = MENU_FIRST + 15;
	// private final int MENU_STATUS = MENU_FIRST + 16;
	// private final int MENU_EXIT = MENU_FIRST + 30;

	// private final int MENU_PASTE = MENU_FIRST + 10;

	private int selectedPosition;
	private boolean isOperating;
	private boolean selectedAll = false;
	boolean hasContextItemSelected = false;
	private boolean backgroundOperation = false;
	HuzAlertDialog comfirDialog;
	HuzAlertDialog operationProgressDialog;
	private NotificationManager nm;
	PendingIntent contentIntent;
	private HuzAlertDialog renameDialog;
	private HuzAlertDialog passwordDialog;
	private HuzAlertDialog passwordDialog2;
	private HuzAlertDialog alertDialog;
	String pw;
	IAppLoader mLoader;
	public FragmentActivity activity;

	public MusicFileBrowser(Context context) {
		super(context);
		Log.i(TAG, "MusicFileBrowser");
		initView();
		mViewMode = ViewMode.LISTVIEW;
		topDir = Singleton.LOCAL_ROOT;
		// currFolder = topDir;
		if (!onResume) {
			QueryData(topDir, true, FileFilter.MUSIC);
			onResume = true;
		}
	}

	public MusicFileBrowser(Context context, String path,
			FragmentActivity activity, ViewMode mode) {
		super(context);
		Log.i(TAG, "MusicFileBrowser " + mode);
		this.activity = activity;
		mViewMode = mode;// ViewMode.LISTVIEW;
		initView();
		topDir = path;
		// currFolder = topDir;
		// wdx rewrite 1216
		if (!onResume) {
			if (!ListActivity.buildVoidFileBrowser()) {
				QueryData((topDir), true, FileFilter.MUSIC);
			} else {
				toggleViewMode();
			}
			onResume = true;
		}
	}

	public void onResume() {
		if (mViewMode == ViewMode.SEARCHVIEW) {
			if (!mSearchBox.getText().equals(""))
				mLoader.updateList(mSearchBox.getText().toString(),
						mSearchAdapter.mdata);
			else {
				mSearchAdapter.updatSearchData(mData);
				mSearchAdapter.notifyDataSetChanged();
			}
		} else if (!onResume) {
			QueryData((topDir), true, FileFilter.MUSIC);
			onResume = true;
		}

	}

	private void initView() {
		mView = mInflater.inflate(R.layout.music_browser, null);
		pb = (ProgressBar) mView.findViewById(R.id.pbar);

		mListView = (ListView) mView.findViewById(R.id.lvMusicList);
		mListView.setOnItemClickListener(this);

		mGridView = (GridView) mView.findViewById(R.id.gvMusicList);
		mGridView.setOnItemClickListener(this);
		mGridView.setNumColumns(ListActivity.mScreenWidth / 160);
		/*
		 * if(mViewMode == ViewMode.LISTVIEW){
		 * mListView.setVisibility(View.VISIBLE);
		 * mGridView.setVisibility(View.GONE); } else{
		 * mListView.setVisibility(View.GONE);
		 * mGridView.setVisibility(View.VISIBLE); }
		 */
		// mGridView.setVisibility(View.GONE);

		mSearchView = (LinearLayout) mView.findViewById(R.id.search);
		mSearchBox = (WPTextBox) mView.findViewById(R.id.searchbox);
		mSearchMode = SearchMode.NORMALVIEW;
		// mSearchView.setVisibility(View.GONE);

		mSearchList = (ListView) mView.findViewById(R.id.lvSearchList);
		mSearchList.setOnItemClickListener(this);
		// mSearchList.setVisibility(View.GONE);

		((Activity) mContext).registerForContextMenu(mListView);
		((Activity) mContext).registerForContextMenu(mGridView);
		((Activity) mContext).registerForContextMenu(mSearchList);
		// search

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
				Log.i("test", "onTextChanged " + paramCharSequence.toString());
				if (!paramCharSequence.toString().equals(""))
					mLoader.updateList(paramCharSequence.toString(),
							mSearchAdapter.mdata);// mSearchAdapter
			}
		});

		Loader<List<FileItemForOperation>> loader = ((FragmentActivity) activity)
				.getSupportLoaderManager().initLoader(0, null, this);
		loader.forceLoad();
		mLoader = ((IAppLoader) loader);
	}

	public void QueryData() {
		super.QueryData(topDir, true, FileFilter.MUSIC);
		pb.setVisibility(View.VISIBLE);
		if (mViewMode == ViewMode.LISTVIEW)
			mListView.setAdapter(mItemsAdapter);
		else
			mGridView.setAdapter(mItemsAdapter);
	}

	@Override
	public void QueryData(String path, boolean clear, FileFilter filter) {
		super.QueryData(path, clear, filter);
		pb.setVisibility(View.VISIBLE);
		// mListView.setAdapter(mItemsAdapter);
		toggleViewMode();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileItemForOperation fileItem;
		itemClickPosition = position;
		if (mViewMode == ViewMode.SEARCHVIEW) {
			fileItem = mSearchAdapter.mSearchData.getFileItems().get(position);
		} else
			fileItem = mData.getFileItems().get(position);

		/*
		 * if(topDir.startsWith("smb")){ //Uri uri =
		 * getContentUri(fileItem.getFileItem()); Intent intent = new
		 * Intent(mContext,MusicActivity.class); intent.putExtra("smbpath",
		 * fileItem.getFileItem().getFilePath());
		 * mContext.startActivity(intent); }else
		 */
		clickFileItem(fileItem);
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	/*
	 * @Override public void whichOperation(FilesFor filesFor, int size) {
	 * 
	 * }
	 */
	@Override
	public void queryFinished() {
		pb.setVisibility(View.GONE);
		/*
		 * int cnt = getData().getFileItems().size(); Log.i(TAG,"queryFinished "
		 * + cnt); for(int i = 0;i<cnt;i++) Log.i(TAG,"filename " +
		 * getData().getFileItems().get(i).getFileItem().getFileName());
		 */
	}

	@Override
	public void queryMatched() {
		if (mViewMode == ViewMode.SEARCHVIEW) {
			if (!mSearchBox.getText().equals(""))
				mLoader.updateList(mSearchBox.getText().toString(),
						mSearchAdapter.mdata);
			else {
				mSearchAdapter.updatSearchData(mData);
				mSearchAdapter.notifyDataSetChanged();
			}
		} else
			refreshData();
	}

	/*
	 * zhaoyu 1205 private void goBack() { //Log.i(TAG,"goBack " + isRoot() +
	 * " " + topDir + " " + currFolder); if (isRoot()) { return; } //willExit =
	 * false; String parentPath = null; if(topDir.contains("smb")){ SmbFile smb;
	 * try { smb = new SmbFile(topDir); parentPath = smb.getParent(); } catch
	 * (MalformedURLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } if(parentPath != null){
	 * mData.getFileItems().clear(); //QueryData(parentFile);
	 * //QueryData(parentPath); QueryData(parentPath, true, FileFilter.MUSIC);
	 * refreshData(); currFolder = parentPath.equals("/") ? "/" : parentPath; }
	 * 
	 * }else{ File file = new File(currFolder); File parentFile =
	 * file.getParentFile(); parentPath = parentFile.getAbsolutePath();
	 * if(parentPath != null){ mData.getFileItems().clear();
	 * //QueryData(parentFile); //QueryData(parentPath); QueryData(parentPath,
	 * true, FileFilter.MUSIC); refreshData(); currFolder =
	 * parentPath.equals("/") ? "/" : parentPath + "/"; } }
	 * 
	 * }
	 */
	@Override
	public boolean onBackPressed() {
		// Log.i(TAG,"onBackPressed " + currFolder + " " + topDir);
		/*
		 * zhaoyu 1205 if (isRoot()) {//willExit //ViewEffect.cancelToast();
		 * return false; } goBack();
		 */
		StopQueryData();
		return true;
	}

	// file operation =======================================zhaoyu
	private void SelectAll() {
		selectedAll = true;
		for (FileItemForOperation fileItem : mData.getFileItems()) {
			fileItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
		}
		refreshData();
	}

	public void finishOperation() {
		isOperating = false;
	}

	private void toggleOperatingView(boolean b) {
		isOperating = b;
	}

	private void SelectNothing() {
		selectedAll = false;
		for (FileItemForOperation fileItem : mData.getFileItems()) {
			fileItem.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
		}
		refreshData();
	}

	private boolean currFolderCanOperate(boolean justBrowser) {
		if (!mFileManager.getSdcardState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	private void addSelectedItemToApp(FilesFor filesFor) {
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

	private void showOperationProgressDialog(int titleId, int count,
			boolean reCreat) {
		if (operationProgressDialog == null || reCreat) {
			operationProgressDialog = ViewEffect.createCustProgressDialog(
					mContext, titleId, count, hideOperationListener,
					negativeListener, cancelListener);
		}
		operationProgressDialog.show();
	}

	private CustomListener hideOperationListener = new CustomListener() {
		@Override
		public void onListener() {
			backgroundOperation = true;
			setMood(R.drawable.smallicon, R.string.operating_background);
		}
	};

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

	private PendingIntent makeMoodIntent() {
		if (contentIntent == null)
			contentIntent = PendingIntent.getActivity(
					mContext,
					0,
					new Intent(mContext, FileBrowser.class).setFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(
							"com.hualu.wifistart.filebrowser.path", topDir),
					PendingIntent.FLAG_UPDATE_CURRENT);
		return contentIntent;
	}

	private CustomListener negativeListener = new CustomListener() {
		@Override
		public void onListener() {
			// pasteThreadManager.setCanceled(true);
		}
	};
	private OnCancelListener cancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			// pasteThreadManager.setCanceled(true);
		}
	};
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case RefreshData.LOAD_APK_ICON_FINISHED:
			// refreshData();
			// return;
			case FileOperationThreadManager.RENAME_FAILED:
				renameDialog.dismiss();
				handleFailed(msg);
				break;
			case FileOperationThreadManager.RENAME_SUCCEED:
				renameDialog.dismiss();
				mItemsAdapter.notifyDataSetChanged();
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
				if (mViewMode == ViewMode.SEARCHVIEW) {
					// mLoader.updateList(mSearchBox.getText().toString(),mSearchAdapter.mdata);//mSearchAdapter
					mSearchAdapter.mSearchData.getFileItems().remove(
							selectedPosition);
					mSearchAdapter.notifyDataSetChanged();
				} else
					QueryData(topDir, true, FileFilter.MUSIC);

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
			case FileOperationThreadManager.PASTE_COMPLETED:
				handleSucceed(msg);
				QueryData((topDir), true, FileFilter.MUSIC);
				break;
			default:
				break;
			}

			finishOperation();
		}
	};

	public void pasteComplete() {
		mHandler.sendEmptyMessage(FileOperationThreadManager.PASTE_COMPLETED);
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
			// ViewEffect.showToast(mContext, R.string.toast_operation_failed);
			break;
		case FileOperationThreadManager.FAILED_REASON_INVALNAME:
			// ViewEffect.showToast(mContext, R.string.toast_inval_filename);
			break;
		case FileOperationThreadManager.FAILED_REASON_FROM_FILE_NOT_EXIST:
			// ViewEffect.showToast(mContext, R.string.toast_file_not_find);
			break;
		case FileOperationThreadManager.FAILED_REASON_READ_ONLY_FILE_SYSTEM:
			// ViewEffect.showToast(mContext,
			// formatStr(R.string.toast_read_only_file_system, currName));
			break;
		case FileOperationThreadManager.FAILED_REASON_FOLDER_HAS_EXIST:
			// ViewEffect.showToast(mContext,
			// R.string.toast_rename_or_new_folder_failed_folder_exist);
			break;
		case FileOperationThreadManager.FAILED_REASON_FOLDER_LIMIT:
			// ViewEffect.showToast(mContext, R.string.toast_folder_limit);
			break;
		case FileOperationThreadManager.FAILED_REASON_SAME_FOLDER:
			// ViewEffect.showToast(mContext,
			// R.string.toast_cont_move_in_same_folder);
			break;
		case FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT:
			// ViewEffect.showToast(mContext, R.string.toast_no_space_left);
			break;
		case FileOperationThreadManager.DELETE_LOCKFILE:
			ToastBuild.toast(mContext, R.string.toast_lockfile_undelete);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		selectedPosition = info.position;
		FileItemForOperation operationItem;// =
											// mData.getFileItems().get(selectedPosition);
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

		// xuw updata 2014 12 18
		if (!mData.getFileItems().get(selectedPosition).getFileItem()
				.getFileName().contains("lockfile")) {
			menu.add(0, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
			menu.add(0, MENU_RENAME, Menu.NONE, R.string.menu_rename);
			menu.add(0, MENU_LOCK, Menu.NONE, R.string.menu_read_lock);
		} else {
			menu.add(0, MENU_UNLOCK, Menu.NONE, R.string.menu_read_unlock);
		}
		menu.add(0, MENU_READPROP, Menu.NONE, R.string.menu_read_prop);
		// if(!topDir.startsWith("smb"))
		// jh- 2013.12.13 menu.add(0, MENU_ADDTO_PLAYLIST,Menu.NONE,
		// R.string.addPlayList);
		// xuw add lockfile 2014/12/05
		// if (!mData.getFileItems().get(selectedPosition).getFileItem()
		// .getFilePath().startsWith("smb")) {

		// SharedPreferences sh = mContext.getSharedPreferences("lockedfile",
		// 0);
		// String pickpath = sh.getString(
		// mData.getFileItems().get(selectedPosition).getFileItem()
		// .getFilePath(), null);
		// if (pickpath == null) {
		// } else {
		// // menu.add(0, MENU_UNLOCK, Menu.NONE, R.string.menu_read_unlock);
		// }
		// }

		if (isOperating)
			operationItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
		else {
			SelectNothing();
			operationItem.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
		}
		refreshData();
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		if (!hasContextItemSelected) {
			FileItemForOperation fileItemForOperation = mData.getFileItems()
					.get(selectedPosition);
			fileItemForOperation
					.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
			refreshData();
		}
		hasContextItemSelected = false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		hasContextItemSelected = true;
		final FileItemForOperation fileItemForOperation;// =
														// mData.getFileItems().get(selectedPosition);
		if (mViewMode == ViewMode.SEARCHVIEW) {
			fileItemForOperation = mSearchAdapter.mSearchData.getFileItems()
					.get(selectedPosition);
		} else
			fileItemForOperation = mData.getFileItems().get(selectedPosition);

		switch (item.getItemId()) {
		/*
		 * case MENU_OPEN_AS: {
		 * openAsDialog(fileItemForOperation.getFileItem()).show(); } break;
		 * case MENU_SELECT_ALL: if (!selectedAll) { SelectAll(); } else {
		 * SelectNothing(); } toggleOperatingView(selectedAll); break;
		 */
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
							if (topDir.startsWith("smb")) {
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
							if (topDir.startsWith("smb")) {
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
		case MENU_ADDTO_PLAYLIST:

			FileItem file = fileItemForOperation.getFileItem();
			String fileName = file.getFileName();
			// String fileName =
			// file.getName().substring(0,file.getName().indexOf("."));
			Log.i(TAG, "add to playlist " + fileName);
			MusicList.insertMusic(mContext, file);// 添加音乐

			/*
			 * List<FileItemForOperation> list = mFileManager
			 * .getDataForOperation().getFileItems(); Log.i(TAG,"insertMusic " +
			 * list.size()); comfirDialog =
			 * ViewEffect.createComfirDialog(mContext,
			 * R.string.title_comfir_delete,
			 * R.string.dialog_msg_comfir_addplaylist, new CustomListener() {
			 * 
			 * @Override public void onListener() {
			 * //mFileManager.resetDataForOperation();
			 * mFileManager.addFileItem(fileItemForOperation);
			 * addSelectedItemToApp(FilesFor.UNKOWN);
			 * mFileManager.setFilesFor(FilesFor.UNKOWN);
			 * 
			 * List<FileItemForOperation> list = mFileManager
			 * .getDataForOperation().getFileItems(); Log.i(TAG,"insertMusic " +
			 * list.size()); MusicList.insertMusic(mContext,list);
			 * mFileManager.resetDataForOperation(); } }, new CustomListener() {
			 * 
			 * @Override public void onListener() { comfirDialog.dismiss(); }
			 * }); comfirDialog.show();
			 */
			break;
		case MENU_READPROP:
			FileOperationThreadManager manager = new FileOperationThreadManager();
			try {
				FilePropertyAdapter adapter = manager.readProp(mContext,
						fileItemForOperation);
				HuzAlertDialog propertyDialog = ViewEffect
						.createPropertyDialog(mContext,
								R.string.title_read_property, adapter);
				propertyDialog.show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case MENU_LOCK:
			if (mData.getFileItems().get(selectedPosition).getFileItem()
					.getFilePath().startsWith("smb")) {
				alertDialog = ViewEffect.createComfirDialog(mContext,
						R.string.menu_read_lock, R.string.menu_read_smblock,
						new CustomListener() {

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
										passwordString2, "music",null);
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
//										SharedPreferences sh = mContext
//												.getSharedPreferences(
//														"lockedfile", 0);
//										Editor editor = sh.edit();
										//editor.putString(tmp.getName(),passwordString2);
										//editor.putString(fed.getValueOfKeyCode(tmp),passwordString2);
//										editor.commit();
										fed.setPassWordtoFile(newPath,passwordString2);
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
									ListActivity.onBtnRefresh();
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
			staticfileItem=fileItemForOperation.getFileItem();
			if (fileItemForOperation.getFileItem().getFilePath()
					.startsWith("smb")) {
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
			} else {
				passwordDialog = ViewEffect.createRenameDialog(mContext,
						R.string.set_pwd_input, "", new CustomListener() {

							@Override
							public void onListener() {
								passwordDialog.dismiss();
								// 密码
								EditText et = (EditText) passwordDialog
										.findViewById(R.id.rename);
								String passwordString = et.getText().toString();
//								SharedPreferences sh = mContext
//										.getSharedPreferences("lockedfile", 0);
								FileEnDecryptManager fed=FileEnDecryptManager.getInstance();
								String pString = fed.getStringOf(new File(staticfileItem.getFilePath()),2);
								int paslength=Integer.parseInt(pString)+2;
								 pString=fed.getStringOf(new File(staticfileItem.getFilePath()),Integer.parseInt(pString)+2);
								 pString=pString.substring(0, pString.length()-2);
								pString = fed.decryptPassWord(pString);
								if (pString != null
										&& !pString.equals(passwordString)) {
									alertDialog = ViewEffect
											.createComfirDialog(
													mContext,
													R.string.menu_read_unlock,
													R.string.menu_read_passwordfail,
													new CustomListener() {
														@Override
														public void onListener() {
															alertDialog
																	.dismiss();
															passwordDialog
																	.show();
														}
													}, null);
									alertDialog.show();
									return;
								}
								fed.deletePassWord(new File(staticfileItem.getFilePath()), paslength);
								boolean flag = fed.InitEncrypt(
										fileItemForOperation.getFileItem()
												.getFilePath(), passwordString,
										"music",null);
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
											if (tmp.isFile()) {
												tmp.delete();
											}
										}
										tmp = new File(newPath);
										file.renameTo(new File(newPath));
										fileItemForOperation
												.getFileItem()
												.setFileName(
														filename.substring(
																0,
																filename.indexOf(".lockfile")));
										fileItemForOperation.getFileItem()
												.setFilePath(newPath);
									}
									ListActivity.onBtnRefresh();
									alertDialog = ViewEffect
											.createComfirDialog(
													mContext,
													R.string.menu_read_unlock,
													R.string.menu_read_unlocksucess,
													new CustomListener() {

														@Override
														public void onListener() {
															alertDialog
																	.dismiss();
														}
													}, null);
									alertDialog.show();
								} else {
									alertDialog = ViewEffect
											.createComfirDialog(
													mContext,
													R.string.menu_read_unlock,
													R.string.menu_read_unlockfail,
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

						}, new CustomListener() {

							@Override
							public void onListener() {
								passwordDialog.dismiss();
							}
						});
				passwordDialog
						.getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				passwordDialog.show();
				break;
			}
		default:
			break;
		}
		return false;
	}

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
	}

	public void onBtnDelete() {
		if (backgroundOperation) {
			ViewEffect.showToast(mContext, R.string.toast_please_waite);
			return;
		}
		if (!currFolderCanOperate(false))
			return;
		comfirDialog = ViewEffect.createComfirDialog(mContext,
				R.string.title_comfir_delete,
				R.string.dialog_msg_comfir_delete, new CustomListener() {
					@Override
					public void onListener() {
						// mFileManager.resetDataForOperation();
						// mFileManager.addFileItem(fileItemForOperation);
						addSelectedItemToApp(FilesFor.DELETE);
						mFileManager.setFilesFor(FilesFor.DELETE);
						// lrh add 1219
						if (mFileManager.getDataForOperation().getFileItems()
								.size() == 0) {
							/* liuyufa change for toastcolor 20140115 start */
							// Toast.makeText(mContext,"没有选择任何文件",
							// Toast.LENGTH_LONG).show();
							ToastBuild.toast(mContext,
									R.string.nothing_selected);
							/* liuyufa change for toastcolor 20140115 end */
							return;
						}
						// end
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
						if (list.size() == 0) {
							ListActivity.onBtnRefresh();
							return;
						}
						showOperationProgressDialog(R.string.title_deleting,
								list.size(), true);
						if (topDir.startsWith("smb")) {
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
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.i(TAG, "onPrepareOptionsMenu");
		// menu.add(1, MENU_SWITCH_MODE, Menu.NONE,
		// mViewMode == ViewMode.LISTVIEW ? R.string.menu_mode_grid :
		// R.string.menu_mode_list)
		// .setIcon(mViewMode == ViewMode.LISTVIEW ?
		// R.drawable.toolbar_mode_icon : R.drawable.toolbar_mode_list);
		/*
		 * if(mViewMode != ViewMode.SEARCHVIEW){ //menu.clear(); //menu.add(1,
		 * MENU_REFRESH, Menu.NONE,
		 * R.string.menu_refresh);//.setIcon(R.drawable.ic_menu_refresh);
		 * //menu.add(1, MENU_EXIT, Menu.NONE, R.string.menu_exit); menu.add(1,
		 * MENU_SELECT_ALL, Menu.NONE,
		 * R.string.menu_select_all);//.setIcon(R.drawable.ic_menu_refresh);
		 * //menu.add(2, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
		 * //menu.add(2, MENU_COPY, Menu.NONE, R.string.menu_copy_selected);
		 * menu.add(2, MENU_CUT, Menu.NONE, R.string.menu_cut_selected);
		 * //menu.add(1, MENU_DISPLAY, Menu.NONE, R.string.display);
		 * //menu.add(1, MENU_STATUS, Menu.NONE, R.string.status); //menu.add(1,
		 * MENU_PASTE, Menu.NONE, R.string.menu_paste);
		 * //if(!topDir.startsWith("smb")) //jh- 2013.12.13 menu.add(2,
		 * MENU_ADDTO_PLAYLIST,Menu.NONE, R.string.addPlayList);
		 * 
		 * }
		 */
		return true;
	}

	/*
	 * public boolean onCreateOptionsMenu(Menu menu) {
	 * mContext.getMenuInflater().inflate(R.menu.optmenu, menu); return true; }
	 */

	public boolean onClickPopMenu(int item) {
		switch (item) {
		case MENU_CUT:
			if (backgroundOperation) {
				ViewEffect.showToast(mContext, R.string.toast_please_waite);
				break;
			}
			mFileManager.resetDataForOperation();
			addSelectedItemToApp(FilesFor.CUT);
			mFileManager.setFilesFor(FilesFor.CUT);
			toggleOperatingView(false);
			break;
		case MENU_SELECT_ALL:
			if (!selectedAll) {
				SelectAll();
			} else {
				SelectNothing();
			}
			toggleOperatingView(selectedAll);
			break;
		}
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// /*case MENU_SWITCH_MODE:
		// toggleMode();
		// toggleViewMode();
		// return true;
		// case MENU_EXIT:
		// //case R.id.mExit:
		// Intent intent = new Intent(mContext, MusicService.class);
		// mContext.stopService(intent);
		// SysApplication.getInstance().exit();
		// break;*/
		// case MENU_COPY:
		// //case R.id.mCopy:
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
		// //case R.id.mCut:
		// if (backgroundOperation) {
		// ViewEffect.showToast(mContext, R.string.toast_please_waite);
		// break;
		// }
		// mFileManager.resetDataForOperation();
		// //mFileManager.addFileItem(fileItemForOperation);
		// addSelectedItemToApp(FilesFor.CUT);
		// mFileManager.setFilesFor(FilesFor.CUT);
		// //pastDir = currFolder;
		// toggleOperatingView(false);
		// break;
		//
		// case MENU_REFRESH:
		// //case R.id.mRefresh:
		// QueryData((topDir),true, FileFilter.MUSIC);
		// break;
		// case MENU_SELECT_ALL:
		// //case R.id.mSslectAll:
		// if (!selectedAll) {
		// SelectAll();
		// } else {
		// SelectNothing();
		// }
		// toggleOperatingView(selectedAll);
		// break;
		// case MENU_DELETE:
		// //case R.id.mDelete:
		// if (backgroundOperation) {
		// ViewEffect.showToast(mContext, R.string.toast_please_waite);
		// break;
		// }
		// if (!currFolderCanOperate(false))
		// return false;
		// comfirDialog = ViewEffect.createComfirDialog(mContext,
		// R.string.title_comfir_delete,
		// R.string.dialog_msg_comfir_delete, new CustomListener() {
		// @Override
		// public void onListener() {
		// //mFileManager.resetDataForOperation();
		// //mFileManager.addFileItem(fileItemForOperation);
		// addSelectedItemToApp(FilesFor.DELETE);
		// mFileManager.setFilesFor(FilesFor.DELETE);
		// List<FileItemForOperation> list = mFileManager
		// .getDataForOperation().getFileItems();
		// showOperationProgressDialog(
		// R.string.title_deleting, list.size(), true);
		// if(topDir.startsWith("smb")){
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
		// comfirDialog.show();
		// break;
		// case MENU_ADDTO_PLAYLIST:
		// //case R.id.mAddList:
		// comfirDialog = ViewEffect.createComfirDialog(mContext,
		// R.string.title_comfir_delete,
		// R.string.dialog_msg_comfir_addplaylist, new CustomListener() {
		// @Override
		// public void onListener() {
		// mFileManager.resetDataForOperation();
		// //mFileManager.addFileItem(fileItemForOperation);
		// addSelectedItemToApp(FilesFor.UNKOWN);
		// mFileManager.setFilesFor(FilesFor.UNKOWN);
		//
		// List<FileItemForOperation> list = mFileManager
		// .getDataForOperation().getFileItems();
		// Log.i(TAG,"insertMusic " + list.size());
		// MusicList.insertMusic(mContext,list);
		// mFileManager.resetDataForOperation();
		// }
		// }, new CustomListener() {
		// @Override
		// public void onListener() {
		// comfirDialog.dismiss();
		// }
		// });
		// comfirDialog.show();
		// break;
		// /*case MENU_DISPLAY:
		// ListActivity.popUpDisplayWindow();
		// break;
		// case MENU_STATUS:
		// ListActivity.popUpStatusWindow();*/
		// default:
		// break;
		// }
		return false;
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

		// mSearchAdapter.updatSearchData(mData);
		Log.i("SearchAdapter", "onLoadFinished " + mSearchAdapter.getCount());
		// mSearchList.setAdapter(mSearchAdapter);
		mSearchAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<FileItemForOperation>> arg0) {
		// TODO Auto-generated method stub
		// mSearchAdapter.clear();
		Log.i(TAG, "onLoaderReset ");
		mSearchAdapter.notifyDataSetChanged();
	}

}
