package com.hualu.wifistart.wifiset;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.hualu.wifistart.MApplication;
import com.hualu.wifistart.wifisetting.utils.HttpForWiFiUtils;
import com.hualu.wifistart.wifisetting.utils.SaxForWifiUtil;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class HotThreadWithLooper extends Thread {
	public HotSpotsFeed feed;
	private Handler handler;
	private Handler HotHandler;
	public int flag = 0;
	public List<WiFiInfo> list;
	// private PullWiFiInfoParser pullparser;
	public static String localDir = "/data/data/com.hualu.wifistart/";
	public static int onlyone = 0;

	public HotThreadWithLooper(Handler mHandler) {
		this.HotHandler = mHandler;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WiFiSetMessages.MSG_GET_HOTSPOTS:
					String setcmd = "http://10.10.1.1/:.wop:smode:repeater";
					Log.i("setcmd==================", String
							.valueOf(HttpForWiFiUtils
									.HttpForConnectCheck(setcmd)));
					if (1 == HttpForWiFiUtils.HttpForConnectCheck(setcmd)) {
						String hoturi = "http://10.10.1.1/:.wop:survey";
						String strFileName = localDir + "site_survey.xml";
						flag = HttpForWiFiUtils.HttpForWiFiXml(hoturi,
								strFileName);
						switch (flag) {
						case 0:
							HotHandler
									.sendEmptyMessage(WiFiSetMessages.MSG_GET_HOTSPOTS_FAIL);
							break;
						case 1:
							HotHandler
									.sendEmptyMessage(WiFiSetMessages.MSG_GET_PRE_AP);
							break;
						case 2:
							HotHandler
									.sendEmptyMessage(WiFiSetMessages.MSG_GET_HOTSPOTS_XML);
							break;
						}
					} else {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_GET_HOTSPOTS_FAIL);
					}
					break;
				case WiFiSetMessages.MSG_PARSER_HOTSPOTS:
					String FileName = localDir + "site_survey.xml";
					feed = SaxForWifiUtil.SaxForHotSpots(FileName);
					if (feed != null) {
						Message parsermsg = HotHandler.obtainMessage(
								WiFiSetMessages.MSG_PARSER_HOTSPOTS_SUCCESS, 1,
								1, feed);
						HotHandler.sendMessage(parsermsg);
					} else {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_PARSER_HOTSPOTS_FAIL);
					}
					break;
				case WiFiSetMessages.MSG_CHECK_HOTSPOTS_STATUS:
					Log.i("myhandleMessage", "Checking");
					String chkuri = "http://10.10.1.1/:.wop:jresult";
					// String filename =
					// Environment.getExternalStorageDirectory() +
					// "/wifidock/xml/hotresult.xml";
					// String filename =localDir + "hotresult.xml";
					// flag = HttpForWiFiUtils.HttpForWiFiXml(chkuri,filename);
					flag = HttpForWiFiUtils.HttpForHotcheck(chkuri);
					if (1 == flag) {
						onlyone = 0;
						Log.i("liuyufa test for check", "connected");
						Message message = HotHandler.obtainMessage(
								WiFiSetMessages.MSG_CONNECT_HOTSPOTS_SUCCESS,
								1, 1, list);
						HotHandler.sendMessage(message);
					} else if (flag == 2) {
						onlyone = 0;
						Log.i("liuyufa test for check", "not connected");
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_CHK_CONNECT_HOTSPOTS_FAIL);
					} else {
						if (onlyone < 3) {
							onlyone++;
							HotHandler
									.sendEmptyMessage(WiFiSetMessages.MSG_CHK_CONNECT_HOTSPOTS_FAIL);
						} else {
							HotHandler
									.sendEmptyMessage(WiFiSetMessages.MSG_CHK_CMD_HOTSPOTS_FAIL);
						}
					}
					break;
				case WiFiSetMessages.MSG_CONNECT_HOTSPOTS:
					String uriAPI = (String) msg.obj;
					Log.i("myhandleMessage", uriAPI);
					flag = HttpForWiFiUtils.HttpForWiFi(uriAPI);

					if ("G81".equals(MApplication.ROUTER)
							|| "bw".equals(MApplication.ROUTER)) {

						// flag = 1;
						// String cmdString = "http://10.10.1.1/:.wop:reset";
						// HttpPost httpRequest = new HttpPost(cmdString);
						//
						// BasicHttpParams httpParams = new BasicHttpParams();
						// HttpConnectionParams.setConnectionTimeout(httpParams,
						// 4000);
						// HttpConnectionParams.setSoTimeout(httpParams, 4000);
						// HttpClient client = new
						// DefaultHttpClient(httpParams);
						// try {
						// client.execute(httpRequest);
						// } catch (ClientProtocolException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// } catch (IOException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// "重启连接中，请一分钟后重新连接");
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_SUCCESS);
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_SUCCESS_WAIT);
					} else if (1 == flag) {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_SUCCESS);
					} else {
						Log.i("fail==================", "fail===========");
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_SEND_CONNECT_HOTSPOTS_FAIL);
					}
					break;
				case WiFiSetMessages.MSG_DISCONNECT_AP:
					String uridis = (String) msg.obj;
					Log.i("myhandleMessage", "disconnect ap");
					flag = HttpForWiFiUtils.HttpForWiFi(uridis);
					if (1 == flag) {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_SEND_DISCONNECT_SUCCESS);
					} else {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_SEND_DISCONNECT_FAIL);
					}
					break;
				case WiFiSetMessages.MSG_CHECK_CONNECTED_AP:
					String checkcmd = (String) msg.obj;
					flag = HttpForWiFiUtils.HttpForConnect(checkcmd);
					if (3 == flag) {
						String uri = "http://10.10.1.1/:.wop:disjoin";
						flag = HttpForWiFiUtils.HttpForWiFi(uri);
						if (1 == flag) {
							HttpForWiFiUtils.isscanfailed = 0;
						}
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_ERROR_FOR_AP);
					}
					if (0 == flag) {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_ERROR_FOR_AP);
					}
					if (2 == flag) {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_CHECK_AP_SUCCESS);
					} else {
						HotHandler
								.sendEmptyMessage(WiFiSetMessages.MSG_CHECK_AP_FAIL);
					}
					break;
				}
			}
		};
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		Looper.loop();
	}
}
