package com.hualu.wifistart.smbsrc.widgets;

import com.hualu.wifistart.custom.HuzAlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class CustomDialog extends Dialog {


	public boolean isOK = false;
	public boolean isWait = false;
	static public  Context context;

	public void ShowOK(String title, String msg) {
		new HuzAlertDialog.Builder(this.getContext())
				.setMessage(msg)
				.setTitle(title)
				.setCancelable(false)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								isOK = true;
							}
						}).show();
	}
	public Boolean ShowOKCancel(String title,String msg){
		new HuzAlertDialog.Builder(this.getContext())
		.setMessage(msg)
		.setTitle(title)
		.setCancelable(true)
		.setNeutralButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						isOK = true;
						isWait = true;
					}
				})
		.show();
		return isOK;
	}
	
	public Boolean ShowWaitOKCancel(String title,String msg){
		return true;
	}
	public CustomDialog(Context context) {
		super(context);
	}
	public CustomDialog(Context context,int theme) {
		super(context,theme);
	}
}
