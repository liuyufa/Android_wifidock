package com.hualu.wifistart.wifiset;

public class WiFiInfo {
	private String essid;
	private String bssid;  
	private String ip;  
	private String mask;  
	private String status;
	
	public String getEssid() {  
		return essid;  
	}  
	  
	public void setEssid(String essid) {  
		this.essid = essid;  
	}  
	  
	public String getBssid() {  
		return bssid;  
	}  
	  
	public void setBssid(String bssid) {  
		this.bssid = bssid;  
	}  
  
	public String getIp() {  
		return ip;  
	}  
	  
	public void setIp(String ip) {  
		this.ip = ip;  
	}  
	public String getMask() {  
		return mask;  
	}  
		  
	public void setMask(String mask) {  
		this.mask = mask;  
	}
	public String getStatus() {  
		return status;  
	}  
		  
	public void setStatus(String status) {  
		this.status = status;  
	}
	@Override  
	public String toString() {  
	    return status;  
	}  
}
