package com.hualu.wifistart.views;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

public final class WPFonts
{
	public static final String TAG = WPFonts.class.getSimpleName();
	public static final String boldPath = "fonts/Roboto_Bold.ttf";
	public static final String italicPath = "fonts/Roboto_Regular.ttf";
	public static final String lightPath = "fonts/Roboto_Light.ttf";
	private static FontSet mFontSet;
	public static final String mediumPath = "fonts/Roboto_Regular.ttf";
	public static final String regularPath = "fonts/Roboto_Regular.ttf";

	public static final FontSet getFontSet()
	{
		return mFontSet;
	}

	public static final void setDefaultFonts(AssetManager paramAssetManager)
	{
		try
		{
			if (mFontSet == null)
			{
				mFontSet = new FontSet();
				mFontSet.setLight(Typeface.createFromAsset(paramAssetManager, "fonts/Roboto_Light.ttf"));
				mFontSet.setBold(Typeface.createFromAsset(paramAssetManager, "fonts/Roboto_Bold.ttf"));
				mFontSet.setMedium(Typeface.createFromAsset(paramAssetManager, "fonts/Roboto_Regular.ttf"));
				mFontSet.setRegular(mFontSet.MEDIUM);
				mFontSet.setItalic(mFontSet.MEDIUM);
			}		
		}
		catch (Exception localException)
		{
			Log.e(TAG, "Typeface was unable to be constructed.", localException);
		}
	}

	public static final void setFontSet(FontSet paramFontSet)
	{
		if (paramFontSet != null)
			mFontSet = paramFontSet;
	}

	public static final void setFonts(AssetManager paramAssetManager)
	{
		setDefaultFonts(paramAssetManager);
	}

	public static final class FontSet
	{
		private Typeface BOLD;
		private Typeface ITALIC;
		private Typeface LIGHT;
		private Typeface MEDIUM;
		private Typeface REGULAR;

		public FontSet()
		{
		}

		public FontSet(Typeface paramTypeface1, Typeface paramTypeface2, Typeface paramTypeface3, Typeface paramTypeface4, Typeface paramTypeface5)
		{
			this.BOLD = paramTypeface2;
			this.MEDIUM = paramTypeface3;
			this.REGULAR = paramTypeface4;
			this.LIGHT = paramTypeface1;
			this.ITALIC = paramTypeface5;
		}

		public final Typeface getBold()
		{
			return this.BOLD;
		}

		public final Typeface getItalic()
		{
			return this.ITALIC;
		}

		public final Typeface getLight()
		{
			return this.LIGHT;
		}

		public final Typeface getMedium()
		{
			return this.MEDIUM;
		}

		public final Typeface getRegular()
		{
			return this.REGULAR;
		}

		public final void setBold(Typeface paramTypeface)
		{
			this.BOLD = paramTypeface;
		}

		public final void setItalic(Typeface paramTypeface)
		{
			this.ITALIC = paramTypeface;
		}

		public final void setLight(Typeface paramTypeface)
		{
			this.LIGHT = paramTypeface;
		}

		public final void setMedium(Typeface paramTypeface)
		{
			this.MEDIUM = paramTypeface;
		}

		public final void setRegular(Typeface paramTypeface)
		{
			this.REGULAR = paramTypeface;
		}
	}
}