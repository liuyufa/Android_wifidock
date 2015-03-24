package com.hualu.wifistart.views;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Environment;
import java.io.File;

public final class WPTheme
{
	//private static final String DIVIDER = ":";
	public static final String PACKAGE;
	//private static final String TAG = WPTheme.class.getSimpleName();
	public static final String XMLNS = null;
	public static final int calcLight;
	public static final int calcTrigMode;
	public static final String colorFileName = "color.dat";
	public static final int commandDown;
	public static final int commandUp;
	public static final int defMenuBackground;
	public static final int defThemeColor;
	public static final float defTextSize = 30.0F;
	//private static final boolean defThemeDark = true;
	public static final int dropDownPressed;
	public static final int jumpViewBg = 0;
	public static final int listPickerRest;
	private static boolean mHasInitialized = false;
	public static final int numOfThemeColors;
	public static final int pivotDark;
	public static final int pivotHeader;
	public static final int pivotLight;
	public static final int radioDisabled;
	public static final int radioRest;
	public static final int seekBackground;
	public static final int selectedTextColor;
	public static final int textBoxBorderDisabled;
	public static final int textBoxDisabled = 0;
	public static final int textBoxGray;
	private static int themeColor = 0;
	public static String[] themeColorNames;
	public static final String themeColorPref = "THEME_COLOR";
	public static final int[] themeColors;
	private static boolean themeDark = true;
	public static final String themeDirectory;
	public static final int timeItem;
	public static final int timeItemSelected;
	private static float textSize = 30.0F;//28
	private static String tileOption = "tile";
	private static String tileDir = null;

	static
	{
		textBoxBorderDisabled = Color.rgb(33, 33, 33);
		textBoxGray = Color.rgb(34, 34, 34);
		commandUp = Color.rgb(170, 170, 170);
		commandDown = Color.rgb(138, 138, 138);
		listPickerRest = Color.rgb(191, 191, 191);
		timeItem = Color.rgb(165, 165, 165);
		timeItemSelected = Color.rgb(43, 43, 43);
		defMenuBackground = Color.rgb(31, 31, 31);
		seekBackground = Color.rgb(45, 45, 45);
		dropDownPressed = Color.rgb(244, 240, 236);
		pivotHeader = Color.argb(170, 155, 155, 155);
		pivotDark = Color.argb(170, 102, 102, 102);
		pivotLight = Color.argb(170, 178, 178, 178);
		calcLight = Color.rgb(57, 57, 57);
		calcTrigMode = Color.rgb(150, 150, 150);
		radioRest = Color.rgb(204, 204, 204);
		radioDisabled = Color.rgb(82, 82, 82);
		PACKAGE = WPTheme.class.getPackage().getName();
		themeDirectory = File.separator + "Android" + File.separator + "data" + File.separator + PACKAGE + File.separator + "files" + File.separator;
		mHasInitialized = false;
		int[] arrayOfInt = new int[11];
		arrayOfInt[0] = Color.rgb(255, 0, 151);
		arrayOfInt[1] = Color.rgb(162, 0, 225);
		arrayOfInt[2] = Color.rgb(0, 171, 169);
		arrayOfInt[3] = Color.rgb(139, 207, 38);
		arrayOfInt[4] = Color.rgb(153, 102, 0);
		arrayOfInt[5] = Color.rgb(230, 113, 184);
		arrayOfInt[6] = Color.rgb(240, 150, 9);
		arrayOfInt[7] = Color.rgb(27, 161, 226);
		arrayOfInt[8] = Color.rgb(229, 20, 0);
		arrayOfInt[9] = Color.rgb(51, 153, 51);
		arrayOfInt[10] = Color.rgb(0, 76, 154);
		themeColors = arrayOfInt;
		String[] arrayOfString = new String[11];
		arrayOfString[0] = "Ñóºì";
		arrayOfString[1] = "×Ï";
		arrayOfString[2] = "Çà";
		arrayOfString[3] = "»ÆÂÌ";
		arrayOfString[4] = "ºÖ";
		arrayOfString[5] = "·Ûºì";
		arrayOfString[6] = "³È";
		arrayOfString[7] = "À¶";
		arrayOfString[8] = "ºì";
		arrayOfString[9] = "ÂÌ";
		arrayOfString[10] = "ÉîÀ¶";
		themeColorNames = arrayOfString;
		numOfThemeColors = themeColors.length;
		selectedTextColor = Color.rgb(84, 152, 187);
		defThemeColor = themeColors[7];
	}

/*	private static File getColorFile() throws IOException
	{
		File localFile = new File(getDataDir(), "color.dat");
		if ((localFile != null) && (!localFile.exists()))
		localFile.createNewFile();
		return localFile;
	}*/

/*	private static File getDataDir()
	{
		File localFile = null;
		if (isReadable())
		{
			localFile = new File(Environment.getExternalStorageDirectory(), themeDirectory);
			if ((localFile != null) && (!localFile.exists()))
			localFile.mkdirs();
		}
		return localFile;
	}*/

	public static final int getThemeColor()
	{
		return ((themeColor==0)?defThemeColor:themeColor);
	}

	public static final String getTileOption()
	{
		return tileOption;
	}

	public static final String getTileDir()
	{
		return tileDir;
	}

	public static final float getTextSize()
	{
		return ((textSize==0)?defTextSize:textSize);
	}
	public static final boolean isDark()
	{
		return themeDark;
	}

	public static boolean isReadable()
	{
		String str = Environment.getExternalStorageState();
		if ("mounted".equals(str))
			return true;
		else if(!"mounted_ro".equals(str))
			return false;
		else
			return false;
	}

	public static boolean isWritable()
	{
		String str = Environment.getExternalStorageState();
		if ("mounted".equals(str))
			return true;
		else if("mounted_ro".equals(str))
			return false;
		else
			return true;
	}

	public static void setDefaultThemeColor()
	{
		if (mHasInitialized);
		else{
			mHasInitialized = true;
			themeColor = defThemeColor;
			themeDark = true;
		}
	}

	public static final boolean setThemeColor(int paramInt)
	{
		themeColor = paramInt;
		return true;
	}
	public static void setTextSize(float paramInt)
	{
		textSize = paramInt;
	}	

	public static void setTileOption(String option)
	{
		tileOption = option;
	}

	public static void setTileDir(String dir)
	{
		tileDir = dir;
	}
		
	public static final void setThemeColorNames(String[] paramArrayOfString)
	{
		themeColorNames = paramArrayOfString;
	}

/*	private static void setThemeColorToPref(int paramInt)
	{
	}*/

	public static final boolean setThemeDark(boolean paramBoolean)
	{
		if (themeDark != paramBoolean)
		{
			themeDark = paramBoolean;
			setThemeDarkToPref(paramBoolean);
			return true;
		}
		else
			return false;
	}

	private static void setThemeDarkToPref(boolean paramBoolean)
	{
	}

	public static final String stringForResource(Resources paramResources, String paramString1, String paramString2)
	{
		return null;
	}

	public static final class Operation
	{
		File file;
		String value;

		public Operation(File paramFile, String paramString)
		{
			this.file = paramFile;
			this.value = paramString;
		}
	}
}