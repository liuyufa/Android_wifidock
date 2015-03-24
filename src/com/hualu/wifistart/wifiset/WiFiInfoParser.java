package com.hualu.wifistart.wifiset;

import java.io.InputStream;
import java.util.List;

public interface WiFiInfoParser {
	 /** 
	     * 解析输入流 得到WiFiInfo对象集合 
	     * @param is 
	     * @return 
	     * @throws Exception 
	     */  
	    public List<WiFiInfo> parse(InputStream is) throws Exception;  
	      
	    /** 
	     * 序列化WiFiInfo对象集合 得到XML形式的字符串 
	     * @param wifiInfos 
	     * @return 
	     * @throws Exception 
	     */  
	   //public String serialize(List<WiFiInfo> wifiInfos) throws Exception;  
}
