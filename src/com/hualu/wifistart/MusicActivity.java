package com.hualu.wifistart;

import java.util.ArrayList;
import java.util.List;



import com.hualu.wifistart.MusicService;
import com.hualu.wifistart.music.Music;
import com.hualu.wifistart.music.MusicAdapter;
import com.hualu.wifistart.music.MusicList;
import com.hualu.wifistart.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MusicActivity extends Activity{ //implements SensorEventListener
	private final String TAG = "MusicActivity";
	//private static enum PlayState {SINGLE_PLAY, SINGLE_REPEAT,ALL_REPEAT,RANDOM_PLAY};
	private TextView textName;
	private TextView textSinger;
	private TextView textStartTime;
	private TextView textEndTime;
	//private ImageButton imageBtnLast;
	//private ImageButton imageBtnRewind;
	public static ImageButton imageBtnPlay;
	private ImageButton imageBtnForward;
	private ImageButton imageBtnNext;
	//private ImageButton imageBtnLoop;
	private ImageButton imageBtnRandom;
	private ImageButton imageBtnPlayList;
	
	public ImageView btnHome;
	public ImageView btnBack;
	
	//public static LrcView lrc_view;
	public static ListView lrc_List;
	//public static LinearLayout downLrc;
	//public static RelativeLayout showLrc;
	MusicAdapter musicAdapter;
	//public Button btnDwnLrc;
	//public static TextView txtLyrInfo;
	
	//private ImageView icon;
	private SeekBar seekBar1;
	private AudioManager audioManager;// 音量管理者
	private int maxVolume;// 最大音量
	private int currentVolume;// 当前音量
	private SeekBar seekBarVolume;
	private List<Music> lists;
	private Boolean isPlaying = false;
	//private static int id = 1;
	private static int currentId = 0;
	private static Boolean replaying=false;
	private MyProgressBroadCastReceiver receiver = null;
	private MyCompletionListner completionListner = null;
	public static Boolean isLoop=true;
	public static PlayState playState;// = PlayState.ALL_REPEAT;
	private SharedPreferences mPreferences;	
	ArrayList<String> lrcs = new ArrayList<String>();
	
	//public static PlayState playState = PlayState.SINGLE_PLAY;
	//private SensorManager sensorManager;
	//private boolean mRegisteredSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		super.onCreate(savedInstanceState);
		Log.i(TAG,"onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_music);
		

		textName = (TextView) this.findViewById(R.id.music_name);
		textSinger = (TextView) this.findViewById(R.id.music_singer);
		textStartTime = (TextView) this.findViewById(R.id.music_start_time);
		textEndTime = (TextView) this.findViewById(R.id.music_end_time);
		seekBar1 = (SeekBar) this.findViewById(R.id.music_seekBar);
		//Log.i(TAG,"getMusicData");
		//icon = (ImageView) this.findViewById(R.id.image_icon);
		//imageBtnLast = (ImageButton) this.findViewById(R.id.music_lasted);
		//imageBtnRewind = (ImageButton) this.findViewById(R.id.music_rewind);
		imageBtnPlay = (ImageButton) this.findViewById(R.id.music_play);
		imageBtnForward = (ImageButton) this.findViewById(R.id.music_foward);
		imageBtnNext = (ImageButton) this.findViewById(R.id.music_next);
		//imageBtnLoop = (ImageButton) this.findViewById(R.id.music_loop);
		seekBarVolume = (SeekBar) this.findViewById(R.id.music_volume);
		imageBtnRandom = (ImageButton) this.findViewById(R.id.music_random);
		imageBtnPlayList = (ImageButton) this.findViewById(R.id.music_playlist);
		//lrc_view = (LrcView) findViewById(R.id.LyricShow);
		lrc_List = (ListView) findViewById(R.id.lrcList);
		//downLrc = (LinearLayout)findViewById(R.id.downLrc);
		//downLrc.setVisibility(View.VISIBLE);
		//showLrc = (RelativeLayout)findViewById(R.id.showLrc);
		//showLrc.setVisibility(View.GONE);
		//btnDwnLrc = (Button)findViewById(R.id.music_down_lrc);
		//txtLyrInfo = (TextView)findViewById(R.id.lyrInfo);
		//imageBtnLast.setOnClickListener(new MyListener());
		
		//imageBtnRewind.setOnClickListener(new MyListener());
		imageBtnPlay.setOnClickListener(new MyListener());
		imageBtnForward.setOnClickListener(new MyListener());
		imageBtnNext.setOnClickListener(new MyListener());
		imageBtnPlayList.setOnClickListener(new MyListener());
		imageBtnRandom.setOnClickListener(new MyListener());
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		btnHome=(ImageView)findViewById(R.id.homebg);
		btnHome.setOnClickListener(new btnClickListener());
		btnBack=(ImageView)findViewById(R.id.backbg);
		btnBack.setOnClickListener(new btnClickListener());
		
		String state = mPreferences.getString("playState",PlayState.ALL_REPEAT.toString());	
		Log.i(TAG,"mPreferences get playstate = " + state);
		playState = PlayState.toPlayState(state);
		
		if(playState==PlayState.SINGLE_REPEAT){					
			imageBtnRandom.setBackgroundResource(R.drawable.singlerepeat);					
		}else if(playState==PlayState.ALL_REPEAT){					
			imageBtnRandom.setBackgroundResource(R.drawable.allrepeat);
			
		}else if(playState==PlayState.RANDOM_PLAY){					
			imageBtnRandom.setBackgroundResource(R.drawable.randomplay);					
		}else if(playState==PlayState.SINGLE_PLAY){					
			imageBtnRandom.setBackgroundResource(R.drawable.singleplay);					
		}
				
		//btnDwnLrc.setOnClickListener(new MyListener());
		//sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
	
		//lists = MusicList.getMusicData(this);
		lists = MusicList.getPlayListMusicData(this);
		//if(lists.size() == 0)
		//	 Toast.makeText(this, getText(R.string.toast_playlist_empty).toString(), Toast.LENGTH_SHORT);

		//Log.i(TAG,"playlist size = " + lists.size());
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获得最大音量
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 获得当前音量
		seekBarVolume.setMax(maxVolume);
		seekBarVolume.setProgress(currentVolume);
		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
			}
		});
		//电话状态监听
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(new MobliePhoneStateListener(),PhoneStateListener.LISTEN_CALL_STATE);
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			
				seekBar1.setProgress(seekBar.getProgress());
				Intent intent=new Intent("com.hualu.wifistart.music.seekBar");
				intent.putExtra("seekBarPosition", seekBar.getProgress());
				//System.out.println("==========="+seekBar.getProgress());
				sendBroadcast(intent);
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
		 completionListner=new MyCompletionListner();
		IntentFilter filter=new IntentFilter("com.hualu.wifistart.music.completion");
		this.registerReceiver(completionListner, filter);
		
	    musicAdapter=new MusicAdapter(this,lists);
	    
		lrc_List.setAdapter(musicAdapter);
		
		lrc_List.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				Intent pausePlayer = new Intent(MusicActivity.this,
							MusicService.class);
				pausePlayer.putExtra("play", "pause");				
				startService(pausePlayer);
				
				Music m = lists.get(arg2);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				imageBtnPlay.setBackgroundResource(R.drawable.pause);
				Intent intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "play");
				intent.putExtra("id", arg2);					
				startService(intent);
				isPlaying = true;
				replaying=true;
				currentId = arg2;
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}
	private class MobliePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			 ActivityManager am = (ActivityManager) MusicActivity.this.getSystemService(Context.ACTIVITY_SERVICE);  
			 ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
			
			 //String top = cn.getClassName();
			 Log.i(TAG,"" + cn.getPackageName());
			 if(cn.getPackageName().equals("com.hualu.wifistart"))
			 {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE: /* 无任何状态时 */
					if(lists.size() > 0){
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "playing");
					intent.putExtra("id", currentId);	
					Log.i(TAG,"startService CALL_STATE_IDLE " + currentId);
					startService(intent);
					isPlaying = true;
					imageBtnPlay.setBackgroundResource(R.drawable.pause);
					replaying=true;
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK: /* 接起电话时 */
					
				case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
					
					Intent intent2 = new Intent(MusicActivity.this,
							MusicService.class);
					intent2.putExtra("play", "pause");		
					Log.i(TAG,"startService CALL_STATE_RINGING " + currentId);
					startService(intent2);
					isPlaying = false;
					imageBtnPlay.setBackgroundResource(R.drawable.play);
					replaying=false;
					break;
				default:
					break;
				}
			}
		}

	}
	/*
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i(TAG,"onStart");
		super.onStart();
		receiver=new MyProgressBroadCastReceiver();
		IntentFilter filter=new IntentFilter("com.hualu.wifistart.music.progress");
		this.registerReceiver(receiver, filter);
		
		if(lists.size() > 0){
			int id = getIntent().getIntExtra("id", -1);
			Log.i(TAG,"get id" + id);
			if(id == -1){
				MediaPlayer player = MusicService.player;
				if(player !=null && player.isPlaying()){
					Log.i(TAG,"player.isPlaying " + MusicService._id + " " + lists.size());
					Music m = lists.get(MusicService._id);
					currentId = MusicService._id;					
					textName.setText(m.getTitle());
					textSinger.setText(m.getSinger());
					textEndTime.setText(toTime((int) m.getTime()));
					int position = player.getCurrentPosition();
					int total = player.getDuration();
					int progress = position * 100 / total;
					seekBar1.setProgress(progress);
					return;
				}else
					id = 0;
			}
			if (id == currentId) {
				Music m = lists.get(id);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				Intent intent = new Intent(MusicActivity.this, MusicService.class);
				intent.putExtra("play", "replaying");
				intent.putExtra("id", currentId);
				Log.i(TAG,"startService 1 " + id);
				startService(intent);
				if (replaying == true) {
					imageBtnPlay.setBackgroundResource(R.drawable.pause);
					///replaying=false;
					isPlaying = true;
				} else {
					imageBtnPlay.setBackgroundResource(R.drawable.play);
					//replaying=true;
					isPlaying=false;
				}
				
				
			} else {
				Music m = lists.get(id);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				imageBtnPlay.setBackgroundResource(R.drawable.pause);
				Intent intent = new Intent(MusicActivity.this, MusicService.class);
				intent.putExtra("play", "play");
				intent.putExtra("id", id);	
				Log.i(TAG,"startService 2 " + id);
				startService(intent);
				isPlaying = true;
				replaying=true;
				currentId = id;
			}
		}		
	}*/
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG,"onresume");
		super.onResume();
		receiver=new MyProgressBroadCastReceiver();
		IntentFilter filter=new IntentFilter("com.hualu.wifistart.music.progress");
		this.registerReceiver(receiver, filter);
		
		lists = MusicList.getPlayListMusicData(this);
		if(lists.size() == 0)
			/*liuyufa change for toast 20140116 satrt*/
			/*
			 Toast.makeText(this, getText(R.string.toast_playlist_empty).toString(), Toast.LENGTH_SHORT);
			*/
			//ToastBuild.toast(this,getText(R.string.toast_playlist_empty).toString());
			;
			/*liuyufa change for toast 20140116 end*/
		else{			
			musicAdapter.setListItem(lists);
			musicAdapter.notifyDataSetChanged();
		}		
		
		if(lists.size() > 0){
			int id = getIntent().getIntExtra("id", -1);
			Log.i(TAG,"get id" + id);
			if(id == -1){
				MediaPlayer player = MusicService.player;
				if(player !=null && player.isPlaying()){
					Log.i(TAG,"player.isPlaying " + MusicService._id + " " + lists.size());
					Music m = lists.get(MusicService._id);
					currentId = MusicService._id;					
					textName.setText(m.getTitle());
					textSinger.setText(m.getSinger());
					textEndTime.setText(toTime((int) m.getTime()));
					int position = player.getCurrentPosition();
					int total = player.getDuration();
					int progress = position * 100 / total;
					seekBar1.setProgress(progress);
					return;
				}else
					id = 0;
			}
			if (id == currentId) {
				Music m = lists.get(id);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				Intent intent = new Intent(MusicActivity.this, MusicService.class);
				intent.putExtra("play", "replaying");
				intent.putExtra("id", currentId);
				Log.i(TAG,"startService 1 " + id);
				startService(intent);
				if (replaying == true) {
					imageBtnPlay.setBackgroundResource(R.drawable.pause);
					///replaying=false;
					isPlaying = true;
				} else {
					imageBtnPlay.setBackgroundResource(R.drawable.play);
					//replaying=true;
					isPlaying=false;
				}
				
				
			} else {
				Music m = lists.get(id);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				imageBtnPlay.setBackgroundResource(R.drawable.pause);
				Intent intent = new Intent(MusicActivity.this, MusicService.class);
				intent.putExtra("play", "play");
				intent.putExtra("id", id);	
				Log.i(TAG,"startService 2 " + id);
				startService(intent);
				isPlaying = true;
				replaying=true;
				currentId = id;
			}
		}	
		/*
		List<Sensor> sensors=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size()>0){
			Sensor sensor=sensors.get(0);
			mRegisteredSensor=sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
			Log.e("--------------", sensor.getName());
		}*/
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		/*if(mRegisteredSensor){
			sensorManager.unregisterListener(this);
			mRegisteredSensor=false;
		}*/
		super.onPause();
		//Log.i(TAG,"onPause");
		//Intent intent = new Intent(MusicActivity.this, MusicService.class);
		//stopService(intent);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG,"onDestroy");
		if(receiver != null)
			this.unregisterReceiver(receiver);
		if(completionListner != null)
			this.unregisterReceiver(completionListner);

		//Intent intent = new Intent(MusicActivity.this, MusicService.class);
		//Log.i(TAG,"stopService");
		//stopService(intent);
		super.onDestroy();
	}
    public class MyProgressBroadCastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int position=intent.getIntExtra("position", 0);
			int total=intent.getIntExtra("total", 1);
			int progress = position * 100 / total;
			textStartTime.setText(toTime(position));
			textEndTime.setText(toTime(total));
			seekBar1.setProgress(progress);
			seekBar1.invalidate();
		}
    	
    }
	private class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*if (v == imageBtnLast) {
				// 第一首
				id = 0;
				Music m = lists.get(0);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				imageBtnPlay.setImageResource(R.drawable.pause);
				Intent intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "first");
				intent.putExtra("id", id);
				startService(intent);
				isPlaying = true;
			} else if (v == imageBtnRewind) {
				// 前一首
				int id=MusicService._id-1;
				if(id>=lists.size()-1){
					id=lists.size()-1;
				}else if(id<=0){
					id=0;
				}
				Music m = lists.get(id);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				imageBtnPlay.setImageResource(R.drawable.pause);
				Intent intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "rewind");
				intent.putExtra("id", id);
				startService(intent);
				isPlaying = true;
			} else */if (v == imageBtnPlay) {
				// 正在播放
				
				if (isPlaying == true) {
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "pause");				
					startService(intent);
					isPlaying = false;
					imageBtnPlay.setBackgroundResource(R.drawable.play);					
					replaying=false;
				} else {
					if(lists.size() > 0){
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "playing");
					intent.putExtra("id", currentId);			
					startService(intent);
					isPlaying = true;
					imageBtnPlay.setBackgroundResource(R.drawable.pause);				
					replaying=true;
					}
				}
			} else if (v == imageBtnForward) {
				// 前一首
				Intent pausePlayer = new Intent(MusicActivity.this,
						MusicService.class);
				pausePlayer.putExtra("play", "pause");				
				startService(pausePlayer);
			
				int id=MusicService._id-1;
				if(id<=0){
					id=0;
				}
				//Log.i();
				Music m = lists.get(id);
				textName.setText(m.getTitle());
				textSinger.setText(m.getSinger());
				textEndTime.setText(toTime((int) m.getTime()));
				imageBtnPlay.setBackgroundResource(R.drawable.pause);
				Intent intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "rewind");
				intent.putExtra("id", id);
				startService(intent);
				isPlaying = true;
				// 下一首
				/*
				if(lists.size() > 0){
					int id=MusicService._id+1;
					if(id>=lists.size()-1){
						id=lists.size()-1;
					}else if(id<=0){
						id=0;
					}
					Music m = lists.get(id);
					textName.setText(m.getTitle());
					textSinger.setText(m.getSinger());
					textEndTime.setText(toTime((int) m.getTime()));
					imageBtnPlay.setBackgroundResource(R.drawable.pause);
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "forward");
					intent.putExtra("id", id);
					Log.i(TAG,"startService 7");
					startService(intent);
					isPlaying = true;
				}*/
				
			} else if (v == imageBtnNext) {				
				// 下一首
				Intent pausePlayer = new Intent(MusicActivity.this,
				MusicService.class);
				pausePlayer.putExtra("play", "pause");				
				startService(pausePlayer);
				
				if(lists.size() > 0){
					int id=MusicService._id+1;
					if(id>=lists.size()-1){
						id=lists.size()-1;
					}else if(id<=0){
						id=0;
					}
					Music m = lists.get(id);
					textName.setText(m.getTitle());
					textSinger.setText(m.getSinger());
					textEndTime.setText(toTime((int) m.getTime()));
					imageBtnPlay.setBackgroundResource(R.drawable.pause);
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "forward");
					intent.putExtra("id", id);					
					startService(intent);
					isPlaying = true;
				}				
			} else if (v == imageBtnRandom) {
				//SINGLE_PLAY, SINGLE_REPEAT,ALL_REPEAT,RANDOM_PLAY
				
				if(playState ==PlayState.SINGLE_PLAY){
					playState = PlayState.SINGLE_REPEAT;
					imageBtnRandom.setBackgroundResource(R.drawable.singlerepeat);
					isLoop = true;
				}else if(playState==PlayState.SINGLE_REPEAT){
					playState = PlayState.ALL_REPEAT;
					imageBtnRandom.setBackgroundResource(R.drawable.allrepeat);
					isLoop = true;
				}else if(playState==PlayState.ALL_REPEAT){
					playState = PlayState.RANDOM_PLAY;
					imageBtnRandom.setBackgroundResource(R.drawable.randomplay);
					isLoop = true;
				}else if(playState==PlayState.RANDOM_PLAY){
					playState = PlayState.SINGLE_PLAY;
					imageBtnRandom.setBackgroundResource(R.drawable.singleplay);
					isLoop = false;
				}
				SharedPreferences.Editor editor = mPreferences.edit(); 
				Log.i(TAG,"mPreferences save playstate = " + playState.toString());
				editor.putString("playState", playState.toString());
				editor.commit();
				/*
				if (isLoop == true) {
					// 顺序播放
					imageBtnRandom.setBackgroundResource(R.drawable.allrepeat);
					isLoop = false;
				} else {
					// 单曲播放
					imageBtnRandom.setBackgroundResource(R.drawable.singlerepeat);
					isLoop = true;
				}*/
			//} else if (v == imageBtnRandom) {
			//	imageBtnRandom.setImageResource(R.drawable.randomplay);
			}else if(v == imageBtnPlayList){
				//playlist
				Intent intent = new Intent(MusicActivity.this, StatusActivity.class);
				intent.putExtra("listType", "play");	
				MusicActivity.this.startActivity(intent); 
				MusicActivity.this.finish();
			}
			/*else if(v ==btnDwnLrc){
				if(Singleton.SMB_ONLINE){
					txtLyrInfo.setText("无法连接到外部网络，请设置网络后重试！");
				}else{
					
					if(lists.size() > 0){
						int id=MusicService._id;					
						final Music m = lists.get(id);
						Log.i(TAG," " + m.getTitle() + " " + m.getSinger());
						txtLyrInfo.setText("正在连接网络，下载歌词，请稍后....");
						
						new Thread(new Runnable()
						{		
							public void run()
							{
								
								String extraName = m.getUrl().substring(m.getUrl().lastIndexOf(".") + 1);
								String path = m.getUrl().replace(extraName, "lrc");
								//Log.i(TAG,"readLRC " + m.getUrl().replace(extraName, "lrc"));
								//File f = new File(song_path.replace(extraName, "lrc"));
								
								SearchLRC search = new SearchLRC(m.getTitle(),m.getSinger(),path);  
								if(search.fetchLyric())
								{
									
									
									LrcProcess process = new LrcProcess();
									// 读取歌词文件
									process.readLRC(m.getUrl());
									// 传回处理后的歌词文件
									//List<LrcContent> lrcList = new ArrayList<LrcContent>()
									List<LrcContent> lrcList = process.getLrcContent();
									
									//ArrayList<String> lrcs = new ArrayList<String>();
									lrcs.clear();
									for(int i =0;i<lrcList.size();i++){
										lrcs.add(lrcList.get(i).getLrc());
									}
									//showLrc.setVisibility(View.VISIBLE);
									//downLrc.setVisibility(View.GONE);
									//ArrayAdapter<String> adapter=new ArrayAdapter<String>(MusicActivity.this,R.layout.music_lrc_item,R.id.lrcText,lrcs);
									//MusicActivity.lrc_List.setAdapter(adapter);
									mHandler.sendEmptyMessage(201);
								}else{
									//txtLyrInfo.setText("下载失败，网络连接异常或没有找到相应的歌词文件！");
									//Message msg = Message.obtain();
									//msg.what = 200;
									mHandler.sendEmptyMessage(200);
								}
							}
						}).start();
					
					}
				}
		        //search.downloadLyric();
			}*/

		}
	}
/*	private  Handler mHandler = new Handler() { 
		 public void handleMessage(android.os.Message msg) {  
			 switch (msg.what){
			 	case 200:
			 		//txtLyrInfo.setText("下载失败，网络连接异常或没有找到相应的歌词文件！");
			 		break;
			 	case 201:
			 		
			 		//showLrc.setVisibility(View.VISIBLE);
					//downLrc.setVisibility(View.GONE);
					//ArrayAdapter<String> adapter=new ArrayAdapter<String>(MusicActivity.this,R.layout.music_lrc_item,R.id.lrcText,lrcs);
					//MusicActivity.lrc_List.setAdapter(adapter);
			 		break;
			 }
		 }
	};*/
   private class MyCompletionListner extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG,"MyCompletionListner " + playState);
		if(playState==PlayState.SINGLE_PLAY){
			isPlaying = false;
			imageBtnPlay.setBackgroundResource(R.drawable.play);	
			seekBar1.setProgress(0);
			replaying=false;
		}else{
		Music m = lists.get(MusicService._id);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));
		imageBtnPlay.setBackgroundResource(R.drawable.pause);
		}
	}
	   
   }
	class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.homebg:
				MusicActivity.this.finish();
				startActivity(new Intent(MusicActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:								
				dispachBackKey();
				break;		
			}
		}
	}
	public void dispachBackKey() {
		//Log.i(TAG,"dispachBackKey");
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
	/**
	 * 时间格式转换
	 * 
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
	/*
	//重力感应 甩歌代码
	private static final int SHAKE_THRESHOLD = 3000;
	private long lastUpdate=0;
	private double last_x=0;
	private double last_y= 4.50;
	private double last_z=9.50;
	//这个控制精度，越小表示反应越灵敏
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		//处理精准度改变
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			long curTime = System.currentTimeMillis();
			
			// 每200毫秒检测一次   
			if ((curTime - lastUpdate) > 100) { 
			long diffTime = (curTime - lastUpdate);  
			lastUpdate = curTime;   
			double x=event.values[SensorManager.DATA_X];
			double y=event.values[SensorManager.DATA_Y];
			double z=event.values[SensorManager.DATA_Z];
			Log.e("---------------", "x="+x+"   y="+y+"   z="+z);
			float speed = (float) (Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000);   			  
			if (speed > SHAKE_THRESHOLD) {   
                        //检测到摇晃后执行的代码
				  if(MusicService.playing==true){
					  Intent intent = new Intent(MusicActivity.this,
								MusicService.class);
						intent.putExtra("play", "pause");
						startService(intent);
						isPlaying = false;
						imageBtnPlay.setImageResource(R.drawable.play);
						replaying=false;
				  }else{
					  Intent intent = new Intent(MusicActivity.this,
								MusicService.class);
						intent.putExtra("play", "playing");
						intent.putExtra("id", id);
						startService(intent);
						isPlaying = true;
						imageBtnPlay.setImageResource(R.drawable.pause);
						replaying=true;
				  }
			}  
			last_x = x;   
			last_y = y;   
			last_z = z;   
			}
		}
	}*/

}
