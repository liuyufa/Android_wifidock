package com.hualu.wifistart.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopupWindows
{
	protected Drawable mBackground = null;
	protected Context mContext;
	protected View mRootView;
	protected PopupWindow mWindow;
	protected WindowManager mWindowManager;

	public PopupWindows(Context paramContext)
	{
		mContext = paramContext;
		mWindow = new PopupWindow(paramContext);
		mWindow.setTouchInterceptor(new View.OnTouchListener()
		{
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
			{
				if (paramMotionEvent.getAction() == MotionEvent.ACTION_OUTSIDE)//4
				{
					PopupWindows.this.mWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
	}

	public void dismiss()
	{
		mWindow.dismiss();
	}

	protected void onDismiss()
	{
	}

	protected void onShow()
	{
	}

	@SuppressWarnings("deprecation")
	protected void preShow()
	{
		if (mRootView == null)
			throw new IllegalStateException("setContentView was not called with a view to display.");
		onShow();
		if (mBackground == null)
			mWindow.setBackgroundDrawable(new BitmapDrawable());
		else
			mWindow.setBackgroundDrawable(this.mBackground);
		mWindow.setTouchable(true);
		mWindow.setFocusable(true);
		mWindow.setOutsideTouchable(true);
		mWindow.setContentView(this.mRootView);
	}

	public void setBackgroundDrawable(Drawable paramDrawable)
	{
		mBackground = paramDrawable;
	}
	public void setContentView(int paramInt)
	{
		setContentView(((LayoutInflater)mContext.getSystemService("layout_inflater")).inflate(paramInt, null));
	}

	public void setContentView(View paramView)
	{
		mRootView = paramView;
		mWindow.setContentView(paramView);
	}

	public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener)
	{
		mWindow.setOnDismissListener(paramOnDismissListener);
	}
}