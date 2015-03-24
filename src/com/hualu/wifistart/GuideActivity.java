package com.hualu.wifistart;

import java.util.ArrayList;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hualu.wifistart.R;

public class GuideActivity extends FragmentActivity implements OnPageChangeListener{
	public float mDensity;
	private static ArrayList<View> views;
	private ArrayList<String> tittles;
	private static ViewPager mViewPager;
	View mRoot;
	public int mHeight;
	public static int mScreenWidth;
	
	final static String TAG = "GuideActivity";
	final int image_guides[]=new int[]{R.layout.guide01,R.layout.guide02,R.layout.guide03,R.layout.guide04,R.layout.guide05};
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mDensity = getResources().getDisplayMetrics().density;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();		
		views = new ArrayList<View>();
		tittles = new ArrayList<String>();	
		mRoot = ((ViewGroup)((LayoutInflater)getSystemService("layout_inflater")).inflate(R.layout.activity_guideintro, null));
		setContentView(R.layout.activity_guideintro);	
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		initializeSettings();	         
	}
	@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			setResult(RESULT_OK);
			finishActivity(1);
			finish();
		}	
	
	PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tittles.get(position);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			Log.i(TAG, "instantiateItem :" + position + " " + position % views.size());
		
			try {
				((ViewPager) container).addView(
						views.get(position % views.size()), 0);
			} catch (Exception e) {
			}
			return views.get(position % views.size());
		}
	};
	BaseAdapter tabAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(GuideActivity.this);
			}
			((TextView) convertView).setText(tittles.get(position));
			((TextView) convertView).setTextAppearance(GuideActivity.this,R.style.tvTitle);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return tittles.size();
		}
	};
	
	public void initializeSettings()
	{
		tittles.clear();
		views.clear();
		int j=1;
		for(int i =0;i<image_guides.length;i++){
			j=i+1;
			tittles.add("image_guides0"+j);
		}	
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mView = new View(this);
		mView = mInflater.inflate(R.layout.guide01, null);	
		views.add(0,mView);
		mView = mInflater.inflate(R.layout.guide02, null);	
		views.add(1,mView);
		mView = mInflater.inflate(R.layout.guide03, null);	
		views.add(2,mView);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageSelected(int index) {
		// TODO Auto-generated method stub		
	}
}
