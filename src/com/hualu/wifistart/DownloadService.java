package com.hualu.wifistart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jcifs.smb.SmbFile;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.hualu.wifistart.StatusActivity.TransferItemInfo;
import com.hualu.wifistart.filecenter.files.FileOperationThreadManager;
import com.hualu.wifistart.filecenter.files.FileOperationThreadManager.CopyOperation;
import com.hualu.wifistart.filecenter.files.PasteFileThread;
import com.hualu.wifistart.filecenter.files.SmbPasteFileThread;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

/**
 * 后台下载Service
 * 
 * @author oneRain
 * 
 */
public class DownloadService extends Service {  //xuw  update 20140331
	private List<PasteFileThread> threadList = new ArrayList<PasteFileThread>();
	String srcDir;
	String toDir;
	String opertation;
	boolean isCut;
	private final String TAG = "DownloadService";
	String brand;
	ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Service onCreate()");
		brand = Build.BRAND;
		// threadList = new ArrayList<DownloadTaskThread>();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// srcDir = intent.getStringExtra("srcDir");
		// toDir = intent.getStringExtra("toDir");

		// DownloadTaskThread task = new DownloadTaskThread();
		// threadList.add(task);
		// task.start();
		if (intent.getExtras() != null) {
			srcDir = intent.getExtras().getString("srcDir");
			toDir = intent.getExtras().getString("toDir");
			isCut = intent.getBooleanExtra("isCut", false);
			opertation = intent.getStringExtra("CopyOperation");
			CopyOperation operationType = CopyOperation.APPEND2;
			if (opertation.equals("COVER")) // COVER,JUMP,APPEND2,CANCEL,UNKOWN
				operationType = CopyOperation.COVER;

			Log.i(TAG, "Service onStart() " + srcDir + " " + isCut + " "
					+ operationType + " " + opertation);

			if (srcDir.startsWith("smb")) {
				SmbPasteFileThread task = new SmbPasteFileThread(srcDir, toDir,
						handler, isCut, operationType);
				threadList.add(task);
				executorService.execute(task);
				//task.start();
			} else {
				PasteFileThread task = new PasteFileThread(srcDir, toDir,
						handler, isCut, operationType);
				threadList.add(task);
				executorService.execute(task);
				//task.start();
			}
		}
	}
	@Override
	public void onDestroy() {
         executorService.shutdown();
		super.onDestroy();
	}

	/**
	 * 返回绑定实例，通过此实例可以得到想要的DownloadService数据
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return new DownloadBinder();
	}

	/**
	 * 用一个随机增长速率的频率模拟下载的速度
	 * 
	 * @author oneRain
	 * 
	 */
	public class DownloadTaskThread extends Thread {
		private int rate = 0;
		private int process = 0;

		/**
		 * @param rate
		 *            每个不同线程所对应不同的增长速率
		 */
		public DownloadTaskThread() {
			Random random = new Random();
			this.rate = random.nextInt(9) + 1;
		}

		public int getCurrentProcess() {
			return this.process;
		}

		@Override
		public void run() {
			while (process < 100) {
				try {
					Thread.sleep(1000);
					process += this.rate;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 自定义的Binder对象，内部类可以获取本DownloadService中的数据
	 * 
	 * @author oneRain
	 * 
	 */
	private class DownloadBinder extends Binder implements IGetProcess {
		@Override
		public ArrayList<TransferItemInfo> getProcessList() {
			ArrayList<TransferItemInfo> processList = new ArrayList<TransferItemInfo>();

			int size = threadList.size();
			for (int i = 0; i < size; i++) {
				PasteFileThread thread = threadList.get(i);
				TransferItemInfo transferItem = new TransferItemInfo(
						thread.tittle, thread.totalSize, thread.hasPasted,
						thread.pasted_rate);// (String tittle,double filesize)
				// //xuw add 0325
				// while(processList.size()>72&&"samsung".equals(brand)) {
				// synchronized (processList) {
				// try {
				// wait(500);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// }
				processList.add(transferItem);
			}
			// Log.i(TAG,"getProcessList " + processList.size());
			if (processList.size() == 0
					&& (PasteCompletionListner.mFilesPaste > 0)) {
				Intent intent = new Intent("com.hualu.wifistart.paste.complete");
				sendBroadcast(intent);
			}
			return processList;
		}

		@Override
		public void removeProcess(TransferItemInfo item) {
			// TODO Auto-generated method stub
			// Log.i("DownloadService","removeProcess " + threadList.size());
			for (int i = 0; i < threadList.size(); i++) {
				if (threadList.get(i).tittle.equals(item.tittle)) {
					PasteFileThread removeItem = threadList.get(i);
					removeItem.setCancel();
					threadList.remove(i);
					String filePath = removeItem.getSrc();
					int index = filePath.lastIndexOf("/");
					String filename = filePath.substring(index + 1);
					doDelete(removeItem.getTo() + filename);
					Log.i("DownloadService", "removeProcess delete "
							+ removeItem.getTo() + filename);
				}
			}
		}
	}

	private void doDelete(final String srcPath) {
		Log.i(TAG, "doDelete " + srcPath);
		new Thread() {
			public void run() {
				try {
					if (srcPath.startsWith("smb")) {
						SmbFile srcFile = new SmbFile(srcPath);
						if (srcFile.canWrite()) {
							if (srcFile.isDirectory()) {
								deleteFolderForCut(srcFile.getPath());
							} else
								srcFile.delete();
						}
					} else {
						File theFile = new File(srcPath);
						if (theFile.canWrite()) {
							if (theFile.isDirectory()) {
								deleteFolderForCut(theFile.getPath());
							} else {
								theFile.delete();
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	private void deleteFolderForCut(final String folderPath) throws Exception {
		Log.i(TAG, "deleteFolderForCut " + folderPath);
		if (folderPath.startsWith("smb")) {
			SmbFile smbfolder = new SmbFile(folderPath);
			SmbFile[] smbfiles = smbfolder.listFiles();
			if (smbfiles != null) {
				for (int j = 0; j < smbfiles.length; j++) {

					SmbFile aFile = smbfiles[j];
					Log.i(TAG, "deleteFolderForCut smb " + aFile.getPath());
					if (aFile.isDirectory()) {
						deleteFolderForCut(aFile.getPath());
					} else {
						if (aFile.canWrite())
							aFile.delete();
					}
				}
				smbfolder.delete();
			} else {
				smbfolder.delete();
			}
		} else {
			File folder = new File(folderPath);
			File[] files = folder.listFiles();
			if (files != null) {
				for (int j = 0; j < files.length; j++) {
					File aFile = files[j];
					Log.i(TAG, "deleteFolderForCut " + aFile.getPath());
					if (aFile.isDirectory()) {
						deleteFolderForCut(aFile.getAbsolutePath());
					} else {
						if (aFile.canWrite())
							aFile.delete();
					}
				}
				folder.delete();
			} else {
				folder.delete();
			}
		}
	}

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FileOperationThreadManager.PASTE_FAILED: {
				Bundle bundle = (Bundle) msg.obj;
				String src = bundle.getString("srcDir");
				ViewEffect.showToast(DownloadService.this, src + "文件粘贴失败");
			}
				break;
			case FileOperationThreadManager.PASTE_COMPLETED: {

				Bundle bundle = (Bundle) msg.obj;
				if (bundle != null) {
					String src = bundle.getString("srcDir");
					String to = bundle.getString("toFolder");
					for (int i = 0; i < threadList.size(); i++) {
						if (threadList.get(i).getSrc().equals(src)
								&& threadList.get(i).getTo().equals(to)) {
							if (threadList.get(i).isCut) {
								doDelete(threadList.get(i).getSrc());
							}
							threadList.remove(i);

						}
					}
					Intent intent = new Intent(
							"com.hualu.wifistart.paste.complete");
					sendBroadcast(intent);
					Log.i(TAG, "PASTE_COMPLETED " + src + " " + to);
				}
				break;
			}
			case FileOperationThreadManager.PASTE_PROGRESS_CHANGE:
				// msg.arg1
				Bundle bundle = (Bundle) msg.obj;
				if (bundle != null) {
					Log.i(TAG,
							"" + bundle.getString("currPos") + " "
									+ bundle.getString("percentage"));
				}
				break;
			case FileOperationThreadManager.PASTE_LOCKFILEEXIST:
				ViewEffect.showToast(DownloadService.this, R.string.paste_lockfile_exist);
				break;
			}
		}
	};
}
