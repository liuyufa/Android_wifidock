package com.hualu.wifistart.wifisetting.utils;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.hualu.wifistart.wifiset.HotSpotsFeed;
import com.hualu.wifistart.wifiset.HotSpotsHandler;

public final class SaxForWifiUtil {
	public final static HotSpotsFeed SaxForHotSpots(String filename){
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			HotSpotsHandler handler = new HotSpotsHandler();
			reader.setContentHandler(handler);
			FileInputStream fileis = new FileInputStream(filename);   
			InputSource is = new InputSource(fileis);
			reader.parse(is);
			return handler.getFeed();
		 	} catch (ParserConfigurationException e) {
		 		// TODO Auto-generated catch block
		 		e.printStackTrace();
		 	} catch (SAXException e) {
		 		// TODO Auto-generated catch block
		 		e.printStackTrace();
		 	} catch (IOException e) {
		 		// TODO Auto-generated catch block
		       e.printStackTrace();
		 	}
		return null;
	}
}
