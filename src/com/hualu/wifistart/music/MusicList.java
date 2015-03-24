package com.hualu.wifistart.music;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.hualu.wifistart.HttpService.FileUtil;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.db.DBProvider;
import com.hualu.wifistart.db.FileColumn;
import com.hualu.wifistart.filecenter.files.FileItem;
import com.hualu.wifistart.filecenter.files.FileItemForOperation;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;
import com.hualu.wifistart.R;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


public class MusicList {
	private final static String TAG = "MusicList";
	
	static String[] supportAudio = new String[]{"mp3","wav","wave","aac","amr"};
	
	private static MediaPlayer player = new MediaPlayer();;
	
	/*public static MediaPlayer getMediaPlayer() {
		if (player == null)
			player = new MediaPlayer();
		return player;
	}*/
	
	public static boolean query(Context context,String name) {
		ContentResolver cr = context.getContentResolver();
		Uri uri = DBProvider.CONTENT_URI;		
		String[] projection = { "filename" };
		//Cursor c = cr.query(uri, projection, null, null, null);
		String[] arrayofnames = new String[1];
		arrayofnames[0] = name;
		Cursor c = cr.query(uri, projection, "filename=?", arrayofnames, null);
		if(c != null){
			HuzAlertDialog.Builder builder = new HuzAlertDialog.Builder(context);
			builder.setMessage(R.string.set_file_exists).setCancelable(false)
					/*.setPositiveButton("返回列表",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog, int id) {
									Intent intent = new Intent(
											FileExplorerActivity.this,
											PlayListActivity.class);
									startActivity(intent);
								}
							})*/
					.setPositiveButton(R.string.set_back_list, null);
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		/*
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToPosition(i);
				String filename_DB = c.getString(0);
				if (name.equals(filename_DB)) {// 判断播放列表中是否存在该歌曲
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("文件已存在").setCancelable(false)							
							.setPositiveButton("返回列表", null);
					AlertDialog alert = builder.create();
					alert.show();
					return false;
				}

			}
		}*/
		return true;
	}
	public static boolean isBelongSupport(String filename){
		for (String aString:supportAudio)
			if (filename.toLowerCase().endsWith(aString))	return true;
		return false;
	}
	public static void insertMusic(Context context,FileItem file) {

		ContentResolver cr = context.getContentResolver();
		
		Uri uri = DBProvider.CONTENT_URI;
		String fileName = file.getFileName();
		String[] projection = { "filename" };
		Cursor c = cr.query(uri, projection, "filename=?", new String[]{fileName}, null);
		
		if(c != null && c.moveToFirst()){
			HuzAlertDialog.Builder builder = new HuzAlertDialog.Builder(context);
			builder.setMessage(R.string.set_file_exists).setCancelable(false)		
					.setPositiveButton(R.string.set_back_list, null);
			HuzAlertDialog alert = builder.create();
			alert.show();
		
		}else if(isBelongSupport(fileName)){	
			
			//Uri uri = Uri.parse(file.getFilePath(); 	
			//Music m = new Music();
			Music m = getMusicData(context,file.getFilePath());
			if(m.getUrl() == null){			
				//MediaPlayer mplayer = getMediaPlayer();
				String path = file.getFilePath();
				if(path.startsWith("smb")){					
		        	path = path.replaceFirst("smb", "http");
		        	path = path.replaceFirst("airdisk:123456@", "");
		        	path = path.replaceFirst("Hualu:123456@", "");
		        	path = path.replaceFirst("/airdisk/", "/");
		        	path = path.replaceFirst("/Hualu/", "/");
		        	path = path.replaceFirst("admin:admin@", "");
		        	path = path.replaceFirst("/Share/", "/");
		        	//path = path.replace(' ', '\0');
		        	path =path.replace( " " , "%20");
				}
				player.reset();
				m.setUrl(path);
				int index = file.getFileName().lastIndexOf(".");
				m.setTitle(file.getFileName().substring(0,index));
				m.setName(file.getFileName());			
				m.setSinger("未知艺术家");				
				try {					
					
					player.setDataSource(path);
					player.prepare();
					int time = player.getDuration();	
					Log.i(TAG,"player get music message time = " + time);
					m.setTime(time);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					m.setTime(0);
					Log.i(TAG,"" + e.getMessage());
					e.printStackTrace();					
				} 			
			}
			Log.i(TAG,""+m.getName() + " " + m.getUrl());
			ContentValues values = new ContentValues();
			values.put(FileColumn.TITTLE, m.getTitle());
			values.put(FileColumn.NAME, m.getName());
			values.put(FileColumn.PATH, m.getUrl());
			values.put(FileColumn.SINGER, m.getSinger());
			values.put(FileColumn.TIME, m.getTime());
			
			values.put(FileColumn.TYPE, "Music");
			values.put(FileColumn.SORT, "popular");
			cr.insert(uri, values);
			/*liuyufa change for taost start*/
			ToastBuild.toast(context, R.string.isinput);
			/*
			Toast.makeText(context, "已加入", Toast.LENGTH_LONG)
			.show();
			*/
			/*liuyufa change for taost end*/
		}else{
			/*liuyufa change for toast 20140116 satrt*/
			/*
			Toast.makeText(context, "播放列表不支持" + fileName, Toast.LENGTH_LONG)
				.show();
			*/
			ToastBuild.toast(context,R.string.set_not_support + fileName);
			/*liuyufa change for toast 20140116 end*/
		}
		//String fileName = file.getFileName().substring(0,
		//		file.getFileName().indexOf("."));
		
	}

	public static void insertMusic(Context context,List<FileItemForOperation> list) {

		ContentResolver cr = context.getContentResolver();
		Uri uri = DBProvider.CONTENT_URI;
		String[] projection = { "filename" };	
		
		int cnt = 0;
		for(int i =0 ;i < list.size();i++){
			FileItem file = list.get(i).getFileItem();
			String fileName = file.getFileName();
			//.substring(0,file.getFileName().indexOf("."));
			Cursor c = cr.query(uri, projection, "filename=?", new String[]{fileName}, null);
			if(c!= null && c.moveToFirst())
				continue;
			else if(isBelongSupport(fileName))/*{
				
				ContentValues values = new ContentValues();				
				values.put(FileColumn.NAME, fileName);
				values.put(FileColumn.PATH, file.getFilePath());
				values.put(FileColumn.TYPE, "Music");
				values.put(FileColumn.SORT, "popular");
				cr.insert(uri, values);
				cnt++;
			}*/{
				Music m = getMusicData(context,file.getFilePath());
				
				if(m.getUrl() == null){	
					//zhaoyu	1205
					String path = file.getFilePath();
					if(path.startsWith("smb")){					
			        	/*
						path = path.replaceFirst("smb", "http");
			        	path = path.replaceFirst("airdisk:123456@", "");
			        	path = path.replaceFirst("Hualu:123456@", "");
			        	path = path.replaceFirst("/airdisk/", "/");
			        	path = path.replaceFirst("/Hualu/", "/");
			        	path = path.replaceFirst("admin:admin@", "");
			        	path = path.replaceFirst("/Share/", "/");
			        	//path = path.replace(' ', '\0');
			        	path =path.replace( " " , "%20");*/
			        	
			        	//wdx	add	1210
			        	String ipVal = FileUtil.ip;
			        	int portVal = FileUtil.port;
			        	String httpReq = "http://" + ipVal + ":" + portVal + "/smb=";
			        	path = path.substring(6);
			        	try
						{
							path = URLEncoder.encode(path, "UTF-8");
						}
						catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
			        	path = httpReq + path;
					}
					//MediaPlayer mplayer = getMediaPlayer();
					player.reset();
					m.setUrl(path);					
					int index = file.getFileName().lastIndexOf(".");
					m.setTitle(file.getFileName().substring(0,index));
					m.setName(file.getFileName());					
					//int index = file.getFileName().lastIndexOf(".");
					//m.setName(file.getFileName().substring(index + 1));
					m.setSinger("未知艺术家");
					try {
						player.setDataSource(path);	//zhaoyu	1205
						player.prepare();
						int time = player.getDuration();
						m.setTime(time);
						//m.setTitle(file.getFileName());						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						m.setTime(0);
						e.printStackTrace();
					} 			
				}
				ContentValues values = new ContentValues();
				/*
				values.put(FileColumn.NAME, fileName);
				values.put(FileColumn.PATH, file.getFilePath());
				values.put(FileColumn.TYPE, "Music");
				values.put(FileColumn.SORT, "popular");*/
				values.put(FileColumn.TITTLE, m.getTitle());
				values.put(FileColumn.NAME, m.getName());
				values.put(FileColumn.PATH, m.getUrl());
				values.put(FileColumn.SINGER, m.getSinger());
				values.put(FileColumn.TIME, m.getTime());
				
				values.put(FileColumn.TYPE, "Music");
				values.put(FileColumn.SORT, "popular");
				cr.insert(uri, values);
				cnt++;
			}
			else{
				/*liuyufa change for toast 20140116 satrt*/
				/*
				Toast.makeText(context, "播放列表不支持" + fileName, Toast.LENGTH_LONG)
				.show();
				*/
				ToastBuild.toast(context,R.string.set_not_support + fileName);
				/*liuyufa change for toast 20140116 end*/
			}
		}
		/*liuyufa change for toast 20140116 satrt*/
		/*
		Toast.makeText(context, String.format(context.getString(R.string.intent_to_addplaylist), "" + cnt), Toast.LENGTH_LONG).show();
		*/
		ToastBuild.toast(context,String.format(context.getString(R.string.intent_to_addplaylist), "" + cnt));
		/*liuyufa change for toast 20140116 end*/
		//Toast.makeText(context, "%d个文件已加入播放列表"+cnt, Toast.LENGTH_LONG)
		
	}
	public static void delOne(Context context,String musicName) {// 删除单首歌曲

		ContentResolver cr = context.getContentResolver();
		Uri uri = DBProvider.CONTENT_URI;
		String where = "fileName=?";

		String[] selectionArgs = { musicName };
		cr.delete(uri, where, selectionArgs);
		//player.setNextMediaPlayer(next);
	}
	
	public static void delAll(Context context) {// 删除全部歌曲

		ContentResolver cr = context.getContentResolver();
		Uri uri = DBProvider.CONTENT_URI;

		cr.delete(uri, null, null);
	}
	
	public static List<Music> getPlayListMusicData(Context context){
		ContentResolver cr = context.getContentResolver();
		Uri uri = DBProvider.CONTENT_URI;
		List<Music> list = new ArrayList<Music>();

		String[] projection = { "filename","path","singer","time","tittle"};
		Cursor cursor = cr.query(uri, projection, null, null, null);
		if (cursor.getCount() == 0) {
			//showDialog("播放列表为空");
		}
		if (cursor.moveToFirst()){
			do {
				Music m = new Music();
				String name = cursor.getString(0);
				String path = cursor.getString(1);
				Log.i(TAG,"getPlayListMusicData " + name + " " + path);
				{
					m.setTitle(cursor.getString(4));				
					m.setName(name);
					m.setSinger(cursor.getString(2));		
					m.setUrl(path);
					m.setTime( cursor.getLong(3));
					list.add(m);
				}
			}while(cursor.moveToNext());
		}		
		/*
		if (music.length > 0) {
			playlist.setAdapter(new MusicAdapter(this, list));
				
		}*/
		return list;
	}
	public static Music getMusicData(Context context,String uri) {	
		Music m = new Music();		
		ContentResolver cr = context.getContentResolver();
		//Log.i(TAG,"getMusicData " + uri + " " + cr);
		if (cr != null) 
		{
			Cursor cursor = cr.query(		
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "_data=?", new String[]{uri},
					MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			
			if (null == cursor) 
				return null;
			if (cursor.moveToFirst()) {
				//do 
				{					
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String singer = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					if ("<unknown>".equals(singer)) {
						singer = "未知艺术家";
					}
					String album = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ALBUM));
					long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.SIZE));
					long time = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.DURATION));
					String url = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA));
					String name = cursor
							.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					//String sbr = name.substring(name.length() - 3,name.length());
					//Log.i(TAG, "" + singer + " " + url + " " + title + " " + name);//李宗盛 /mnt/sdcard/WifiShare/凡人歌.mp3 凡人歌 凡人歌.mp3
					//if (sbr.equals("mp3")) {
						m.setTitle(title);
						m.setSinger(singer);
						m.setAlbum(album);
						m.setSize(size);
						m.setTime(time);
						m.setUrl(url);
						m.setName(name);
						//ret = true;
						//musicList.add(m);
					//}
				}// while (cursor.moveToNext());
			}
			}
		return m;
	}
	public static List<Music> getMusicData(Context context) {
		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			// 获取所有歌曲

			Cursor cursor = cr.query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if (null == cursor) {
				return null;
			}
			if (cursor.moveToFirst()) {
				do {
					Music m = new Music();
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String singer = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					if ("<unknown>".equals(singer)) {
						singer = "未知艺术家";
					}
					String album = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ALBUM));
					long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.SIZE));
					long time = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.DURATION));
					String url = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA));
					String name = cursor
							.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					String sbr = name.substring(name.length() - 3,
							name.length());
					//Log.e("--------------", sbr);
					if (sbr.equals("mp3")) {
						m.setTitle(title);
						m.setSinger(singer);
						m.setAlbum(album);
						m.setSize(size);
						m.setTime(time);
						m.setUrl(url);
						m.setName(name);
						musicList.add(m);
					}
				} while (cursor.moveToNext());
			}
		}
		return musicList;
	}
}
