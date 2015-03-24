package com.hualu.wifistart;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hualu.wifistart.update.UpdateNew;
import com.hualu.wifistart.wifiset.ConfigurationActivity;

public class SettingActivity extends Activity {
	final int items[] = { R.string.setting_wifishare, R.string.setting_file,
			R.string.setting_help, R.string.setting_guide,
			R.string.setting_about };
	private UpdateNew mUpdateNew = new UpdateNew(SettingActivity.this);
	ArrayList<String> itemsList = new ArrayList<String>();
	ImageView imgHome, imgBack;
	final int settingItems[] = { R.string.setting_web, R.string.setting_router,
			R.string.setting_upgrade, R.string.setting_about,
			R.string.setting_exit,R.string.setting_help};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsettings);
		ListView localListView = (ListView) findViewById(R.id.copyslist);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new btnClickListener());


		for (int i = 0; i < settingItems.length; i++) {
			itemsList.add(getResources().getString(settingItems[i]));
		}

		if ("EL976".equals(MApplication.ROUTER)) {
			itemsList.remove(1);
		}
		if ("G81".equals(MApplication.ROUTER)) {
			itemsList.remove(1);
		}


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.settingitem, R.id.itemTitle, itemsList);
		localListView.setAdapter(adapter);
		if ("G81".equals(MApplication.ROUTER)
				|| "EL976".equals(MApplication.ROUTER)) {

			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							// 新手指南 用户管理 冲电功能 路由功能 热点功能 U盘功能 在线共享 局端服务 显示设备
							// 进入Web设置 关于 退出
							switch (arg2) {
							case 0: {
								Uri uri = Uri.parse("http://10.10.1.1");
								Intent intent = new Intent(Intent.ACTION_VIEW,
										uri);
								SettingActivity.this.startActivity(intent);
							}
								break;

							case 1: {
								mUpdateNew.checkDailog();
								mUpdateNew.updateRun(true);
							}
								break;
							case 2: {
//								Intent info = new Intent(SettingActivity.this,
//										InfoActivity.class);
//								info.putExtra("infoType", "about");
//								SettingActivity.this.startActivity(info);
							}
								break;
							case 3: {
								Intent intent = new Intent(
										SettingActivity.this,
										MusicService.class);
								stopService(intent);
								SysApplication.getInstance().exit();
								Intent intent1 = new Intent(
										SettingActivity.this,
										DownloadService.class);
								stopService(intent1);
								SettingActivity.this.finish();
								System.exit(0);
							}
								break;
							}
						}

					});
		} else {
			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							// 新手指南 用户管理 冲电功能 路由功能 热点功能 U盘功能 在线共享 局端服务 显示设备
							// 进入Web设置 关于 退出
							switch (arg2) {
							case 0: {
								Uri uri = Uri.parse("http://10.10.1.1");
								Intent intent = new Intent(Intent.ACTION_VIEW,
										uri);
								SettingActivity.this.startActivity(intent);
							}
								break;
							case 1: {
								SettingActivity.this.finish();
								Intent Photo = new Intent(SettingActivity.this,
										ConfigurationActivity.class);
								SettingActivity.this.startActivity(Photo);
								break;
							}
							case 2: {
								mUpdateNew.checkDailog();
								mUpdateNew.updateRun(true);
							}
								break;
							case 3: {
//								Intent info = new Intent(SettingActivity.this,
//										InfoActivity.class);
//								info.putExtra("infoType", "about");
//								SettingActivity.this.startActivity(info);
							}
								break;
							case 4: {
								Intent intent = new Intent(
										SettingActivity.this,
										MusicService.class);
								stopService(intent);
								SysApplication.getInstance().exit();
								Intent intent1 = new Intent(
										SettingActivity.this,
										DownloadService.class);
								stopService(intent1);
								SettingActivity.this.finish();
								System.exit(0);
							}
								break;
							case 5: {
								Intent info = new Intent(SettingActivity.this,
										HelpActivity.class);
								SettingActivity.this.startActivity(info);
							}
							break;
							}
						}

					});
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				SettingActivity.this.finish();
				startActivity(new Intent(SettingActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				SettingActivity.this.finish();
				startActivity(new Intent(SettingActivity.this,
						WifiStarActivity.class));
				// dispachBackKey();
				break;
			}
		}
	}

	public void dispachBackKey() {
		// Log.i(TAG,"dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
