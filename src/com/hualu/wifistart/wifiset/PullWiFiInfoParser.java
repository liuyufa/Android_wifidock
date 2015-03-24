package com.hualu.wifistart.wifiset;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullWiFiInfoParser implements WiFiInfoParser {
	 @Override  
	 public List<WiFiInfo> parse(InputStream is) throws Exception {
		 List<WiFiInfo> wifiInfos = null;  
		 WiFiInfo wifiInfo = null;
		 XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例 
		 parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式 
		 int eventType = parser.getEventType();
		 while (eventType != XmlPullParser.END_DOCUMENT) {  
			 switch (eventType) {  
			 case XmlPullParser.START_DOCUMENT: 
				 wifiInfos = new ArrayList<WiFiInfo>();
				 break; 
			 case XmlPullParser.START_TAG:
				 if (parser.getName().equals("JRESULT")) { 
					 wifiInfo = new WiFiInfo();  
				 }else if (parser.getName().equals("ESSID")) { 
					 eventType = parser.next();  
					 wifiInfo.setEssid(parser.getText());
				 }else if (parser.getName().equals("BSSID")) { 
					 eventType = parser.next();  
					 wifiInfo.setBssid(parser.getText());  
				 }else if (parser.getName().equals("IP")) {  
					 eventType = parser.next();  
					 wifiInfo.setIp(parser.getText());  
				 }else if (parser.getName().equals("MASK")) {  
					 eventType = parser.next();  
					 wifiInfo.setMask(parser.getText());  
				 }else if (parser.getName().equals("STATUS")) {  
					 eventType = parser.next();  
					 wifiInfo.setStatus(parser.getText());  
				 }
				 break;
			 case XmlPullParser.END_TAG:
				 if (parser.getName().equals("JRESULT")) {  
					 wifiInfos.add(wifiInfo);  
					 wifiInfo = null;   
				 }
				 break;  
			 }
			 eventType = parser.next();  
		 }
		 return wifiInfos; 
	 }
}
