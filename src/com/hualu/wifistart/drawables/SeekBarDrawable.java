package com.hualu.wifistart.drawables;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;

import com.hualu.wifistart.views.WPTheme;

public class SeekBarDrawable extends LayerDrawable
{
	private ShapeDrawable mProgDrawable;

	public SeekBarDrawable(Drawable[] paramArrayOfDrawable)
	{
		super(paramArrayOfDrawable);
	}

	public boolean setDrawableByLayerId(int paramInt, Drawable paramDrawable)
	{
		if (paramInt == 16908301)
			this.mProgDrawable = ((ShapeDrawable)paramDrawable);
		return super.setDrawableByLayerId(paramInt, paramDrawable);
	}

	public void updateProgressColor()
	{
		if (this.mProgDrawable != null)
			this.mProgDrawable.getPaint().setColor(WPTheme.getThemeColor());
	}
}