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
import android.widget.TextView;

import com.hualu.wifistart.update.UpdateNew;
import com.hualu.wifistart.wifiset.ConfigurationActivity;

public class HelpActivity extends Activity {
	ArrayList<String> itemsList = new ArrayList<String>();
	ImageView imgHome, imgBack;
	final int settingItems[] = { R.string.connection, R.string.readandwrite,
			R.string.toolbar, R.string.selectfiles, R.string.copy_paste,
			R.string.copy_paste2, R.string.deletefiles,
			R.string.fileproperties, R.string.filerename, R.string.takephoto,
			R.string.contactsbackup, R.string.photosbackup,R.string.qanda};

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
		TextView textView = (TextView) findViewById(R.id.menuTitle);
		textView.setText(getResources().getString(R.string.setting_help));
		for (int i = 0; i < settingItems.length; i++) {
			itemsList.add(getResources().getString(settingItems[i]));
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.settingitem, R.id.itemTitle, itemsList);
		localListView.setAdapter(adapter);
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
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "connection");
							HelpActivity.this.startActivity(info);
						}
							break;
						case 1: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "readandwrite");
							HelpActivity.this.startActivity(info);

						}
							break;

						case 2: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "toolbar");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 3: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "selectfiles");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 4: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "copyandpaste");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 5: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "copyandpaste2");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 6: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "deletefiles");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 7: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "fileproperties");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 8: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "filerename");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 9: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "takephoto");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 10: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "contactsbackup");
							HelpActivity.this.startActivity(info);

						}
							break;
						case 11: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "photosbackup");
							HelpActivity.this.startActivity(info);

						}
							break;
//						case 12: {
//							Intent info = new Intent(HelpActivity.this,
//									HelpsActivity.class);
//							info.putExtra("option", "route");
//							HelpActivity.this.startActivity(info);
//							
//						}
//						break;
						case 12: {
							Intent info = new Intent(HelpActivity.this,
									HelpsActivity.class);
							info.putExtra("option", "qanda");
							HelpActivity.this.startActivity(info);
							
						}
						break;
						}
					}

				});

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
				HelpActivity.this.finish();
				startActivity(new Intent(HelpActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				setResult(RESULT_OK);
				finishActivity(1);
				finish();
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
