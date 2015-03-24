package com.hualu.wifistart.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.StateSet;

import com.hualu.wifistart.drawables.ButtonDrawable;
import com.hualu.wifistart.drawables.CheckBoxDrawable;
import com.hualu.wifistart.drawables.CircleDrawable;
import com.hualu.wifistart.drawables.SeekBarDrawable;

public final class WPDrawables
{
	public static final int BVBlack = 1;
	public static final int BVDisabled = 3;
	public static final int BVGray = 4;
	public static final int BVWhite = 2;
	public static final String TAG = WPDrawables.class.getSimpleName();
	public static final int TBBlur = 2;
	public static final int TBDisabled = 4;
	public static final int TBFocus = 1;
	public static final int TBSent = 3;

	public static final ButtonDrawable getBVDrawable(int paramInt)
	{
		int i;
		int j;
		if (WPTheme.isDark())
		{
			i = 0xff000000;
			j = 0xffffffff;
		}else{
			i = 0xffffffff;
			j = 0xff000000;
		}
		
		switch (paramInt)
		{
			default:
			case 1:
				i = 0xff000000;
				j = 0xffffffff;
				break;		
			case 2:
				i = 0xffffffff;
				j = 0xff000000;
				break;
			case 3:
			case 4:
				i = 0xff000000;
				j = Color.rgb(200, 200, 200);
				break;			
		}
		
		ButtonDrawable localButtonDrawable = new ButtonDrawable(i, j);
		localButtonDrawable.setPadding(11, 11, 16, 11);
		return localButtonDrawable;
	}

	public static final StateListDrawable getCheckBoxDrawable(Context paramContext)
	{
		CheckBoxDrawable localCheckBoxDrawable1 = new CheckBoxDrawable(paramContext, WPTheme.radioDisabled, false);
		CheckBoxDrawable localCheckBoxDrawable2 = new CheckBoxDrawable(paramContext, WPTheme.radioDisabled, true);
		CheckBoxDrawable localCheckBoxDrawable3 = new CheckBoxDrawable(paramContext, WPTheme.radioRest, false);
		CheckBoxDrawable localCheckBoxDrawable4 = new CheckBoxDrawable(paramContext, -1, false);
		CheckBoxDrawable localCheckBoxDrawable5 = new CheckBoxDrawable(paramContext, WPTheme.radioRest, true);
		CheckBoxDrawable localCheckBoxDrawable6 = new CheckBoxDrawable(paramContext, -1, true);
		StateListDrawable localStateListDrawable = new StateListDrawable();
		int[] arrayOfInt1 = new int[3];
		arrayOfInt1[0] = 16842910;
		arrayOfInt1[1] = 16842912;
		arrayOfInt1[2] = 16842919;
		localStateListDrawable.addState(arrayOfInt1, localCheckBoxDrawable6);
		int[] arrayOfInt2 = new int[2];
		arrayOfInt2[0] = 16842910;
		arrayOfInt2[1] = 16842912;
		localStateListDrawable.addState(arrayOfInt2, localCheckBoxDrawable5);
		int[] arrayOfInt3 = new int[2];
		arrayOfInt3[0] = 16842910;
		arrayOfInt3[1] = 16842919;
		localStateListDrawable.addState(arrayOfInt3, localCheckBoxDrawable4);
		int[] arrayOfInt4 = new int[1];
		arrayOfInt4[0] = 16842910;
		localStateListDrawable.addState(arrayOfInt4, localCheckBoxDrawable3);
		int[] arrayOfInt5 = new int[2];
		arrayOfInt5[0] = 16842919;
		arrayOfInt5[1] = 16842912;
		localStateListDrawable.addState(arrayOfInt5, localCheckBoxDrawable2);
		int[] arrayOfInt6 = new int[1];
		arrayOfInt6[0] = 16842912;
		localStateListDrawable.addState(arrayOfInt6, localCheckBoxDrawable2);
		localStateListDrawable.addState(StateSet.WILD_CARD, localCheckBoxDrawable1);
		localStateListDrawable.setAlpha(255);
		return localStateListDrawable;
	}

	public static final StateListDrawable getDropDownDrawable(boolean paramBoolean)
	{
		ButtonDrawable localButtonDrawable1 = new ButtonDrawable(-1, -1);
		ButtonDrawable localButtonDrawable2 = new ButtonDrawable(WPTheme.dropDownPressed, WPTheme.dropDownPressed);
		if (paramBoolean)
		{
			localButtonDrawable1.setPadding(0, 14, 25, 14);
			localButtonDrawable2.setPadding(0, 14, 25, 14);
		}
		int[] arrayOfInt1 = new int[1];
		arrayOfInt1[0] = 16842919;
		localButtonDrawable2.setState(arrayOfInt1);
		StateListDrawable localStateListDrawable = new StateListDrawable();
		int[] arrayOfInt2 = new int[1];
		arrayOfInt2[0] = 16842919;
		localStateListDrawable.addState(arrayOfInt2, localButtonDrawable2);
		int[] arrayOfInt3 = new int[1];
		arrayOfInt3[0] = 16842910;
		localStateListDrawable.addState(arrayOfInt3, localButtonDrawable1);
		localStateListDrawable.setAlpha(255);
		return localStateListDrawable;
	}

	public static final StateListDrawable getRadioDrawable()
	{

		CircleDrawable localCircleDrawable1 = new CircleDrawable(WPTheme.radioDisabled, false);
		CircleDrawable localCircleDrawable2 = new CircleDrawable(WPTheme.radioDisabled, true);
		CircleDrawable localCircleDrawable3 = new CircleDrawable(WPTheme.radioRest, false);
		int i = -1;
		int j;

		CircleDrawable localCircleDrawable4;
		CircleDrawable localCircleDrawable5;
		if (WPTheme.isDark())
		{
			j = i;
			localCircleDrawable4 = new CircleDrawable(j, false);
			localCircleDrawable5 = new CircleDrawable(WPTheme.radioRest, true);
		}
		else{
			i = 0xff000000;
			j = i;
			localCircleDrawable4 = new CircleDrawable(j, false);
			localCircleDrawable5 = new CircleDrawable(WPTheme.radioRest, true);
		}
	
		CircleDrawable localCircleDrawable6 = new CircleDrawable(i, true);
		StateListDrawable localStateListDrawable = new StateListDrawable();
		int[] arrayOfInt1 = new int[3];
		arrayOfInt1[0] = 16842910;
		arrayOfInt1[1] = 16842912;
		arrayOfInt1[2] = 16842919;
		localStateListDrawable.addState(arrayOfInt1, localCircleDrawable6);
		int[] arrayOfInt2 = new int[2];
		arrayOfInt2[0] = 16842910;
		arrayOfInt2[1] = 16842912;
		localStateListDrawable.addState(arrayOfInt2, localCircleDrawable5);
		int[] arrayOfInt3 = new int[2];
		arrayOfInt3[0] = 16842910;
		arrayOfInt3[1] = 16842919;
		localStateListDrawable.addState(arrayOfInt3, localCircleDrawable4);
		int[] arrayOfInt4 = new int[1];
		arrayOfInt4[0] = 16842910;
		localStateListDrawable.addState(arrayOfInt4, localCircleDrawable3);
		int[] arrayOfInt5 = new int[2];
		arrayOfInt5[0] = 16842919;
		arrayOfInt5[1] = 16842912;
		localStateListDrawable.addState(arrayOfInt5, localCircleDrawable2);
		int[] arrayOfInt6 = new int[1];
		arrayOfInt6[0] = 16842912;
		localStateListDrawable.addState(arrayOfInt6, localCircleDrawable2);
		localStateListDrawable.addState(StateSet.WILD_CARD, localCircleDrawable1);
		localStateListDrawable.setAlpha(255);
		return localStateListDrawable;
	}

	public static final SeekBarDrawable getSeekBar()
	{
		ShapeDrawable localShapeDrawable1 = new ShapeDrawable(new RectShape());
		ShapeDrawable localShapeDrawable2 = new ShapeDrawable(new RectShape());
		localShapeDrawable1.getPaint().setColor(WPTheme.seekBackground);
		localShapeDrawable2.getPaint().setColor(WPTheme.getThemeColor());
		ClipDrawable localClipDrawable = new ClipDrawable(localShapeDrawable2, 3, 1);
		Drawable[] arrayOfDrawable = new Drawable[2];
		arrayOfDrawable[0] = localShapeDrawable1;
		arrayOfDrawable[1] = localClipDrawable;
		SeekBarDrawable localSeekBarDrawable = new SeekBarDrawable(arrayOfDrawable);
		localSeekBarDrawable.setDrawableByLayerId(16908301, localShapeDrawable2);
		localSeekBarDrawable.setDrawableByLayerId(16908288, localShapeDrawable1);
		return localSeekBarDrawable;
	}

	public static final ShapeDrawable getSeekThumb()
	{
		ShapeDrawable localShapeDrawable = new ShapeDrawable(new RectShape());
		localShapeDrawable.setIntrinsicHeight(16);
		localShapeDrawable.setIntrinsicWidth(16);
		localShapeDrawable.getPaint().setColor(-1);
		return localShapeDrawable;
	}

	public static final ButtonDrawable getTBDrawable(int paramInt)
	{
		int i = -1;
		int j = -1;
		switch (paramInt)
		{    
			case 1:
				if (WPTheme.isDark())
				{
				    i = 0xff000000;
					j = 0xffffffff;
				}else{
					i = 0xffffffff;
					j = 0xff000000;
				}
				break;
			case 2:
				j = WPTheme.listPickerRest;
				i = j;
				break;
			case 3:
				j = WPTheme.getThemeColor();
				i = j;
				break;
			case 4:
				j = WPTheme.textBoxBorderDisabled;
				i = j;
				break;
			default:
			break;
		}
		ButtonDrawable localButtonDrawable = new ButtonDrawable(i, j);
		//localButtonDrawable.setPadding(11, 11, 16, 0);
		return localButtonDrawable;    
	}

	public static final ButtonDrawable getTimeItemDrawable()
	{
		ButtonDrawable localButtonDrawable = new ButtonDrawable(-16777216, WPTheme.timeItemSelected);
		localButtonDrawable.setPadding(8, 8, 8, 8);
		return localButtonDrawable;
	}

/*	public static final SeekBarDrawable getToggleDrawable(boolean paramBoolean, int paramInt)
	{
		ButtonDrawable localButtonDrawable1 = new ButtonDrawable(paramInt, paramInt);
		if (paramBoolean);
		for (int i = WPTheme.getThemeColor(); ; i = WPTheme.textBoxBorderDisabled)
		{
			ButtonDrawable localButtonDrawable2 = new ButtonDrawable(i, paramInt);
			ClipDrawable localClipDrawable = new ClipDrawable(localButtonDrawable2, 3, 1);
			Drawable[] arrayOfDrawable = new Drawable[2];
			arrayOfDrawable[0] = localButtonDrawable1;
			arrayOfDrawable[1] = localClipDrawable;
			SeekBarDrawable localSeekBarDrawable = new SeekBarDrawable(arrayOfDrawable);
			localSeekBarDrawable.setDrawableByLayerId(android.R.id.progress, localButtonDrawable2);//16908301
			localSeekBarDrawable.setDrawableByLayerId(android.R.id.background,localButtonDrawable1);//16908288
			return localSeekBarDrawable;
		}
	}*/
}