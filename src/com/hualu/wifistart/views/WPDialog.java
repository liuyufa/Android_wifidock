package com.hualu.wifistart.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hualu.wifistart.views.WPButtonView;
import com.hualu.wifistart.views.WPTextView;
import com.hualu.wifistart.views.WPTheme;

public class WPDialog extends Dialog
{
	public static final int NEGATIVE_BUTTON = -2445;
	public static final int POSITIVE_BUTTON = -1984;
	public static final String TAG = WPDialog.class.getSimpleName();
	private LinearLayout buttonLayout;
	private boolean hasInitialized = false;
	private boolean hasLaidOut = false;
	private boolean isFullScreen = false;
	private int mButtonCount = 0;
	private final LinearLayout.LayoutParams mButtonParams = new LinearLayout.LayoutParams(-1, -2);
	private LinearLayout mContainer;
	private LinearLayout mInnerLayout;
	private RelativeLayout mLayout;
	private WPTextView mTitle;
	private final ColorDrawable mWindowColor = new ColorDrawable(-1);
	private WPButtonView negativeButton;
	private WPButtonView positiveButton;

	public WPDialog(Context paramContext)
	{
		super(paramContext, -1);
		init();
	}

	public WPDialog(Context paramContext, int paramInt)
	{
		super(paramContext, paramInt);
		init();
	}

	public WPDialog(Context paramContext, boolean paramBoolean, DialogInterface.OnCancelListener paramOnCancelListener)
	{
		super(paramContext, paramBoolean, paramOnCancelListener);
		init();
	}

	private void init()
	{
		if (!this.hasInitialized)
		{
			Window localWindow = getWindow();
			localWindow.requestFeature(Window.FEATURE_NO_TITLE);
			localWindow.setBackgroundDrawable(this.mWindowColor);
			localWindow.setGravity(48);
			this.hasInitialized = true;
		}
		ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);//(-1, -2);
		this.mLayout = new RelativeLayout(getContext());
		this.mLayout.setLayoutParams(localLayoutParams);
		this.mLayout.setBackgroundColor(WPTheme.timeItemSelected);
		this.mLayout.setPadding(24, 20, 24, 20);
		this.mContainer = new LinearLayout(getContext());
		this.mContainer.setOrientation(1);
		this.mContainer.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		localLayoutParams1.bottomMargin = 18;
		this.mTitle = new WPTextView(getContext());
		this.mTitle.setLayoutParams(localLayoutParams1);
		this.mTitle.setTextSize(3, 10.0F);
		this.mTitle.setTextColor(0xffffffff);
		this.mContainer.addView(this.mTitle);
		this.mInnerLayout = new LinearLayout(getContext());
		this.mInnerLayout.setLayoutParams(localLayoutParams);
		this.mInnerLayout.setPadding(0, 0, 0, 24);
		this.mContainer.addView(this.mInnerLayout);
		this.mLayout.addView(this.mContainer);
		this.buttonLayout = new LinearLayout(getContext());
		this.buttonLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.buttonLayout.setOrientation(0);
		this.buttonLayout.setPadding(0, 12, 0, 0);
		this.buttonLayout.setBackgroundColor(WPTheme.timeItemSelected);
		this.buttonLayout.setWeightSum(100.0F);
	}

	private void makeNegativeButton()
	{
		if (this.negativeButton == null){
			this.mButtonCount = (1 + this.mButtonCount);
			this.negativeButton = new WPButtonView(getContext());
			LinearLayout.LayoutParams localLayoutParams1 = this.mButtonParams;
			if (this.mButtonCount == 2)
			{
				LinearLayout.LayoutParams localLayoutParams2 = this.mButtonParams;
				localLayoutParams2.weight = 50.0F;
				localLayoutParams1.weight = 50.0F;
				localLayoutParams1.leftMargin = 12;
				localLayoutParams2.rightMargin = 12;
				this.negativeButton.setLayoutParams(localLayoutParams1);
				this.positiveButton.setLayoutParams(localLayoutParams2);
			}
			else
			{
				this.negativeButton.setIsGray(true);
				this.negativeButton.setDialog(this);
				this.negativeButton.setLayoutParams(localLayoutParams1);
				this.buttonLayout.addView(this.negativeButton);
				localLayoutParams1.weight = 100.0F;
			}
		}
	}

	private void makePositiveButton()
	{
		if (this.positiveButton == null){
			this.mButtonCount = (1 + this.mButtonCount);
			this.positiveButton = new WPButtonView(getContext());
			LinearLayout.LayoutParams localLayoutParams1 = this.mButtonParams;
			if (this.mButtonCount == 2)
			{
				LinearLayout.LayoutParams localLayoutParams2 = this.mButtonParams;
				localLayoutParams1.weight = 50.0F;
				localLayoutParams2.weight = 50.0F;
				localLayoutParams1.leftMargin = 12;
				localLayoutParams2.rightMargin = 12;
				this.negativeButton.setLayoutParams(localLayoutParams2);
				this.positiveButton.setLayoutParams(localLayoutParams1);
			}
			else
			{
				this.positiveButton.setIsGray(true);
				this.positiveButton.setDialog(this);
				this.positiveButton.setLayoutParams(localLayoutParams1);
				this.buttonLayout.addView(this.positiveButton);
				localLayoutParams1.weight = 100.0F;
			}
		}
	}

	public void hideButtons()
	{
		this.buttonLayout.setVisibility(8);
	}

	public boolean isFullScreen()
	{
		return this.isFullScreen;
	}

	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		super.onSaveInstanceState();
	}

	public void setFullScreen(boolean paramBoolean)
	{
		this.isFullScreen = paramBoolean;
	}

	public void setMessageText(CharSequence paramCharSequence)
	{
		WPTextView localWPTextView = new WPTextView(getContext());
		localWPTextView.setText(paramCharSequence);
		localWPTextView.setTextSize(3, 8.0F);
		localWPTextView.setTextColor(0xffffffff);
		localWPTextView.setFont(-5937);
		setMessageView(localWPTextView);
	}

	public void setMessageView(int paramInt)
	{
		this.mInnerLayout.removeAllViews();
		getWindow().getLayoutInflater().inflate(paramInt, this.mInnerLayout);
	}

	public void setMessageView(View paramView)
	{
		this.mInnerLayout.removeAllViews();
		this.mInnerLayout.addView(paramView);
	}

	public WPDialog setNegativeButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
	{
		return setNegativeButton(getContext().getResources().getString(paramInt), paramOnClickListener);
	}

	public WPDialog setNegativeButton(String paramString, DialogInterface.OnClickListener paramOnClickListener)
	{
		makeNegativeButton();
		setNegativeText(paramString);
		setNegativeOnClickListener(paramOnClickListener);
		return this;
	}

	public void setNegativeOnClickListener(DialogInterface.OnClickListener paramOnClickListener)
	{
		this.negativeButton.setOnDialogClickListener(paramOnClickListener, -2445);
	}

	public void setNegativeText(int paramInt)
	{
		makeNegativeButton();
		this.negativeButton.setText(paramInt);
	}

	public void setNegativeText(CharSequence paramCharSequence)
	{
		makeNegativeButton();
		this.negativeButton.setText(paramCharSequence);
	}

	public WPDialog setPositiveButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
	{
		return setPositiveButton(getContext().getResources().getString(paramInt), paramOnClickListener);
	}

	public WPDialog setPositiveButton(String paramString, DialogInterface.OnClickListener paramOnClickListener)
	{
		makePositiveButton();
		setPositiveText(paramString);
		setPositiveOnClickListener(paramOnClickListener);
		return this;
	}

	public void setPositiveOnClickListener(DialogInterface.OnClickListener paramOnClickListener)
	{
		makePositiveButton();
		this.positiveButton.setOnDialogClickListener(paramOnClickListener, -1984);
	}

	public void setPositiveText(int paramInt)
	{
		makePositiveButton();
		this.positiveButton.setText(paramInt);
	}

	public void setPositiveText(CharSequence paramCharSequence)
	{
		makePositiveButton();
		this.positiveButton.setText(paramCharSequence);
	}

	public void setTitle(int paramInt)
	{
		this.mTitle.setText(paramInt);
		super.setTitle(paramInt);
	}

	public void setTitle(CharSequence paramCharSequence)
	{
		this.mTitle.setText(paramCharSequence);
		super.setTitle(paramCharSequence);
	}

	public void show()
	{
		if (!this.hasLaidOut)
		{
			Window localWindow = getWindow();
			if (this.isFullScreen)
				localWindow.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			else
				localWindow.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
			if (this.mButtonCount > 0)
			{
				if (!this.isFullScreen)
				this.mContainer.addView(this.buttonLayout);//break label124;
				RelativeLayout.LayoutParams localLayoutParams1 = (RelativeLayout.LayoutParams)this.buttonLayout.getLayoutParams();
				localLayoutParams1.addRule(12);
				this.mLayout.addView(this.buttonLayout, localLayoutParams1);
				RelativeLayout.LayoutParams localLayoutParams2 = (RelativeLayout.LayoutParams)this.mContainer.getLayoutParams();
				localLayoutParams2.bottomMargin = 65;
				this.mContainer.setLayoutParams(localLayoutParams2);
			}
		}		
		else
		{
			super.setContentView(this.mLayout);
			this.hasLaidOut = true;
			super.show();
		}
	}

	public void showButtons()
	{
		this.buttonLayout.setVisibility(0);
	}
}