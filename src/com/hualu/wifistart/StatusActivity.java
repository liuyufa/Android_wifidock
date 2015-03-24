package com.hualu.wifistart;

import java.util.ArrayList;
import java.util.List;

import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.music.Music;
import com.hualu.wifistart.music.MusicAdapter;
import com.hualu.wifistart.music.MusicList;
import com.hualu.wifistart.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusActivity extends Activity{

	private static ArrayList<TransferItemInfo> transferList;
	ImageView btnHome,btnBack;
	TextView menuTitle;
	
	String listType;
	private List<Music> lists;	
	MusicAdapter adapter;
	private int selectedPosition=0;
	static TransferAdapter mTransferAdapter;
	private final int MENU_FIRST = Menu.FIRST + 100;
	private final int MENU_DELETE = MENU_FIRST + 1;
	private final int MENU_PLAY = MENU_FIRST + 2;
	private final int MENU_DELETEALL = MENU_FIRST + 3;
	
	private static boolean firstBind = false;
	private static IGetProcess downloadServiceBinder = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer);
		transferList = new ArrayList<TransferItemInfo>();
		lists = MusicList.getPlayListMusicData(this);
		//if(lists.size() == 0)
		btnHome = (ImageView)findViewById(R.id.homebg);	
		btnHome.setOnClickListener(new btnClickListener());
		btnBack = (ImageView)findViewById(R.id.backbg);	
		btnBack.setOnClickListener(new btnClickListener());		
		menuTitle=(TextView)findViewById(R.id.menuTitle);
		if (getIntent().getExtras() != null)
			listType = getIntent().getExtras().getString("listType");
		else
			listType = null;
		if(listType.equals("transfer")){
				bindDownloadService();			
			initTransferPage();
		}
		else
			initPlayListPage();
	}

	/**
	 * 每次启动这个Activity都要重新绑定一次，因为从返回到进入到这个Activity会
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}
	private void bindDownloadService()
	{
		System.out.println(firstBind);
		Log.i("test","bindDownloadService");
		firstBind=this.getApplicationContext().bindService(new Intent(StatusActivity.this, DownloadService.class), 
				connection, BIND_AUTO_CREATE);
	}
	
	static ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			System.out.println("BindService success!");
			Log.i("test","BindService success");
			downloadServiceBinder = (IGetProcess)service;
			listenProgress(); 
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			
		}
	};
	private static void listenProgress() {  
        new Thread() {  
            public void run() {  
                while(true) { 
                	if(downloadServiceBinder!= null){
            			transferList = downloadServiceBinder.getProcessList();
            			mHandler.sendEmptyMessage(1);
            		}
                    try {  
                        Thread.sleep(500);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            };  
        }.start();  
    }  
	
	private static Handler mHandler = new Handler() { 
		 public void handleMessage(android.os.Message msg) {  
			 mTransferAdapter.notifyDataSetChanged();
		 }
	};
	public void initTransferPage(){
		transferList.clear();
		ListView mList = (ListView)findViewById(R.id.list);
		mTransferAdapter = new TransferAdapter(this); 
		mList.setAdapter(mTransferAdapter);
		menuTitle.setText(R.string.transferList);
	}
	public void initPlayListPage(){
		ListView mList = (ListView)findViewById(R.id.list);
		adapter=new MusicAdapter(this, lists);
		mList.setAdapter(adapter);
		menuTitle.setText(R.string.playList);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(StatusActivity.this,
						MusicActivity.class);
				intent.putExtra("id", arg2);
				startActivity(intent);
				StatusActivity.this.finish();
			}
		});
		registerForContextMenu(mList);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_DELETE:
			MusicList.delOne(StatusActivity.this,lists.get(selectedPosition).getName());
			lists.remove(selectedPosition);
			adapter.notifyDataSetChanged();
			break;
		case MENU_DELETEALL:{
			MusicList.delAll(StatusActivity.this);	
			lists.clear();
			adapter.notifyDataSetChanged();
		}
			break;
		case MENU_PLAY:
			Intent intent = new Intent(StatusActivity.this,
					MusicActivity.class);//.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);;
			intent.putExtra("id", selectedPosition);
			startActivity(intent);
			break;		
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		selectedPosition = info.position;
		
		menu.setHeaderTitle(lists.get(selectedPosition).getTitle());
		menu.setHeaderIcon(R.drawable.toolbar_operation);
		
		menu.add(0, MENU_DELETE, Menu.NONE, R.string.menu_delete_selected);
		menu.add(0, MENU_DELETEALL, Menu.NONE, R.string.menu_delete_all);
		
		menu.add(0, MENU_PLAY, Menu.NONE, R.string.menu_play);
	}
	public static class TransferItemInfo
	{
		String tittle;
		int progress;
		double filesize;
		double downloadsize;
		
		public TransferItemInfo(String tittle,double filesize,double paste,int progress)
		{
			this.tittle = tittle;
			this.filesize = filesize;
			this.downloadsize = paste;
			this.progress = progress;
		}
		
	}
	class TransferAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		public TransferAdapter(Context context)
		{
			super();
			mInflater = LayoutInflater.from(context);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			TransferItemInfo tmpfile = (TransferItemInfo)getItem(position);
			if(convertView == null){
				 convertView = mInflater.inflate(R.layout.downloading_list_item, parent, false);
			}
			TextView tittle = (TextView)convertView.findViewById(R.id.download_title);
			ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.download_progress);
			TextView progressText = (TextView)convertView.findViewById(R.id.download_progress_text);
			TextView downloadSize = (TextView)convertView.findViewById(R.id.download_size);
			ImageButton btnCancel = (ImageButton)convertView.findViewById(R.id.btnCancel);
			btnCancel.setTag(position);
			btnCancel.setOnClickListener(new btnClickListener());
			tittle.setTextColor(android.graphics.Color.BLACK);	
			tittle.setText(tmpfile.tittle);
			progressBar.setProgress(tmpfile.progress);
			progressText.setText(tmpfile.progress+"%");
			downloadSize.setText(Helper.formatFromSize((long)tmpfile.downloadsize)+"/"+ Helper.formatFromSize((long)tmpfile.filesize));
			
			return convertView;
		}
		class btnClickListener implements OnClickListener{

			@Override
			public void onClick(View v) {
				final Integer index = (Integer)v.getTag();
				final Dialog dialog = new HuzAlertDialog.Builder(StatusActivity.this)
				.setTitle(R.string.title_comfir_delete)
				.setMessage(R.string.dialog_msg_comfir_delete)
				.setPositiveButton(R.string.set_done, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface paramDialogInterface, int paramInt)
					{								
						downloadServiceBinder.removeProcess(transferList.get(index));
						paramDialogInterface.dismiss();					
					}
				})
				.setNegativeButton(R.string.set_cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface paramDialogInterface, int paramInt)
					{	
						paramDialogInterface.dismiss();
					}
				}).create();
				dialog.show();	
			}
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return transferList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return transferList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}
	class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.homebg:
				StatusActivity.this.finish();
				startActivity(new Intent(StatusActivity.this, WifiStarActivity.class));
				break;
			case R.id.backbg:								
				dispachBackKey();
				break;		
			}
		}
	}
	public void dispachBackKey() {
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(firstBind){
			getApplicationContext().unbindService(connection);
				firstBind=false;
			}
		super.onDestroy();
		
	}
	
}
