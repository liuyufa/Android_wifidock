package com.hualu.wifistart;

import com.hualu.wifistart.R;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.graphics.Canvas;  
import android.graphics.Matrix;  
import android.graphics.Paint;  
import android.graphics.Rect;  
import android.util.AttributeSet;  
import android.util.Log;
import android.view.MotionEvent;  
import android.view.View;  
import android.view.View.OnTouchListener; 

public class SlidButton extends View implements OnTouchListener{
	 private boolean nowChoose = false;// 记录当前按钮是否打开，true为打开，false为关闭  
	    private boolean onSlip = false;// 记录用户是否在滑动  
	    private float downX, nowX; // 按下时的x，当前的x  
	    private Rect btn_on, btn_off;// 打开和关闭状态下，游标的Rect  
	  
	    private boolean isChgLsnOn = false;//是否设置监听  
	    private OnChangedListener changedLis;  
	  
	    private Bitmap bg_on, bg_off, slip_btn;  
	    private final String TAG = "SlidButton";
	  
	    public SlidButton(Context context, AttributeSet attrs) {  
	        super(context, attrs); 	       
	        init();  
	        //setWillNotDraw(false);
	    }  
	  
	    public SlidButton(Context context) {  
	        super(context); 
	        init();  
	        //setWillNotDraw(false);
	    }  	      
	  
	    private void init() {  
	        // 载入图片资源  
	    	Log.i(TAG,"init");
	        bg_on = BitmapFactory.decodeResource(getResources(),  
	                R.drawable.sild_bg_on);  
	        bg_off = BitmapFactory.decodeResource(getResources(),  
	                R.drawable.sild_bg_off);  
	        slip_btn = BitmapFactory.decodeResource(getResources(),  
	                R.drawable.sild_btn);  
	        // 获得需要的Rect数据  
	        btn_on = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());  
	        btn_off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,  
	                bg_off.getWidth(), slip_btn.getHeight());  
	        setOnTouchListener(this);  
	    }  
	      
	      
	    @Override  
	    protected void onDraw(Canvas canvas) {  
	        // TODO Auto-generated method stub  
	        super.onDraw(canvas);  
	          
	        Matrix matrix = new Matrix();  
	        Paint paint = new Paint();  
	        float x;  
	        Log.i(TAG,"onDraw");
	        {  
	            if (nowX<(bg_on.getWidth()/2)) //滑动到前半段与后半段的背景不同,在此做判断  
	                canvas.drawBitmap(bg_off, matrix, paint);//画出关闭时的背景  
	            else  
	                canvas.drawBitmap(bg_on, matrix, paint);//画出打开时的背景   
	                  
	            if (onSlip) {//是否是在滑动状态,    
	                if(nowX >= bg_on.getWidth())//是否划出指定范围,不能让游标跑到外头,必须做这个判断  
	                    x = bg_on.getWidth() - slip_btn.getWidth()/2;//减去游标1/2的长度  
	                else  
	                    x = nowX - slip_btn.getWidth()/2;  
	            }else {  
	                if(nowChoose)//根据现在的开关状态设置画游标的位置   
	                    x = btn_off.left;  
	                else  
	                    x = btn_on.left;  
	            }  
	              
	            if (x < 0 ) //对游标位置进行异常判断..  
	                x = 0;  
	            else if(x > bg_on.getWidth() - slip_btn.getWidth())  
	                x = bg_on.getWidth() - slip_btn.getWidth();  
	              
	            canvas.drawBitmap(slip_btn, x,0, paint);//画出游标.     
	        }  
	    }  
	  
	    @Override  
	    public boolean onTouch(View v, MotionEvent event) {  
	          
	        switch (event.getAction()) {//根据动作来执行代码  
	              
	            case MotionEvent.ACTION_MOVE://滑动  
	                nowX = event.getX();  
	                break;  
	            case MotionEvent.ACTION_DOWN://按下  
	                if (event.getX() > bg_on.getWidth() || event.getY() > bg_on.getHeight())   
	                    return false;  
	                onSlip = true;  
	                downX = event.getX();  
	                nowX = downX;  
	                break;  
	            case MotionEvent.ACTION_UP://松开  
	                onSlip = false;  
	                boolean lastChoose = nowChoose;  
	                if (event.getX() >= (bg_on.getWidth()/2))   
	                    nowChoose = true;  
	                else   
	                    nowChoose = false;  
	                if(isChgLsnOn && (lastChoose != nowChoose))//如果设置了监听器,就调用其方法.  
	                    changedLis.OnChanged(nowChoose);  
	                break;  
	            default:  
	                break;  
	        }  
	        invalidate();  
	        return true;  
	    }  
	  
	      
	    public void SetOnChangedListener(OnChangedListener l){//设置监听器,当状态修改的时候  
	        isChgLsnOn = true;  
	        changedLis = l;  
	    }  
	      
	    public interface OnChangedListener {  
	        abstract void OnChanged(boolean checkState);  
	    }  
}
