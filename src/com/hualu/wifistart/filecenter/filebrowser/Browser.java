package com.hualu.wifistart.filecenter.filebrowser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.hualu.wifistart.DownloadService;
import com.hualu.wifistart.FileEnDecryptManager;
import com.hualu.wifistart.MApplication;
import com.hualu.wifistart.PasteCompletionListner;
import com.hualu.wifistart.R;
import com.hualu.wifistart.HttpService.FileUtil;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.PreparedResource;
import com.hualu.wifistart.filecenter.files.FileItem;
import com.hualu.wifistart.filecenter.files.FileItemForOperation;
import com.hualu.wifistart.filecenter.files.FileItemSet;
import com.hualu.wifistart.filecenter.files.FileManager;
import com.hualu.wifistart.filecenter.files.FileManager.FileComparatorMode;
import com.hualu.wifistart.filecenter.files.FileManager.FileFilter;
import com.hualu.wifistart.filecenter.files.FileManager.FilesFor;
import com.hualu.wifistart.filecenter.files.FileManager.OnFileSetUpdated;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.files.FileOperationThreadManager.CopyOperation;
import com.hualu.wifistart.filecenter.files.FilesAdapter;
import com.hualu.wifistart.filecenter.files.RefreshData;
import com.hualu.wifistart.filecenter.files.SearchAdapter;
import com.hualu.wifistart.filecenter.utils.CustomListener;
import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.views.WPTextBox;

public abstract class Browser implements OnItemClickListener,
		OnLongClickListener, OnFileSetUpdated {// OnClickListener,
	protected static String TAG = "Browser";
	public FileManager mFileManager;
	protected LayoutInflater mInflater;
	public static RefreshData fechRefreshData;
	protected Context mContext;
	public FileItemSet mData;
	public FileItemSet mFileData;
	protected FilesAdapter mItemsAdapter;
	public SearchAdapter mSearchAdapter;
	protected boolean pickPath = false;
	public String topDir;
	public int itemClickPosition;
	protected WPTextBox mSearchBox;
	protected SearchMode mSearchMode;
	protected LinearLayout mSearchView;
	public SmbHelper smbHelper = new SmbHelper();
	public ViewMode mViewMode;
	public ViewMode mpreViewMode;
	private String keySearch;
	protected View mView;
	protected GridView mGridView;
	protected ListView mListView;
	protected ListView mSearchList;
	public String pastDir = null;
	public CopyOperation operationType = CopyOperation.APPEND2;

	private final int SUB_MENU_TXT = Menu.FIRST + 10;
	private final int SUB_MENU_AUDIO = Menu.FIRST + 11;
	private final int SUB_MENU_VIDEO = Menu.FIRST + 12;
	private final int SUB_MENU_PIC = Menu.FIRST + 13;
	private final int VCF =130;
	public static int isclick = 0;
	public static int isshow = 0;
	public static int size = 0;

	// public static LruCache<String, Bitmap> mMemoryCache;
	public enum SearchMode {
		SEARCHVIEW, NORMALVIEW
	};

	protected PreparedResource preResource;

	public View getView() {
		return mView;
	}

	public abstract boolean onPrepareOptionsMenu(Menu menu);

	public abstract void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo);

	public abstract void onContextMenuClosed(Menu menu);

	public abstract boolean onContextItemSelected(MenuItem item);

	public abstract boolean onOptionsItemSelected(MenuItem item);

	public abstract boolean onBackPressed();

	public abstract void QueryData();

	static abstract interface IAppLoader {
		public abstract void updateList(String paramString, FileItemSet data);// SearchAdapter
																				// adapter
	}

	public Handler mHandler;
	public ProgressDialog mLockProgressDialog;
	public final int LOCK_START=400;
	public final int LOCK_INCREACE=500;
	public final int LOCK_END=600;

	protected Browser(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		preResource = new PreparedResource(context);
		mData = new FileItemSet();
		mFileData = new FileItemSet();
		mFileManager = new FileManager(context, mData);
		mItemsAdapter = new FilesAdapter(mContext, mData);
		mSearchAdapter = new SearchAdapter(mContext, mData);
		
		mHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOCK_START :
			  	   mLockProgressDialog = new ProgressDialog(mContext);
						mLockProgressDialog.setTitle(R.string.menu_read_unlock);
						mLockProgressDialog.setMessage(mContext.getString(R.string.unlocking));
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
			     case 0:
			    	 AfterUnlock(statictype, staticfileItem);
			    	 break;
			     case 1:
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
			    	 break;
					default:
						break;
				}
			}
		};
		/* liuyufa add for picture 20140313 */
		/*
		 * int maxMemory = (int) Runtime.getRuntime().maxMemory(); int
		 * mCacheSize = maxMemory/8; mMemoryCache = new LruCache<String,
		 * Bitmap>(mCacheSize){
		 * 
		 * @Override protected int sizeOf(String key, Bitmap value) { return
		 * value.getRowBytes() * value.getHeight(); } };
		 */
		/* liuyufa add for picture 20140313 */
	}

	public void SetFileComparator(FileComparatorMode mode) {
		switch (mode) {
		case ComparatorByName:
			if (topDir.startsWith("smb"))
				FileManager.mSmbComparator = mFileManager.smb_comp_name;
			else
				FileManager.mComparator = mFileManager.comp_name;
			break;
		case ComparatorBySize:
			if (topDir.startsWith("smb"))
				FileManager.mSmbComparator = mFileManager.smb_comp_size;
			else
				FileManager.mComparator = mFileManager.comp_size;
			break;
		case ComparatorByTime:
			if (topDir.startsWith("smb"))
				FileManager.mSmbComparator = mFileManager.smb_comp_update;
			else
				FileManager.mComparator = mFileManager.comp_update;
			break;
		}
	}

	protected void QueryData(File preFile, boolean clear, FileFilter filter) {
		if (clear) {
			mData.clear();
		}
		mFileManager.setOnFileSetUpdated(this);
		mFileManager.query(preFile.getAbsolutePath(), filter);
	}

	protected void QueryData(String path, boolean clear, FileFilter filter) {
		if (clear) {
			mData.clear();
		}
		if (filter == FileFilter.SEARCH || filter == FileFilter.ALLSEARCH) {
			mFileData.clear();
			mFileManager.setFileBrowser(mFileData);
		}
		mFileManager.setOnFileSetUpdated(this);
		mFileManager.query(path, filter);
	}

	public void StopQueryData() {
		mFileManager.StopQueryData();
	}

	public void StartQueryData() {
		mFileManager.SartQueryData();
	}

	public boolean StopQueryStatus() {
		return mFileManager.StopQueryStatus();
	}

	/**
	 * refresh adapter
	 */
	public void refreshData() {
		if (mViewMode != ViewMode.SEARCHVIEW && mItemsAdapter != null) {
			mItemsAdapter.notifyDataSetChanged();
		} else if (mSearchAdapter != null)
			mSearchAdapter.notifyDataSetChanged();
	}

	protected void clickFileItem(FileItemForOperation fileItem) {
		if (pickPath) {
			Intent intent = new Intent();
			Uri uri = getContentUri(fileItem.getFileItem());
			intent.setData(uri);
			Log.i(TAG, "uri:" + uri);
			((Activity) mContext).setResult(Activity.RESULT_OK, intent);
			((Activity) mContext).finish();
		} else {
			doOpenFile(null, fileItem.getFileItem());
		}
	}

	private HuzAlertDialog passwordDialog;
	private HuzAlertDialog alertDialog;

	protected void clickFileItem(String type, FileItemForOperation fileItem) {
		doOpenFile(type, fileItem.getFileItem());
	}

	/**
	 * open file depending on file type
	 */
	static FileItem staticfileItem;
	static String statictype;
	protected void doOpenFile(String type, FileItem fileItem) {
		final FileItem fileItem2 = fileItem;
		staticfileItem=fileItem;
		String url = "";
		Uri uri = null;
		String fileDir = fileItem.getFilePath();
		String filename = fileItem.getFileName();
		String fileDir2 = fileItem.getFilePath();
		if (type == null) {
			// type =
			// MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileItem.getExtraName().toLowerCase(Locale.getDefault()));
			type = preResource.getMineType(fileItem.getExtraName().toLowerCase(
					Locale.getDefault()));// zhaoyu0401
		}
		if (fileItem.getFileName().contains("lockfile")) {
			if (fileItem.getFilePath().startsWith("smb")) {
				alertDialog = ViewEffect.createComfirDialog(mContext,
						R.string.menu_read_unlock,
						R.string.menu_read_smbunlock, new CustomListener() {

							@Override
							public void onListener() {
								alertDialog.dismiss();
							}
						}, null);
				alertDialog.show();
				return;
			}else {
			if (type == null) {
				type = fileItem.getExtraName();
			}
			final String tString = type;
			statictype=type;
			passwordDialog = ViewEffect.createRenameDialog(mContext,
					R.string.set_pwd_input, "", new CustomListener() {

						@Override
						public void onListener() {
							passwordDialog.dismiss();
							// 密码
							EditText et = (EditText) passwordDialog
									.findViewById(R.id.rename);
							String passwordString = et.getText().toString();
//							SharedPreferences sh = mContext.getSharedPreferences("lockedfile", 0);
							FileEnDecryptManager fed = FileEnDecryptManager.getInstance();
							File file1 = new File(fileItem2.getFilePath());
							Log.i("lllllllllllllllllllllllll", String.valueOf(file1.length()));
							String pString = fed.getStringOf(file1,2);
							 Log.i("加密密码长度", pString);
							 int paslength=Integer.parseInt(pString)+2;
							 pString=fed.getStringOf(file1,Integer.parseInt(pString)+2);
							 Log.i("加密密码长度3", pString);
							 pString=pString.substring(0, pString.length()-2);
							pString = fed.decryptPassWord(pString);
							Log.i("加密密码长度2", pString);
							if (pString != null&& !pString.equals(passwordString)) {
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
							fed.deletePassWord(file1, paslength);
							boolean flag;
							if (tString.contains("txt")) {
								new TxtUnLockThread(fileItem2
										.getFilePath(),passwordString,mHandler).start();
									ViewEffect.showToast(mContext, R.string.backgroundunlocking);
							}else {
							flag = fed.InitEncrypt(
									fileItem2.getFilePath(), passwordString,
									tString,null);
							if (flag) {
								AfterUnlock(tString, fileItem2);
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
			return;
		}
		}
		Log.i(TAG, "doOpenFile " + type);
		if (type == null) {
//			type = fileItem.getExtraName();
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setDataAndType(uri, type);
//			((Activity)mContext).startActivity(intent);
//			Uri uri3=Uri.parse(fileItem.getFilePath().substring(0, fileItem.getFilePath().length()-fileItem.getFileName().length()));
//			Uri uri3=Uri.parse(fileItem.getFilePath());
//			Log.i("vcf+++++++++================", fileItem.getFilePath().substring(0, fileItem.getFilePath().length()-fileItem.getFileName().length()-1));
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri3, "text/x-vcard");
//			Intent intent= new Intent(Intent.ACTION_VIEW, null);
//	         intent.setType("text/x-vcard");
//            ((Activity)mContext).startActivityForResult(intent, VCF);

			openAsDialog(fileItem).show();
			return;
		}
		Log.i(TAG, "doOpenFile " + type + " " + fileDir);

		if (fileDir.startsWith("smb") && (type.startsWith("image"))) {
			String tempPath = MApplication.CACHE_PICTURE_PATH
					+ fileItem.getFileName();
			File tmpFile = new File(tempPath);
			if (!tmpFile.exists())
				smbHelper.smbGet(fileDir, MApplication.CACHE_PICTURE_PATH);
			fileDir = tempPath;
			uri = Uri.parse("file://" + tempPath);
			uri = Uri.parse(uri.toString().replace(" ", "%20"));
		}

		Intent intent;// = new Intent(Intent.ACTION_VIEW); //zhaoyu07401
		if (type.startsWith("video")) {
			// intent = new Intent(mContext,VideoViewDemo.class);
			intent = new Intent(Intent.ACTION_VIEW);
			// intent.setDataAndType(Uri.parse(fileItem.getFilePath().toString().replace(" ",
			// "%20")),
			// "video/mp4");
			// uri=Uri.parse(fileDir2);
			// intent.setDataAndType(uri, type);
		} else {
			intent = new Intent(Intent.ACTION_VIEW);
		}
		Log.e("2014/11/8  ++++++++++++++++++++++++++filetype++++++++++++++",
				type);
		Log.i(TAG, "doOpenFile " + fileDir);
		if (fileDir.startsWith("smb") && (type.startsWith("image"))) {
			fileDir = fileDir.replaceFirst("smb", "http");
			fileDir = fileDir.replaceFirst("airdisk:123456@", "");
			fileDir = fileDir.replaceFirst("Hualu:123456@", "");
			fileDir = fileDir.replaceFirst("/airdisk/", "/");
			fileDir = fileDir.replaceFirst("/Hualu/", "/");
			fileDir = fileDir.replaceFirst("admin:admin@", "");
			fileDir = fileDir.replaceFirst("/Share/", "/");
			Uri tmp = Uri.parse(fileDir);
			uri = Uri.parse(tmp.toString().replace(" ", "%20"));
		}
		if ((fileItem.getFilePath().startsWith("smb") && type
				.startsWith("text"))
				|| (fileItem.getFilePath().startsWith("smb") && type
						.startsWith("application"))) {
			// File [] tempFiles = new
			// File(Singleton.SMB_DOWNLOAD_TEMP).listFiles();
			String tempPath = Singleton.SMB_DOWNLOAD_TEMP + "/"
					+ fileItem.getFileName();
			smbHelper.smbGet(fileItem.getFilePath(),
					Singleton.SMB_DOWNLOAD_TEMP);
			uri = Uri.parse("file://" + tempPath);
			uri = Uri.parse(uri.toString().replace(" ", "%20"));
		} else {
			if ((fileItem.getFilePath().startsWith("smb") && type
					.startsWith("audio"))
					|| (fileItem.getFilePath().startsWith("smb") && type
							.startsWith("video"))) {
				String ipVal = FileUtil.ip;
				int portVal = FileUtil.port;
				String path = fileItem.getFilePath();
				String httpReq = "http://" + ipVal + ":" + portVal + "/smb=";
				Log.i(TAG, "" + FileUtil.ip + ":" + FileUtil.port + " " + path);
				path = path.substring(6);
				// path="Hualu"+path.substring(14);
				try {
					path = URLEncoder.encode(path, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				url = httpReq + path;
				// url="http://127.0.0.1:59777/smb%2F"+path;
				fileDir = url;// zhaoyu0401
				Log.i(TAG + "=================2014/11/08=====================",
						"url: " + url);
				Uri tmp = Uri.parse(url);
				uri = Uri.parse(tmp.toString().replace(" ", "%20"));
			} else {
				if (fileDir.startsWith("smb") && (type.startsWith("image")))
					;
				else {
					uri = Uri.parse("file://" + fileDir);
					if (type.startsWith("image")) {
						uri = Uri.parse(uri.toString().replace(" ", "%20"));
					}
				}
			}
		}
		if (type != null) {
			Log.i(TAG, "doOpenFile " + uri + " " + type);
			if (type.startsWith("video")) {
				intent.putExtra("path", fileDir);
				intent.putExtra("filename", filename);
				intent.putExtra("path2", fileDir2);
				// 2014/11/08
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				// intent.putExtra("bufferpercentage", 1024);
				intent.setDataAndType(uri, "video/*");
			} else {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				// intent.putExtra("bufferpercentage", 1024);

				// 2014/12/02
				// intent.setDataAndType(uri,"video/*");
				intent.setDataAndType(uri, type);
			}
			if (filename.contains("samsung") && topDir.startsWith("smb")) {
				// 弹出提示框
				final String src = fileItem.getFilePath();
				final String todirname = fileItem.getFileName();
				Dialog dialog = new HuzAlertDialog.Builder(mContext)
						.setTitle(R.string.title_comfir_delete)
						.setMessage(
								(mContext.getResources()
										.getString(R.string.video_tips)))
						.setPositiveButton(R.string.video_download,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface paramDialogInterface,
											int paramInt) {
										// 将文件下载到本地文件夹
										File file = new File(Environment
												.getExternalStorageDirectory()
												+ "/DCIM/Camera/" + todirname);
										if (file.exists()) {
											// 存在同名文件
											paramDialogInterface.dismiss();
											Dialog dialog2 = new HuzAlertDialog.Builder(
													mContext)
													.setTitle(
															R.string.title_comfir_delete)
													.setMessage(
															(mContext
																	.getResources()
																	.getString(R.string.video_tips2)))
													.setPositiveButton(
															R.string.video_download,
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface paramDialogInterface,
																		int paramInt) {
																	// 将文件下载到本地文件夹

																	Intent intent = new Intent(
																			mContext,
																			DownloadService.class);
																	intent.putExtra(
																			"srcDir",
																			src);
																	intent.putExtra(
																			"toDir",
																			Environment
																					.getExternalStorageDirectory()
																					+ "/DCIM/Camera/");
																	intent.putExtra(
																			"CopyOperation",
																			"copy");
																	intent.putExtra(
																			"isCut",
																			false);
																	mContext.startService(intent);

																	paramDialogInterface
																			.dismiss();
																}
															})
													.setNegativeButton(
															R.string.set_cancel,
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface paramDialogInterface,
																		int paramInt) {
																	paramDialogInterface
																			.dismiss();
																}
															}).create();
											dialog2.show();

										} else {

											Intent intent = new Intent(
													mContext,
													DownloadService.class);
											intent.putExtra("srcDir", src);
											intent.putExtra(
													"toDir",
													Environment
															.getExternalStorageDirectory()
															+ "/DCIM/Camera/");
											intent.putExtra("CopyOperation",
													"copy");
											intent.putExtra("isCut", false);
											mContext.startService(intent);

											paramDialogInterface.dismiss();

										}
									}
								})
						.setNegativeButton(R.string.set_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface paramDialogInterface,
											int paramInt) {
										paramDialogInterface.dismiss();
									}
								}).create();
				dialog.show();

			} else {

				try {
					if (topDir.startsWith("smb"))
						((Activity) mContext).startActivityForResult(intent,
								100);
					else
						((Activity) mContext).startActivityForResult(intent,
								101);
				} catch (ActivityNotFoundException e) {
					openAsDialog(fileItem).show();
				}

			}
		}

	}
private void AfterUnlock(String type,FileItem fileItem){
	final String tString=type;
	final FileItem fileItem2=fileItem;
	File file = new File(fileItem2.getFilePath());
	if (file.canWrite()) {
		String filename = fileItem2.getFileName();

		String[] pathAndName = Helper.reName(
				fileItem2,
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
		fileItem2.setFileName(filename.substring(0,
				filename.indexOf(".lockfile")));
		fileItem2.setFilePath(newPath);
	}
	//20150107
	QueryData();
	refreshData();
	alertDialog = ViewEffect.createComfirDialog(
			mContext, R.string.menu_read_unlock,
			R.string.menu_read_unlocksucess,
			new CustomListener() {

				@Override
				public void onListener() {
					alertDialog.dismiss();
					doOpenFile(tString, fileItem2);
				}
			}, null);
	alertDialog.show();

}
	private void openAs(int id, FileItem fileItem) {
		String type = null;
		switch (id) {
		case SUB_MENU_TXT:
			type = "text/plain";
			break;
		case SUB_MENU_AUDIO:
			type = "audio/*";
			break;
		case SUB_MENU_VIDEO:
			type = "video/*";
			break;
		case SUB_MENU_PIC:
			type = "image/*";
			break;
		default:
			break;
		}
		doOpenFile(type, fileItem);
	}

	protected Dialog openAsDialog(final FileItem fileItem) {
		return new HuzAlertDialog.Builder(mContext)
				.setTitle(R.string.menu_open_as)
				.setItems(R.array.open_as_items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								openAs(Menu.FIRST + 10 + which, fileItem);
							}
						}).create();
	}

	/*
	 * protected void toggleMode() { if (mViewMode == ViewMode.LISTVIEW) {
	 * mViewMode = ViewMode.GRIDVIEW; }else { mViewMode = ViewMode.LISTVIEW; } }
	 */
	public void toggleSearchMode(String key) {
		Log.i(TAG, "toggleSearchMode " + mViewMode);
		keySearch = key;
		if (mViewMode != ViewMode.SEARCHVIEW) {
			mpreViewMode = mViewMode;
			mViewMode = ViewMode.SEARCHVIEW;
		} else {
			StopQueryData();
			mViewMode = mpreViewMode;
			mpreViewMode = ViewMode.SEARCHVIEW;
		}
	}

	/**
	 * switch viewmode
	 */
	public void toggleViewMode() {
		Log.i(TAG, "" + mViewMode);
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

			if (keySearch.equals("文件名")) {
				mSearchAdapter.updatSearchData(mData);
				mSearchList.setAdapter(mSearchAdapter);
				mSearchBox.setText(null);
			} else {
				FileItemSet itemSet = new FileItemSet();
				List<FileItemForOperation> items = new ArrayList<FileItemForOperation>();
				List<FileItemForOperation> itemOrg = mData.getFileItems();
				for (int i = 0; i < mData.getFileItems().size(); i++) {
					FileItemForOperation item = itemOrg.get(i);

					if (item.getFileItem().getExtraName()
							.toUpperCase(Locale.getDefault())
							.endsWith(keySearch))
						items.add(item);// localBaseListItem);
				}
				itemSet.setFileItems(items);
				mSearchAdapter.updatSearchData(itemSet);
				mSearchList.setAdapter(mSearchAdapter);
				mSearchBox.setText(null);
			}
		default:
			break;
		}
	}

	protected Uri getContentUri(FileItem item) {
		Uri uri = null;
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				item.getExtraName().toLowerCase(Locale.getDefault()));
		ContentResolver cr = mContext.getContentResolver();
		if (type != null) {
			if (type.startsWith("image/")) {
				final String[] BUCKET_PROJECTION_IMAGES = new String[] { Images.ImageColumns._ID };
				final String where = Images.ImageColumns.DATA + " = '"
						+ item.getFilePath() + "'";
				final Cursor cursor = cr.query(
						Images.Media.EXTERNAL_CONTENT_URI,
						BUCKET_PROJECTION_IMAGES, where, null, null);
				if (null != cursor && cursor.moveToFirst()) {
					int mediaId = cursor.getInt(0);
					uri = ContentUris.withAppendedId(
							Images.Media.EXTERNAL_CONTENT_URI, mediaId);
				}
			} else if (type.startsWith("video/")) {
				final String[] BUCKET_PROJECTION_VIDEO = new String[] { Video.VideoColumns._ID };
				final String where = Video.VideoColumns.DATA + " = '"
						+ item.getFilePath() + "'";
				final Cursor cursor = cr.query(
						Video.Media.EXTERNAL_CONTENT_URI,
						BUCKET_PROJECTION_VIDEO, where, null, null);
				if (null != cursor && cursor.moveToFirst()) {
					int mediaId = cursor.getInt(0);
					uri = ContentUris.withAppendedId(
							Video.Media.EXTERNAL_CONTENT_URI, mediaId);
				}
			} else if (type.startsWith("audio/")) {
				final String[] BUCKET_PROJECTION_AUDIO = new String[] { Audio.AudioColumns._ID };
				final String where = Audio.AudioColumns.DATA + " = '"
						+ item.getFilePath() + "'";
				final Cursor cursor = cr.query(
						Audio.Media.EXTERNAL_CONTENT_URI,
						BUCKET_PROJECTION_AUDIO, where, null, null);
				if (null != cursor && cursor.moveToFirst()) {
					int mediaId = cursor.getInt(0);
					uri = ContentUris.withAppendedId(
							Audio.Media.EXTERNAL_CONTENT_URI, mediaId);
				}
			}
		}

		if (uri == null) {
			File file = new File(item.getFilePath());
			uri = Uri.fromFile(file);
		}
		return uri;
	}

	protected String formatStr(int resId, String str) {
		String res = mContext.getText(resId).toString();
		return String.format(res, str);
	}

	public void onBtnPaste(String dir) {
		pastDir = dir;
		// pastBrowser = browser;
		// if (!currFolderCanOperate(false)) {
		// return;
		// }
		/*
		 * if (!mFileManager.canOperation()) { ViewEffect.showToast(mContext,
		 * R.string.toast_no_file_to_paste); return; }
		 */

		List<FileItemForOperation> list = mFileManager.getDataForOperation()
				.getFileItems();
		// showOperationProgressDialog(R.string.title_copying,
		// list.size(),true);
		Log.i(TAG, "past files " + list.size() + " ");

		if (list.size() > 0) {
			/* liuyufa add for pastetoast 20140117 */
			size = list.size();
			/* liuyufa add for pastetoast 20140117 */
			for (int i = 0; i < list.size(); i++) {
				FileItem item = list.get(i).getFileItem();
				if (pastDir.startsWith(item.getFilePath())) {
					continue;// responseHandler.sendMessage(responseMsg(PASTE_FAILED,
								// FAILED_REASON_PASTE_NOT_ALLOWED));
				}
				String FileName = item.getFileName();
				String newFilePath = dir + FileName;
				PasteCompletionListner.mFilesPaste++;

				if (newFilePath.startsWith("smb")) {
					try {
						SmbFile smbFile = new SmbFile(newFilePath);
						if (smbFile.exists()) {
							showChooseOperationDialog(item.getFilePath(), dir);
							continue;
						}

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SmbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					File newFile = new File(newFilePath);
					if (newFile.exists()) {
						showChooseOperationDialog(item.getFilePath(), dir);
						continue;
					}
				}

				Intent intent = new Intent(mContext, DownloadService.class);
				intent.putExtra("srcDir", item.getFilePath());
				intent.putExtra("toDir", dir);
				Log.i(TAG, "" + operationType);
				if (operationType == CopyOperation.COVER) // COVER,JUMP,APPEND2,CANCEL,UNKOWN
					intent.putExtra("CopyOperation", "COVER");
				else
					intent.putExtra("CopyOperation", "APPEND2");

				if (mFileManager.getFilesFor() == FilesFor.CUT)
					intent.putExtra("isCut", true);
				else
					intent.putExtra("isCut", false);
				mContext.startService(intent);
				ViewEffect.showToastLongTime(mContext,
						formatStr(R.string.intent_to_paste, "" + list.size()));
			}
			if (1 == isclick && 0 == isshow) {
				isshow = 1;
				ViewEffect.showToastLongTime(mContext,
						formatStr(R.string.intent_to_paste, "" + list.size()));
			}
			/* liuyufa add for toast 20140117 */
		}
		/* liuyufa add for toast 20140117 */
		isclick = 0;
		isshow = 0;
		/* liuyufa add for toast 20140117 */
		mFileManager.resetDataForOperation();
	}

	/**
	 * 出现同名文件时和用户交互的对话框
	 */

	HuzAlertDialog chooseOperationDialog = null;
	private List<String> sameSrcDirs = new ArrayList<String>();
	private List<String> sameDirDirs = new ArrayList<String>();

	protected void showChooseOperationDialog(final String srcDir,
			final String dstDir) {
		if (chooseOperationDialog == null) {
			sameSrcDirs.clear();
			sameDirDirs.clear();
			sameSrcDirs.add(srcDir);
			sameDirDirs.add(dstDir);
			chooseOperationDialog = ViewEffect.createTheDialog(mContext,
					(R.string.title_has_same_file), new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							// operationProgressDialog.dismiss();
							for (int i = 0; i < sameSrcDirs.size(); i++) {
								Intent intent = new Intent(
										"com.hualu.wifishare.paste.complete");
								mContext.sendBroadcast(intent);
								operationType = CopyOperation.JUMP;
							}
						}
					}, new android.widget.RadioGroup.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							switch (checkedId) {
							case R.id.cover:
							/*
							 * try {
							 * pasteThreadManager.beginPaste(CopyOperation.
							 * COVER); } catch (Exception e) { // TODO
							 * Auto-generated catch block e.printStackTrace(); }
							 */{
								operationType = CopyOperation.COVER;
								for (int i = 0; i < sameSrcDirs.size(); i++) {
									Intent intent = new Intent(mContext,
											DownloadService.class);
									intent.putExtra("srcDir",
											sameSrcDirs.get(i));
									intent.putExtra("toDir", sameDirDirs.get(i));

									intent.putExtra("CopyOperation", "COVER");

									if (mFileManager.getFilesFor() == FilesFor.CUT)
										intent.putExtra("isCut", true);
									else
										intent.putExtra("isCut", false);
									mContext.startService(intent);
									/* liuyufa add for pastetoast 20140117 */
									isclick = 1;
									if (1 == isclick && 0 == isshow) {
										isshow = 1;
										ViewEffect
												.showToastLongTime(
														mContext,
														formatStr(
																R.string.intent_to_paste,
																"" + size));
									}
									/* liuyufa add for pastetoast 20140117 */
								}
							}
								// Log.i(TAG,"showChooseOperationDialog COVER CHOOSE "
								// + operationType);
								break;
							case R.id.jump:
								/*
								 * try {
								 * pasteThreadManager.beginPaste(CopyOperation
								 * .JUMP); } catch (Exception e) { // TODO
								 * Auto-generated catch block
								 * e.printStackTrace(); }
								 */
								for (int i = 0; i < sameSrcDirs.size(); i++) {
									Intent intent = new Intent(
											"com.hualu.wifishare.paste.complete");
									mContext.sendBroadcast(intent);
									operationType = CopyOperation.JUMP;
								}
								break;
							case R.id.append2:
								/*
								 * try { pasteThreadManager
								 * .beginPaste(CopyOperation.APPEND2); } catch
								 * (Exception e) { // TODO Auto-generated catch
								 * block e.printStackTrace(); }
								 */
								/* liuyufa add for pastetoast 20140117 */
								isclick = 1;
								if (1 == isclick && 0 == isshow) {
									isshow = 1;
									ViewEffect.showToastLongTime(
											mContext,
											formatStr(R.string.intent_to_paste,
													"" + size));
								}
								/* liuyufa add for pastetoast 20140117 */
								for (int i = 0; i < sameSrcDirs.size(); i++) {
									operationType = CopyOperation.APPEND2;
									Intent intent = new Intent(mContext,
											DownloadService.class);
									intent.putExtra("srcDir",
											sameSrcDirs.get(i));
									intent.putExtra("toDir", sameDirDirs.get(i));
									Log.i(TAG, "" + operationType);
									intent.putExtra("CopyOperation", "APPEND2");

									if (mFileManager.getFilesFor() == FilesFor.CUT)
										intent.putExtra("isCut", true);
									else
										intent.putExtra("isCut", false);
									mContext.startService(intent);
								}
								// Log.i(TAG,"showChooseOperationDialog APPEND2 CHOOSE "
								// + operationType);
								break;
							/*
							 * case R.id.cancel:
							 * //ViewEffect.showToast(mContext, //
							 * R.string.toast_operation_canceled); { Intent
							 * intent = new
							 * Intent("com.hualu.wifishare.paste.complete");
							 * mContext.sendBroadcast(intent); operationType =
							 * CopyOperation.CANCEL;
							 * //operationProgressDialog.dismiss(); } break;
							 */
							default:
								break;
							}
							// Log.i(TAG,"dissmiss " + chooseOperationDialog);
							chooseOperationDialog.dismiss();
							chooseOperationDialog = null;
						}

					}, new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (operationType == CopyOperation.COVER
									|| operationType == CopyOperation.APPEND2) {
								for (int i = 0; i < sameSrcDirs.size(); i++) {
									Intent intent = new Intent(mContext,
											DownloadService.class);
									intent.putExtra("srcDir",
											sameSrcDirs.get(i));
									intent.putExtra("toDir", sameDirDirs.get(i));
									Log.i(TAG, "" + operationType);
									if (operationType == CopyOperation.COVER)
										intent.putExtra("CopyOperation",
												"COVER");
									else
										intent.putExtra("CopyOperation",
												"APPEND2");

									if (mFileManager.getFilesFor() == FilesFor.CUT)
										intent.putExtra("isCut", true);
									else
										intent.putExtra("isCut", false);
									mContext.startService(intent);
								}
							} else {
								for (int i = 0; i < sameSrcDirs.size(); i++) {
									Intent intent = new Intent(
											"com.hualu.wifishare.paste.complete");
									mContext.sendBroadcast(intent);
								}
							}
							// Log.i(TAG,"dissmiss " + chooseOperationDialog);
							chooseOperationDialog.dismiss();
							chooseOperationDialog = null;
							// pasteThreadManager.setDoitAsSame(isChecked);
						}
					});
			chooseOperationDialog.show();
		} else {
			sameSrcDirs.add(srcDir);
			sameDirDirs.add(dstDir);
		}
	}
	
	class TxtUnLockThread extends Thread{
		String filepath;
		String passwordString;
		Handler mhandler;
		public TxtUnLockThread(String srcfilepath,String password,Handler handler) {
			filepath=srcfilepath;
			passwordString=password;
			mhandler=handler;
		}
		@Override
		public void run() {
			FileEnDecryptManager fed = FileEnDecryptManager
					.getInstance();
			boolean flag = fed.InitEncrypt(
					filepath,
					passwordString, "txt",null);
			if (flag) {
					Message msg=Message.obtain();
					msg.what=0;
					mhandler.sendMessage(msg);
			}else {
				Message msg=Message.obtain();
				msg.what=1;
				mhandler.sendMessage(msg);
			}
		
		}
	}
	/* liuyufa add for picture 20140313 */
	/*
	 * public static void addBitmapToMemoryCache(String key, Bitmap bitmap) { if
	 * (getBitmapFromMemCache(key) == null) { if(mMemoryCache!=null){
	 * mMemoryCache.put(key, bitmap); } } } public static Bitmap
	 * getBitmapFromMemCache(String key) { if(mMemoryCache!=null){ return
	 * mMemoryCache.get(key); } else{ return null; } }
	 * 
	 * public static void clearCache() { if (mMemoryCache != null) { if
	 * (mMemoryCache.size() > 0) { mMemoryCache.evictAll(); } mMemoryCache =
	 * null; } }
	 */
	/* liuyufa add for picture 20140313 */
}
