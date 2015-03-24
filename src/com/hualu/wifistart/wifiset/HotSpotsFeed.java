package com.hualu.wifistart.wifiset;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class HotSpotsFeed {
    private String title;
    private int itemcount;
    private List<HotSpotsItem> itemlist;
    
    public HotSpotsFeed(){
        itemlist = new Vector<HotSpotsItem>(0);
    }
    
    /**
     * 负责将一个HotSpotsItem加入到HotSpotsFeed类中
     * @param item
     * @return
     */
    public int addItem(HotSpotsItem item){
        itemlist.add(item);
        itemcount++;
        return itemcount;
    }
    
    public HotSpotsItem getItem(int location){
        return itemlist.get(location);
    }
    
    public List<HotSpotsItem> getAllItems(){
        return itemlist;
    }
    
    /**
     * 负责从HotSpotsFeed类中生成列表所需要的数据
     * @return
     */
    public ArrayList<Map<String, Object>> getAllItemForListView(){
    	ArrayList<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
        int size = itemlist.size();
        if(size>0){
        	sortItemList();
        }
        else{
        	return null;
        }
        for(int i=0 ; i<size ; i++){
            HashMap<String , Object> item = new HashMap<String, Object>();
            if(0==itemlist.get(i).getEssid().length()||"0"==itemlist.get(i).getSignal()){
            	continue;
            }
            item.put(HotSpotsItem.ESSID, itemlist.get(i).getEssid());
            //item.put(HotSpotsItem.BSSID, itemlist.get(i).getBssid());
            item.put(HotSpotsItem.CHANNEL, itemlist.get(i).getChannel());
            //item.put(HotSpotsItem.QUALITY, itemlist.get(i).getQuality());
            item.put(HotSpotsItem.SIGNAL, itemlist.get(i).getSignal());
            item.put(HotSpotsItem.ENCRYPTION, itemlist.get(i).getEncryption());
            data.add(item);

        }
        return data;
    }
    @SuppressWarnings("unchecked")
	private void sortItemList(){
    	Collections.sort(itemlist, new SortBySignal());
    }
    @SuppressWarnings("rawtypes")
	class SortBySignal implements Comparator {
    	public int compare(Object o1, Object o2) {
    	HotSpotsItem s1 = (HotSpotsItem) o1;
    	HotSpotsItem s2 = (HotSpotsItem) o2;
    	if (Integer.parseInt(s1.getSignal()) < Integer.parseInt(s2.getSignal()))
    	  return 1;
    	if(Integer.parseInt(s1.getSignal()) > Integer.parseInt(s2.getSignal()))
    	  return -1;
    	return 0;
    	}
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemcount() {
        return itemcount;
    }

    public void setItemcount(int itemcount) {
        this.itemcount = itemcount;
    }

    public List<HotSpotsItem> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<HotSpotsItem> itemlist) {
        this.itemlist = itemlist;
    }
    
}
