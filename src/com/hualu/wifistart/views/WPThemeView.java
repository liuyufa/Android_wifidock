package com.hualu.wifistart.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.hualu.wifistart.views.WPTheme;

import java.lang.reflect.Method;

public final class WPThemeView extends RelativeLayout implements View.OnClickListener
{
	public static final int OVER_SCROLL_ALWAYS = 0;
	public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
	public static final int OVER_SCROLL_NEVER = 2;
	public static final String TAG = WPThemeView.class.getSimpleName();
	private static final RelativeLayout.LayoutParams accentsParams;
	//private static final String accentsString = "主题";
	private static final RelativeLayout.LayoutParams backsParams;
	private static final RelativeLayout.LayoutParams innersParams = new RelativeLayout.LayoutParams(-1, -1);
	private static Method mSetOverScrollMode;
	private static final Class<View> mViewClass;
	private ImageView back;
	private LinearLayout innerLayout;
	private OnAccentSelectedListener mAccentListener;
	boolean mChangeGlobal;
	private WPTextView mTitle;

	static
	{
		accentsParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		backsParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mViewClass = View.class;
	}

	public WPThemeView(Context paramContext)
	{
		super(paramContext);
		init(true);
	}

	public WPThemeView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		init(true);
	}

	public WPThemeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet);
		init(true);
	}

	public WPThemeView(Context paramContext, boolean paramBoolean)
	{
		super(paramContext);
		init(paramBoolean);
	}

	private void init(boolean paramBoolean)
	{
		this.mChangeGlobal = paramBoolean;
		ScrollView localScrollView;
		if (WPTheme.isDark())		
			setBackgroundColor(WPTheme.defMenuBackground);
		else
			setBackgroundColor(0xffffffff);
		localScrollView = new ScrollView(getContext());
		localScrollView.setBackgroundColor(0);
		localScrollView.setDrawingCacheBackgroundColor(0);
		localScrollView.setDrawingCacheEnabled(false);
		localScrollView.setFadingEdgeLength(0);
		innersParams.topMargin = 60;
		innersParams.addRule(10);
		localScrollView.setLayoutParams(innersParams);
		localScrollView.setVerticalScrollBarEnabled(false);
		localScrollView.setHorizontalScrollBarEnabled(false);
		setOverScrollMode(localScrollView, 2);
		setPadding(0, 16, 0, 0);
		this.mTitle = new WPTextView(getContext());
		accentsParams.leftMargin = 16;
		this.mTitle.setText("主题");
		this.mTitle.setFont(WPTextView.BOLD);
		this.mTitle.setTextSize(3, 8.0F);
		this.mTitle.setLayoutParams(accentsParams);
		addView(this.mTitle);
		this.innerLayout = new LinearLayout(getContext());
		this.innerLayout.setPadding(24, 8, 0, 0);
		this.innerLayout.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0;i<WPTheme.numOfThemeColors; ++i)
		{     
			WPColorSetting localWPColorSetting = new WPColorSetting(getContext());
			localWPColorSetting.setText(WPTheme.themeColorNames[i]);
			localWPColorSetting.setColor(WPTheme.themeColors[i]);
			localWPColorSetting.setOnClickListener(this);
			this.innerLayout.addView(localWPColorSetting);
		}

		this.back = new ImageView(getContext());
		backsParams.bottomMargin = 24;
		backsParams.rightMargin = 24;
		backsParams.alignWithParent = true;
		backsParams.addRule(12);
		backsParams.addRule(11);
		this.back.setLayoutParams(backsParams);
		localScrollView.addView(this.innerLayout);
		addView(localScrollView);
		addView(this.back);
		setTextColors(); 
	}

	public static final boolean setOverScrollMode(View paramView, int paramInt)
	{
		if (mSetOverScrollMode == null){
			try
			{
				Class<?> localClass = mViewClass;
				Class<?>[] arrayOfClass = new Class[1];
				arrayOfClass[0] = Integer.TYPE;
				mSetOverScrollMode = localClass.getMethod("setOverScrollMode", arrayOfClass);
			}
			catch (NoSuchMethodException localNoSuchMethodException)
			{
				return false;
			}
		}
		try
		{
			Method localMethod = mSetOverScrollMode;
			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = Integer.valueOf(paramInt);
			localMethod.invoke(paramView, arrayOfObject);
			return true;       
		}
		catch (Exception localException)
		{
			return false;
		}   
	}

	private void setTextColors()
	{
		int j = this.innerLayout.getChildCount();
		WPColorSetting localWPColorSetting;

		for(int i=0;i<j;i++)
		{
			localWPColorSetting = (WPColorSetting)this.innerLayout.getChildAt(i);
			if (localWPColorSetting.getColor() == WPTheme.getThemeColor())
				localWPColorSetting.setTextColor(WPTheme.selectedTextColor);
			else
				localWPColorSetting.setTextColor(0xffffffff);
		}  
	}

	public WPTextView getAccentTitle()
	{
		return this.mTitle;
	}

	public ImageView getBackButton()
	{
		return this.back;
	}

	public void onClick(View paramView)
	{
		WPColorSetting localWPColorSetting = (WPColorSetting)paramView;
		int i = localWPColorSetting.getColor();
		if (i != WPTheme.getThemeColor()){
			WPTheme.setThemeColor(i);
			setTextColors();
			localWPColorSetting.setTextColor(WPTheme.selectedTextColor);
			Log.i(TAG, "A new accent color has been choosen. " + i);
			if (this.mAccentListener != null)
				this.mAccentListener.onAccentSelected(i);
		}
	}

	public void setAccentText(int paramInt)
	{
		this.mTitle.setText(paramInt);
	}

	public void setOnAccentSelectedListener(OnAccentSelectedListener paramOnAccentSelectedListener)
	{
		this.mAccentListener = paramOnAccentSelectedListener;
	}

	public static abstract interface OnAccentSelectedListener
	{
		public abstract void onAccentSelected(int paramInt);
	}

	public static final class WPColorSetting extends LinearLayout
	{
		private int color;
		private View colorBox;
		private WPTextView textView;

		public WPColorSetting(Context paramContext)
		{
			super(paramContext);
			init();
		}

		public WPColorSetting(Context paramContext, AttributeSet paramAttributeSet)
		{
			super(paramContext, paramAttributeSet);
			init();
		}

		public WPColorSetting(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
		{
			super(paramContext, paramAttributeSet);
			init();
		}

		private void init()
		{
			setOrientation(0);
			this.colorBox = new View(getContext());
			LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(50, 50);
			localLayoutParams1.rightMargin = 15;
			this.colorBox.setLayoutParams(localLayoutParams1);
			this.textView = new WPTextView(getContext());
			this.textView.setTextSize(3, 13.0F);
			addView(this.colorBox);
			addView(this.textView);
			LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);//(-1, -2);
			localLayoutParams2.setMargins(10, 25, 10, 25);
			setLayoutParams(localLayoutParams2);
		}

		public int getColor()
		{
			return this.color;
		}

		public void setColor(int paramInt)
		{
			this.color = paramInt;
			this.colorBox.setBackgroundColor(this.color);
		}

		public void setText(String paramString)
		{
			this.textView.setText(paramString);
		}

		public void setTextColor(int paramInt)
		{
			this.textView.setTextColor(paramInt);
		}
	}
}