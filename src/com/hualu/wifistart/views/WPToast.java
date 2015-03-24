package com.hualu.wifistart.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hualu.wifistart.views.WPFonts;
import com.hualu.wifistart.views.WPTheme;

import java.util.Date;

public class WPToast extends Toast
{
	public static final String TAG = WPToast.class.getSimpleName();
	private static Drawable icon;
	private WPToastView mToastView;

	public WPToast(Context paramContext)
	{
		super(paramContext);
		init(paramContext, false);
	}

	public WPToast(Context paramContext, boolean paramBoolean)
	{
		super(paramContext);
		init(paramContext, paramBoolean);
	}

	private void init(Context paramContext, boolean paramBoolean)
	{
		this.mToastView = new WPToastView(paramContext, paramBoolean);
		setGravity(48, 0, 0);
		if (icon != null)
			this.mToastView.setIcon(icon);
		setView(this.mToastView);		
	}

	public static WPToast makeText(Context paramContext, int paramInt1, int paramInt2)
	{
		return makeText(paramContext, paramContext.getResources().getString(paramInt1), paramInt2);
	}

	public static WPToast makeText(Context paramContext, CharSequence paramCharSequence, int paramInt)
	{
		WPToast localWPToast = new WPToast(paramContext);
		localWPToast.setIcon(icon);
		localWPToast.setText(paramCharSequence, paramInt);
		return localWPToast;
	}

	public static WPToast makeText(Context paramContext, String paramString, int paramInt, Drawable paramDrawable)
	{
		WPToast localWPToast = new WPToast(paramContext);
		localWPToast.setIcon(paramDrawable);
		localWPToast.setText(paramString, paramInt);
		return localWPToast;
	}

	public static WPToast makeTextWithTime(Context paramContext, CharSequence paramCharSequence, int paramInt)
	{
		WPToast localWPToast = new WPToast(paramContext, true);
		localWPToast.setText(paramCharSequence, paramInt);
		return localWPToast;
	}

	public static WPToast makeTextWithTime(Context paramContext, String paramString, int paramInt, Drawable paramDrawable)
	{
		WPToast localWPToast = new WPToast(paramContext, true);
		localWPToast.setIcon(paramDrawable);
		localWPToast.setText(paramString, paramInt);
		return localWPToast;
	}

	public static void setUniversalIcon(Drawable paramDrawable)
	{
		icon = paramDrawable;
	}

	public void setDisplayTime(boolean paramBoolean)
	{
		mToastView.setDisplayTime(paramBoolean);
	}

	public void setIcon(Drawable paramDrawable)
	{
		mToastView.setIcon(paramDrawable);
	}

	public WPToast setText(CharSequence paramCharSequence, int paramInt)
	{
		mToastView.setText(paramCharSequence);
		setDuration(paramInt);
		return this;
	}

	public static final class WPToastView extends LinearLayout
	{
		//private static final int iconPadding = 10;
		//private static final int textSize = 7;
		//private static final int units = 3;
		private WPTextView textView;
		private boolean timeOn = false;
		private WPTextView timeView;

		public WPToastView(Context paramContext)
		{
			super(paramContext);
			init();
		}

		public WPToastView(Context paramContext, AttributeSet paramAttributeSet)
		{
			super(paramContext, paramAttributeSet);
			init();
		}

		public WPToastView(Context paramContext, boolean paramBoolean)
		{
			super(paramContext);
			this.timeOn = paramBoolean;
			init();
		}

		@SuppressWarnings("deprecation")
		private String currentTime()
		{
			Date localDate = new Date();
			//int i = localDate.getHours();
			int j = localDate.getMinutes();
			//StringBuilder localStringBuilder = new StringBuilder(String.valueOf(i)).append(":");
			String curTime;
			if (j < 10)
				curTime = "0" + j;
			else
				curTime = "" + j;		
			return curTime;
		}

		private void init()
		{
			this.textView = new WPTextView(getContext());
			this.textView.setTextSize(3, 12.0F);
			this.textView.setGravity(3);
			this.textView.setTextColor(-1);
			this.textView.setSingleLine(false);
			this.textView.setCompoundDrawablePadding(10);
			this.textView.setTypeface(WPFonts.getFontSet().getLight());
			if (this.timeOn)
			{
				this.timeView = new WPTextView(getContext());
				this.timeView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
				this.timeView.setTextSize(3, 12.0F);
				this.timeView.setGravity(5);
				this.timeView.setTextColor(-1);
				this.timeView.setPadding(0, 0, 10, 0);
				this.timeView.setText(currentTime());
				addView(this.timeView);
			}
			setBackgroundColor(WPTheme.getThemeColor());
			if (this.timeOn)
				setPadding(15, 5, 5, 8);
			else
				setPadding(10, 8, 8, 8);
			
			setOrientation(1);
			setMinimumWidth(9999);
			addView(this.textView);
			
		}

		public void setDisplayTime(boolean paramBoolean)
		{
			this.timeOn = paramBoolean;
		}

		public void setIcon(Drawable paramDrawable)
		{
			this.textView.setCompoundDrawablesWithIntrinsicBounds(paramDrawable, null, null, null);
		}

		public void setText(CharSequence paramCharSequence)
		{
			if (this.timeOn)
				this.timeView.setText(currentTime());
			else
				this.textView.setText(paramCharSequence);
		}
	}
}