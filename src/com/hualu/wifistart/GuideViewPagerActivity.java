package com.hualu.wifistart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
  
public class GuideViewPagerActivity extends Activity implements OnClickListener, OnPageChangeListener{  
      
    private ViewPager viewPager;  
    private ViewPagerAdapter pagerAdapter;  
    private List<View> views;  
    private Button button;
      
    public String versioncode = "";
    //引导图片资源  
    private static final int[] pics = {   
            R.drawable.guide02, R.drawable.guide03,  
            R.drawable.guide04,R.drawable.guide05 };  
      
    //底部小店图片  
    private ImageView[] dots ;  
      
    //记录当前选中位置  
    private int currentIndex;  
      
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  
          
        views = new ArrayList<View>();  
         
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  
                LinearLayout.LayoutParams.WRAP_CONTENT);  
          
        //初始化引导图片列表  
        for(int i=0; i<pics.length; i++) {  
            ImageView iv = new ImageView(this);  
            iv.setLayoutParams(mParams);  
            iv.setImageResource(pics[i]);  
            views.add(iv);  
        }  
        viewPager = (ViewPager) findViewById(R.id.viewpager);  
        //初始化Adapter  
        pagerAdapter = new ViewPagerAdapter(views);  
        viewPager.setAdapter(pagerAdapter);  
        //绑定回调  
        viewPager.setOnPageChangeListener(this);  
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				SharedPreferences shared = new SharedConfig(GuideViewPagerActivity.this).GetConfig();
				Editor editor = shared.edit();
				versioncode = getVersionName();
				editor.putString("versioncode", versioncode);
				editor.putBoolean("isFirstUse", false);
				editor.commit();
				
				Intent intent = new Intent(GuideViewPagerActivity.this,WifiStarActivity.class);
				
				GuideViewPagerActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				GuideViewPagerActivity.this.finish();
			}
        	
        });
        //初始化底部小点  
        initDots();  
          
    }  
      
   

	private void initDots() {  
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
  
        dots = new ImageView[pics.length];  
  
        //循环取得小点图片  
        for (int i = 0; i < pics.length; i++) {  
            dots[i] = (ImageView) ll.getChildAt(i);  
            dots[i].setEnabled(true);//都设为灰色  
            dots[i].setOnClickListener(this);  
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应  
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态  
    }  
      
    /** 
     *设置当前的引导页  
     */  
    private void setCurView(int position)  
    {  
        if (position < 0 || position >= pics.length) {  
            return;  
        }  
  
        viewPager.setCurrentItem(position);  
    }  
  
    /** 
     *这只当前引导小点的选中  
     */  
    private void setCurDot(int positon)  
    {  
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {  
            return;  
        }  
  
        dots[positon].setEnabled(false);  
        dots[currentIndex].setEnabled(true);  
  
        currentIndex = positon;  
    }  
  
    //当滑动状态改变时调用  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
        // TODO Auto-generated method stub  
          
    }  
  
    //当当前页面被滑动时调用  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
        // TODO Auto-generated method stub  
          
    }  
  
    //当新的页面被选中时调用  
    @Override  
    public void onPageSelected(int arg0) {  
        //设置底部小点选中状态  
        setCurDot(arg0);  
        if(arg0 == 3){
        	button.setVisibility(View.VISIBLE);
        	
        }
    }  
  
    @Override  
    public void onClick(View v) {  
        int position = (Integer)v.getTag();  
        setCurView(position);  
        setCurDot(position);
        
    }  
    
    private String getVersionName() {
	
    	try{
    		 PackageManager manager = this.getPackageManager();
    		 PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
    		 String version = info.versionName;
    		 return version;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    	
        
    }
}