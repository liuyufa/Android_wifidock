package com.hualu.wifistart.wifiset;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class HotSpotsHandler extends DefaultHandler{
	HotSpotsFeed HotSpotsFeed;
	HotSpotsItem HotSpotsItem;
    final int HS_ESSID = 1;
    final int HS_BSSID = 2;
    final int HS_CHANNEL = 3;
    final int HS_QUALITY = 4;
    final int HS_SIGNAL = 5;
    final int HS_ENCRYPTION = 6;
    int currentstate = 0;
    
    public HotSpotsHandler(){}
    
    public HotSpotsFeed getFeed(){
        return HotSpotsFeed;
    }
    
    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
    	HotSpotsFeed = new HotSpotsFeed();
    	HotSpotsItem = new HotSpotsItem();
    }
    
    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        if(localName.equals("LIST")){
            currentstate = 0;
            return;
        }
        if(localName.equals("APINFO")){
        	HotSpotsItem = new HotSpotsItem();
            return;
        }
        if(localName.equals("ESSID")){
            currentstate = HS_ESSID;
            return;
        }
        if(localName.equals("BSSID")){
            currentstate = HS_BSSID;
            return;
        }
        if(localName.equals("CHANNEL")){
            currentstate = HS_CHANNEL;
            return;
        }
        if(localName.equals("QUALITY")){
            currentstate = HS_QUALITY;
            return;
        }
        if(localName.equals("SIGNAL")){
            currentstate = HS_SIGNAL;
            return;
        }
        if(localName.equals("ENCRYPTION")){
            currentstate = HS_ENCRYPTION;
            return;
        }
        currentstate = 0;
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        if(localName.equals("ESSID")){
        	HotSpotsFeed.addItem(HotSpotsItem);
            return;
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        String theString = new String(ch, start, length);
        switch(currentstate){
        case HS_ESSID:
        	HotSpotsItem.setEssid(theString);
            currentstate = 0;
            break;
        case HS_BSSID:
        	HotSpotsItem.setBssid(theString);
            currentstate = 0;
            break;
        case HS_CHANNEL:
        	HotSpotsItem.setChannel(theString);
            currentstate = 0;
            break;
        case HS_QUALITY:
        	HotSpotsItem.setQuality(theString);
            currentstate = 0;
            break;
        case HS_SIGNAL:
        	HotSpotsItem.setSignal(theString);
            currentstate = 0;
            break;
        case HS_ENCRYPTION:
        	HotSpotsItem.setEncryption(theString);
            currentstate = 0;
            break;
        default:
            return;
        }
    }
}