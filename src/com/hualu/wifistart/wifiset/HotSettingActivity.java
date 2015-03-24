package com.hualu.wifistart.wifiset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jcifs.smb.Dfs;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import com.hualu.wifistart.MApplication;
import com.hualu.wifistart.R;
import com.hualu.wifistart.WifiStarActivity;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.filebrowser.SearchDialog;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HotSettingActivity extends Activity {
	public HotSpotsAdapter adapter;
	public Runnable ConnectRunable;
	public int isthread = 0;
	private EditText editText;
	private Button btnwifimode;
	private String sendcmd, passwd;
	private ListView hslistview;
	private HotThreadWithLooper thread;
	private ArrayList<Map<String, Object>> data;
	public static Handler Hothandler;
	private Runnable gethotRunable, getxmlRunable, preapRunable, disconRunable,
			connectRunable;
	private Runnable CheckRunable;
	private SearchDialog hotspotsDialog;
	public MApplication appState;
	public List<WiFiInfo> list;
	ImageView imgHome, imgBack;
	public static String preap = "";
	public String essid, channel, encryption;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_setting);
		imgHome = (ImageView) findViewById(R.id.homebg);
		imgHome.setOnClickListener(new btnClickListener());
		imgBack = (ImageView) findViewById(R.id.backbg);
		imgBack.setOnClickListener(new btnClickListener());
		btnwifimode = (Button) findViewById(R.id.hotBtn);
		btnwifimode.setOnClickListener(new btnClickListener());
		hslistview = (ListView) findViewById(R.id.hotListView);
		appState = (MApplication) getApplicationContext();
		appState.setState(true);
		Hothandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.MSG_GET_HOTSPOTS_XML:
					Hothandler.removeCallbacks(gethotRunable);
					getxmlRunable = new Runnable() {
						@Override
						public void run() {
							thread.getHandler().sendEmptyMessage(
									WiFiSetMessages.MSG_PARSER_HOTSPOTS);
						}
					};
					Hothandler.post(getxmlRunable);
					break;
				case WiFiSetMessages.MSG_PARSER_HOTSPOTS_SUCCESS:
					hotspotsDialog.dismiss();
					Hothandler.removeCallbacks(getxmlRunable);
					HotSpotsFeed feed = (HotSpotsFeed) msg.obj;
					data = new ArrayList<Map<String, Object>>();
					if (feed.getAllItemForListView() != null) {
						data = feed.getAllItemForListView();
						if (data.size() > 0) {
							showList();
						} else {
							disConnectAp();
						}
					}
					break;
				case WiFiSetMessages.MSG_GET_HOTSPOTS_FAIL:
					hotspotsDialog.dismiss();
					Hothandler.removeCallbacks(gethotRunable);
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_hot_fail);
					break;
				case WiFiSetMessages.MSG_PARSER_HOTSPOTS_FAIL:
					hotspotsDialog.dismiss();
					Hothandler.removeCallbacks(getxmlRunable);
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_hot_fail);
					break;
				case WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_SUCCESS_WAIT:
					hotspotsDialog.dismiss();
					Hothandler.removeCallbacks(getxmlRunable);
					showresetdialog();
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_hot_reset_wait);
					break;
				case WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_SUCCESS:
					Hothandler.removeCallbacks(connectRunable);
					CheckRunable = new Runnable() {
						@Override
						public void run() {
							if (HttpForWiFiUtils.time < 8) {
								thread.getHandler()
										.sendEmptyMessage(
												WiFiSetMessages.MSG_CHECK_HOTSPOTS_STATUS);
								Hothandler.postDelayed(this, 2500);
							}
						}
					};
					Hothandler.postDelayed(CheckRunable, 4000);
					break;
				case WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_FAIL:
					hotspotsDialog.dismiss();
					Hothandler.removeCallbacks(connectRunable);
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_send_error);
					break;
				case WiFiSetMessages.MSG_CONNECT_HOTSPOTS_SUCCESS:
					Hothandler.removeCallbacks(CheckRunable);
					hotspotsDialog.dismiss();
					list = (List<WiFiInfo>) msg.obj;
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_connect_success);
					preap = parsePreApInfo();
					showList();
					break;
				case WiFiSetMessages.MSG_CHK_CONNECT_HOTSPOTS_FAIL:
					if (7 <= HttpForWiFiUtils.time) {
						Hothandler.removeCallbacks(CheckRunable);
						hotspotsDialog.dismiss();
						String uridis = "http://10.10.1.1/:.wop:disjoin";
						HttpForWiFiUtils.HttpForWiFi(uridis);
						ToastBuild.toast(HotSettingActivity.this,
								R.string.set_connect_timeout);
					}
					break;
				case WiFiSetMessages.MSG_CHK_CMD_HOTSPOTS_FAIL:
					Hothandler.removeCallbacks(CheckRunable);
					hotspotsDialog.dismiss();
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_disconnect);
					break;
				case WiFiSetMessages.MSG_GET_PRE_AP:
					Hothandler.removeCallbacks(gethotRunable);
					hotspotsDialog.dismiss();
					getPreAp();
					break;
				case WiFiSetMessages.MSG_SEND_DISCONNECT_SUCCESS:
					Hothandler.removeCallbacks(disconRunable);
					HttpForWiFiUtils.preapssid = "";
					preap = "";
					Log.i("preap", "empty");
					hotspotsDialog.dismiss();
					hotspotsDialog = new SearchDialog(HotSettingActivity.this,
							R.layout.sethot_dialog, R.style.Theme_dialog);
					hotspotsDialog.show();
					gethotrun();
					break;
				case WiFiSetMessages.MSG_SEND_DISCONNECT_FAIL:
					Hothandler.removeCallbacks(disconRunable);
					hotspotsDialog.dismiss();
					ToastBuild.toast(HotSettingActivity.this,
							R.string.set_disconnect_fail);
					break;
				case WiFiSetMessages.MSG_CHECK_AP_SUCCESS:
					Hothandler.removeCallbacks(preapRunable);
					preap = parsePreApInfo();
					showpredialog();
					break;
				case WiFiSetMessages.MSG_CHECK_AP_FAIL:
					Hothandler.removeCallbacks(preapRunable);
					break;
				case WiFiSetMessages.MSG_ERROR_FOR_AP:
					disConnectAp();
					break;
				}
			}
		};
		hotspotsDialog = new SearchDialog(HotSettingActivity.this,
				R.layout.sethot_dialog, R.style.Theme_dialog);
		hotspotsDialog.show();
		if (0 == isthread) {
			HttpForWiFiUtils.time = 0;
			thread = new HotThreadWithLooper(Hothandler);
			thread.start();
			isthread = 1;
		}
		gethotrun();
	}

	private void disConnectAp() {
		disconRunable = new Runnable() {
			@Override
			public void run() {
				sendcmd = "http://10.10.1.1/:.wop:disjoin";
				Message msg = thread.getHandler().obtainMessage(
						WiFiSetMessages.MSG_DISCONNECT_AP, 1, 1, sendcmd);
				thread.getHandler().sendMessage(msg);
			}
		};
		Hothandler.post(disconRunable);
	}

	private void resetg81() {
		String cmdString = "http://10.10.1.1/:.wop:reset";
		HttpPost httpRequest = new HttpPost(cmdString);

		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				4000);
		HttpConnectionParams.setSoTimeout(httpParams, 4000);
		HttpClient client = new DefaultHttpClient(httpParams);
		try {
			client.execute(httpRequest);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getPreAp() {
		preapRunable = new Runnable() {
			@Override
			public void run() {
				String cmd = "http://10.10.1.1/:.wop:jresult";
				Message msg = thread.getHandler().obtainMessage(
						WiFiSetMessages.MSG_CHECK_CONNECTED_AP, 1, 1, cmd);
				thread.getHandler().sendMessage(msg);
			}
		};
		Hothandler.post(preapRunable);
	}

	private String parsePreApInfo() {
		String ssid = "";
		int start = 0, end = 0;
		String str = HttpForWiFiUtils.preapssid;
		if (str != null) {
			int len = str.length();
			for (int i = 45; i < len; i++) {
				if ('<' == str.charAt(i) && 'E' == str.charAt(i + 1)) {
					start = i + 7;
					i = i + 5;
				}
				if ('/' == str.charAt(i) && 'E' == str.charAt(i + 1)) {
					end = i - 1;
					break;
				}
			}
			ssid = str.substring(start, end);
		}
		return ssid;
	}

	private void showpredialog() {
		final HuzAlertDialog dialog = new HuzAlertDialog.Builder(
				HotSettingActivity.this)
				.setTitle("已中继" + preap + "确定断开")
				.setPositiveButton(R.string.set_done,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
								disConnectAp();
							}
						})
				.setNegativeButton(R.string.set_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
							}
						}).create();
		dialog.show();

	}

	private void showresetdialog() {
		final HuzAlertDialog dialog = new HuzAlertDialog.Builder(
				HotSettingActivity.this)
				.setTitle(R.string.set_hot_reset_choice)
				.setPositiveButton(R.string.set_done,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
								resetg81();
							}
						})
				.setNegativeButton(R.string.set_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
							}
						}).create();
		dialog.show();

	}

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				isthread = 0;
				HotSettingActivity.this.finish();
				startActivity(new Intent(HotSettingActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				isthread = 0;
				dispachBackKey();
				break;
			case R.id.hotBtn:
				hotspotsDialog = new SearchDialog(HotSettingActivity.this,
						R.layout.sethot_dialog, R.style.Theme_dialog);
				hotspotsDialog.show();
				gethotrun();
				break;
			}
		}
	}

	private void gethotrun() {
		HttpForWiFiUtils.time = 0;
		preap = "";
		gethotRunable = new Runnable() {
			@Override
			public void run() {
				thread.getHandler().sendEmptyMessage(
						WiFiSetMessages.MSG_GET_HOTSPOTS);
			}
		};
		Hothandler.postDelayed(gethotRunable, 1000);
		// Hothandler.post(gethotRunable);
	}

	/*
	 * @Override protected void onStop() { super.onStop(); }
	 */
	public void showList() {
		adapter = new HotSpotsAdapter(this);
		hslistview.setAdapter(adapter);
		hslistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				if (id == -1) {
					return;
				}
				int realPosition = (int) id;
				essid = (String) data.get(realPosition).get("ESSID");
				if (preap.length() > 0) {
					showpredialog();
				} else {
					channel = (String) data.get(realPosition).get("CHANNEL");
					encryption = (String) data.get(realPosition).get(
							"ENCRYPTION");
					if (encryption.equals("WPA") || encryption.equals("WPA2")
							|| encryption.equals("WPAmix")) {
						encryption = "WPA";
						sendcmd = "http://10.10.1.1/:.wop:join:" + essid + ":"
								+ channel + ":" + encryption + ":";
					}
					if (encryption.equals("NONE")) {
						encryption = "None";
						sendcmd = "http://10.10.1.1/:.wop:join:" + essid + ":"
								+ channel + ":" + encryption;
					}
					editText = new EditText(HotSettingActivity.this);
					new HuzAlertDialog.Builder(HotSettingActivity.this)
							.setTitle("请输入密码")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(editText)
							.setPositiveButton(R.string.set_connect,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// 隐藏软键盘
											InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
											if (imm.isActive()) {
												imm.toggleSoftInput(
														InputMethodManager.SHOW_IMPLICIT,
														InputMethodManager.HIDE_NOT_ALWAYS);
											}
											passwd = editText.getText()
													.toString();
											if (0 == passwd.length()
													&& encryption.equals("WPA")) {
												ToastBuild
														.toast(HotSettingActivity.this,
																R.string.set_password_empty);
												dialog.dismiss();
											} else if (passwd.length() < 8) {
												ToastBuild
														.toast(HotSettingActivity.this,
																R.string.set_password_error);
												dialog.dismiss();
											} else {
												hotspotsDialog = new SearchDialog(
														HotSettingActivity.this,
														R.layout.sethotconnect_dialog,
														R.style.Theme_dialog);
												hotspotsDialog.show();
												connectRunable = new Runnable() {
													@Override
													public void run() {
														if (encryption
																.equals("None")) {
															passwd = "";
														}
														sendcmd = sendcmd
																+ passwd;
														Log.i("sendcmd",
																sendcmd);
														HttpForWiFiUtils.time = 0;
														Message msg = thread
																.getHandler()
																.obtainMessage(
																		WiFiSetMessages.MSG_CONNECT_HOTSPOTS,
																		1, 1,
																		sendcmd);
														thread.getHandler()
																.sendMessage(
																		msg);
													}
												};
												Hothandler.post(connectRunable);
												// String cmdString =
												// "http://10.10.1.1/:.wop:reset";
												// HttpPost httpRequest = new
												// HttpPost(cmdString);
												//
												// BasicHttpParams httpParams =
												// new BasicHttpParams();
												// HttpConnectionParams.setConnectionTimeout(httpParams,
												// 4000);
												// HttpConnectionParams.setSoTimeout(httpParams,
												// 4000);
												// HttpClient client = new
												// DefaultHttpClient(httpParams);
												// HttpResponse httpResponse;
												// try {
												// httpResponse =
												// client.execute(httpRequest);
												// } catch
												// (ClientProtocolException e) {
												// // TODO Auto-generated catch
												// block
												// e.printStackTrace();
												// } catch (IOException e) {
												// // TODO Auto-generated catch
												// block
												// e.printStackTrace();
												// }
												// ToastBuild.toast(HotSettingActivity.this,
												// "重启连接中，请一分钟后重新连接wifi");
											}
										}
									})
							.setNegativeButton(R.string.set_cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}
									}).show();
				}
			}
		});
	}

	public class HotSpotsAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private HotSpotsAdapter(Context context) {
			// 根据context上下文加载布局，这里的是HotSettingActivity本身，即this
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// How many items are in the data set represented by this Adapter.
			// 在此适配器中所代表的数据集中的条目数
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// Get the data item associated with the specified position in the
			// data set.
			// 获取数据集中与指定索引对应的数据项
			return position;
		}

		@Override
		public long getItemId(int position) {
			// Get the row id associated with the specified position in the
			// list.
			// 获取在列表中与指定索引对应的行id
			return position;
		}

		// Get a View that displays the data at the specified position in the
		// data set.
		// 获取一个在数据集中指定索引的视图来显示数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			// 如果缓存convertView为空，则需要创建View
			if (convertView == null) {
				holder = new ViewHolder();
				// 根据自定义的Item布局加载布局
				convertView = mInflater.inflate(R.layout.listitem_sid, null);
				holder.essid = (TextView) convertView.findViewById(R.id.ssid1);
				holder.signal = (ImageView) convertView
						.findViewById(R.id.hotIntensity);
				// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (preap.length() > 1
					&& ((String) data.get(position).get("ESSID")).equals(preap)) {
				// holder.essid.setTextColor(0xFFFF0000);
				holder.essid.setTextColor(0xFF66CD00);
			} else {
				holder.essid.setTextColor(0xFF556B2F);
			}
			holder.essid.setText((String) data.get(position).get("ESSID"));
			int level = Integer.parseInt((String) data.get(position).get(
					"SIGNAL"));
			if ((String) data.get(position).get("ENCRYPTION") == "NONE") {
				if (level >= -53) {
					holder.signal.setImageResource(R.drawable.hots4);
				} else if (level <= -54 && level >= -81) {
					holder.signal.setImageResource(R.drawable.hots3);
				} else if (level <= -72 && level >= -82) {
					holder.signal.setImageResource(R.drawable.hots2);
				} else if (level <= -83 && level >= -91) {
					holder.signal.setImageResource(R.drawable.hots1);
				} else {
					holder.signal.setImageResource(R.drawable.hots0);
				}
			} else {
				if (level >= -53) {
					holder.signal.setImageResource(R.drawable.hotslock4);
				} else if (level <= -54 && level >= -81) {
					holder.signal.setImageResource(R.drawable.hotslock3);
				} else if (level <= -72 && level >= -82) {
					holder.signal.setImageResource(R.drawable.hotslock2);
				} else if (level <= -83 && level >= -91) {
					holder.signal.setImageResource(R.drawable.hotslock1);
				} else {
					holder.signal.setImageResource(R.drawable.hotslock0);
				}
			}
			return convertView;
		}

	}

	// ViewHolder静态类
	static class ViewHolder {
		public TextView essid;
		public ImageView signal;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}

	public void dispachBackKey() {
		// Log.i(TAG,"dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
}
