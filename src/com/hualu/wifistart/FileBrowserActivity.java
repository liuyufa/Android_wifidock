package com.hualu.wifistart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hualu.wifistart.smbsrc.Helper.SmbHelper;

public class FileBrowserActivity extends Activity {

	private List<String> items=null;
	private List<String> paths=null;
	private String rootPath="/sdcard/";
	private TextView mPath;
	private TextView mPath2;
	private ListView list;  
	private FileAdapter m_FileAdapter;
	private Context mContext;
	private String target;
	public SmbHelper smbHelper = new SmbHelper();
	private FileCopyThread mFileCopyThread = null;
	private SmbFileCopyThread mSmbCopyThread = null;
	private boolean islock;
	private String pasString;
		
	private void getFileDir(String filePath){

		File f=new File(filePath); 
		if(f.exists() && f.canWrite()){
			mPath.setText(filePath);
			items=new ArrayList<String>();
			paths=new ArrayList<String>();
			File[] files=f.listFiles();
			if(!filePath.equals(rootPath)){
				items.add("goroot");
				paths.add(rootPath);
				items.add("goparent");
				paths.add(f.getParent());
			}
			for(int i=0;i<files.length;i++){
				File file=files[i];
//				if(file.isDirectory()){
					items.add(file.getName());
					paths.add(file.getPath());
//				}
			}
			m_FileAdapter = new FileAdapter(this,items,paths);
			list.setAdapter(m_FileAdapter);
			list.setOnItemClickListener(new OnItemClickListener(){
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					
					if(items.get(position).toString().equals("goparent")){
						getFileDir(paths.get(position));
					}else if(items.get(position).toString().equals("goroot")){
						getFileDir(paths.get(position));
						return;
					}else{
						File file=new File(paths.get(position));
						if(file.canWrite()){
							if (file.isDirectory()){
								getFileDir(paths.get(position));
							}else {
								
								mPath.setText(file.getPath());
							}
						}else{
							LinearLayout lay = new LinearLayout(FileBrowserActivity.this);
							lay.setOrientation(LinearLayout.HORIZONTAL);
							ImageView image = new ImageView(FileBrowserActivity.this);
							TextView text = new TextView(FileBrowserActivity.this);
							text.setTextColor(Color.RED);
							text.setTextSize(20);
							text.setText("很抱歉您的权限不足!");
							Toast toast = Toast.makeText(FileBrowserActivity.this, text.getText().toString(), Toast.LENGTH_LONG);
							image.setImageResource(android.R.drawable.stat_sys_warning);
							lay.addView(image);
							lay.addView(text);
							toast.setView(lay);
							toast.show();
						}
					}
				}
			});
		}else{
			LinearLayout lay = new LinearLayout(FileBrowserActivity.this);
			lay.setOrientation(LinearLayout.HORIZONTAL);
			ImageView image = new ImageView(FileBrowserActivity.this);
			TextView text = new TextView(FileBrowserActivity.this);
			text.setTextColor(Color.RED);
			text.setTextSize(20);
			text.setText("无SD卡,无法完成下载!");
			Toast toast = Toast.makeText(FileBrowserActivity.this, text.getText().toString(), Toast.LENGTH_LONG);
			image.setImageResource(android.R.drawable.stat_sys_warning);
			lay.addView(image);
			lay.addView(text);
			toast.setView(lay);
			toast.show();
			this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.filelist);
		mPath = (TextView)this.findViewById(R.id.mPath);
		mPath2 = (TextView)this.findViewById(R.id.mPath2);
		list = (ListView)this.findViewById(R.id.filelist);
		mContext=this;
		getFileDir(rootPath);
		target=getIntent().getExtras().getString("target");
		islock=getIntent().getExtras().getBoolean("islock");
//		mPath.setTextColor(this.getResources().getColor(R.color.white));
//		mPath2.setBackgroundColor((this.getResources().getColor(R.color.green)));
		mPath2.setText("请选择备份源文件或目录:");
		Button ok = (Button)this.findViewById(R.id.fileok);
//		ok.setBackgroundColor(getResources().getColor(R.color.green));
		ok.setPadding(0, 5, 0, 5);
		ok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				/*if(mPath.getText().toString().equals(rootPath)){
					LinearLayout lay = new LinearLayout(FileBrowserActivity.this);
					lay.setOrientation(LinearLayout.HORIZONTAL);
					ImageView image = new ImageView(FileBrowserActivity.this);
					TextView text = new TextView(FileBrowserActivity.this);
					text.setTextColor(FileBrowserActivity.this.getResources().getColor(R.color.text_color));
					text.setTextSize(16);
					text.setText("很抱歉您的权限不足!");
					Toast toast = Toast.makeText(FileBrowserActivity.this, text.getText().toString(), Toast.LENGTH_SHORT);
					image.setImageResource(android.R.drawable.stat_sys_warning);
					lay.addView(image);
					lay.addView(text);
					toast.setView(lay);
					toast.show();
				}else{*/
				
				
				
//					Intent i = new Intent();
//					Bundle b = new Bundle();  
//					b.putString("savePath", mPath.getText().toString());  
//					b.putString("url", FileBrowserActivity.this.getIntent().getStringExtra("url"));  
//					b.putString("fileName", FileBrowserActivity.this.getIntent().getStringExtra("fileName"));  
//					i.putExtras(b);
//					FileBrowserActivity.this.setResult(RESULT_OK, i);
//					FileBrowserActivity.this.finish();
			
				
				//}
					
				
//					Intent intent = new Intent(mContext, DownloadService.class);
//					intent.putExtra("srcDir",mPath.getText().toString());
//					Log.i("mpath+++++=========================", mPath.getText().toString());
//					intent.putExtra("toDir", target);
//					Log.i("target======================", target);
//					intent.putExtra("CopyOperation", "copy");
//					intent.putExtra("isCut", false);
//					startService(intent);
				
				String srcFolder=mPath.getText().toString();
				String dstString = Environment.getExternalStorageDirectory()
						.getPath() + "/lockcache";
				if (islock) {
					File srcFile = new File(srcFolder);
					File desFile = new File(dstString);
					if (!desFile.exists()) {
						desFile.mkdirs();
					}
					if (desFile.exists()) {
						File[] files=desFile.listFiles();
						for (int i = 0; i < files.length; i++) {
							if (files[i].isFile()) {
								files[i].delete();
							}
						}
					}
					File[] files1 = srcFile.listFiles();
					for (int i = 0; i < files1.length; i++) {
						copyFile(files1[i].getAbsolutePath(), dstString);
					}
					File[] files2 = desFile.listFiles();
					for (File f : files2) {
						if (f.isFile()) {
							FileEnDecryptManager.getInstance().InitEncrypt(
									f.getPath(), pasString, "image",null);
							File lf = new File(f.getAbsolutePath()
									+ ".lockfile."
									+ f.getName().substring(
											f.getName().lastIndexOf(".")));
							f.renameTo(lf);
							SharedPreferences sh = mContext
									.getSharedPreferences("lockedfile",
											0);
							Editor editor = sh.edit();
							editor.putString(lf.getName(),
									pasString);
							editor.commit();
						}
					}
				}
				String targetFolder=target;
				if (islock) {
					if (targetFolder.startsWith("smb")) {
						mSmbCopyThread = new SmbFileCopyThread(dstString, targetFolder);
						mSmbCopyThread.start();
					}else {
						mFileCopyThread = new FileCopyThread(dstString, targetFolder);
						mFileCopyThread.start();
						
					}
				}else {
					if (targetFolder.startsWith("smb")) {
						mSmbCopyThread = new SmbFileCopyThread(srcFolder, targetFolder);
						mSmbCopyThread.start();
					}else {
						mFileCopyThread = new FileCopyThread(srcFolder, targetFolder);
						mFileCopyThread.start();
						
					}
				}
			}
		});
		Button cancel = (Button)this.findViewById(R.id.filecancel);
		cancel.setPadding(0, 5, 0, 5);
//		cancel.setBackgroundColor(getResources().getColor(R.color.green));
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FileBrowserActivity.this.finish();
			}
		});
	}
	
	private class FileCopyThread extends Thread {
		String mSrcFolderDir;
		String mDstFolderDir;
		private ProgressDialog mProgressDialog;

		public FileCopyThread(String srcDir, String dstDir) {
			mSrcFolderDir = srcDir;
			mDstFolderDir = dstDir;
			String title = getString(R.string.exporting_data_title);
			String message = getString(R.string.exporting_data_message,
					mDstFolderDir);
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle(title);
			mProgressDialog.setMessage(message);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.show();
		}

		public void run() {
			boolean flag = false;
			File srcFile = new File(mSrcFolderDir);
			if (!srcFile.exists()) { // 源文件夹不存在
				mProgressDialog.dismiss();
				return;
			}

			String dirName = getDirName(mSrcFolderDir);
			String destPath = mDstFolderDir + File.separator + dirName;
			// String destPath = mDstFolderDir + dirName; //jh+

			File destDirFile = new File(destPath);
			if (destDirFile.exists()) { // 目标位置有一个同名文件夹
				File[] fileList = destDirFile.listFiles();
				for (File temp : fileList)
					temp.delete();
				// return false;

			}
			destDirFile.mkdirs(); // 生成目录

			File[] fileList = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
			if (fileList.length == 0) { // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
				return;
			} else {
				mProgressDialog.setMax(fileList.length);
				mProgressDialog.setProgress(0);
				for (File temp : fileList) {
					if (temp.isFile()) { // 文件
						flag = copyFile(temp.getAbsolutePath(), destPath);
					}else {
						//复制文件夹 xuwu 2014 /10 /09
                        Log.i("path======mDstFolderDir+srcFile.getName()", mDstFolderDir+"/"+srcFile.getName());
                        Log.i("path=====srcFile.getName()", srcFile.getName());
						Intent intent = new Intent(mContext, DownloadService.class);
						intent.putExtra("srcDir",temp.getAbsolutePath());
						intent.putExtra("toDir",mDstFolderDir+srcFile.getName()+File.separator);
						intent.putExtra("CopyOperation", "copy");
						intent.putExtra("isCut", false);
						startService(intent);
						
					}
					if (!flag) {
						break;
					}
					mProgressDialog.incrementProgressBy(1);
				}
			}
			mProgressDialog.dismiss();
			finish();
			if (flag) {
			}
		}
	}
	
	private class SmbFileCopyThread extends Thread {
		String mSrcFolderDir;
		String mDstFolderDir;
		private ProgressDialog mProgressDialog;

		public SmbFileCopyThread(String srcDir, String dstDir) {
			mSrcFolderDir = srcDir;
			mDstFolderDir = dstDir;
//			Log.i(TAG, "src=" + mSrcFolderDir + " dst=" + mDstFolderDir);
			String title = getString(R.string.exporting_data_title);
			String message = getString(R.string.exporting_data_message,
					mDstFolderDir);
			mProgressDialog = new ProgressDialog(FileBrowserActivity.this);
			mProgressDialog.setTitle(title);
			mProgressDialog.setMessage(message);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.show();
		}

		public void run() {
			boolean flag = false;
			File srcFile = new File(mSrcFolderDir);
			if (!srcFile.exists()) { // 源文件夹不存在
				mProgressDialog.dismiss();
				return;
			}

			String dirName = getDirName(mSrcFolderDir);
//			String destPath = mDstFolderDir + dirName + File.separator;
//
//			try {
//				SmbFile destDirFile = new SmbFile(destPath);
//				if (destDirFile.exists()) { // 目标位置有一个同名文件夹、
//					destDirFile.delete();
//				} else
//					destDirFile.mkdirs(); // 生成目录
				// xuwu update 20140523
				File[] fileList1 = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
				File[] fileList = new File[fileList1.length];
				int j = 0;
				for (int i = 0; i < fileList1.length; i++) {
					String nameString = fileList1[i].getName();
					Log.i("==================extraName", nameString);
					if (nameString.endsWith(".jpg")
							|| nameString.endsWith(".gif")
							|| nameString.endsWith(".bmp")
							|| nameString.endsWith(".png")
							|| nameString.endsWith(".jpeg")
							|| nameString.endsWith(".tif")) {
						fileList[j] = fileList1[i];
						j++;
					}
				}
				if (fileList.length == 0) { // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
					return;
				} else {
					mProgressDialog.setMax(j);
					mProgressDialog.setProgress(0);
					for (int i = 0; i < j; i++) {
						File temp = fileList[i];
						
						if (temp.isFile()) {
							// 文件
							// flag = copyFileToSmb(temp.getAbsolutePath(),
							// destPath);
							String srcPath = temp.getAbsolutePath();
							String fileName = srcPath.substring(srcPath
									.lastIndexOf(File.separator));
//							smbHelper.copyFile(srcPath, destPath + fileName);
							Intent intent = new Intent(mContext, DownloadService.class);
							intent.putExtra("srcDir",srcPath);
//							intent.putExtra("toDir",destPath + fileName);
							intent.putExtra("toDir",mDstFolderDir);
							intent.putExtra("CopyOperation", "copy");
							intent.putExtra("isCut", false);
							startService(intent);
						}
						mProgressDialog.incrementProgressBy(1);
					}
				}
				mProgressDialog.dismiss();
				finish();
				if (flag) {
				}
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	private static boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			return false;
		}
		// 获取待复制文件的文件名
		String fileName = srcPath
				.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;

		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			destFile.delete();
		}

		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}

		if (flag) {
		}
		return flag;
	}
	
	private static String getDirName(String dir) {
		if (dir.endsWith(File.separator)) { // 如果文件夹路径以"//"结尾，则先去除末尾的"//"
			dir = dir.substring(0, dir.lastIndexOf(File.separator));
		}
		return dir.substring(dir.lastIndexOf(File.separator) + 1);
	}
}

