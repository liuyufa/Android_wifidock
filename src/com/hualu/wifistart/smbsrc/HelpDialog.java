package com.hualu.wifistart.smbsrc;

import com.hualu.wifistart.R;
import com.hualu.wifistart.custom.HuzAlertDialog;

import android.content.Context;
import android.view.View;

public class HelpDialog extends HuzAlertDialog
{
	@SuppressWarnings("deprecation")
	protected HelpDialog(Context context) {
		super(context);
	        final View view = getLayoutInflater().inflate(R.layout.help_dialog,   null);  
                setButton(context.getText(R.string.help_close), (OnClickListener) null);  
                setIcon(R.drawable.icon);  
                setTitle("SDCardManager version: 2.0"  );  
                setView(view);  
	}
}
