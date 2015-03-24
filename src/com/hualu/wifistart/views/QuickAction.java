package com.hualu.wifistart.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.GestureDetector;
import java.util.List;

import com.hualu.wifistart.R;

public class QuickAction extends PopupWindows implements GestureDetector.OnGestureListener
{
	public static final int ANIM_AUTO = 5;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_REFLECT = 4;
	private int animStyle;	
	private LayoutInflater inflater;
	private Context mContext;
	//private LinearLayout columnsInfo;
	private View mRootView;
	private ListView mList;
	private TextView mText;
	private int columnIndex;	
	final String TAG = "POPWINDOW";	
	private boolean showFlag = false;
	public OnItemClickListener mListener;
	
	//private List<ColAndAct> info = new ArrayList<ColAndAct>();
	private List<String> info;
	//public QuickAction(Context paramContext,List<String> info)
	public QuickAction(Context paramContext,String[] info)
	{
		super(paramContext);
		mContext = paramContext;
		//this.info = info;
		
		inflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
		//setRootViewId(R.layout.popup);
		mRootView = ((ViewGroup)inflater.inflate(R.layout.popup, null));	
		//columnsInfo = ((LinearLayout)mRootView.findViewById(R.id.columnsInfo));	
		//mText = ((TextView)mRootView.findViewById(R.id.columnsText));	
		
		
		mList = (ListView)mRootView.findViewById(R.id.list);
		//String[] displays = new String[]{"1","2","3"};
		mList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.popitem,info));
		//lrh add 1218
		mList.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
					int action=event.getAction();
					switch(action){
					case MotionEvent.ACTION_DOWN:{
						return false;
					}
					case MotionEvent.ACTION_MOVE:{
						return true;
					}
					case MotionEvent.ACTION_UP:{
						return false;
					}
					}
					return true;
			}
		});
		//end
		columnIndex = info.length;
		setOnDismissListener(new PopupWindow.OnDismissListener()
		{
			public void onDismiss()
			{
				QuickAction.this.dismiss();
			}
		});
		setContentView(mRootView);	
		animStyle = ANIM_GROW_FROM_CENTER;
		showFlag = false;
	}
	public QuickAction(Context paramContext)
	{
		super(paramContext);
		mContext = paramContext;
		//this.info = info;
		
		inflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
		//setRootViewId(R.layout.popup);
		mRootView = ((ViewGroup)inflater.inflate(R.layout.popup, null));	
		//columnsInfo = ((LinearLayout)mRootView.findViewById(R.id.columnsInfo));
		mList = (ListView)mRootView.findViewById(R.id.list);
		String[] displays = new String[]{"1","2","3"};
		mList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.popitem,displays));
		setContentView(mRootView);	
		setOnDismissListener(new PopupWindow.OnDismissListener()
		{
			public void onDismiss()
			{
				QuickAction.this.dismiss();
			}
		});
		animStyle = ANIM_GROW_FROM_CENTER;
		showFlag = false;
	}	
	private void setAnimationStyle(boolean paramBoolean)
	{
		//int i = paramInt2 - this.mArrowUp.getMeasuredWidth() / 2;
		switch (this.animStyle)
		{
			case ANIM_GROW_FROM_LEFT:
				if (paramBoolean)
					mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Left);
				else
					mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Left);
				break;
			case ANIM_GROW_FROM_RIGHT:
				if (paramBoolean)
					mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Right);
				else
					mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Right);
				break;
			case ANIM_GROW_FROM_CENTER:
				if (paramBoolean)
				mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
				else
					mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
				break;
			case ANIM_REFLECT:
				if (paramBoolean)
					mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Reflect);
				else
					mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Reflect);
				break;
			case ANIM_AUTO:		
			/*	if (i <= paramInt1 / 4)
				{
					if (paramBoolean)
						mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Left);
					else
						mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Left);
				}
				else if ((i > paramInt1 / 4) && (i < 3 * (paramInt1 / 4)))
				{
					if (paramBoolean)
						mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
					else
						mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
				}else*/
				{
					if (paramBoolean)
						mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Right);
					else
						mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Right);
				}
				break;
			default:
				break;
		}
	}
    /*
	private void windowDismissed()
	{
		this.scrolledAnchor.systemReady = true;
		this.scrolledAnchor.postInvalidateDelayed(340L);
		if ((this.scrolledOffset != 0) && (this.scrolledAnchor != null))
			this.scrolledAnchor.doForcedScroll(0, -this.scrolledOffset);
	}*/
/*
	public void addActionItem(ActionItem paramActionItem)
	{
		String str = paramActionItem.getTitle();
		Drawable localDrawable = paramActionItem.getIcon();
		View localView = this.inflater.inflate(R.layout.action_item, null);
		ImageView localImageView = (ImageView)localView.findViewById(R.id.iv_icon);
		TextView localTextView = (TextView)localView.findViewById(R.id.tv_title);
		if (localDrawable != null)
		{
			localImageView.setImageDrawable(localDrawable);

		}else
			localImageView.setVisibility(8);
		if (str == null)
			localTextView.setVisibility(8);
		else
			localTextView.setText(str);	

		localView.setOnClickListener(new View.OnClickListener()//this.mChildPos
		{
			public void onClick(View paramView)
			{
				if (QuickAction.this.mListener != null)
					QuickAction.this.mListener.onItemClick(mChildPos);
				QuickAction.this.dismiss();
			}
		});
		localView.setFocusable(true);
		localView.setClickable(true);
		this.mTrack.addView(localView, this.mChildPos);
		this.mChildPos = (1 + this.mChildPos);
	}*/
	public void setOnActionItemClickListener(OnItemClickListener paramOnActionItemClickListener)
	{
		this.mListener = paramOnActionItemClickListener;
		
		mList.setOnItemClickListener(paramOnActionItemClickListener);
		
	}
	/*
	public static abstract interface OnActionItemClickListener
	{
		public abstract void onItemClick(int paramInt);
	}*/
	public void setAnimStyle(int paramInt)
	{
		animStyle = paramInt;
	}
	public void setInfo(List<String> info)
	{
		this.info = info;
	}
	public int getInfoSize()
	{
		return info.size();
	}
	/*
	public void setRootViewId(int paramInt)
	{
		mRootView = ((ViewGroup)inflater.inflate(paramInt, null));	
		columnsInfo = ((LinearLayout)mRootView.findViewById(R.id.columnsInfo));		
		getColumnInfo();
		setContentView(this.mRootView);	
	}*/
	void getColumnInfo()
	{
		//mRootView.setLayoutParams(new ViewGroup.LayoutParams(160, LayoutParams.WRAP_CONTENT));
		//mRootView.measure(160, LayoutParams.WRAP_CONTENT);
		/*columnsInfo.removeAllViews();
		for(int i=0;i<info.size();i++)
		{			
			TextView column = new TextView(mContext);
			column.setText(info.get(i).getCol());
			column.setTextAppearance(mContext, R.style.profile_2_style);//style_font_pop_window);	
			column.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			column.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int width = column.getMeasuredWidth();
			//int textWidth = column.getMeasuredWidth();	
			//int leftMargin = (rootWidth - textWidth)/2;
			Log.i(TAG,"textwidth = " + width);
			int leftMargin = (160 - width)/4;
			if(leftMargin > 0)
				column.setPadding(leftMargin, 0, 0, 0);
			columnsInfo.addView(column);
		}*/
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<info.size()-1;i++)
		{
			builder.append(info.get(i));
			builder.append("\n");
		}
		if(info.size()>0)
			builder.append(info.get(info.size()-1));		
		mText.setText(builder.toString());
		if(info.size() > 2)
			mText.setPadding(0, 10, 0, 20);
	}
	public void show(View v,int startx,int starty,int posy)
	{
		//setRootViewId(R.layout.popup);
		//getColumnInfo();
		Log.i(TAG,"view size " + starty + " " + posy);
		preShow();	
		//mRootView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int height = mRootView.getMeasuredHeight();
		int width = mRootView.getMeasuredWidth();
		Log.i(TAG,"rootsize " + width + "x" + height);
		setAnimationStyle(true);		
		//final int NUM_OF_VISIBLE_LIST_ROWS = 4;
		mList.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);	//UNSPECIFIED
		//Log.i(TAG,"listsize " + mList.getMeasuredWidth() + "x" + mList.getMeasuredHeight());
		width = mList.getMeasuredWidth()+70;
		height = (int)((mList.getMeasuredHeight()) * (((float)columnIndex))/2);		
		mWindow.setWidth(width);
		mWindow.setHeight(height);	
		//int x = (posx - width)>>1;
		//int y = (posy - height)>>1;
		int x = 0;	
		if(startx >0)
			x = startx - width;
		int y = starty - height;
		Log.i(TAG,"listsize " + width + "x" + height + " x =" + x + " y=" + y);
		mWindow.showAtLocation(v, 0, startx, y);  
		showFlag = true;      
	}

	public void showPop(View v,int posx,int posy)
	{
		//setRootViewId(R.layout.popup);
		//getColumnInfo();
		preShow();	
		mRootView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int height = mRootView.getMeasuredHeight();
		int width = mRootView.getMeasuredWidth();
		Log.i(TAG,"rootsize " + width + "x" + height);
		setAnimationStyle(true);
		//final int NUM_OF_VISIBLE_LIST_ROWS = 4;
		mList.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);	//UNSPECIFIED
		//Log.i(TAG,"listsize " + mList.getMeasuredWidth() + "x" + mList.getMeasuredHeight());
		//width = mList.getMeasuredWidth()+30;
		//lrh add 1218
		width = mList.getMeasuredWidth()+70;
		//end
		
		height = (int)((mList.getMeasuredHeight()) * (((float)columnIndex))/2);
		
		mWindow.setWidth(width);
		mWindow.setHeight(height);
		//mWindow.setWidth(mList.getMeasuredWidth() + 40);
		//mWindow.setHeight((mList.getMeasuredHeight() + 10) * columnIndex);
		//height = mWindow.getMeasuredHeight();
		//width = mWindow.getMeasuredWidth();
		//mWindow.setWidth(width);
		//mWindow.setHeight(height);
		int x = posx;
		//height = height>>1;
		int y = posy-height;
		Log.i(TAG,"listsize " + width + "x" + height + " x =" + x + " y=" + y);
		mWindow.showAtLocation(v, 0, x-15, y);  
		showFlag = true;
	}
	public boolean getShow(){
		return showFlag;
	}
	public void clearShow(){
		showFlag = false;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
