package com.hualu.wifistart.views;

import com.hualu.wifistart.views.WPTheme;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;


public class AutoTextView extends TextView
{
	private int mColor = 0;
	private boolean mHasColor = false;

	public AutoTextView(Context paramContext)
	{
		super(paramContext);
	}

	public AutoTextView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
	}

	public AutoTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onDraw(Canvas paramCanvas)
	{
		if (this.mHasColor)
			setTextColor(this.mColor);
		else
		{
		if (WPTheme.isDark())
			setTextColor(0xffffffff);
		else
			setTextColor(0xff000000);
		}
		super.onDraw(paramCanvas);	
	}

	public void setTextColor2(int paramInt)
	{
		this.mColor = paramInt;
		this.mHasColor = true;
	}
}
