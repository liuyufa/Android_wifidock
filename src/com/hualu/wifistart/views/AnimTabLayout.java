package com.hualu.wifistart.views;

//import android.R;
import com.hualu.wifistart.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;

public class AnimTabLayout extends LinearLayout implements OnClickListener{
	private LinearLayout mTabContainer;
	//private LinearLayout mIndicatorLayout;
	private Context mContext;
	//private ImageView mIndicator;
	private int mTabCount = 5;
	private int mCurrIndex = 0;
	private BaseAdapter mAdapter;
	//private int mIndicatorWidth = 0;
	private OnTabChangeListener mChangeListener;
	public AnimTabLayout(Context context) {
		this(context,null);
	}
	public AnimTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	
	private void initView(){
		setOrientation(LinearLayout.VERTICAL);
		mTabContainer = new LinearLayout(mContext);
		mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
		//((RelativeLayout)mTabContainer.getChildAt(mCurrIndex)).setBackgroundResource(R.drawable.tabfocusin);
		//mTabContainer.setBackgroundResource(R.drawable.topbar_bg);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		params.weight = 1;
		addView(mTabContainer,params);
		
		//mIndicatorLayout = new LinearLayout(mContext);
//		mIndicatorLayout.setBackgroundColor(Color.GRAY);
		//addView(mIndicatorLayout,new LayoutParams(LayoutParams.FILL_PARENT,5));
		
		//mIndicator = new ImageView(mContext);
		//mIndicator.setBackgroundColor(Color.GREEN);
		//mIndicatorLayout.addView(mIndicator,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		MeasureSpec.getSize(widthMeasureSpec);
		//mIndicatorWidth = width / mTabCount;
		//int childWidthSpec = getChildMeasureSpec(
        //        MeasureSpec.makeMeasureSpec(mIndicatorWidth, MeasureSpec.EXACTLY), 0, 
        //        mIndicator.getLayoutParams().width);
		//mIndicator.measure(childWidthSpec, heightMeasureSpec);
	}

	public void setAdapter(BaseAdapter adapter){
		//Log.i(VIEW_LOG_TAG, "setAdapter IndicatorWidth:" + mIndicatorWidth);
		mAdapter = adapter;
		mTabCount = mAdapter.getCount();
		if (mTabCount <= 0) {
			return;
		}
		for (int i = 0; i < mTabCount; i++) {
			RelativeLayout layout = new RelativeLayout(mContext);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT);
			layoutParams.weight = 1;
			mTabContainer.addView(layout, layoutParams);
			View child = adapter.getView(i, null, layout);
			RelativeLayout.LayoutParams childParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT);
			childParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			child.setTag(i);
			//child.setTag(R.drawable.topbar_bg, i);
			layout.addView(child, childParams);
			/*
			 * update  by huwei 
			 * change ClickListener from children to parent
			 * */
			//child.setOnClickListener(this);
			layout.setTag(i);
			layout.setOnClickListener(this);
		}
		((RelativeLayout)mTabContainer.getChildAt(mCurrIndex)).setBackgroundResource(R.drawable.tabfocusin);
		((RelativeLayout)mTabContainer.getChildAt(mCurrIndex)).setSelected(true);
	}
	/*
	public void setIndicatorBackgroundColor(int color){
		//mIndicator.setBackgroundColor(color);
		invalidate();
	}
	public void setIndicatorBackgroundRe(int resid){
		//mIndicator.setBackgroundResource(resid);
		invalidate();
	}
	*/
	@Override
	public void onClick(View v) {
		//final int index = (Integer) v.getTag(R.drawable.topbar_bg);
		final int index = (Integer) v.getTag();
		if (mChangeListener != null) {
			mChangeListener.tabChange(index);
		}
//		postDelayed(new Runnable() {
//			@Override
//			public void run() {
				moveTo(index);
//			}
//		}, 200);
	}
	
	public void moveTo(int index){
		if (index < 0 || index >= mTabCount) {
			return;
		}
		RelativeLayout tabCurr = (RelativeLayout)mTabContainer.getChildAt(mCurrIndex);
		RelativeLayout tabIndex = (RelativeLayout)mTabContainer.getChildAt(index);
		tabCurr.setBackgroundColor(Color.TRANSPARENT);
		tabCurr.setSelected(false);
		tabIndex.setBackgroundResource(R.drawable.tabfocusin);
		tabIndex.setSelected(true);
		int start = tabCurr.getLeft();
		int end = tabIndex.getLeft();

		final TranslateAnimation animation = new TranslateAnimation(start, end, 0, 0);
		animation.setDuration(400);
		animation.setFillAfter(true);
		mCurrIndex = index;
//		mIndicator.bringToFront();
		//mIndicator.startAnimation(animation);
	}
	
	public OnTabChangeListener getOnTabChangeListener() {
		return mChangeListener;
	}
	public void setOnTabChangeListener(OnTabChangeListener mChangeListener) {
		this.mChangeListener = mChangeListener;
	}

	public interface OnTabChangeListener{
		public void tabChange(int index);
	}
}
