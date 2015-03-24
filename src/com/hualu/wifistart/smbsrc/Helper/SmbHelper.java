package com.hualu.wifistart.smbsrc.Helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import android.util.Log;

import com.hualu.wifistart.ListActivity;
//import com.hualu.wifistart.smbsrc.BaseFileBrowserActivity;
import com.hualu.wifistart.smbsrc.Singleton;

public class SmbHelper {
	SmbFile smb;
	public static String SMB_LOG_TAG = "SMB";
	public SmbHelper() {
		jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "2");
		jcifs.Config.setProperty( "jcifs.smb.client.useExtendedSecurity", "false");
		jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
	
	}

	public boolean isSmbConnect(String dirPath)throws Exception
	{
		SmbFile smb = new SmbFile(dirPath);
		smb.connect();
		if (smb.exists()) {			
			return true;
		}else
			return false;
	}
	public List<SmbFile> dir(String dirPath, Boolean isRecursive) throws Exception {
		List<SmbFile> files = new ArrayList<SmbFile>();
		smb = new SmbFile(dirPath);
		Log.i(SMB_LOG_TAG,"smb connect " + dirPath);
		smb.connect();
		if (!smb.exists()) {
			Log.d(SMB_LOG_TAG, dirPath + " is not exists");
			return null;
		}
		Log.d(SMB_LOG_TAG, smb.getName() + ">");
		SmbFile[] allFile = smb.listFiles();
		for (SmbFile smbFile : allFile) {
			Log.d(SMB_LOG_TAG, smbFile.getName()
					+ (smbFile.isDirectory() ? ">" : ""));
			if (isRecursive) {
				if (smbFile.isDirectory())
					files.addAll(dir(smbFile.getPath(), isRecursive));
				else
					files.add(smbFile);
			} else {
				files.add(smbFile);
			}
		}
		return files;
	}
	
	//zhaoyu	1205
	public List<SmbFile> dirFile(String dirPath) throws Exception { //1205
		List<SmbFile> files = new ArrayList<SmbFile>();
		//NtlmPasswordAuthentication nt = new NtlmPasswordAuthentication(  
		//	     "network", "admin", "12345678"); 
		
		smb = new SmbFile(dirPath);	
		smb.connect();
		if (!smb.exists()) {
			Log.d(SMB_LOG_TAG, dirPath + " is not exists");
			return null;
		}
		Log.d(SMB_LOG_TAG, smb.getName() + ">");
		SmbFile[] allFile = smb.listFiles();
		for (SmbFile smbFile : allFile) {
			Log.d(SMB_LOG_TAG, smbFile.getName()
					+ (smbFile.isDirectory() ? ">" : ""));	
			files.add(smbFile);		
			if (smbFile.isDirectory()){		
				files.addAll(dirFile(smbFile.getPath()));
			}	
		}
		return files;
	}

	public List<SmbFile> dirby(String dirPath, Boolean isRecursive, String kind)
			throws Exception {
		Log.i(SMB_LOG_TAG, "" + dirPath + " " + isRecursive + " " + kind);
		List<SmbFile> files = new ArrayList<SmbFile>();
		smb = new SmbFile(dirPath);
		smb.connect();
		if (!smb.exists()) {
			Log.d(SMB_LOG_TAG, dirPath + " is not exists");
			return null;
		}
		SmbFile[] allFile = smb.listFiles();
		Log.d(SMB_LOG_TAG,  "all files " + allFile.length);
		//context.handler.sendMessage(context.handler.obtainMessage(BaseFileBrowserActivity.MSG_PRCESS_MAX, (int) allFile.length));
		//int processValue = 0;
		for (SmbFile smbFile : allFile) {
			Log.d(SMB_LOG_TAG, smbFile.getName()
					+ (smbFile.isDirectory() ? ">" : ""));
			//processValue++;
			//context.handler.sendMessage(context.handler.obtainMessage(BaseFileBrowserActivity.MSG_PRCESS_VALUE, processValue));
			if (isRecursive) {
				if (smbFile.isDirectory()){
					if(smbFile.getName().charAt(0) == '.'){
						continue;
					}
					if( (smbFile.getAttributes() & SmbFile.ATTR_HIDDEN) > 0){
						continue;
					}
					if( (smbFile.getAttributes() & SmbFile.ATTR_SYSTEM) > 0){
						continue;
					}
					if( (smbFile.getAttributes() & SmbFile.ATTR_VOLUME) > 0){
						continue;
					}
					files.addAll(dirby(smbFile.getPath(), isRecursive, kind));
				}else {
					if (Singleton.isBelong(smbFile.getPath(),
							Singleton.fileKinds.get(kind))) {
						if(smbFile.getName().charAt(0) == '.'){
							continue;
						}
						files.add(smbFile);
					}
				}
			} else {
				if (smbFile.isDirectory()
						|| (smbFile.isFile() && Singleton.isBelong(
								smbFile.getPath(),
								Singleton.fileKinds.get(kind)))) {
					files.add(smbFile);
				}
			}
		}
		return files;
	}

	public void delDir(String dir) throws Exception {
		SmbFile smbFile = new SmbFile(dir);
		smbFile.delete();
	}

	public void delDirs(List<String> dirs) throws Exception {
		for (String dir : dirs) {
			delDir(dir);
		}
	}

	public void delFile(String file) throws Exception {
		SmbFile smbFile = new SmbFile(file);
		smbFile.delete();
	}

	public void delFiles(List<String> files) throws Exception {
		for (String file : files) {
			delFile(file);
		}
	}

	public void mvFile(String from, String to) throws Exception {
		SmbFile f = new SmbFile(from);
		if (f.exists()) {
			SmbFile t = new SmbFile(to);
			f.copyTo(t);
		}
	}

	public void mvDir(String from, String to) throws Exception {
		SmbFile f = new SmbFile(from);
		if (f.exists()) {
			SmbFile t = new SmbFile(to);
			f.copyTo(t);
		}
	}

	public void smb2smb(String from, String to) {
		InputStream in = null;
		OutputStream out = null;
		try {
			SmbFile fromFile = new SmbFile(from);
			fromFile.connect();
			SmbFile toFile = new SmbFile(to);
			toFile.connect();
			in = new BufferedInputStream(new SmbFileInputStream(fromFile));
			out = new BufferedOutputStream(new SmbFileOutputStream(toFile));
			byte[] buffer = new byte[1024];
			//context.handler.sendMessage(context.handler.obtainMessage(BaseFileBrowserActivity.MSG_PRCESS_MAX, (int) fromFile.length()));
			int len = in.read(buffer);
			//int processValue = len;
			while (len!= -1) {
				//processValue += len;
				//context.handler.sendMessage(context.handler.obtainMessage(BaseFileBrowserActivity.MSG_PRCESS_VALUE, processValue));
				out.write(buffer);
				buffer = new byte[1024];
				len = in.read(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void local2local(String from, String to) {
		InputStream in = null;
		OutputStream out = null;
		try {
			File fromFile = new File(from);
			if (!fromFile.exists()) {
				System.out.println("待拷贝文件不存在");
				return;
			}
			/* 查找目录，如果不存在，就创建 ，已经存在则覆盖 */
			File toFile = new File(to);
			if (!toFile.exists()) {
				if (!toFile.createNewFile())
					throw new Exception("文件创建失败！");
			}
			in = new BufferedInputStream(new FileInputStream(fromFile));
			out = new BufferedOutputStream(new FileOutputStream(toFile));
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			//int processValue = len;
			while (len != -1) {
				//processValue += len;
				//context.handler.sendMessage(context.handler.obtainMessage(BaseFileBrowserActivity.MSG_PRCESS_VALUE, processValue));
				out.write(buffer);
				buffer = new byte[1024];
				len = in.read(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void local2smb(String from, String to) {
		InputStream in = null;
		OutputStream out = null;
		try {
			File localFile = new File(from);
			SmbFile remoteFile = new SmbFile(to);
			remoteFile.connect();
			if (remoteFile.exists() && remoteFile.isFile()) { // 该路径下已经有一个同名文件
//				destFile.delete();
				return;
			}
			if (!remoteFile.exists())
				remoteFile.createNewFile();
			in = new BufferedInputStream(new FileInputStream(localFile));
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			//int processValue = len;
			while (len != -1) {
				//processValue += len;
				out.write(buffer);
				buffer = new byte[1024];
				len = in.read(buffer);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
////				out.close();
////				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}

	public void smb2local(String from, String to) {

		InputStream in = null;
		OutputStream out = null;
		try {
			SmbFile remoteFile = new SmbFile(from);
			// 这一句很重要
			remoteFile.connect();
			if (!remoteFile.exists()) {
				System.out.println("共享文件不存在");
				return;
			}

			/* 查找文件，如果不存在，就创建 */
			File localFile = new File(to);
			if (!localFile.exists()) {
				if (!localFile.createNewFile())
					throw new Exception("文件不存在，创建失败！");

			}
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			while (len != -1) {
				out.write(buffer);
				buffer = new byte[1024];
				len = in.read(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从共享目录拷贝文件到本地
	 * 
	 * @param remoteUrl
	 *            共享目录上的文件路径
	 * @param localDir
	 *            本地目录
	 */
	public void smbGet(String remoteUrl, String localDir) {
		InputStream in = null;
		OutputStream out = null;
		try {
			SmbFile remoteFile = new SmbFile(remoteUrl);
			// 这一句很重要
			remoteFile.connect();
			if (!remoteFile.exists()) {
				System.out.println("共享文件不存在");
				return;
			}

			String fileName = remoteFile.getName();
			/* 查找目录，如果不存在，就创建 */
			File dirFile = new File(localDir);
			if (!dirFile.exists()) {
				if (!dirFile.mkdirs())
					throw new Exception("目录不存在，创建失败！");
			}
			/* 查找文件，如果不存在，就创建 */
			File localFile = new File(localDir + File.separator + fileName);
			if (!localFile.exists()) {
				if (!localFile.createNewFile())
					throw new Exception("文件不存在，创建失败！");

			}
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			byte[] buffer = new byte[1024];
			while (in.read(buffer) != -1) {
				out.write(buffer);
				buffer = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
/**/
	public int smbGettest(String remoteUrl, String localDir) {
		InputStream in = null;
		OutputStream out = null;
		try {
			SmbFile remoteFile = new SmbFile(remoteUrl);
			// 这一句很重要
			remoteFile.connect();
			if (!remoteFile.exists()) {
				System.out.println("共享文件不存在");
				return 0;
			}

			String fileName = remoteFile.getName();
			/* 查找目录，如果不存在，就创建 */
			File dirFile = new File(localDir);
			if (!dirFile.exists()) {
				if (!dirFile.mkdirs())
					throw new Exception("目录不存在，创建失败！");
			}
			/* 查找文件，如果不存在，就创建 */
			File localFile = new File(localDir + File.separator + fileName);
			if (!localFile.exists()) {
				if (!localFile.createNewFile())
					throw new Exception("文件不存在，创建失败！");

			}
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			byte[] buffer = new byte[1024];
			while (in.read(buffer) != -1) {
				if(ListActivity.mBusy==true){
					out.close();
					in.close();
					File thumbFile = new File(localDir + File.separator + fileName);
					if(thumbFile.exists()){
						thumbFile.delete();
					}
					thumbFile = new File(localDir + File.separator+fileName.replace(".", ""));
					if(thumbFile.exists()){
						thumbFile.delete();
					}
					return 0;
				}
				out.write(buffer);
				buffer = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}
/**/
	/**
	 * 从本地上传文件到共享目录
	 * 
	 * @Version1.0 Sep 25, 2009 3:49:00 PM
	 * @param remoteUrl
	 *            共享文件目录
	 * @param localFilePath
	 *            本地文件绝对路径
	 */
	public void smbPut(String remoteUrl, String localFilePath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			File localFile = new File(localFilePath);

			String fileName = localFile.getName();
			SmbFile remoteFile = new SmbFile(remoteUrl + "/" + fileName);
			in = new BufferedInputStream(new FileInputStream(localFile));
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
			byte[] buffer = new byte[1024];
			while (in.read(buffer) != -1) {
				out.write(buffer);
				buffer = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void copyFile(String from, String to) {
		if (from.contains("smb:/") && to.contains("smb:/")) {
			smb2smb(from, to);
		}
		if (from.contains("smb:/") && !to.contains("smb:/")) {
			smb2local(from, to);
		}
		if (!from.contains("smb:/") && to.contains("smb:/")) {
			local2smb(from, to);
		}
		if (!from.contains("smb:/") && !to.contains("smb:/")) {
			local2local(from, to);
		}
	}
}
