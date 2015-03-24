package com.hualu.wifistart.wifisetting.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import android.util.Log;

public final class HttpForWiFiUtils {
	public static int time = 0;
	public static String info;
	public static String pppoeinfo;
	public static String staticinfo;
	public static String connectssid;
	public static String preapssid;
	public static int isscanfailed = 0;
	public static String localDir = "/data/data/com.hualu.wifistart/";
	private static final int SO_TIMEOUT = 40* 1000; // 设置等待数据超时时间4秒钟
	private static final int REQUEST_TIMEOUT = 40* 1000;// 设置请求超时4秒钟
	private static final int SO_TIMEOUT1 = 40 * 1000; // 设置等待数据超时时间12秒钟
	private static final int REQUEST_TIMEOUT1 = 40 * 1000;// 设置请求超时10秒钟

	public final static int HttpForWiFiXml(String uriAPI, String strFileName) {
		HttpPost httpRequest = new HttpPost(uriAPI);
		if (strFileName != null) {
			File xmlfile = new File(strFileName);
			if (xmlfile.exists()) {
				xmlfile.delete();
			}
		}
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT1);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT1);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			// HttpResponse httpResponse= new
			// DefaultHttpClient().execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				Log.i("myHttpMessage1", strResult);
				if (strResult.contains("repeater enabled, scan failed")) {
					isscanfailed = 1;
					return 1;
				}
				if (strResult.contains("dhcp")) {
					return 3;
				}

				if (strResult.contains("connected")) {
					return 4;
				}
				if (strResult.contains("not connected")) {
					return 5;
				}
				Log.i("myHttpMessage2", strResult);
				String mSavePath = localDir;
				File file = new File(mSavePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				FileWriter fw = new FileWriter(strFileName);
				fw.write(strResult);
				fw.flush();
				fw.close();
				return 2;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static int HttpForHotcheck(String uriAPI) {
		HttpPost httpRequest = new HttpPost(uriAPI);
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			// HttpResponse httpResponse= new
			// DefaultHttpClient().execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				Log.i("myHttpMessage2", strResult);
				if (strResult.contains("connected")
						&& (!strResult.contains("Not-Associated"))) {
					preapssid = strResult;
					Log.i("myHttpMessage2", strResult);
					return 1;
				}
				if (strResult.contains("not connected")
						|| strResult.contains("connecting")
						|| strResult.contains("Not-Associated")) {
					time++;
					Log.i("myHttpMessage2", strResult);
					return 2;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static int HttpForWiFi(String uriAPI) {
		HttpPost httpRequest = new HttpPost(uriAPI);
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			Log.i("=======================response", String
					.valueOf(httpResponse.getStatusLine().getStatusCode()));

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return 1;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
//			String cmdString = "http://10.10.1.1/:.wop:bw";
//			HttpPost httpRequest1 = new HttpPost(cmdString);
//
//			BasicHttpParams httpParams = new BasicHttpParams();
//			HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
//			HttpConnectionParams.setSoTimeout(httpParams, 4000);
//			HttpClient client = new DefaultHttpClient(httpParams);
//			HttpResponse httpResponse;
//
//			try {
//				httpResponse = client.execute(httpRequest1);
//				if (httpResponse.getStatusLine().getStatusCode() == 200) {
//					String strResult = EntityUtils.toString(httpResponse
//							.getEntity());
//					if (strResult.contains("bw")) {
//						return 1;
//					}
//				}
//			} catch (ClientProtocolException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}

		return 0;
	}

	public final static int HttpForGetMode(String uriAPI, int mode) {
		HttpPost httpRequest = new HttpPost(uriAPI);
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				if (strResult.contains("3g")) {
					Log.i("myHttpMessage", "3g");
					return 1;
				}

				if (strResult.contains("repeater")) {
					Log.i("myHttpMessage", "repeater");
					return 2;
				}
				if (strResult.contains("router")) {
					Log.i("myHttpMessage", "router");
					if (1 == mode) {
						return 3;
					} else {
						if (strResult.contains("dhcp")) {
							return 3;
						} else if (strResult.contains("pppoe")) {
							info = strResult;
							pppoeinfo = strResult;
							return 4;
						} else if (strResult.contains("static")) {
							info = strResult;
							staticinfo = strResult;
							return 5;
						} else {
							return 0;
						}
					}
				}
				return 6;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static int HttpForConnect(String uriAPI) {
		HttpPost httpRequest = new HttpPost(uriAPI);
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			Log.i("===================responsestatuscode", String
					.valueOf(httpResponse.getStatusLine().getStatusCode()));
			// Log.e("=========strResult",
			// EntityUtils.toString(httpResponse.getEntity()));

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				Log.e("=========strResult", strResult);
				if (strResult.contains("router-repeater")
						|| strResult.contains("router-repeater-3g")) {
					return 1;
				}
				Log.i("connected ap", strResult);
				if (strResult.contains("connecting")
						&& HttpForWiFiUtils.isscanfailed == 1) {
					return 3;
				}
				if (strResult.contains("JRESULT")
						&& strResult.contains("ESSID")
						&& !strResult.contains("not")) {
					preapssid = strResult;
					return 2;
				}

			}
			return 0;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static int HttpForConnectCheck(String uriAPI) {
		HttpPost httpRequest = new HttpPost(uriAPI);
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return 1;
			}
			return 0;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
