package com.hualu.wifistart;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.hualu.wifistart.custom.HuzAlertDialog;

public class AlertActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_alert);		
		Dialog dialog = new HuzAlertDialog.Builder(this)
				.setTitle(R.string.title_comfir_delete)
				.setMessage(
						(getResources().getString(R.string.shareDisConnectMsg)))
				.setPositiveButton(R.string.set_done,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
								
								Intent intent2 = new Intent(
										"com.hualu.wifistart.HttpService.FileHttpService");
								stopService(intent2);

								Intent intent1 = new Intent(AlertActivity.this,
										DownloadService.class);
								stopService(intent1);
								
								SysApplication.getInstance().exit();
								
							}
						}).create();
		dialog.show();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		SysApplication.getInstance().exit(); 
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}
}
