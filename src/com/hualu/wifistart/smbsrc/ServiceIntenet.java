package com.hualu.wifistart.smbsrc;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.hualu.wifistart.R;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.wifisetting.utils.LanguageCheck;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;


public class ServiceIntenet
{
	private Context context;
	private String packageString;
	private Handler handler;
	
	private static final int MSG_CLOSE_PROGRESS = 1;
	private static final int MSG_SHWO_DIALOG = 2;
	private static final int MSG_PROGRESS_SET_TEXT = 3;
	
	public ServiceIntenet(Context context, String packageString) {
		super();
		this.context = context;
		this.packageString = packageString;
	}

	public void uploadFile() {

		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(R.string.set_please_wait);
		if(LanguageCheck.isZh()){
			progressDialog.setMessage("正在努力上传中...");
		}
		else{
			progressDialog.setMessage("uploading...");
		}
		progressDialog.show();

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
					case MSG_CLOSE_PROGRESS:
						progressDialog.dismiss();
						break;
					case MSG_SHWO_DIALOG:
						showDialog((String)msg.obj);
						break;
					case MSG_PROGRESS_SET_TEXT:
						progressDialog.setMessage((String)msg.obj);
					default:
						break;
				}
				//super.handleMessage(msg);
			}

		};

		new Thread() {

			@Override
			public void run() {
				FTPClient ftp = new FTPClient();
				// SERVER ip
				String server = "221.1.217.92";
				// 登录密码和账号
				String username = "sdcardmanage";
				String password = "kingsollyu";
				String file = packageString;
				try
				{
					ftp.setDefaultTimeout(30000);
					ftp.connect(server);
					int reply = ftp.getReplyCode();
					
					// 开始与远程空间建立连接
					handler.obtainMessage(MSG_PROGRESS_SET_TEXT,context.getString(R.string.intent_progress_ftp_connect)).sendToTarget();//开始与远程空间建立连接
					if (!FTPReply.isPositiveCompletion(reply))
					{
						ftp.disconnect();
						handler.obtainMessage(MSG_SHWO_DIALOG,context.getString(R.string.intent_connect_ftp_failue)).sendToTarget();
						//ftp服务器连接失败！
					} 
					else
					{
						// 进行登录验证合法性
						handler.obtainMessage(MSG_PROGRESS_SET_TEXT,context.getString(R.string.intent_progress_ftp_login)).sendToTarget();
						//进行登录验证合法性
						if (ftp.login(username,password))
						{
							ftp.enterLocalPassiveMode();
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
							FileInputStream fis = new FileInputStream(new File(file));
							// 开始进行上传共享操作
							handler.obtainMessage(MSG_PROGRESS_SET_TEXT,context.getString(R.string.intent_progress_ftp_update)).sendToTarget();
							//开始进行上传共享操作
							if (ftp.storeFile(getRandomString(16)+ ".db",fis) == true) 
								handler.obtainMessage(MSG_SHWO_DIALOG,context.getString(R.string.intent_ftp_update_success)).sendToTarget();
								//您的共享操作成功！\n\n<h1>谢谢您的共享，本软件有了您的支持将会更加完美！
							else
								handler.obtainMessage(MSG_SHWO_DIALOG,context.getString(R.string.intent_ftp_update_failue)).sendToTarget();
								//文件上传失败！
						} else
						{
							handler.obtainMessage(MSG_SHWO_DIALOG,context.getString(R.string.intent_ftp_login_failure)).sendToTarget();
						}
						ftp.logout();
					}
				} catch (Exception e)
				{
					handler.obtainMessage(MSG_SHWO_DIALOG,context.getString(R.string.intent_ftp_Exception) + e.toString()).sendToTarget();
				} finally
				{
					if (ftp.isConnected())
					{
						try
						{
							ftp.disconnect();
						} catch (Exception ioe)
						{
							handler.obtainMessage(MSG_SHWO_DIALOG,context.getString(R.string.intent_ftp_Exception) + ioe.toString()).sendToTarget();
						}
					}
					handler.obtainMessage(MSG_CLOSE_PROGRESS).sendToTarget();
					
				}
				//super.run();
			}

		}.start();

	}
	
	private void showDialog(String mess) {
		new HuzAlertDialog.Builder(context)
				.setTitle(context.getString(R.string.intent_alert_dialog_title))
				.setMessage(mess)
				.setNegativeButton(context.getString(R.string.intent_alert_dialog_OK),( OnClickListener)null).show();
	}
	
	public static void showDialog(String mess,Context context) {
		new HuzAlertDialog.Builder(context)
				.setTitle(context.getString(R.string.intent_alert_dialog_title))
				.setMessage(mess)
				.setNegativeButton(context.getString(R.string.intent_alert_dialog_OK),( OnClickListener)null).show();
	}

	public static String getRandomString(int len){  
		        String returnStr="";  
		        char[] ch=new char[len];  
		        Random rd=new Random();  
		        for(int i=0;i<len;i++){  
		            ch[i]=(char)(rd.nextInt(9)+97);  
		        }  
		        returnStr=new String(ch);  
		        return returnStr;  
		    }  
}
