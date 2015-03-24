package com.hualu.wifistart.filecenter.utils;

import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.files.FilePropertyAdapter;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;
import com.hualu.wifistart.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEffect {
	
	public static HuzAlertDialog dialog;
	private static Toast mToast;

	public static HuzAlertDialog newFolderDialog(final Context context,int titleId,final CustomListener positiveListener,final CustomListener negativeListener){
		
		LayoutInflater flater = LayoutInflater.from(context);
		final View view = flater.inflate(R.layout.rename_dialog, null);
		HuzAlertDialog.Builder builder = new HuzAlertDialog.Builder(context).setTitle(titleId);
		builder.setView(view);
		if(positiveListener!=null){
			builder.setPositiveButton(context.getText(R.string.confirm), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positiveListener.onListener();
				}
			});
		}
		if(negativeListener!=null){
			builder.setNegativeButton(context.getText(R.string.cancel), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					negativeListener.onListener();
				}
			});
		}
		dialog = builder.create();
		return dialog;
	}
	
	public static HuzAlertDialog createPassWordDialog(Context context,int titleId,String prepassword,final CustomListener positiveListener,final CustomListener negativeListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.password_dialog, null);
		HuzAlertDialog.Builder builder = new HuzAlertDialog.Builder(context).setTitle(titleId);
		if (prepassword!=null) {
			((EditText)view.findViewById(R.id.inputpassword1)).setText(prepassword);
			((EditText)view.findViewById(R.id.inputpassword2)).setText(prepassword);
		}
		
		builder.setView(view);
		if(positiveListener!=null){
			builder.setPositiveButton(context.getText(R.string.btn_positive), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positiveListener.onListener();
				}
			});
		}
		if(negativeListener!=null){
			builder.setNegativeButton(context.getText(R.string.btn_negative), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					negativeListener.onListener();
				}
			});
		}
		return builder.create();
	}
	
	public static HuzAlertDialog createRenameDialog(Context context,int titleId,String originName,final CustomListener positiveListener,final CustomListener negativeListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.rename_dialog, null);
		((EditText)view.findViewById(R.id.rename)).setText(originName);
		HuzAlertDialog.Builder builder = new HuzAlertDialog.Builder(context).setTitle(titleId);
		builder.setView(view);
		if(positiveListener!=null){
			builder.setPositiveButton(context.getText(R.string.btn_positive), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positiveListener.onListener();
				}
			});
		}
		if(negativeListener!=null){
			builder.setNegativeButton(context.getText(R.string.btn_negative), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					negativeListener.onListener();
				}
			});
		}
		return builder.create();
	}
	
	public static HuzAlertDialog createComfirDialog(Context context,int titleId,
			int msgId,final CustomListener positiveListener,final CustomListener negativeListener){
		HuzAlertDialog dialog = new HuzAlertDialog.Builder(context)
		.setTitle(titleId)
		.setMessage(msgId)
		.create();
		if(positiveListener != null){
			String btnText = context.getResources().getText(R.string.btn_positive).toString();
			dialog.setButton(HuzAlertDialog.BUTTON_POSITIVE, btnText, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positiveListener.onListener();
				}
			});
		}
		if(negativeListener != null){
			String btnText = context.getResources().getText(R.string.btn_negative).toString();
			dialog.setButton(HuzAlertDialog.BUTTON_NEGATIVE, btnText, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					negativeListener.onListener();
				}
			});
		}
		return dialog;
	}
	public static HuzAlertDialog createComfirDialog2(Context context,int titleId,
			int msgId,final CustomListener positiveListener,final CustomListener negativeListener){
		HuzAlertDialog dialog = new HuzAlertDialog.Builder(context)
		.setTitle(titleId)
		.setMessage(msgId)
		.create();
		if(positiveListener != null){
			String btnText = context.getResources().getText(R.string.btn_yes).toString();
			dialog.setButton(HuzAlertDialog.BUTTON_POSITIVE, btnText, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positiveListener.onListener();
				}
			});
		}
		if(negativeListener != null){
			String btnText = context.getResources().getText(R.string.btn_no).toString();
			dialog.setButton(HuzAlertDialog.BUTTON_NEGATIVE, btnText, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					negativeListener.onListener();
				}
			});
		}
		return dialog;
	}
//
	public static HuzAlertDialog createCustProgressDialog(Context context,int titleId,int max,
            final CustomListener positiveListener,final CustomListener negativeListener,OnCancelListener cancelListener){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cust_progress, null);
        HuzAlertDialog dialog = new HuzAlertDialog.Builder(context)
        .setTitle(titleId)
        .setView(view)
        .setCancelable(false)//zhaoyu	1217
        .create();
        if(positiveListener != null){
            String btnText = context.getResources().getText(R.string.btn_hide).toString();
            dialog.setButton(HuzAlertDialog.BUTTON_POSITIVE, btnText, 
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveListener.onListener();
                }
            });
        }
        if(negativeListener != null){
            String btnText = context.getResources().getText(R.string.btn_negative).toString();
            dialog.setButton(HuzAlertDialog.BUTTON_NEGATIVE, btnText, 
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    negativeListener.onListener();
                }
            });
        }
        if(cancelListener != null){
            dialog.setOnCancelListener(cancelListener);
        }
        TextView tv = (TextView)view.findViewById(R.id.tvNumber);
        tv.setText("0/" + max);
        return dialog;
    }
//
//	
	public static HuzAlertDialog createTheDialog(Context context,int titleId,OnCancelListener listener,
	        OnCheckedChangeListener checkedChangeListener,
	        android.widget.CompoundButton.OnCheckedChangeListener checkBoxChangeListener){
	    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.has_same_file_check, null);
	    HuzAlertDialog dialog = new HuzAlertDialog.Builder(context)
	    .setView(view)
        .setTitle(titleId)
        .setOnCancelListener(listener)
        .create();
	    RadioGroup rg = (RadioGroup)view.findViewById(R.id.whichOperation);
	    rg.setOnCheckedChangeListener(checkedChangeListener);
	    return dialog;
	}

	public static HuzAlertDialog createPropertyDialog(Context context,int titleId,
	         final FilePropertyAdapter adapter){
	    
       LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view = inflater.inflate(R.layout.read_property, null);
       HuzAlertDialog dialog = new HuzAlertDialog.Builder(context)
       .setView(view)
       .setTitle(titleId)
       .create();
       TextView tvFileName = (TextView)view.findViewById(R.id.fileName);
       TextView tvFileType = (TextView)view.findViewById(R.id.fileType);
       TextView tvFilePath = (TextView)view.findViewById(R.id.filePath);
       final TextView tvInclude = (TextView)view.findViewById(R.id.include);
       final TextView tvFileSize = (TextView)view.findViewById(R.id.fileSize);
       TextView tvCreateDate = (TextView)view.findViewById(R.id.createDate);
       TextView tvModifyDate = (TextView)view.findViewById(R.id.modifyDate);
       TextView tvCanWrite = (TextView)view.findViewById(R.id.canWrite);
       TextView tvCanRead = (TextView)view.findViewById(R.id.canRead);
       TextView tvIsHide = (TextView)view.findViewById(R.id.isHide);
       tvFileName.setText(adapter.name);
       tvFileType.setText(adapter.type);
       tvFilePath.setText(adapter.preFolder);
       final Handler handler = new Handler();
       final Runnable r = new Runnable(){
           @Override
           public void run() {
               tvFileSize.setText(adapter.size);
               tvInclude.setText(adapter.includeStr);
           }
       };
       adapter.getSize(handler,r);
       tvCreateDate.setText(adapter.createDate);
       tvModifyDate.setText(adapter.modifyDate);
       tvCanWrite.setText(adapter.canWrite);
       tvCanRead.setText(adapter.canRead);
       tvIsHide.setText(adapter.isHiden);
       dialog.setOnCancelListener(new OnCancelListener(){
           @Override
           public void onCancel(DialogInterface dialog) {
               adapter.stopGetSize();
               handler.removeCallbacks(r);
           }
       });
       return dialog;
   }

	public static void showToast(Context context,
			int toastId) {
		if(mToast!=null){
			mToast.cancel();
		}
		ToastBuild.toast(context, context.getText(toastId).toString());
	}
	public static void showToast(Context context,String msg){
		if(mToast!=null){
			mToast.cancel();
		}
		ToastBuild.toast(context,msg);
	}

	public static void showToastLongTime(Context context,
			String formatStr) {
		if(mToast!=null){
			mToast.cancel();
		}
		ToastBuild.toast(context,formatStr);
	}
	
	public static void cancelToast(){
		if(mToast!=null){
			mToast.cancel();
		}
	}

}
