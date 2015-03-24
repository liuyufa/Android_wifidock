package com.hualu.wifistart;

import java.util.ArrayList;

import com.hualu.wifistart.smbsrc.ServiceFileManager;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.wfDiskInfo;
import com.hualu.wifistart.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InfoActivity extends Activity {
	private String infoType;
	private final String TYPE_ABOUT="about";
	//private final String TYPE_STORAGE="storage";
	private ListView infoList;
	private LinearLayout infoAbout;
	
	ImageButton btnHome,btnBack;
	//RelativeLayout rlHome,rlBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		infoAbout = (LinearLayout)findViewById(R.id.aboutus);
		infoList = (ListView)findViewById(R.id.infoList);
		
		//btnHome = (ImageButton)findViewById(R.id.btnHome);	
		
		//btnHome.setOnClickListener(new btnClickListener());
	
		
		//btnBack = (ImageButton)findViewById(R.id.btnBack);	
		
		//btnBack.setOnClickListener(new btnClickListener());

		
		if (getIntent().getExtras() != null)
			infoType = getIntent().getExtras().getString("infoType");
		else
			infoType = null;
		if(infoType.equals(TYPE_ABOUT)){
			infoList.setVisibility(View.GONE);
			infoAbout.setVisibility(View.VISIBLE);
		}else{
			infoList.setVisibility(View.VISIBLE);
			initInfo();
			infoAbout.setVisibility(View.GONE);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}
	void initInfo(){
		ArrayList<wfDiskInfo> disks = Singleton.instance().disks;
		StorageAdapter mAdapter = new StorageAdapter(this,disks);
		//StorageAdapter mAdapter = new StorageAdapter(this);
		infoList.setAdapter(mAdapter);
	}
	class StorageAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		ArrayList<wfDiskInfo> mDisks;
		public StorageAdapter(Context context,ArrayList<wfDiskInfo> disks)
		{
			super();
			mInflater = LayoutInflater.from(context);
			mDisks = disks;
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			//wfDiskInfo disk = (wfDiskInfo)getItem(position);
			if(convertView == null){
				 convertView = mInflater.inflate(R.layout.info_item, parent, false);
			}
			TextView tittle = (TextView)convertView.findViewById(R.id.storage);
			TextView total = (TextView)convertView.findViewById(R.id.numTotal);
			TextView free = (TextView)convertView.findViewById(R.id.numFree);			
			ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.barFree);
			/*if(disk.path.startsWith("smb")){
				tittle.setText(disk.des);
				total.setText("0");
				free.setText("0");
				progressBar.setProgress(0);
			}else*/
			{
				StatFs stat = null;
				//stat = new StatFs("/mnt/sdcard");	
				stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
				double dl = (((double)stat.getAvailableBlocks())*stat.getBlockSize());
				double dt = ((double)stat.getBlockCount())*stat.getBlockSize();
				String leftsize = ServiceFileManager.FormetFileSize(dl);
				String totalsize = ServiceFileManager.FormetFileSize(dt);
				//Log.i(TAG,"/mnt/sdcard " + left + " " + total);
				tittle.setText(R.string.set_sd);
				total.setText(totalsize);
				free.setText(leftsize);
				progressBar.setProgress((int)(dl*100/dt));
			}
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 1;//mDisks.size();
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDisks.get(0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
	}
	/*class btnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btnHome:
				InfoActivity.this.finish();
				startActivity(new Intent(InfoActivity.this, WifiStarActivity.class));
				break;
			case R.id.btnBack:								
				dispachBackKey();
				break;		
			}
		}
	}*/
	public void dispachBackKey() {
		//Log.i(TAG,"dispachBackKey");
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
	
}
