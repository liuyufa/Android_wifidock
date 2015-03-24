package com.hualu.wifistart.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;
import com.hualu.wifistart.views.WPDrawables;
import com.hualu.wifistart.views.WPTheme;

public class WPTextBox extends LinearLayout
{
	//private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
	//private static final String LABEL = "label";
	protected static final int STATE_BLURED = -3671;
	protected static final int STATE_DISABLED = -5923;
	protected static final int STATE_FOCUSED = -2763;
	protected static final int STATE_SENT = -6794;
	//private static final String SUPERSTATE = "superState";
	public static final String TAG = WPTextBox.class.getSimpleName();
	//private static final String TEXT = "text";
	protected static final String blankString = "";
	protected String label = "WPTextBox";
	protected boolean mBubble = false;
	protected boolean sent = false;
	protected WPTextBoxView textBox;
	protected WPTextView textView;


	public WPTextBox(Context paramContext)
	{
		super(paramContext);
		init();
	}

	public WPTextBox(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		init();
		setAttrs(paramAttributeSet);
	}

	private void init()
	{
		setOrientation(LinearLayout.VERTICAL);
		this.textBox = new WPTextBoxView(getContext());
		this.textView = new WPTextView(getContext());
		if ("".equals(this.label))
		this.textView.setVisibility(View.GONE);//8
		WPTextView localWPTextView = this.textView;
		int i;
		if (WPTheme.isDark())
			i = 0xffffffff;
		else
			i = 0xff000000;	
		localWPTextView.setTextColor(i);
		//addView(this.textView);
		addView(this.textBox); 
	}

	private void setAttrs(AttributeSet paramAttributeSet)
	{
		/*Resources localResources = getContext().getResources();
		String str = getContext().getPackageName();
		setText(WPTheme.stringForResource(localResources, str, paramAttributeSet.getAttributeValue("http://schema.tombarrasso.com/wp7ui", "text")));
		setPlaceholder(WPTheme.stringForResource(localResources, str, paramAttributeSet.getAttributeValue("http://schema.tombarrasso.com/wp7ui", "placeholder")));
		setLabel(WPTheme.stringForResource(localResources, str, paramAttributeSet.getAttributeValue("http://schema.tombarrasso.com/wp7ui", "label")));
		setSent(paramAttributeSet.getAttributeBooleanValue("http://schema.tombarrasso.com/wp7ui", "sent", false));
		setBubble(paramAttributeSet.getAttributeBooleanValue("http://schema.tombarrasso.com/wp7ui", "bubble", false));
		setInputType(paramAttributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/android", "inputType", 1));
		*/
	}

	public void addTextChangedListener(TextWatcher paramTextWatcher)
	{
		this.textBox.addTextChangedListener(paramTextWatcher);
	}

	public void extendSelection(int paramInt)
	{
		this.textBox.extendSelection(paramInt);
	}

	public String getLabel()
	{
		return this.label;
	}

	public Editable getText()
	{
		return this.textBox.getText();
	}

	public WPTextBoxView getTextBox()
	{
		return this.textBox;
	}

	public void hideLabel()
	{
		this.textView.setVisibility(8);
	}

	public boolean isBubble()
	{
		return this.mBubble;
	}

	public boolean isSent()
	{
		return this.sent;
	}

	protected void onRestoreInstanceState(Parcelable paramParcelable)
	{
		Bundle localBundle = (Bundle)paramParcelable;
		super.onRestoreInstanceState(localBundle.getParcelable("superState"));
		setText(localBundle.getString("text"));
		setLabel(localBundle.getString("label"));
	}

	protected Parcelable onSaveInstanceState()
	{
		Bundle localBundle = new Bundle();
		localBundle.putParcelable("superState", super.onSaveInstanceState());
		localBundle.putString("text", getText().toString());
		localBundle.putString("label", this.textView.getText().toString());
		return localBundle;
	}

	public void selectAll()
	{
		this.textBox.selectAll();
	}

	public void setBubble(boolean paramBoolean)
	{
		this.mBubble = paramBoolean;
		this.textBox.setBubble(paramBoolean);
	}

	public void setEllipsize(TruncateAt paramTruncateAt)//TextUtils
	{
		this.textBox.setEllipsize(paramTruncateAt);
	}

	public void setEnabled(boolean paramBoolean)
	{
		super.setEnabled(paramBoolean);
		this.textBox.setEnabled(paramBoolean);
	}

	public void setInputType(int paramInt)
	{
		this.textBox.setInputType(paramInt);
	}

	public void setLabel(String paramString)
	{
		this.label = paramString;
		if (!"".equals(this.label)){
			this.textView.setVisibility(View.VISIBLE);//0
			this.textView.setText(this.label); 
		}
	}

	public void setOnClickListener(View.OnClickListener paramOnClickListener)
	{
		this.textBox.setOnClickListener(paramOnClickListener);
	}

	public void setOnEditorActionListener(OnEditorActionListener paramOnEditorActionListener)//TextView
	{
		this.textBox.setOnEditorActionListener(paramOnEditorActionListener);
	}

	public void setOnFocusChangeListener(View.OnFocusChangeListener paramOnFocusChangeListener)
	{
		this.textBox.setOnFocusChangeListener(paramOnFocusChangeListener);
	}

	public void setOnKeyListener(View.OnKeyListener paramOnKeyListener)
	{
		this.textBox.setOnKeyListener(paramOnKeyListener);
	}

	public void setOnLongClickListener(View.OnLongClickListener paramOnLongClickListener)
	{
		this.textBox.setOnLongClickListener(paramOnLongClickListener);
	}

	public void setOnTouchListener(View.OnTouchListener paramOnTouchListener)
	{
		this.textBox.setOnTouchListener(paramOnTouchListener);
	}

	public void setPlaceholder(String paramString)
	{
		this.textBox.setPlaceholder(paramString);
	}

	public void setSelection(int paramInt)
	{
		this.textBox.setSelection(paramInt);
	}

	public void setSelection(int paramInt1, int paramInt2)
	{
		this.textBox.setSelection(paramInt1, paramInt2);
	}

	public void setSent(boolean paramBoolean)
	{
		this.sent = paramBoolean;
		this.textBox.setSent(paramBoolean);
	}

	public void setText(CharSequence paramCharSequence, BufferType paramBufferType)//TextView
	{
		this.textBox.setText(paramCharSequence, paramBufferType);
	}

	public void setText(String paramString)
	{
		this.textBox.setText(paramString);
	}

	public static class WPTextBoxView extends EditText
	implements View.OnFocusChangeListener
	{
		private int STATE;
		//private InputMethodManager ime;
		private boolean mIsBubble = false;
		private String placeholder;
		private boolean sent = false;

		public WPTextBoxView(Context paramContext)
		{
			super(paramContext);
			init();
		}

		public WPTextBoxView(Context paramContext, AttributeSet paramAttributeSet)
		{
			super(paramContext, paramAttributeSet);
			setAttrs(paramAttributeSet);
			init();
		}

		public WPTextBoxView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
		{
			super(paramContext, paramAttributeSet, paramInt);
			setAttrs(paramAttributeSet);
			init();
		}

		private void init()
		{
			//WPFonts.setFonts(getContext().getAssets());
			//setTypeface(WPFonts.getFontSet().getRegular());
			Log.i(TAG,"wptextbox init");
			//this.ime = ((InputMethodManager)getContext().getSystemService("input_method"));
			setOnFocusChangeListener(this);
			if (("".equals(getText().toString())) && (this.placeholder != null))
				setText(this.placeholder);
			setSingleLine(true);
			if (isFocused())
				setState(STATE_FOCUSED);
			else if (isEnabled())
				setState(STATE_BLURED);
			else if (isSent())
				setState(STATE_SENT);
			else
				setState(STATE_DISABLED);
		}

		private void setAttrs(AttributeSet paramAttributeSet)
		{
			Resources localResources = getContext().getResources();
			String str = getContext().getPackageName();
			setText(WPTheme.stringForResource(localResources, str, paramAttributeSet.getAttributeValue("http://schema.tombarrasso.com/wp7ui", "text")));
			setPlaceholder(WPTheme.stringForResource(localResources, str, paramAttributeSet.getAttributeValue("http://schema.tombarrasso.com/wp7ui", "placeholder")));
			setSent(paramAttributeSet.getAttributeBooleanValue("http://schema.tombarrasso.com/wp7ui", "sent", false));
			setBubble(paramAttributeSet.getAttributeBooleanValue("http://schema.tombarrasso.com/wp7ui", "bubble", false));
		}

		@SuppressWarnings("deprecation")
		private void setState(int paramInt)
		{
			int i = 0xffffffff;
			setTextColor(i);
			switch (paramInt)
			{     
				case STATE_BLURED://-3671:
					if(this.STATE != STATE_BLURED){
					//setBackgroundDrawable(WPDrawables.getTBDrawable(1));
				//lrh add 20131217
					setBackgroundColor(0xffD1D3D4);
					//end
					//setTextColor(i);
					}
					break;
				case STATE_FOCUSED://-2763:
					if(this.STATE != STATE_FOCUSED){
					// setBackgroundDrawable(WPDrawables.getTBDrawable(1));	        
						//lrh add 20131217
						setBackgroundColor(0xffD1D3D4);
						//end
						//setTextColor(i);
					}
					break;
				case STATE_DISABLED://-5923:
					setBackgroundDrawable(WPDrawables.getTBDrawable(4));
					//setTextColor(WPTheme.textBoxDisabled);
					super.setEnabled(false);
					break;
				case STATE_SENT://-6794:
					setBackgroundDrawable(WPDrawables.getTBDrawable(3));
					//setTextColor(i);
					super.setEnabled(false);			
					break;
				default:
					break;
			} 
			this.STATE = paramInt;
		}

		public boolean isBubble()
		{
			return this.mIsBubble;
		}

		public boolean isSent()
		{
			return this.sent;
		}

		public void onFocusChange(View paramView, boolean paramBoolean)
		{
			/*liuyufa 20131219 add for search start*/
			//if(0==RefreshData.isfinish&&1==RefreshData.isSd){
			//	return ;
			//}
			//else {
			/*liuyufa 20131219 add for search end*/
			Log.i(TAG,"onFocusChange " + paramBoolean);	
			//wdx delete 1219		
			/*if (paramBoolean)
			{
				if (getText().toString().equals(this.placeholder))
					setText("");
				setState(STATE_FOCUSED);
				this.ime.showSoftInput(paramView, 1);
			}
			else
			{			
				if ("".equals(getText().toString()))
					setText(this.placeholder);
				setState(STATE_BLURED);
			}*/
		}

		public void onWindowFocusChanged(boolean paramBoolean)
		{
			super.onWindowFocusChanged(paramBoolean);
			int i = this.STATE;
			this.STATE = -1;
			setState(i);
		}

		protected void onWindowVisibilityChanged(int paramInt)
		{
			super.onWindowVisibilityChanged(paramInt);
			int i = this.STATE;
			this.STATE = -1;
			setState(i);
		}

		public void setBubble(boolean paramBoolean)
		{
			this.mIsBubble = paramBoolean;
		}

		public void setEnabled(boolean paramBoolean)
		{
			super.setEnabled(paramBoolean);
			if ((paramBoolean) || (this.STATE == STATE_SENT))
				return;
			setState(STATE_DISABLED);
		}

		public void setPlaceholder(String paramString)
		{
			this.placeholder = paramString;
		}

		public void setSent(boolean paramBoolean)
		{
			this.sent = paramBoolean;
			if (this.sent)
				setState(STATE_SENT);//-6794
			else
				super.setEnabled(true);
		}
	}
}