package com.hualu.wifistart.smbsrc;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.hualu.wifistart.R;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Context;
import android.util.Log;

public class ServiceFileManager extends File
{
	private static final long	serialVersionUID	= 1L;
	private String nowPath ;
	private File     nowFile = null;
	
	public ServiceFileManager(String path)
	{
		super(path);
		this.nowFile = new File(path);
		this.nowPath = path;
	}
	
	public String getNowPath() {return nowPath;}
	
	public void setNowPath(String nowPath) {this.nowPath = nowPath;}
	
	public static  void sortByTypeName(ArrayList<File> files) {
		
		Collections.sort(files,new Comparator<File>() {

			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile())
					return -1;
				if (o1.isFile() && o2.isDirectory())
					return 1;
				return o1.getName().compareTo(o2.getName());
			}
		});

	}

	public static void sortByTypeName2(ArrayList<SmbFile> files) {

		Collections.sort(files, new Comparator<SmbFile>() {

			public int compare(SmbFile o1, SmbFile o2) {
				try {
					if (o1.isDirectory() && o2.isFile())
						return -1;
					if (o1.isFile() && o2.isDirectory())
						return 1;
				} catch (SmbException e) {
					e.printStackTrace();
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

	}
	
	public static void gerAllFileBy(ArrayList<File> filesArrayList,File curfile,String kind) throws Throwable{
		if (curfile.isDirectory()){
			for (File files:curfile.listFiles()){
				if(files.isFile() && Singleton.isBelong(files.getAbsolutePath(),Singleton.fileKinds.get(kind))){
					Log.d("file list:", files.getAbsolutePath());
					filesArrayList.add(files);
				}else if(files.isDirectory()){
					Log.d("dir list:", files.getAbsolutePath());
					gerAllFileBy(filesArrayList,files, kind);
				}
			}
		}
	}
	
	public ArrayList<File> gerFileList() {
		ArrayList<File> filesArrayList = new ArrayList<File>();
		if (nowFile == null && nowFile.isFile()) return filesArrayList;
		
		if (nowFile.isDirectory())
		{
			for (File files:nowFile.listFiles())
			{
				filesArrayList.add(files);
			}
		}
		return filesArrayList;
	}
	
	public static ArrayList<File> gerFileList(String pathString) throws Throwable {
		ArrayList<File> filesArrayList = new ArrayList<File>();
		File nowFile = new File(pathString);
		if (nowFile.isDirectory())
		{
			for (File files:nowFile.listFiles())
			{
				filesArrayList.add(files);
			}
		}
		return filesArrayList;
	}
	
	public String getFileUriString() {
		//String audioString[] = {".mp3",".wma",".oga",".mid",".midi",".wav"};
		String audioString[] = {".mp3",".oga",".wav"};
		//String textString[] = {".txt",".log",".sh",".ini","js",".xml"};
		String textString[] = {".txt",".xml"};
		String imageString[] = {".jpg",".gif",".bmp",".png",".jpeg",".tif"};
		String apkString[] = {".apk"};
		String videoString[] = {".avi",".flv",".f4v",".mpg",".mp4",".rmvb",
				".rm",".mkv",".wmv",".asf",".3gp",".divx",".mpeg","mov","ram","vod"};
		String zip[] = {".zip",".gz"};
		
		File file = nowFile;
		
		for (String aString:audioString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "audio/*";
		for (String aString:textString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "text/*";
		for (String aString:imageString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "image/*";
		for (String aString:videoString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "video/*";
		for (String aString:apkString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "application/vnd.android.package-archive";		
		for (String aString:zip)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "application/zip";		
				
		return "*/*";
	}
	
	public static String getFileUriString(String path) {
//		String audioString[] = {".mp3",".wma",".oga",".mid",".midi",".wav"};
		String audioString[] = {".mp3",".oga",".wav"};
		//String textString[] = {".txt",".log",".sh",".ini","js",".xml"};
		String textString[] = {".txt",".pdf",".doc",".docx","xls",".xml","wps","ppt"};
		String imageString[] = {".jpg",".gif",".bmp",".png",".jpeg",".tif"};
		String apkString[] = {".apk"};
		String videoString[] = {".avi",".flv",".f4v",".mpg",".mp4",".rmvb",
				".rm",".mkv",".wmv",".asf",".3gp",".divx",".mpeg","mov","ram","vod"};//zhaoyu add flv
		
		File file = new File(path);
		
		for (String aString:audioString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "audio/*";
		
		for (String aString:textString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "text/*";
		
		for (String aString:imageString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "image/*";
		
		for (String aString:videoString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "video/*";
		
		for (String aString:apkString)
			if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(aString))	return "application/vnd.android.package-archive";				
				
		return "*/*";
	}
	
	public int getFileIcon() {
		String fileTypeString = getFileUriString();
		if (nowFile.isDirectory())
		{
			return R.drawable.file_type_folder;
		}
		
		if (fileTypeString.equals("audio/*"))
		{
			return R.drawable.file_type_audio;
		}else if (fileTypeString.equals("text/*"))
		{
			return R.drawable.file_type_text;
		}else if (fileTypeString.equals("image/*"))
		{
			return R.drawable.file_type_img;
		}else if (fileTypeString.equals("video/*"))
		{
			return R.drawable.file_type_video;
		}else if (fileTypeString.equals("application/vnd.android.package-archive"))
		{
			return R.drawable.file_type_apk;
		}else
			return R.drawable.file_type_unknow;
	}
	
	public static int getFileIconBy(String caption) {
		String fileTypeString = caption;
		/*if (fileTypeString.isEmpty())
		{
			return R.drawable.file_type_folder;
		}*/
		
		if (fileTypeString.equals("音频"))
		{
			return R.drawable.file_type_audio;
		}else if (fileTypeString.equals("文档"))
		{
			return R.drawable.file_type_text;
		}else if (fileTypeString.equals("图片"))
		{
			return R.drawable.file_type_img;
		}else if (fileTypeString.equals("视频"))
		{
			return R.drawable.file_type_video;
		}else if (fileTypeString.equals("application/vnd.android.package-archive"))
		{
			return R.drawable.file_type_apk;
		}else
			return R.drawable.file_type_unknow;
	}
	
	public static int getFileIcon(String pathString) {
		String fileTypeString = getFileUriString(pathString);
		if (new File(pathString).isDirectory())
		{
			return R.drawable.file_type_folder;
		}
		
		if (fileTypeString.equals("audio/*"))
		{
			return R.drawable.file_type_audio;
		}else if (fileTypeString.equals("text/*"))
		{
			return R.drawable.file_type_text;
		}else if (fileTypeString.equals("image/*"))
		{
			return R.drawable.file_type_img;
		}else if (fileTypeString.equals("video/*"))
		{
			return R.drawable.file_type_video;
		}else if (fileTypeString.equals("application/vnd.android.package-archive"))
		{
			return R.drawable.file_type_apk;
		}else
			return R.drawable.file_type_unknow;
	}

	public static double getFileSizes(File file) throws Exception {// 取得文件大小
		double fileSize = 0;
		if (file.exists())
		{
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			fileSize = fis.available();
			fis.close();
		}
		else
		{
			file.createNewFile();
			System.out.println("文件不存在");
		}
		return fileSize;
	}

	public static double getFileSize(File filef) throws Exception// 取得文件夹大小
	{
		double size = 0;
		File flist[] = filef.listFiles();
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getFileSize(flist[i]);
			} else
			{
				size = size + flist[i].length();
			}
		}
		return size;
	}
	
	public static String FormetFileSize(double fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}
	
	public long getlist(File file) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = file.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}
	
	/**
	* 删除文件，可以是单个文件或文件夹
	* @param   pathString    待删除的文件名
	* @return 文件删除成功返回true,否则返回false
	*/
	public static boolean delete(String pathString,Context context) {
		
		File file = new File(pathString);
		if (!file.exists())
		{
			/*liuyufa change for toast 20140116 satrt*/
			/*
			Toast.makeText(context,  "删除文件失败：" + pathString + "文件不存在", 1).show();
			*/
			ToastBuild.toast(context,"删除文件失败：" + pathString + "文件不存在");
			/*liuyufa change for toast 20140116 end*/
			return false;
		} 
		else
		{
			boolean willReturn;
			if (file.isFile())
			{
				 willReturn=deleteFile(pathString,context);
			} 
			else
			{
				willReturn = deleteDirectory(pathString,context);
			}
			
			 if (willReturn == true)
					/*liuyufa change for toast 20140116 satrt*/
					/*
					Toast.makeText(context, "删除 " + file.getName() + " 成功", 1).show();
					*/
					ToastBuild.toast(context,"删除 " + file.getName() + " 成功");
					/*liuyufa change for toast 20140116 end*/
			 else
					/*liuyufa change for toast 20140116 satrt*/
					/*
					Toast.makeText(context, "删除 " + file.getName() + " 失败", 1).show();
					*/
					ToastBuild.toast(context,"删除 " + file.getName() + " 失败");
					/*liuyufa change for toast 20140116 end*/
			 return willReturn;
		}
	}
		
	/**
	 * 
	 * 删除单个文件
	 * 
	 * @param pathString
	 *                被删除文件的文件名
	 * 
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String pathString,Context context) {
		File file = new File(pathString);
		try
		{
			if (file.isFile() && file.exists() && file.delete())return true;
			else return false;
			
		} catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * 
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *                被删除目录的文件路径
	 * 
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir,Context context) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator))
		{
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		try
		{
			// 如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory())
			{
//			Toast.makeText(context,  "删除目录失败" + dir + "目录不存在！", 1).show();
				return false;
			}
			boolean flag = true;
			// 删除文件夹下的所有文件(包括子目录)
			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				// 删除子文件
				if (files[i].isFile())
				{
					flag = deleteFile(files[i].getAbsolutePath(),context);
					if (!flag)
					{
						break;
					}
				}
				// 删除子目录
				else
				{
					flag = deleteDirectory(files[i].getAbsolutePath(),context);
					if (!flag)
					{
						break;
					}
				}
			}
			if (!flag)	return false;
			// 删除当前目录
			if (dirFile.delete())
				return true;
			else return false;
		} catch (Exception e)
		{
			return false;
		}
		
	}
}
