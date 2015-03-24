package com.hualu.wifistart.wifisetting.utils;

import com.hualu.wifistart.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 弹出Toast的一个工具类，这里主要是增加了对系统Toast背景的修改
 * @author Administrator
 *
 */
public class ToastBuild {
	
	/**
	 * 
	 * @param context 上下文对象
	 * @param msg 要显示的信息
	 * @param timeTag 时间参数 若是“s”表示短时间显示 
	 * 						     若是“l”（小写L）表示长时间显示
	 */
	public static void toast(Context context, int msg){
		/*
		int time = Toast.LENGTH_SHORT;
		if(timeTag == null || "l".equals(timeTag)){
			time = Toast.LENGTH_LONG;
		}
		Toast toast = Toast.makeText(context, null, time);
		LinearLayout layout = (LinearLayout)toast.getView(); 
		layout.setBackgroundResource(R.drawable.title2);
		//layout.setOrientation(LinearLayout.HORIZONTAL);
		//layout.setGravity(Gravity.CENTER_VERTICAL);
		layout.setGravity(Gravity.CENTER);
		TextView tv = new TextView(context);
		tv.setLayoutParams(new  LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));   
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.parseColor("#ffffffff"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setPadding(0, 0, 0, 0);
		tv.setText(msg);
		layout.addView(tv);
		toast.show();
		*/
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = inflater.inflate(R.layout.toastbuild, null);
		Toast toast=new Toast(context.getApplicationContext());
		toast.setView(toastView);
		TextView tv=(TextView)toastView.findViewById(R.id.TextViewInfo);
		tv.setText(msg);
		toast.show();
	}
	public static void toast(Context context, String msg){
		/*
		int time = Toast.LENGTH_SHORT;
		if(timeTag == null || "l".equals(timeTag)){
			time = Toast.LENGTH_LONG;
		}
		Toast toast = Toast.makeText(context, null, time);
		LinearLayout layout = (LinearLayout)toast.getView(); 
		layout.setBackgroundResource(R.drawable.title2);
		//layout.setOrientation(LinearLayout.HORIZONTAL);
		//layout.setGravity(Gravity.CENTER_VERTICAL);
		layout.setGravity(Gravity.CENTER);
		TextView tv = new TextView(context);
		tv.setLayoutParams(new  LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));   
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.parseColor("#ffffffff"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setPadding(0, 0, 0, 0);
		tv.setText(msg);
		layout.addView(tv);
		toast.show();
		*/
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = inflater.inflate(R.layout.toastbuild, null);
		Toast toast=new Toast(context.getApplicationContext());
		toast.setView(toastView);
		TextView tv=(TextView)toastView.findViewById(R.id.TextViewInfo);
		tv.setText(msg);
		toast.show();
	}
}
