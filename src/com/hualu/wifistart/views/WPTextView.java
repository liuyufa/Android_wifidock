package com.hualu.wifistart.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hualu.wifistart.views.WPFonts;

public class WPTextView extends TextView
{
	public static final int BOLD = -1349;
	public static final int ITALIC = -2837;
	public static final int LIGHT = -5937;
	public static final int MEDIUM = -3853;
	public static final int REGULAR = -4752;
	public static final String TAG = WPTextView.class.getSimpleName();
	private int FONT;

	public WPTextView(Context paramContext)
	{
		super(paramContext);
		init();
	}

	public WPTextView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		init();
	}

	public WPTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
		init();
	}

	public WPTextView(Context paramContext, boolean paramBoolean)
	{
		super(paramContext);
		init();
	}

	private void init()
	{
		WPFonts.setFonts(getContext().getAssets());
		setFont(REGULAR);
	}

	public void setFont(int paramInt)
	{

	switch (paramInt)
	{   
		case REGULAR:
			if (this.FONT != REGULAR)
				setTypeface(WPFonts.getFontSet().getRegular());
			break;
		case BOLD:
			if (this.FONT != BOLD)
				setTypeface(WPFonts.getFontSet().getBold());
			break;
		case ITALIC:
			if (this.FONT != ITALIC)
				setTypeface(WPFonts.getFontSet().getItalic());
			break;
		case LIGHT:
			if (this.FONT != LIGHT)
				setTypeface(WPFonts.getFontSet().getLight());
			break;
		case MEDIUM:
			if (this.FONT != MEDIUM)
				setTypeface(WPFonts.getFontSet().getRegular());
			break;
		default:
			break;
	}
	this.FONT = paramInt;    
	}
}