package com.hualu.wifistart.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class PivotControl extends LinearLayout
{
	private static final int GRAY;
	public static final int SCROLL_STATE_DRAGGING = 1;
	public static final int SCROLL_STATE_IDLE = 0;
	public static final int SCROLL_STATE_SETTLING = 2;
	final static String TAG = "PivotControl";
	public int mActive = 0;
	PivotContainer mContainer;
	TextView mFadeToGrey;
	TextView mFadeToWhite;
	TouchLayout mHeaderContainer;
	Scroller mHeaderScroller;
	int mLeftMargin;
	int mPrevActive;
	private int mRightGap;
	private int mScrollState = 0;
	public String[] mTitles;
	
	private static final Interpolator sInterpolator = new Interpolator()
	{
		public float getInterpolation(float paramFloat)
		{
			float f = paramFloat - 1.0F;
			return 1.0F + f * (f * (f * (f * f)));
		}
	};
	static
	{
		GRAY = Color.argb(255, 128, 128, 128);
	}

	public PivotControl(Context paramContext)
	{
		this(paramContext, null);
	}

	public PivotControl(Context paramContext, AttributeSet paramAttributeSet)
	{
		this(paramContext, paramAttributeSet, 0);
	}

	public PivotControl(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet);
		init();
	}

	private void pageScrolled(int paramInt1, int paramInt2)
	{
	}

	private void scrollHeader(float paramFloat)
	{
		mHeaderContainer.scrollBy((int)(paramFloat / 2.0F), 0);
	}

	private void scrollHeaderTo(int destActive, int duration, boolean paramBoolean)
	{
		int i = 0;
		Log.i(TAG, "scrollHeaderTo " + destActive + " " + (-2 + mContainer.getChildCount()) + " " + mActive);
		Log.i(TAG, "eli " + mActive + " --> " + destActive);
		
		if ((destActive == 1) && (mActive == -2 + mContainer.getChildCount()) && (paramBoolean))
			i = 1;
		
		int statrX = mHeaderContainer.getScrollX();
		int k = mHeaderContainer.getChildAt(mActive).getLeft() - statrX;
		if (i != 0)
		{
			//Log.i(TAG, "left=" + this.mHeaderContainer.getChildAt(0).getLeft() + " " + k + " " + statrX + " " + this.mHeaderContainer.getChildAt(this.mActive).getLeft());
			//statrX = mHeaderContainer.getChildAt(0).getLeft() - k;
			mHeaderContainer.scrollTo(mHeaderContainer.getChildAt(0).getLeft()- k, mHeaderContainer.getScrollY());	
			statrX = this.mHeaderContainer.getChildAt(0).getLeft() - k;
		}
			//statrX = mHeaderContainer.getScrollX();
			//k = mHeaderContainer.getChildAt(mActive).getLeft() - statrX;
			int dx = mHeaderContainer.getChildAt(destActive).getLeft() - statrX;
			mHeaderScroller.startScroll(statrX, 0, dx, 0, duration);//startx,starty,dx,dy,duration
		
		
		//int statrX = mHeaderContainer.getScrollX();
		//int dx = mHeaderContainer.getChildAt(destActive).getLeft() - statrX;
		//mHeaderScroller.startScroll(statrX, 0, dx, 0, duration);//startx,starty,dx,dy,duration
		
		mPrevActive = mActive;
		mActive = destActive;
		mFadeToGrey = ((TextView)mHeaderContainer.getChildAt(mPrevActive));
		mFadeToWhite = ((TextView)mHeaderContainer.getChildAt(mActive));		
	}
	private void setHeaderTo(int paramInt)
	{
		Log.i(TAG,"setHeaderTo " + paramInt);
		mActive = paramInt;
		mHeaderScroller.abortAnimation();
		Log.i(TAG, "header set to " + paramInt);
		mHeaderContainer.scrollTo(mHeaderContainer.getChildAt(paramInt).getLeft(), mHeaderContainer.getScrollY());
		setHeaderColor(paramInt);
	}

	private void setScrollState(int paramInt)
	{
		if (mScrollState != paramInt)
			mScrollState = paramInt;	
	}

	public void addPage(View paramView)
	{
		Log.i(TAG,"addPage");
		mContainer.addView(paramView, -1 + mContainer.getChildCount());
	}

	int calcOffset(ViewGroup paramViewGroup, int paramInt)
	{
		int i = 0;
		for (int j = 0;j< paramInt; ++j)
		{
			if (j < paramViewGroup.getChildCount());
				i += paramViewGroup.getChildAt(j).getMeasuredWidth();
		}
		return i;
	}
/*
	protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
	{
		ViewGroup localViewGroup;
		int curX;
		int curY;
		int childCnt;
		if (paramView instanceof ViewGroup)
		{
			localViewGroup = (ViewGroup)paramView;
			curX = paramView.getScrollX();
			curY = paramView.getScrollY();
			childCnt = -1 + localViewGroup.getChildCount();
			label35: if (childCnt >= 0)
				break label59;
		}
		if ((paramBoolean) && (ViewCompat.canScrollHorizontally(paramView, -paramInt1)));
		for (int i = 1; ; i = 0)
		{
			while (true)
			{
			return i;
			label59: View localView = localViewGroup.getChildAt(l);
			if ((paramInt2 + curX < localView.getLeft()) || 
					(paramInt2 + curX >= localView.getRight()) || 
					(paramInt3 + curY < localView.getTop()) || 
					(paramInt3 + curY >= localView.getBottom()) || 
					(!canScroll(localView, true, paramInt1, paramInt2 + curX - localView.getLeft(), paramInt3 + curY - localView.getTop()))
					)
			break;
			i = 1;
			}
			--l;
			break label35:
		}
	}*/

	void clearHeaderColor()
	{
		for (int i = 0;i<mHeaderContainer.getChildCount(); ++i)
		{
			((TextView)this.mHeaderContainer.getChildAt(i)).setTextColor(GRAY);
		}
	}

	public void computeScroll()
	{
		float f;
		if ((!mHeaderScroller.isFinished()) && (mHeaderScroller.computeScrollOffset()))
		{
			int i = getScrollX();
			int j = getScrollY();
			int k = mHeaderScroller.getCurrX();
			int l = mHeaderScroller.getCurrY();
			if ((i != k) || (j != l))
				mHeaderContainer.scrollTo(k, l);
			f = mHeaderScroller.timePassed() / mHeaderScroller.getDuration();
			if (f > 1.0F)
				f = 1.0F;
			{
				clearHeaderColor();
				mFadeToGrey.setTextColor(Color.argb(255, (int)(255.0F - f * 127.0F), (int)(255.0F - f * 127.0F), (int)(255.0F - f * 127.0F)));
				mFadeToWhite.setTextColor(Color.argb(255, (int)(128.0F + f * 127.0F), (int)(128.0F + f * 127.0F), (int)(128.0F + f * 127.0F)));
			}
			setHeaderColor(mActive);
			invalidate();
		}		
	}

	public int getActivePage()
	{
		return -1 + mActive;
	}

	void init()
	{
		Log.i(TAG,"pivotcontrol init");
		mLeftMargin = (int)(8.0F * getResources().getDisplayMetrics().density);
		mRightGap = (int)(10.0F * getResources().getDisplayMetrics().density);
		mHeaderScroller = new Scroller(getContext(), sInterpolator);
		setOrientation(LinearLayout.VERTICAL);//1
		
		mHeaderContainer = new TouchLayout(getContext());
		mHeaderContainer.setOrientation(LinearLayout.HORIZONTAL);//0
		mHeaderContainer.setLayoutParams(new LinearLayout.LayoutParams(View.MeasureSpec.makeMeasureSpec(6000, MeasureSpec.EXACTLY), LinearLayout.LayoutParams.WRAP_CONTENT));
		addView(mHeaderContainer);
		
		mContainer = new PivotContainer(getContext());
		mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		addView(mContainer);
	}

	public void setActivePage(int paramInt)
	{
		mContainer.scrollToPage(paramInt + 1);
		scrollHeaderTo(paramInt + 1, 0, false);
		mActive = (paramInt + 1);
	}

	void setHeaderColor(int paramInt)
	{
		clearHeaderColor();
		TextView localTextView = (TextView)mHeaderContainer.getChildAt(paramInt);
		localTextView.setTextColor(0xffffffff);
	}

	public void setTitles(String[] titles)
	{
		Log.i(TAG,"setTitles");
		mTitles = titles;
		mHeaderContainer.removeAllViews();
		TextView localTextView1 = new TextView(getContext());
		localTextView1.setText(titles[(-1 + titles.length)]);
		localTextView1.setTextSize(3, 24.0F);
		localTextView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		localTextView1.setPadding(0, 0, mRightGap, 0);
		//localTextView1.setTypeface(WPFonts.getFontSet().getLight());
		mHeaderContainer.addView(localTextView1);
		int i = titles.length;
		for (int j = 0;j<i; ++j)
		{		
			String str = titles[j];
			TextView localTextView3 = new TextView(getContext());
			localTextView3.setText(str);
			localTextView3.setTextSize(3, 24.0F);
			localTextView3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			localTextView3.setPadding(0, 0, this.mRightGap, 0);
			//localTextView3.setTypeface(WPFonts.getFontSet().getLight());
			mHeaderContainer.addView(localTextView3);
		}
		
		TextView localTextView2 = new TextView(getContext());
		localTextView2.setText(titles[0]);
		localTextView2.setTextSize(3, 24.0F);
		localTextView2.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
		localTextView2.setPadding(0, 0, this.mRightGap, 0);
		//localTextView2.setTypeface(WPFonts.getFontSet().getLight());
		mHeaderContainer.addView(localTextView2);
		Log.i(TAG,"setTitles ok");
	}

	class PivotContainer extends LinearLayout
	{
		//private static final int INVALID_POINTER = -1;
		//private static final int MAX_SETTLE_DURATION = 600;
		private int mActivePointerId = -1;
		int mCurItem = 0;
		//private int mFlingDistance;
		boolean mInitial = true;
		private float mInitialMotionX;
		private boolean mIsBeingDragged;
		public boolean mIsUnableToDrag;
		private float mLastMotionX;
		private float mLastMotionY;
		ImageView mLeftMirror;
		private int mMaximumVelocity;
		public int mMinimumVelocity;
		ImageView mRightMirror;
		private Scroller mScroller;
		boolean mScrolling;
		private int mTouchSlop;
		VelocityTracker mVelocityTracker;
		boolean mWrappedRight;

		public PivotContainer(Context context)
		{
			super(context);
			init();
		}

		private void completeScroll()
		{
			if (mScrolling)
			{
				mScroller.abortAnimation();
				PivotControl.this.setScrollState(SCROLL_STATE_IDLE);
			}
			mScrolling = false;
		}
		
		private int determineTargetPage(int paramInt1, float paramFloat, int vel, int dist)
		{
			/*int i = paramInt1;
			
			if ((Math.abs(vel) > this.mMinimumVelocity)){ //(Math.abs(dist) > this.mFlingDistance) &&
				i = (int)(0.5F + (paramFloat + paramInt1));
			}*/
			return (int)(0.5F + (paramFloat + paramInt1));
		}
		/*
	    private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
	    {
	      if ((Math.abs(paramInt3) > this.mFlingDistance) && (Math.abs(paramInt2) > this.mMinimumVelocity))
	        if (paramInt2 <= 0);
	      for (int i = paramInt1; ; i = (int)(0.5F + (paramFloat + paramInt1)))
	        while (true)
	        {
	          Log.i("piip", "calculated from " + paramFloat + " to " + i);
	          return i;
	          i = paramInt1 + 1;
	        }
	    }
	*/

		private void endDrag()
		{
			mIsBeingDragged = false;
			mIsUnableToDrag = false;
			if (mVelocityTracker != null)	
				mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		private void scrollToPage(int paramInt)
		{
			mCurItem = paramInt;
			int i = calcOffset(paramInt);
			scrollTo(i, 0);
			PivotControl.this.pageScrolled(i, paramInt - 1);
		}

/*		private void takeSnapshotToRight()
		{
		}*/
		
		int calcOffset(int paramInt)
		{
			int childLeft = 0;
			for(int i = 0;i < paramInt ;i++){
				if(i < getChildCount()){
					//Log.i(TAG,"chile width = " +  getChildAt(i).getMeasuredWidth());
					childLeft += (int)(getChildAt(i).getMeasuredWidth() + 16.0F);		
				}
			}
			return childLeft;
		}

		public void computeScroll()
		{
			if ((!mScroller.isFinished()) && (mScroller.computeScrollOffset()))
			{
				int i = getScrollX();
				int j = getScrollY();
				int k = mScroller.getCurrX();
				int l = mScroller.getCurrY();
				if ((i != k) || (j != l))
				{
					scrollTo(k, l);
					PivotControl.this.pageScrolled(k, mCurItem);
				}
				invalidate();
				return;
			}
			if (mCurItem == 0)
			{
				mCurItem = (-2 + getChildCount());
				scrollTo(calcOffset(-2 + getChildCount()), 0);
				PivotControl.this.setHeaderTo(-2 + getChildCount());
			}
			
			{
				completeScroll();			
				//getChildCount();
			}
		}

		float distanceInfluenceForSnapDuration(float paramFloat)
		{
			return (float)Math.sin((float)(0.47123891676382D * (paramFloat - 0.5F)));
		}

		void init()
		{
			Log.i(TAG,"pivorcontainer init");
			setOrientation(LinearLayout.HORIZONTAL);
			ImageView localImageView1 = new ImageView(getContext());
			localImageView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			mLeftMirror = localImageView1;
			ImageView localImageView2 = new ImageView(getContext());
			localImageView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			mRightMirror = localImageView2;
			addView(localImageView1);
			addView(localImageView2);
			mScroller = new Scroller(getContext(), PivotControl.sInterpolator);
			ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
			mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(localViewConfiguration);
			mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
			mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
		}
		
		public boolean onInterceptTouchEvent(MotionEvent event)
		{
			int action = MotionEvent.ACTION_MASK & event.getAction();
			
			if ((action == MotionEvent.ACTION_CANCEL) || (action == MotionEvent.ACTION_UP))
			{
				mIsBeingDragged = false;
				mIsUnableToDrag = false;
				mActivePointerId = -1;
				if (mVelocityTracker != null)
				{
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				return false;
			}
			//if (mIsUnableToDrag)
			//	return false;
			
			boolean ret  = true;
			Log.i(TAG,"onInterceptTouchEvent action =" + action);
			switch (action)
			{
				case MotionEvent.ACTION_UP:	//1			
					break;
				case MotionEvent.ACTION_MOVE://2
					if (!mIsBeingDragged)
					{
						if (mVelocityTracker == null)
							mVelocityTracker = VelocityTracker.obtain();
						mVelocityTracker.addMovement(event);
					}
					ret = mIsBeingDragged;
					Log.i(TAG,"onInterceptTouchEvent " + ret);
					//return bool;
					int j = mActivePointerId;
					float f2;					
					float f3;
					float f4;
					float f5;
					float f6;
					if (j != -1){					
						int k = MotionEventCompat.findPointerIndex(event, j);
						if (k >= 0)
						{
							f2 = MotionEventCompat.getX(event, k);							
							f5 = MotionEventCompat.getY(event, k);
						}else{
							f2 = event.getX();
							f5 = event.getY();
						}
						f3 = f2 - this.mLastMotionX;
						f4 = Math.abs(f3);
						f6 = Math.abs(f5 - this.mLastMotionY);
						//if (PivotControl.this.canScroll(this, false, (int)f3, (int)f2, (int)f5))
						{
							mLastMotionX = f2;
							mInitialMotionX = f2;
							mLastMotionY = f5;
							//ret = false;

							if ((f4 > mTouchSlop) && (f4 > f6))
							{
								mIsBeingDragged = true;
								PivotControl.this.setScrollState(SCROLL_STATE_DRAGGING);
								mLastMotionX = f2;
								//Log.i("gllauncher", "starting drag at " + this.mCurItem);
								if (mCurItem == 1)
								{
									renderImages();
									Log.i("gllauncher", "Images refreshed!");
								}
								if (this.mCurItem == -2 + getChildCount())								
									renderImages();
								//Log.i("gllauncher", "Images refreshed (last item)!");
							}
							else if (f6 > this.mTouchSlop){
								mIsUnableToDrag = true;
								ret = false;
							}
						}
					}					
					break;
				case MotionEvent.ACTION_DOWN: //0
					float f1 = event.getX();
					mInitialMotionX = f1;
					mLastMotionX = f1;
					mLastMotionY = event.getY();
					mActivePointerId = MotionEventCompat.getPointerId(event, 0);
					if (PivotControl.this.mScrollState == SCROLL_STATE_SETTLING)
					{
						mIsBeingDragged = true;
						mIsUnableToDrag = false;
						PivotControl.this.setScrollState(SCROLL_STATE_DRAGGING);
					}
					mIsBeingDragged = false;
					mIsUnableToDrag = false;
					ret = false;
					break;
			}
			//Log.i(TAG,"onInterceptTouchEvent " + ret);
			return ret;
		}
		
		protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
		{
			Log.i(TAG,"container onlayout " + getChildCount());
			for (int i = 0; i<getChildCount(); ++i)
			{				
				int j = calcOffset(i);
				View localView = getChildAt(i);
				localView.layout(j, 0, j + localView.getMeasuredWidth(),getMeasuredHeight());
			}
			
			if (mInitial)
			{
				this.mInitial = false;
				renderImages();
				mCurItem = 1;
				scrollTo(calcOffset(1), 0);
				Log.i(TAG, "setting header to 1");
				PivotControl.this.setHeaderTo(1);
			}			
		}

		protected void onMeasure(int paramInt1, int paramInt2)
		{
			setMeasuredDimension(getDefaultSize(0, paramInt1), getDefaultSize(0, paramInt2));
			int i = (int)(getMeasuredWidth() - PivotControl.this.mLeftMargin - 16.0F * getResources().getDisplayMetrics().density);
			int j = getMeasuredHeight();
			int k = View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY);
			int l = View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY);
			for (int cnt = 0; cnt < getChildCount(); ++cnt)
			{
				getChildAt(cnt).measure(k, l);
			}
		}
		
		public boolean onTouchEvent(MotionEvent event)
		{	
			boolean ret = true;
			if ((event.getAction() == MotionEvent.ACTION_DOWN) && (event.getEdgeFlags() != 0))
			{	
				return false;
			}
			if (mVelocityTracker == null)
				mVelocityTracker = VelocityTracker.obtain();
			mVelocityTracker.addMovement(event);
			switch (event.getAction())
			{		
				case MotionEvent.ACTION_DOWN://0
					completeScroll();
					float xpos = event.getX();
					mInitialMotionX = xpos;
					mLastMotionX = xpos;
					mActivePointerId = MotionEventCompat.getPointerId(event, 0);
					break;
				case MotionEvent.ACTION_MOVE://2
					float f6;
					float f8;
					if (!mIsBeingDragged)
					{
						int i7 = MotionEventCompat.findPointerIndex(event, mActivePointerId);
						if (i7 < 0){
							f6 = event.getX();
							f8 = Math.abs(event.getY() - mLastMotionY);
						}
						else{
							f6 = MotionEventCompat.getX(event, i7);
							f8 = Math.abs(MotionEventCompat.getY(event, i7) - mLastMotionY);
						}
						 
						float f7 = Math.abs(f6 - this.mLastMotionX);
						//float f8 = Math.abs(MotionEventCompat.getY(event, i7) - this.mLastMotionY);
						if ((f7 > mTouchSlop) && (f7 > f8))
						{
							mIsBeingDragged = true;
							mLastMotionX = f6;
							PivotControl.this.setScrollState(SCROLL_STATE_DRAGGING);
						}
					}
					if (mIsBeingDragged){	
						int i5 = MotionEventCompat.findPointerIndex(event, mActivePointerId);
						float f2;
						float f3;
						float f4;
						if (i5 >= 0)						
							f2 = MotionEventCompat.getX(event, i5);
						else
							f2 = event.getX();
						
							f3 = mLastMotionX - f2;
							mLastMotionX = f2;
							f4 = f3 + getScrollX();
							//(getWidth() - PivotControl.this.mRightGap);
							int i6 = -2 + getChildCount();
							float f5 = calcOffset(Math.min(1 + mCurItem, i6));
							if(f4 > f5){
								f4 -= f5;
							}
								this.mLastMotionX += f4 - (int)f4;
								scrollTo((int)f4, getScrollY());
								PivotControl.this.scrollHeader(f3);
								PivotControl.this.pageScrolled((int)f4, mCurItem);
							
						//for (this.mWrappedRight = true; ; this.mWrappedRight = false)
						//ret = true;	
					}
						break;
				case MotionEvent.ACTION_UP://1
				case MotionEvent.ACTION_CANCEL://3
					if (mIsBeingDragged){									
						VelocityTracker localVelocityTracker = this.mVelocityTracker;
						localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
						int j = (int)VelocityTrackerCompat.getXVelocity(localVelocityTracker, this.mActivePointerId);
						int k = getWidth() - PivotControl.this.mRightGap;
						int l = getScrollX();
						int i1 = l / k;
						float f1 = (float)(l % k) / k;
						Log.i(TAG, "cpage=" + i1 + " " + f1 + " " + Math.floor(l / k));
						int i2 = MotionEventCompat.findPointerIndex(event, this.mActivePointerId);
						if (i2 >= 0)
						{
							int i4 = determineTargetPage(i1, f1, j, (int)(MotionEventCompat.getX(event, i2) - this.mInitialMotionX));
							Log.i(TAG, "Next page will be " + i4 + "  => " + l + " " + getScrollX());
							setCurrentItemInternal(i4, true, true, j);
						}else{
							int i3 = determineTargetPage(i1, f1, j, (int)(event.getX() - this.mInitialMotionX));
							Log.i(TAG, "Next page will be " + i3 + "  => " + l + " " + getScrollX());
							setCurrentItemInternal(i3, true, true, j);
						}	
					}
					this.mActivePointerId = -1;
					endDrag();
					break;
			}
			return ret;
		}
		
		@SuppressWarnings("deprecation")
		void renderImages()
		{
			View localView1 = getChildAt(1);
			View localView2 = getChildAt(-2 + getChildCount());
			try
			{
				mRightMirror.setImageDrawable(null);
				Bitmap localBitmap1 = Bitmap.createBitmap(localView1.getMeasuredWidth(), localView1.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
				localView1.draw(new Canvas(localBitmap1));
				mRightMirror.setImageDrawable(new BitmapDrawable(localBitmap1));
				mLeftMirror.setImageDrawable(null);
				Bitmap localBitmap2 = Bitmap.createBitmap(localView1.getMeasuredWidth(), localView1.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
				localView2.draw(new Canvas(localBitmap2));
				mLeftMirror.setImageDrawable(new BitmapDrawable(localBitmap2));
				return;
			}
			catch (OutOfMemoryError localOutOfMemoryError)
			{
				Log.w(TAG, "No memory for off-screen images.");
			}
		}

		void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
		{
			setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0);
		}

		void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
		{
			int i;
			if (paramInt1 < 0)
			{
				paramInt1 = 0;
			}
			if (paramInt1 >= -1 + getChildCount())
				paramInt1 = -1 + getChildCount();
			mCurItem = paramInt1;
			i = calcOffset(paramInt1);
			Log.i(TAG, "scroll from " + getScrollX() + " to " + i);
			if (!paramBoolean1)
				scrollTo(i, 0);
			else
				smoothScrollTo(i, 0, paramInt2);
		}

		void smoothScrollTo(int dx, int dy)
		{
			smoothScrollTo(dx, dy, 0);
		}

		void smoothScrollTo(int dstX, int dstY, int paramInt3)
		{
			if (getChildCount() != 0){
				int startX;
				int startY;
				int dx;
				int dy;	
				startX = getScrollX();
				startY = getScrollY();
				dx = dstX - startX;
				dy = dstY - startY;
				if((dx == 0) && (dy== 0))
					setScrollState(SCROLL_STATE_IDLE);
				else{
					mScrolling = true;
					setScrollState(SCROLL_STATE_SETTLING);
					int i1 = getWidth();
					int i2 = i1 / 2;
					float f1 = Math.min(1.0F, 1.0F * Math.abs(dx) / i1);
					float f2 = i2 + i2 * distanceInfluenceForSnapDuration(f1);
					int i3 = Math.abs(paramInt3);
					int i4;
					int i5;
					PivotControl localPivotControl;
					int i6;
					boolean bool;
					if (i3 > 0)
					{
						i4 = 4 * Math.round(1000.0F * Math.abs(f2 / i3));
						bool = true;
					}
					else{
						i4 = (int)(100.0F * (1.0F + Math.abs(dx) / i1));
						bool = false;
					}
						
					i5 = Math.min(i4, 600);
					Log.i(TAG, "starting scroll " + startX + " " + dx + " and header to " + this.mCurItem);
					mScroller.startScroll(startX, startY, dx, dy, i5);
					localPivotControl = PivotControl.this;
					i6 = mCurItem;			
				
					localPivotControl.scrollHeaderTo(i6, i5, bool);
					invalidate();
				}			
				
				//
			}
		}
	}

	class TouchLayout extends LinearLayout
	{		
		public TouchLayout(Context context)
		{
			super(context);
		}

		public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
		{
			Log.i(TAG, "intercepting ");
			return true;
		}
				
	}
}
