package com.hualu.wifistart;

import com.hualu.wifistart.SlidButton.OnChangedListener;
import com.hualu.wifistart.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Android_SlidButtonActivityActivity extends Activity implements OnChangedListener{
    /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.settingitem);
       //ScrollView scroll = (ScrollView)findViewById(R.id.slideList);
   }
   @Override
	public void OnChanged(boolean checkState) {
		// TODO Auto-generated method stub
		 if (checkState){  
	            //打开蓝牙  
	            Toast.makeText(this, "蓝牙打开了。。。", Toast.LENGTH_SHORT).show();  
	        }else{  
	            //关闭蓝牙  
	            Toast.makeText(this, "蓝牙关闭了。。。", Toast.LENGTH_SHORT).show();  
	        }  
	}
}