package com.hualu.wifistart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.hualu.wifistart.music.SearchLRC;
import com.hualu.wifistart.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class SearchTest extends Activity {
	private final String TAG = "getlyric";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.test);
		SearchLRC search = new SearchLRC("´°Íâ","Àîè¡",Environment.getExternalStorageDirectory().getPath() + File.separator + "test.lrc");  
		search.fetchLyric();
	}

	public void getLyric(String key){
    	HttpGet request;
		try {
			String url = "http://www.baidu.com/s?wd=" + URLEncoder.encode("filetype:lrc " + key, "GBK");
			Log.e(TAG,"url = " + url);
			request = new HttpGet(url);
		
    	request.addHeader("Host", "www.baidu.com");
    	request.addHeader("Host", "www.baidu.com");
    	request.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
    	request.addHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
    	request.addHeader("Accept-Language", "zh-cn,zh;q=0.5");
    	request.addHeader("Keep-Alive", "300");
    	request.addHeader("Referer", "http://www.baidu.com/");
    	request.addHeader("Connection", "keep-alive");
    	HttpClient httpClient = new DefaultHttpClient();
    	
    	HttpParams params = httpClient.getParams();		
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		HttpConnectionParams.setSoTimeout(params,2000);
		
		try {
	   		HttpResponse httpResponse = httpClient.execute(request); 
			
			int res = httpResponse.getStatusLine().getStatusCode();
			Log.e(TAG,"res="+res);
	      	if (res == HttpStatus.SC_OK) { 
	      		StringBuilder builder = new StringBuilder(); 
				BufferedReader bufferedReader2 = new BufferedReader( 
				      new InputStreamReader(httpResponse.getEntity().getContent())); 
				//String str2 = ""; 
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2 
				      .readLine()) { 
				  builder.append(s); 
				} 
				Log.i(TAG, ">>>>>>" + builder.toString());
	      	}		
		}catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
		 		Log.i(TAG,"connect fail" + e.getMessage());	 
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//weatherReport.setText("");
				Log.i(TAG,"connect fail " + e.getMessage());		
				e.printStackTrace();
			}
		
		}catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		} 
    	//GetMethod get = new GetMethod("http://www.baidu.com/s?wd=" + URLEncoder.encode("filetype:lrc " + key, "GBK"));
    	//get.addRequestHeader("Host", "www.baidu.com");
    	//get.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
    	//get.addRequestHeade("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
    	//get.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
    	//get.addRequestHeader("Keep-Alive", "300");
    	//get.addRequestHeader("Referer", "http://www.baidu.com/");
    	//get.addRequestHeader("Connection", "keep-alive");
    	//int i = http.executeMethod(get);
    }
}
