package com.hualu.wifistart.drawables;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import com.hualu.wifistart.views.WPTheme;

public class ToggleDrawable
{
	private boolean mEnabled;
	private LayerDrawable mLayer;
	private ShapeDrawable mThumb = new ShapeDrawable(new RectShape());

	public ToggleDrawable(boolean paramBoolean, int paramInt)
	{
		if (paramBoolean);
		else
		{
			this.mEnabled = paramBoolean;
			setEnabled(paramBoolean);
			ShapeDrawable localShapeDrawable1 = new ShapeDrawable(new RectShape());
			ShapeDrawable localShapeDrawable2 = new ShapeDrawable(new RectShape());
			localShapeDrawable1.getPaint().setColor(paramInt);
			localShapeDrawable2.getPaint().setColor(paramInt);
			this.mThumb.setIntrinsicHeight(46);
			this.mThumb.setIntrinsicWidth(23);
			localShapeDrawable1.setIntrinsicHeight(46);
			localShapeDrawable2.setIntrinsicHeight(46);
			localShapeDrawable1.setIntrinsicWidth(6);
			localShapeDrawable2.setIntrinsicWidth(6);
			Drawable[] arrayOfDrawable = new Drawable[3];
			arrayOfDrawable[0] = localShapeDrawable1;
			arrayOfDrawable[1] = this.mThumb;
			arrayOfDrawable[2] = localShapeDrawable2;
			this.mLayer = new LayerDrawable(arrayOfDrawable);
			this.mLayer.setLayerInset(0, 0, 0, 17, 0);
			this.mLayer.setLayerInset(1, 6, 0, 0, 0);
			this.mLayer.setLayerInset(2, 23, 0, 0, 0);

		}
	}

	public LayerDrawable getDrawable()
	{
		return this.mLayer;
	}

	public boolean isEnabled()
	{
		return this.mEnabled;
	}

	public void setEnabled(boolean paramBoolean)
	{
		Paint localPaint;
		if (this.mEnabled != paramBoolean)
		{
			this.mEnabled = paramBoolean;
			localPaint = this.mThumb.getPaint();
			if (!this.mEnabled){
				int i = WPTheme.textBoxBorderDisabled;
				localPaint.setColor(i);
			}
		}	
	}

	public void toggle()
	{
		boolean bool = false;
		if (this.mEnabled)
			bool = true;	
		setEnabled(bool);		
	}
}