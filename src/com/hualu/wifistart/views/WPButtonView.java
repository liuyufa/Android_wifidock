package com.hualu.wifistart.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hualu.wifistart.views.WPDialog;
import com.hualu.wifistart.views.WPDrawables;
import com.hualu.wifistart.views.WPFonts;
import com.hualu.wifistart.views.WPTheme;

public class WPButtonView extends Button implements View.OnClickListener
{
	protected static final int STATE_DISABLED = -5923;
	protected static final int STATE_DOWN = -3671;
	protected static final int STATE_NORMAL = -2763;
	protected static final int STATE_UP = -4562;
	public static final String TAG = WPButtonView.class.getSimpleName();
	protected int STATE;
	private boolean isGray = false;
	private DialogHolder mDialogHolder = new DialogHolder();

	public WPButtonView(Context paramContext)
	{
		super(paramContext);
		init();
		Log.i("test","WPButtonView");
	}

	public WPButtonView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		init();
		Log.i("test","WPButtonView");
	}

	public WPButtonView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
		init();
		Log.i("test","WPButtonView");
	}

	private void init()
	{
		
		WPFonts.setFonts(getContext().getAssets());
		setFocusable(true);
		setTypeface(WPFonts.getFontSet().getRegular());
		//setTextSize(mkUnit(25.0F));
		//float defaultsize = getTextSize();
		//Log.i("test","init " + defaultsize + " " + ViewGlobals.DENSITY);
		//setTextSize(mkUnit(21.0F));
		setGravity(17);
		setOnClickListener(this);
		setState(STATE_NORMAL);
	}
 /* protected float mkUnit(float paramFloat)
  {
    //return paramFloat * ViewGlobals.DENSITY / 1.5F;
    return paramFloat / ViewGlobals.DENSITY;
  }*/
  @SuppressWarnings("deprecation")
private void setState(int paramInt)
  {
    int i = 1;
    //int j = 0xff000000;
	Log.i("test","setstate = " + paramInt);
    switch (paramInt)
    {   
	    case STATE_NORMAL:
			if (this.isGray)
			{
				setBackgroundDrawable(WPDrawables.getBVDrawable(4));
			 	//if (!WPTheme.isDark())
		
			}else{
				if (WPTheme.isDark()){
					setTextColor(0xffffffff);
					setBackgroundDrawable(WPDrawables.getBVDrawable(1));
				}
				else{
					setTextColor(0xff000000);
					setBackgroundDrawable(WPDrawables.getBVDrawable(2));
				}
			}
			break;
	    case STATE_UP:
			setState(STATE_NORMAL);
			break;
	    case STATE_DOWN:
			if (WPTheme.isDark())
	          i = 2;
			if (WPTheme.isDark())
				setTextColor(0xff000000);
			else
				setTextColor(0xffffffff); 
	        setBackgroundDrawable(WPDrawables.getBVDrawable(i));
	        /*if (WPTheme.isDark())
				setTextColor(0xff000000);
			else
				setTextColor(0xffffffff);  */     
			break;
	    case STATE_DISABLED:
			setBackgroundDrawable(WPDrawables.getBVDrawable(3));
			setTextColor(WPTheme.textBoxDisabled);
			break;
		default:
			if (!isEnabled()){                  
                setState(STATE_DISABLED);
                setTextColor(WPTheme.timeItemSelected);
                setBackgroundDrawable(WPDrawables.getBVDrawable(3));
			}
			break;
				
    }
	this.STATE = paramInt;
   // setAllText();
  }

	public int getState()
	{
		return this.STATE;
	}

	public void onClick(View paramView)
	{
		if (this.mDialogHolder.mDialog != null)		
			this.mDialogHolder.dialogClickListener.onClick(this.mDialogHolder.mDialog, this.mDialogHolder.which);
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
	{
		if (paramBoolean)
			setState(STATE_DOWN);
		else
			setState(STATE_UP);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent)
	{
		boolean bool = true;
		Log.i("test","onTouchEvent");
		/*if (!isEnabled())
		{
			bool = super.onTouchEvent(paramMotionEvent);
			return bool;
		}*/
		switch (paramMotionEvent.getAction())
		{
			case MotionEvent.ACTION_MOVE://2
				bool = super.onTouchEvent(paramMotionEvent);
				break;
			case MotionEvent.ACTION_DOWN://0
				setState(STATE_DOWN);
				break;
			case MotionEvent.ACTION_CANCEL://3
				setState(STATE_UP);
				break;
			case MotionEvent.ACTION_UP://1
				setState(STATE_UP);
				break;
		}
		bool = super.onTouchEvent(paramMotionEvent);		
		return bool;
	}

	/*protected void setAllText()
	{
	}*/

	public void setDialog(WPDialog paramWPDialog)
	{
		this.mDialogHolder.mDialog = paramWPDialog;
	}

	public void setEnabled(boolean paramBoolean)
	{
		super.setEnabled(paramBoolean);
		setState(STATE_NORMAL);
	}

	public void setIsGray(boolean paramBoolean)
	{
		this.isGray = paramBoolean;
		if(paramBoolean)
			setState(STATE_DISABLED);
		else
			setState(STATE_NORMAL);
	}

	public void setOnDialogClickListener(DialogInterface.OnClickListener paramOnClickListener, int paramInt)
	{
		this.mDialogHolder.dialogClickListener = paramOnClickListener;
		this.mDialogHolder.which = paramInt;
	}

	private static final class DialogHolder
	{
		public DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
			}
		};
		public WPDialog mDialog;
		public int which;
	}
}