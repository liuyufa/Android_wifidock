package com.hualu.wifistart.smbsrc.widgets;


public class DialogTask extends Thread {
	public CustomDialog dlg;
	public DialogTask(CustomDialog dlg){
		super();
		this.dlg =dlg;
	}
}
