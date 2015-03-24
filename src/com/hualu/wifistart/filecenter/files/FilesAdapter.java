package com.hualu.wifistart.filecenter.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.hualu.wifistart.FileActivity;
import com.hualu.wifistart.FileAdapter;
import com.hualu.wifistart.MApplication;
import com.hualu.wifistart.filecenter.files.FileManager.ViewMode;
import com.hualu.wifistart.filecenter.utils.ComputeSample;
import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.ListActivity;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FilesAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public FileItemSet mdata;
	private Context mContext;
	ViewHolder holder = null;
	public ViewMode mViewMode;
	/* liuyufa add for picture 20140313 */
	public static ArrayList<AsyncLoadVideoImage> Asynclist;
	public static ArrayList<FileItem> filelist;
	public AsyncLoadVideoImage async;
	public boolean isDone = true;
	/* liuyufa add for picture 20140313 */
	private SmbHelper smbHelper = new SmbHelper();// zhaoyu 1205

	private ExecutorService loadingpictureExecutorService = Executors
			.newCachedThreadPool();

	public void setViewMode(ViewMode mode) {
		mViewMode = mode;
	}

	public FilesAdapter(Context context, FileItemSet data) {
		super();
		this.mContext = context;
		this.mdata = data;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return mdata.getFileItems().size();
	}

	@Override
	public Object getItem(int position) {
		return mdata.getFileItems().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mViewMode != FileManager.ViewMode.GRIDVIEW) {
			return getListViewItem(position, convertView);
		} else {
			// return getListViewItem(position, convertView); //wdx 1130
			return getGridViewItem(position, convertView); // lrh 1210
		}
	}

	private View getListViewItem(int position, View convertView) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_view_item_file, null);
			holder.img_head = (ImageView) convertView
					.findViewById(R.id.paraList_imgHead);
			holder.title = (TextView) convertView
					.findViewById(R.id.paraList_title);
			// lrh add 2013.12.12
			holder.type = (TextView) convertView
					.findViewById(R.id.paraList_type);
			// end
			// holder.time =
			// (TextView)convertView.findViewById(R.id.paraList_updatetime);
			holder.info = (TextView) convertView
					.findViewById(R.id.paraList_size);
			holder.checked = (CheckBox) convertView
					.findViewById(R.id.checkstatus);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final FileItemForOperation file = mdata.getFileItems().get(position);

		holder.checked
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					file.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
				} else {
					file.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
				}
			}
			
		});
		
		FileItem fileItem = file.getFileItem();
//		SharedPreferences sh = mContext.getSharedPreferences("lockedfile",
//				0);
//		String pickpath = sh.getString(
//				fileItem.getFilePath(), null);
		//2014/12/11  xuw 加锁状态 图标
		
			
		if (fileItem.getFileName().contains("lockfile")) {
//			holder.checked
//			.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView,
//						boolean isChecked) {
//					// TODO Auto-generated method stub
//					if (isChecked) {
//						file.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
//					} else {
//						file.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
//					}
//				}
//				
//			});
			fileItem.setIconId(R.drawable.lock);
			//2014/12/16 加密文件不可选
//			holder.checked.setClickable(false);
//			holder.checked.setChecked(false);
//			holder.checked.setActivated(false);
		}
		setImageView(holder.img_head, fileItem);
		String displayName = fileItem.getFileName();
		if (displayName.contains("lockfile")) {
			displayName=displayName.substring(0,displayName.lastIndexOf(".")-9);
		}
		displayName = checkTitle(displayName);
		holder.title.setText(displayName);
		holder.title.setPadding(10, 0, 0, 0);
		holder.type.setText(fileItem.getExtraName());

		holder.info.setText(Helper.formatFromSize(fileItem.getFileSize()));
		// holder.time.setText(Helper.formatFromTime(fileItem.getModifyData()));
		// holder.time.setText((fileItem.getModifyData()));
		switch (file.getSelectState()) {
		case FileItemForOperation.SELECT_STATE_CUT:
			// holder.title.setTextAppearance(mContext,
			// R.style.tvInListViewCut);
			holder.checked.setChecked(true);
			break;
		case FileItemForOperation.SELECT_STATE_NOR:
			// holder.title.setTextAppearance(mContext, R.style.tvInListView);
			holder.checked.setChecked(false);
			break;
		case FileItemForOperation.SELECT_STATE_SEL:
			// holder.title.setTextAppearance(mContext,
			// R.style.tvInListViewSelected);
			holder.checked.setChecked(true);
			break;
		default:
			break;
		}
		return convertView;
	}

	// lrh add 20131212
	public String checkTitle(String title) {
		if (title.contains("."))
			title = title.substring(0, title.lastIndexOf("."));
		if (title.length() > 28) {
			title = title.substring(0, 26) + "...";
		}
		return title;
	}

	// end
	private View getGridViewItem(int position, View convertView) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.grid_view_item, null);
			holder.img_head = (ImageView) convertView
					.findViewById(R.id.ivOfGVItem);
			holder.title = (TextView) convertView.findViewById(R.id.tvOfGVItem);
			holder.checked = (CheckBox) convertView
					.findViewById(R.id.checkstatus);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FileItemForOperation file = mdata.getFileItems().get(position);

		holder.checked
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							file.setSelectState(FileItemForOperation.SELECT_STATE_SEL);
						} else {
							file.setSelectState(FileItemForOperation.SELECT_STATE_NOR);
						}
					}

				});

		FileItem fileItem = file.getFileItem();
		setImageView(holder.img_head, fileItem);
		String displayName = fileItem.getFileName();

		displayName = checkTitle(displayName);
		holder.title.setText(displayName);
		switch (file.getSelectState()) {
		case FileItemForOperation.SELECT_STATE_CUT:
			// holder.title.setTextAppearance(mContext,
			// R.style.tvInGridViewCut);
			holder.checked.setChecked(true);
			break;
		case FileItemForOperation.SELECT_STATE_NOR:
			// holder.title.setTextAppearance(mContext, R.style.tvInGridView);
			holder.checked.setChecked(false);
			break;
		case FileItemForOperation.SELECT_STATE_SEL:
			// holder.title.setTextAppearance(mContext,
			// R.style.tvInGridViewSelected);
			holder.checked.setChecked(true);
			break;
		default:
			break;
		}
		return convertView;
	}

	private void setImageView(ImageView iv, FileItem fileItem) {
		if (fileItem.getIcon() != null) {
			iv.setImageBitmap(fileItem.getIcon());
			return;
		}
		int iconId = fileItem.getIconId();
		if (iconId > 0) {
			iv.setImageResource(iconId);
		}
		switch (iconId) {
		case R.drawable.app_default_icon:
			new AsyncLoadApkicon().execute(fileItem);
			// picturethread(fileItem);
			break;
		case R.drawable.pictureicon:
			if (!ListActivity.mBusy) {// xuw update 20140514
				Thread thread = new LoadPictureImageThread(fileItem);
				loadingpictureExecutorService.execute(thread);
				if (!thread.isAlive()) {
					FilesAdapter.this.notifyDataSetChanged();
				}
				// new AsyncLoadPictureImage().execute(fileItem);
			} else {
				ListActivity.mBusy = true;
			}
			// picturethread(fileItem);
			break;
		// wdx 20140212 add
		case R.drawable.videoicon:
			if (!ListActivity.mBusy
					&& !fileItem.getFilePath().startsWith("smb")) {
				String m = android.os.Build.MODEL;
				if (FileActivity.isTrue == 1
						&& fileItem.getFilePath()
								.contains("/storage/emulated/")
						&& m.contains("GT")) {
					break;
				}
				/* liuyufa add for picture 20140313 */
				async = new AsyncLoadVideoImage();
				if (Asynclist == null || filelist == null) {
					Asynclist = new ArrayList<AsyncLoadVideoImage>();
					filelist = new ArrayList<FileItem>();
				}
				int size = Asynclist.size();
				Log.i("videoicon async add", fileItem.getFilePath());
				Asynclist.add(size, async);
				filelist.add(fileItem);
				if (isDone) {
					isDone = false;
					int size1 = Asynclist.size();
					Asynclist.get(0).execute(filelist.get(0));
					Log.i("vedio start", "isDone");
					if (size1 > 1) {
						for (int i = 1; i < size1; i++) {
							filelist.set(i - 1, filelist.get(i));
							Asynclist.set(i - 1, Asynclist.get(i));
						}
					}
					filelist.remove(size1 - 1);
					Asynclist.remove(size1 - 1);
				}
			} else {
				if (Asynclist != null && filelist != null
						&& !filelist.isEmpty() && !Asynclist.isEmpty()) {
					Asynclist.clear();
					filelist.clear();
				}
			}
			/* liuyufa add for picture 20140313 */
			break;
		default:
			break;
		}
	}

	private final class ViewHolder {
		public ImageView img_head;
		public TextView title;
		public TextView info;
		// public TextView time;
		public TextView type;
		public CheckBox checked;
	}

	class LoadPictureImageThread extends Thread {// xuw 20140402 add
		private FileItem fileItem;

		public LoadPictureImageThread(FileItem fileItem) {
			this.fileItem = fileItem;
		}

		@Override
		public void run() {
			String path = MApplication.CACHE_PICTURE_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			Bitmap bitmap;
			Bitmap newBitmap;
			FileItem item = fileItem;
			File thumbFile = new File(path
					+ item.getFileName().replace(".", ""));
			FileInputStream is;
			if (thumbFile.exists()) {
				try {
					/* liuyufa add for picture 20140313 */
					// newBitmap =
					// Browser.getBitmapFromMemCache(item.getFilePath());
					// if(newBitmap== null){
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inPreferredConfig = Bitmap.Config.RGB_565;
					options2.inPurgeable = true;
					options2.inInputShareable = true;
					options2.inJustDecodeBounds = false;
					options2.inTempStorage = new byte[12 * 1024];
					is = new FileInputStream(thumbFile.getAbsolutePath());
					newBitmap = BitmapFactory.decodeFileDescriptor(is.getFD(),
							null, options2);
					is.close();
					if (newBitmap == null) {
						return;
					}
					item.setIcon(newBitmap);
					// FilesAdapter.this.notifyDataSetChanged();
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					String fileDir = item.getFilePath();
					BitmapFactory.Options options = new BitmapFactory.Options();
					if (fileDir.startsWith("smb")) {
						int result = smbHelper.smbGettest(fileDir, path);
						if (result == 0) {
							return;
						}
						options.inJustDecodeBounds = true;
						bitmap = BitmapFactory.decodeFile(
								path + item.getFileName(), options);
						if (options.mCancel || options.outWidth == -1
								|| options.outHeight == -1) {
							return;
						}
						options.inSampleSize = ComputeSample.computeSampleSize(
								options, -1, 96 * 96);
						options.inJustDecodeBounds = false;
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inDither = false;
						options.inPurgeable = true;
						options.inInputShareable = true;
						options.inTempStorage = new byte[12 * 1024];
						is = new FileInputStream(path + item.getFileName());
						if (is != null) {
							bitmap = BitmapFactory.decodeFileDescriptor(
									is.getFD(), null, options);
							is.close();
						}
					} else {
						options.inJustDecodeBounds = true;
						bitmap = BitmapFactory.decodeFile(item.getFilePath(),
								options);
						options.inSampleSize = ComputeSample.computeSampleSize(
								options, -1, 96 * 96);
						options.inJustDecodeBounds = false;
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inDither = false;
						options.inPurgeable = true;
						options.inInputShareable = true;
						options.inTempStorage = new byte[12 * 1024];
						is = new FileInputStream(item.getFilePath());
						if (is != null) {
							bitmap = BitmapFactory.decodeFileDescriptor(
									is.getFD(), null, options);
							is.close();
						}
					}
					if (bitmap != null) {
						thumbFile.createNewFile();
						OutputStream out = new FileOutputStream(thumbFile);
						bitmap.compress(CompressFormat.PNG, 30, out);
						Log.i("length", String.valueOf(thumbFile.length()));
						if (thumbFile.length() == 0) {
							thumbFile.delete();
							return;
						}
						// Browser.addBitmapToMemoryCache(item.getFilePath(),
						// bitmap);
						item.setIcon(bitmap);
						// FilesAdapter.this.notifyDataSetChanged();
						Thread.sleep(200);
						out.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;

		}
	}

	class AsyncLoadPictureImage extends AsyncTask<FileItem, Void, Object> { // wdx
																			// 20140212
																			// rewrite
		@Override
		protected Object doInBackground(FileItem... params) {
			String path = MApplication.CACHE_PICTURE_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			Bitmap bitmap;
			Bitmap newBitmap;
			FileItem item = params[0];
			File thumbFile = new File(path
					+ item.getFileName().replace(".", ""));
			FileInputStream is;
			if (thumbFile.exists()) {
				try {
					/* liuyufa add for picture 20140313 */
					// newBitmap =
					// Browser.getBitmapFromMemCache(item.getFilePath());
					// if(newBitmap== null){
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inPreferredConfig = Bitmap.Config.RGB_565;
					options2.inPurgeable = true;
					options2.inInputShareable = true;
					options2.inJustDecodeBounds = false;
					options2.inTempStorage = new byte[12 * 1024];
					is = new FileInputStream(thumbFile.getAbsolutePath());
					newBitmap = BitmapFactory.decodeFileDescriptor(is.getFD(),
							null, options2);
					is.close();
					if (newBitmap == null) {
						return null;
					}
					item.setIcon(newBitmap);
					publishProgress();
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					String fileDir = item.getFilePath();
					BitmapFactory.Options options = new BitmapFactory.Options();
					if (fileDir.startsWith("smb")) {
						int result = smbHelper.smbGettest(fileDir, path);
						if (result == 0) {
							return null;
						}
						options.inJustDecodeBounds = true;
						bitmap = BitmapFactory.decodeFile(
								path + item.getFileName(), options);
						if (options.mCancel || options.outWidth == -1
								|| options.outHeight == -1) {
							return null;
						}
						options.inSampleSize = ComputeSample.computeSampleSize(
								options, -1, 96 * 96);
						options.inJustDecodeBounds = false;
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inDither = false;
						options.inPurgeable = true;
						options.inInputShareable = true;
						options.inTempStorage = new byte[12 * 1024];
						is = new FileInputStream(path + item.getFileName());
						if (is != null) {
							bitmap = BitmapFactory.decodeFileDescriptor(
									is.getFD(), null, options);
							is.close();
						}
					} else {
						options.inJustDecodeBounds = true;
						bitmap = BitmapFactory.decodeFile(item.getFilePath(),
								options);
						options.inSampleSize = ComputeSample.computeSampleSize(
								options, -1, 96 * 96);
						options.inJustDecodeBounds = false;
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inDither = false;
						options.inPurgeable = true;
						options.inInputShareable = true;
						options.inTempStorage = new byte[12 * 1024];
						is = new FileInputStream(item.getFilePath());
						if (is != null) {
							bitmap = BitmapFactory.decodeFileDescriptor(
									is.getFD(), null, options);
							is.close();
						}
					}
					if (bitmap != null) {
						thumbFile.createNewFile();
						OutputStream out = new FileOutputStream(thumbFile);
						bitmap.compress(CompressFormat.PNG, 30, out);
						Log.i("length", String.valueOf(thumbFile.length()));
						if (thumbFile.length() == 0) {
							thumbFile.delete();
							return null;
						}
						// Browser.addBitmapToMemoryCache(item.getFilePath(),
						// bitmap);
						item.setIcon(bitmap);
						publishProgress();
						Thread.sleep(200);
						out.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Void... value) {
			FilesAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}

	// wdx 20140212 add
	class AsyncLoadVideoImage extends AsyncTask<FileItem, Void, String> {
		@Override
		protected String doInBackground(FileItem... params) {
			String path = MApplication.CACHE_VIDEO_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			} else {
				Bitmap bitmap = null;
				Bitmap newBitmap = null;
				FileItem item = params[0];
				FileInputStream is;
				File thumbFile = new File(path
						+ item.getFileName().replace(".", ""));
				if (thumbFile.exists()) {
					try {
						// newBitmap =
						// Browser.getBitmapFromMemCache(item.getFilePath());
						if (newBitmap == null) {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inPreferredConfig = Bitmap.Config.RGB_565;
							options.inPurgeable = true;
							options.inInputShareable = true;
							options.inJustDecodeBounds = false;
							options.inTempStorage = new byte[12 * 1024];
							is = new FileInputStream(
									thumbFile.getAbsolutePath());
							newBitmap = BitmapFactory.decodeFileDescriptor(
									is.getFD(), null, options);
							is.close();
						}
						item.setIcon(newBitmap);
						Thread.sleep(500);
						publishProgress();
						if (newBitmap == null) {
							thumbFile.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						String fileDir = item.getFilePath();
						// 1205
						if (fileDir.startsWith("smb")) {
							smbHelper.smbGet(fileDir, path);
						} else {
							String i = android.os.Build.MODEL;
							if (fileDir.contains("/storage/emulated/")
									&& i.contains("GT")) {
								ContentResolver cr = mContext
										.getContentResolver();
								bitmap = getVideoThumbnail(mContext, cr,
										item.getFilePath());
							} else {
								bitmap = ThumbnailUtils.createVideoThumbnail(
										item.getFilePath(),
										Thumbnails.MICRO_KIND);
							}
						}
						if (bitmap != null) {
							thumbFile.createNewFile();
							OutputStream out = new FileOutputStream(thumbFile);
							bitmap.compress(CompressFormat.PNG, 26, out);
							Log.i("length", String.valueOf(thumbFile.length()));
							if (thumbFile.length() == 0) {
								thumbFile.delete();
								return null;
							}
							// Browser.addBitmapToMemoryCache(item.getFilePath(),
							// bitmap);
							item.setIcon(bitmap);
							publishProgress();
							Thread.sleep(200);
							out.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			isDone = true;
			return "ok";
		}

		@Override
		public void onProgressUpdate(Void... value) {
			FilesAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				return;
			}
			if (result.equals("ok")) {
				Log.i("vedio end ok", "====>ok");
				int size = Asynclist.size();
				if (size >= 1) {
					try {
						System.gc();
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Asynclist.get(0).execute(filelist.get(0));
					isDone = false;
					Log.i("onPostExecute", "start");
					if (size > 1) {
						for (int i = 1; i < size; i++) {
							filelist.set(i - 1, filelist.get(i));
							Asynclist.set(i - 1, Asynclist.get(i));
						}
					}
					filelist.remove(size - 1);
					Asynclist.remove(size - 1);
				}
			}
		}
	}

	class AsyncLoadApkicon extends AsyncTask<FileItem, Void, Object> {
		@Override
		protected Object doInBackground(FileItem... params) {
			String path = MApplication.CACHE_PICTURE_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			} else {
				Bitmap bm;
				FileItem item = params[0];
				File thumbFile = new File(path
						+ item.getFileName().replace(".", ""));
				FileInputStream is;
				if (thumbFile.exists()) {
					try {
						BitmapFactory.Options options2 = new BitmapFactory.Options();
						options2.inPreferredConfig = Bitmap.Config.RGB_565;
						options2.inPurgeable = true;
						options2.inInputShareable = true;
						options2.inJustDecodeBounds = false;
						options2.inTempStorage = new byte[12 * 1024];
						is = new FileInputStream(thumbFile.getAbsolutePath());
						bm = BitmapFactory.decodeFileDescriptor(is.getFD(),
								null, options2);
						item.setIcon(bm);
						publishProgress();
						if (bm == null) {
							thumbFile.delete();
						}
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						Drawable dw = Helper.showUninstallAPKIcon(mContext,
								item.getFilePath());
						if (dw != null) {
							BitmapDrawable bd = (BitmapDrawable) dw;
							bm = bd.getBitmap();
							item.setIcon(bm);
							publishProgress();
							Thread.sleep(200);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Void... value) {
			FilesAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}

	public static Bitmap getVideoThumbnail(Context context, ContentResolver cr,
			String testVideopath) {
		ContentResolver testcr = context.getContentResolver();
		String[] projection = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, };
		String whereClause = MediaStore.Video.Media.DATA + " = '"
				+ testVideopath + "'";
		Cursor cursor = testcr.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
				whereClause, null, null);
		int _id = 0;
		String videoPath = "";
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		if (cursor.moveToFirst()) {
			int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
			int _dataColumn = cursor
					.getColumnIndex(MediaStore.Video.Media.DATA);
			do {
				_id = cursor.getInt(_idColumn);
				videoPath = cursor.getString(_dataColumn);
				System.out.println(_id + " " + videoPath);
			} while (cursor.moveToNext());
		}
		cursor.close();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inInputShareable = true;
		Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, _id,
				Images.Thumbnails.MICRO_KIND, options);
		return bitmap;
	}
}
