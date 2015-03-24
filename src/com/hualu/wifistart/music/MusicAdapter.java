package com.hualu.wifistart.music;

import java.util.List;

import com.hualu.wifistart.music.Music;
import com.hualu.wifistart.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {
	
    private List<Music> listMusic;
    private Context context;
    public MusicAdapter(Context context,List<Music> listMusic){
    	this.context=context;
    	this.listMusic=listMusic;
    }
    public MusicAdapter(Context context){
    	this.context=context;    	
    }
	public void setListItem(List<Music> listMusic){
		this.listMusic=listMusic;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMusic.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listMusic.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.play_list_item, null);
		}
		Music m=listMusic.get(position);
		//音乐名
		TextView textMusicName=(TextView) convertView.findViewById(R.id.music_item_name);
		//lrh mdf 20131214
		String name=checkTitle(m.getName());
		textMusicName.setText(name);
		//end
		//歌手
		TextView textMusicSinger=(TextView) convertView.findViewById(R.id.music_item_singer);
		textMusicSinger.setText(m.getSinger());
	   //持续时间
		TextView textMusicTime=(TextView) convertView.findViewById(R.id.music_item_time);
		textMusicTime.setText(toTime((int)m.getTime()));
		return convertView;
	}
	  /**
			 * 时间格式转换
			 * @param time
			 * @return
			 */
			public String toTime(int time) {
		        
				time /= 1000;
				int minute = time / 60;
				//int hour = minute / 60;
				int second = time % 60;
				minute %= 60;
				return String.format("%02d:%02d", minute, second);
			}
			   //lrh add 20131214
		    public String checkTitle(String title){
		    	if(title.contains("."))
		    	title=title.substring(0,title.lastIndexOf("."));
		    	if(title.length()>28){
		    		title=title.substring(0,26)+"...";
		    	}
		    	return title;
		    }
		    //end
}
