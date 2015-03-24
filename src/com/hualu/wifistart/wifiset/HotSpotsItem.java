package com.hualu.wifistart.wifiset;


public class HotSpotsItem {
    public static String ESSID = "ESSID";
    public static String BSSID = "BSSID";
    public static String CHANNEL = "CHANNEL";
    public static String QUALITY = "QUALITY";
    public static String SIGNAL = "SIGNAL";
    public static String ENCRYPTION = "ENCRYPTION";
    public String essid;
    public String bssid;
    public String channel;
    public String quality;
    public String signal;
    public String encryption;
    public HotSpotsItem() {
    }
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
    public String getChannel() {
        return channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public String getQuality() {
        return quality;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }
    public String getSignal() {
        return signal;
    }
    public void setSignal(String signal) {
        this.signal = signal;
    }
    public String getEncryption() {
        return encryption;
    }
    public void setEncryption(String encryption) {
        this.encryption = encryption;
    } 
}
