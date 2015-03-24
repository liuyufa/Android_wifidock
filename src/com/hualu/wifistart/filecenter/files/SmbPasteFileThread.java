package com.hualu.wifistart.filecenter.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hualu.wifistart.filecenter.files.FileOperationThreadManager.CopyOperation;
import com.hualu.wifistart.filecenter.utils.Helper;

public class SmbPasteFileThread extends PasteFileThread {
	/*
	 * private CopyOperation operationType = CopyOperation.UNKOWN;
	 * FileItemForOperation mFileForOperation; Handler mHandler; Handler
	 * responseHandler; private double totalSize = 0; private double hasPasted =
	 * 0; private int pasted_rate;
	 */
	// private final String TAG = "SmbPasteFileThread";
	/*
	 * public SmbPasteFileThread(FileItemForOperation fileForOperation,Handler
	 * handler,Handler response){ this.mFileForOperation = fileForOperation;
	 * this.mHandler = handler; responseHandler = response; }
	 */
	public SmbPasteFileThread(String src, String to, Handler handler,
			boolean isCut, CopyOperation operationType) {
		super(src, to, handler, isCut, operationType);
		// TODO Auto-generated constructor stub
	}

	public void getSize() throws Exception {
		SmbFile f = new SmbFile(srcDir);
		getDirectorySize(f);
	}

	private void getDirectorySize(SmbFile f) throws IOException {
		f.connect();
		if (f.isFile()) {
			totalSize += f.length();
			totalFileNum += 1;
			return;
		}
		totalFileNum += 1;
		SmbFile flist[] = f.listFiles();
		if (flist == null) {
			return;
		}
		int length = flist.length;
		for (int i = 0; i < length; i++) {
			if (flist[i].isDirectory()) {
				getDirectorySize(flist[i]);
			} else {
				totalSize = totalSize + flist[i].length();
				totalFileNum = totalFileNum + 1;
			}
		}
	}

	private boolean addFilesToTable(FileOperation folder) throws Exception {
		String path = folder.src;
		String folderName = Helper.getFolderNameOfPath(path);
		SmbFile folderDir = new SmbFile(folder.src);
		SmbFile files[] = folderDir.listFiles();

		int size = files.length;
		int tableSize = mSrcToDir.size();

		if (!folder.dst.startsWith("smb")) {
			File tmpFile = new File(folder.dst + folderName + "/");
			if (DEBUG)
				Log.i(TAG, "addFilesToTable " + tmpFile.getPath());
			// 检查目标路径是否存在,不存在就建立路径
			int tmp = 1;
			while (tmpFile.exists()) {
				tmp++;
				tmpFile = new File(folder.dst + folderName + "(" + tmp + ")");
			}

			tmpFile.mkdir();

			for (int i = 0; i < size; i++) {
				// mSrcToDir.put(tableSize + i, files[i].getPath());
				FileOperation item = new FileOperation(files[i].getPath(),
						tmpFile.getAbsolutePath() + "/");
				mSrcToDir.put(tableSize + i, item);
			}
		} else {
			SmbFile tmpFile = new SmbFile(folder.dst + folderName + "/");
			if (DEBUG)
				Log.i(TAG, "addFilesToTable " + tmpFile.getPath());
			// 检查目标路径是否存在,不存在就建立路径
			int tmp = 1;
			while (tmpFile.exists()) {
				tmp++;
				tmpFile = new SmbFile(folder.dst + folderName + "(" + tmp + ")");
			}

			tmpFile.mkdir();

			for (int i = 0; i < size; i++) {
				// mSrcToDir.put(tableSize + i, files[i].getPath());
				FileOperation item = new FileOperation(files[i].getPath(),
						tmpFile.getPath());
				mSrcToDir.put(tableSize + i, item);
			}
		}
		return true;
	}

	@Override
	public void run() {
		try {
			getSize();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e(TAG, " " + e1.getMessage());
		}
		int tablecnt = mSrcToDir.size();

		for (int i = 0; i < tablecnt; i++) {

			try {
				SmbFile fromFile = new SmbFile(mSrcToDir.get(i).src);
				if (fromFile.isDirectory()) {
					if (!addFilesToTable(mSrcToDir.get(i)))
						return;
					tablecnt = mSrcToDir.size();
				} else {
					pasteFile(mSrcToDir.get(i));
				}

			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.e(TAG, " " + e1.getMessage());
			} catch (SmbException se) {
				se.printStackTrace();
				Log.e(TAG, " " + se.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, " " + e.getMessage());
			}
		}
		mHandler.sendMessage(getCompleteMsg());
	}

	public void setOperationType(CopyOperation operationType) {
		this.operationType = operationType;
	}

	public CopyOperation getOperationType() {
		return operationType;
	}

	public int pasteFile(FileOperation from) throws Exception {
		// String toFolder = fileForOperation.getDirFolder();
		// FileItem fileItem = fileForOperation.getFileItem();
		SmbFile fromFile = new SmbFile(from.src);
		if (DEBUG)
			Log.i(TAG, "dstFolder = " + from.dst + " from " + from.src);
		int copyResult = FileOperationThreadManager.PASTE_FAILED;
		// 检查被复制的文件是否存在
		if (!fromFile.exists()) {
			return FileOperationThreadManager.PASTE_FAILED
					+ FileOperationThreadManager.FAILED_REASON_FROM_FILE_NOT_EXIST;
		}
		// 检查被复制的文件是否被限制无法拷贝出
		if (!canPasteFrom(fromFile)) {
			return FileOperationThreadManager.PASTE_FAILED
					+ FileOperationThreadManager.FAILED_REASON_FOLDER_LIMIT;
		}
		// zhaoyu
		if (!from.dst.startsWith("smb")) {
			File tmpFile = new File(from.dst);
			// 检查目标路径是否存在,不存在就建立路径
			if (!tmpFile.exists())
				if (!tmpFile.mkdir()) {
					// 空间不够，经历路径失败
					return FileOperationThreadManager.PASTE_FAILED
							+ FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT;
				}

			// 检查目标文件夹是否可写
			if (!tmpFile.canWrite()) {
				return FileOperationThreadManager.PASTE_FAILED
						+ FileOperationThreadManager.FAILED_REASON_READ_ONLY_FILE_SYSTEM;
			}
			tmpFile = new File(from.dst + fromFile.getName());
			double fileSize = fromFile.length();
			// 检查粘贴的目录是否已经有同名的文件
			if (tmpFile.exists()) {
				// Log.i(TAG,"=============>"+"存在同名的");
				if (operationType == CopyOperation.COVER) {
					if (tmpFile.getPath().equals(fromFile.getPath())) {
						return FileOperationThreadManager.PASTE_PAUSE
								+ FileOperationThreadManager.PAUSE_REASON_CANNOT_COVER;
					} else {
						System.out.println(tmpFile.delete());
						if (DEBUG)
							Log.e(TAG,
									"===========temp.exist" + tmpFile.exists());
					}
				} else if (operationType == CopyOperation.JUMP) {
					// responseHandler.sendEmptyMessage(PASTE_PROGRESS_CHANGE);
					pasted_rate = (int) ((hasPasted + fileSize) / totalSize * 100.0);
					// responseHandler.sendMessage(getProgressChangeMsg());
					return FileOperationThreadManager.PASTE_SUCCEED;
				} else if (operationType == CopyOperation.APPEND2) {
					if (tmpFile.getName().contains("lockfile")) {
						Message msg = new Message();
						msg.what = FileOperationThreadManager.PASTE_LOCKFILEEXIST;
						mHandler.sendMessage(msg);
						pasted_rate = (int) ((hasPasted + fileSize) / totalSize * 100.0);
						return FileOperationThreadManager.PASTE_SUCCEED;
					} else {
						int tmp = 2;
						tmpFile = new File(from.dst
								+ Helper.getNameAppendStr(fromFile.getName(),
										"(" + tmp + ")"));
						while (tmpFile.exists()) {
							tmp++;
							tmpFile = new File(
									from.dst
											+ Helper.getNameAppendStr(
													fromFile.getName(), "("
															+ tmp + ")"));
						}
					}
				} else if (operationType == CopyOperation.CANCEL) {
					return FileOperationThreadManager.PASTE_CANCEL;
				} else {
					return FileOperationThreadManager.PASTE_PAUSE
							+ FileOperationThreadManager.PAUSE_REASON_TO_FOLDER_HAS_EXIST;
				}
			}
			// copyFileLinuxCommand(fileItem.getFilePath(), toFolder);
			copyResult = copyFileJavaCommand(fromFile, tmpFile);
			return copyResult;
		} else {
			SmbFile tmpFile = new SmbFile(from.dst);
			// 检查目标路径是否存在,不存在就建立路径
			if (!tmpFile.exists())
				try {
					tmpFile.mkdir();
				} catch (SmbException se) {
					return FileOperationThreadManager.PASTE_FAILED
							+ FileOperationThreadManager.FAILED_REASON_NO_SPACE_LEFT;
				}
			// if(!tmpFile.mkdir()){
			// 空间不够，经历路径失败
			// return PASTE_FAILED + FAILED_REASON_NO_SPACE_LEFT;
			// }
			// 检查目标文件夹是否可写
			if (!tmpFile.canWrite()) {
				return FileOperationThreadManager.PASTE_FAILED
						+ FileOperationThreadManager.FAILED_REASON_READ_ONLY_FILE_SYSTEM;
			}
			tmpFile = new SmbFile(from.dst + fromFile.getName());
			double fileSize = fromFile.length();
			// 检查粘贴的目录是否已经有同名的文件
			if (tmpFile.exists()) {
				// Log.i(TAG,"=============>"+"存在同名的");
				if (operationType == CopyOperation.COVER) {
					if (tmpFile.getPath().equals(fromFile.getPath())) {
						return FileOperationThreadManager.PASTE_PAUSE
								+ FileOperationThreadManager.PAUSE_REASON_CANNOT_COVER;
					} else {
						tmpFile.delete();// ????zhaoyu
						// System.out.println(tmpFile.delete());
						Log.e(TAG, "===========temp.exist" + tmpFile.exists());
					}
				} else if (operationType == CopyOperation.JUMP) {
					// responseHandler.sendEmptyMessage(PASTE_PROGRESS_CHANGE);
					pasted_rate = (int) ((hasPasted + fileSize) / totalSize * 100.0);
					// responseHandler.sendMessage(getProgressChangeMsg());
					return FileOperationThreadManager.PASTE_SUCCEED;
				} else if (operationType == CopyOperation.APPEND2) {
					if (tmpFile.getName().contains("lockfile")) {
						Message msg = new Message();
						msg.what = FileOperationThreadManager.PASTE_LOCKFILEEXIST;
						mHandler.sendMessage(msg);
						pasted_rate = (int) ((hasPasted + fileSize) / totalSize * 100.0);
						return FileOperationThreadManager.PASTE_SUCCEED;
					} else {
						int tmp = 2;
						tmpFile = new SmbFile(from.dst
								+ Helper.getNameAppendStr(fromFile.getName(),
										"(" + tmp + ")"));
						while (tmpFile.exists()) {
							tmp++;
							tmpFile = new SmbFile(
									from.dst
											+ Helper.getNameAppendStr(
													fromFile.getName(), "("
															+ tmp + ")"));
						}
					}
				} else if (operationType == CopyOperation.CANCEL) {
					return FileOperationThreadManager.PASTE_CANCEL;
				} else {
					return FileOperationThreadManager.PASTE_PAUSE
							+ FileOperationThreadManager.PAUSE_REASON_TO_FOLDER_HAS_EXIST;
				}
			}
			// copyFileLinuxCommand(fileItem.getFilePath(), toFolder);
			copyResult = copyFileJavaCommand(fromFile, tmpFile);
			return copyResult;
		}
	}

	private int copyFileJavaCommand(SmbFile srcFile, SmbFile dirFile) {
		if (DEBUG)
			Log.i(TAG, "===============>" + "开始复制E" + " " + srcFile.getPath()
					+ " " + dirFile.getPath() + " " + totalSize);
		int res = 0;
		int byteread = 0;
		try {
			// 使用".tmp"做临时后缀 2014.12.26 wdx
			dirFile.getName().replace(dirFile.getName(),
					dirFile.getName() + ".tmp");
			srcFile.connect();
			dirFile.connect();

			InputStream inStream = new BufferedInputStream(
					new SmbFileInputStream(srcFile));
			OutputStream outStream = new BufferedOutputStream(
					new SmbFileOutputStream(dirFile));

			byte[] buffer = new byte[1024 * 10];
			int tmpRate = 0;
			while ((byteread = inStream.read(buffer)) != -1 && !cancelFlag) {// &&
																				// !canceled
				hasPasted += byteread; // 字节数 文件大小
				// Log.i(TAG,"read " + byteread);
				outStream.write(buffer, 0, byteread);
				// Log.i(TAG,"write ok");
				pasted_rate = (int) (hasPasted / totalSize * 100.0);
				if (pasted_rate >= tmpRate + 1) {
					mHandler.sendMessage(getProgressChangeMsg());
					tmpRate = pasted_rate;
				}
			}
			inStream.close();
			// 使用".tmp"做临时后缀 2014.12.26 wdx
			dirFile.getName().replace(dirFile.getName(),
					dirFile.getName() + ".tmp");
			if (outStream != null)
				outStream.close();
			/*
			 * if(canceled){ res = PASTE_CANCEL; dirFile.delete();
			 * responseHandler.sendEmptyMessage(PASTE_CANCEL); }else
			 */{
				res = FileOperationThreadManager.PASTE_SUCCEED;
				// responseHandler.sendMessage(getProgressChangeMsg(0,1));
			}
		} catch (Exception ex) {
			// if(DEBUG)
			Log.e(TAG, "EXCEPTION===>" + ex.getMessage());
			res = FileOperationThreadManager.PASTE_FAILED;
			mHandler.sendMessage(getFailedMsg());
			// canceled = true;
			ex.printStackTrace();
		}
		operationType = CopyOperation.UNKOWN;
		return res;
	}

	private int copyFileJavaCommand(SmbFile srcFile, File dirFile) {
		if (DEBUG)
			Log.i(TAG, "===============>" + "开始复制F" + " " + srcFile.getPath()
					+ " " + dirFile.getAbsolutePath() + " " + totalSize);
		int res = 0;
		int byteread = 0;
		try {
			srcFile.connect();
			// dirFile.connect();
			// if(srcFile.length() > left){
			// return PASTE_FAILED + FAILED_REASON_NO_SPACE_LEFT;
			// }

			// InputStream inStream = new FileInputStream(srcFile); // 读入原文件
			// FileOutputStream outStream = new FileOutputStream(dirFile);
			// 使用".tmp"做临时后缀 2014.12.26 wdx
			dirFile.getName().replace(dirFile.getName(),
					dirFile.getName() + ".tmp");
			InputStream inStream = new BufferedInputStream(
					new SmbFileInputStream(srcFile));
			OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(dirFile));

			byte[] buffer = new byte[1024 * 10];
			int tmpRate = 0;
			while ((byteread = inStream.read(buffer)) != -1 && !cancelFlag) {// &&
																				// !canceled
				hasPasted += byteread; // 字节数 文件大小
				// Log.i(TAG,"read " + byteread);
				outStream.write(buffer, 0, byteread);
				// Log.i(TAG,"write ok");
				pasted_rate = (int) (hasPasted / totalSize * 100.0);
				if (pasted_rate >= tmpRate + 1) {
					// responseHandler.sendMessage(getProgressChangeMsg());
					tmpRate = pasted_rate;
				}
			}
			inStream.close();
			// 去掉".tmp"的临时后缀 2014.12.26 wdx
			dirFile.getName().replace(".tmp", "");
			if (outStream != null)
				outStream.close();
			/*
			 * if(canceled){ res = PASTE_CANCEL; dirFile.delete();
			 * responseHandler.sendEmptyMessage(PASTE_CANCEL); }else
			 */{
				res = FileOperationThreadManager.PASTE_SUCCEED;
				// responseHandler.sendMessage(getProgressChangeMsg(0,1));
			}
		} catch (Exception ex) {
			// if(DEBUG)
			Log.e(TAG, "EXCEPTION===>" + ex.getMessage());
			res = FileOperationThreadManager.PASTE_FAILED;
			mHandler.sendMessage(getFailedMsg());
			// canceled = true;
			ex.printStackTrace();
		}
		operationType = CopyOperation.UNKOWN;
		return res;
	}

	private boolean canPasteFrom(SmbFile fromFile) {
		// String fromPath = fromFile.getAbsolutePath();
		// String[] limitFolders = MyApplication.limitFolders;
		// if(limitFolders.length == 0)return true;
		// for (String string : limitFolders) {
		// if(fromPath.startsWith(string)){
		// return false;
		// }
		// }
		return true;
	}

	private Message getProgressChangeMsg() {
		// System.out.println(pasted_rate);
		Message msg = new Message();
		msg.what = FileOperationThreadManager.PASTE_PROGRESS_CHANGE;
		msg.arg1 = pasted_rate;
		Bundle bundle = new Bundle();
		// String currPos = currPosition+"/"+totalFileNum;
		String percentage = pasted_rate + "%";

		// bundle.putString("currPos", currPos);
		bundle.putString("percentage", percentage);
		msg.obj = bundle;
		return msg;
	}

}