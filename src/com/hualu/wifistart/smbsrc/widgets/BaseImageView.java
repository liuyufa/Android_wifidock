package com.hualu.wifistart.smbsrc.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class BaseImageView extends SurfaceView implements Callback, Runnable {

	static String Tag = "BaseImageView";
	static public float pi = 3.1415f;
	//private static final boolean D = true;
	private Thread th;
	
	protected boolean flag;
	protected SurfaceHolder 	sfh;
	protected ScaleSizeF scaleF ;
	protected Bitmap backBmp = null;
	
	protected Canvas 		canvas;
	protected Paint 		paint;
	protected int 			backImageID;
	
	private ImageButton activeBtn1 = null;
	private ImageButton activeBtn2 = null;
	
/*	private static final int ACTION_POINTER_2_MOVE = 0x00000107; 
	private static final int ACTION_POINTER_1_MOVE = 0x00000007; */
	
	public List<ImageButton> bmBtns = new ArrayList<ImageButton>();

	public BaseImageView(Context context) {
		super(context);
		this.setKeepScreenOn(true);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
//		RecordActionService.context = context; 
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		th = new Thread(this);
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    int pointerCount = event.getPointerCount();  
        if (pointerCount > 2) {  
            pointerCount = 2;  
        }  
        //PointF curPos = new PointF();
        switch(pointerCount){
        case 1:
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:{
					PointF tempPos = new PointF(event.getX(), event.getY());
					if (activeBtn1 != null) {
						doMoveButton(activeBtn1, tempPos);
					}
					break;
				}
			case MotionEvent.ACTION_UP:{
					PointF tempPos = new PointF(event.getX(), event.getY());
					if (activeBtn1 != null) {
						doReleaseButton(activeBtn1, tempPos);
					}
					break;
				}
			case MotionEvent.ACTION_DOWN: {
					PointF tempPos = new PointF(event.getX(), event.getY());
					activeBtn1 = getButton(tempPos.x, tempPos.y);
					if (activeBtn1 != null) {
						doPressButton(activeBtn1, tempPos);
					}
					break;
				}
			}
        	break;
        case 2:
        	{
	             switch (event.getAction()) {  
	                 case MotionEvent.ACTION_POINTER_2_DOWN:  {
							final float x2 = event.getX(event.getPointerId(1));
							final float y2 = event.getY(event.getPointerId(1));
		                	PointF tempPos = new PointF(x2,y2);
		 					activeBtn2 = getButton(tempPos.x, tempPos.y);
		 					if (activeBtn2 != null) {
		 						doPressButton(activeBtn2, tempPos);
		 					}
		                    break;  
		                 }
	                 case MotionEvent.ACTION_POINTER_1_DOWN:  {
							final float x1 = event.getX(event.getPointerId(0));
							final float y1 = event.getY(event.getPointerId(0));
							PointF tempPos = new PointF(x1, y1);
							activeBtn1 = getButton(tempPos.x, tempPos.y);
							if (activeBtn1 != null) {
								doPressButton(activeBtn2, tempPos);
							}
							break;
						}
	                 case MotionEvent.ACTION_POINTER_1_UP:  {
							final float x1 = event.getX(event.getPointerId(0));
							final float y1 = event.getY(event.getPointerId(0));
							PointF tempPos = new PointF(x1, y1);
							if (activeBtn1 != null) {
								doReleaseButton(activeBtn1, tempPos);
							}
							break;
						}
	                 case MotionEvent.ACTION_POINTER_2_UP:  {
	                	 	final float x2 = event.getX(event.getPointerId(1));
							final float y2 = event.getY(event.getPointerId(1));
		                	PointF tempPos = new PointF(x2,y2);
							if (activeBtn2 != null) {
								doReleaseButton(activeBtn2, tempPos);
							}
							break;
						}
	                 case MotionEvent.ACTION_MOVE: {
	                	 	final float x1 = event.getX(event.getPointerId(0));
							final float y1 = event.getY(event.getPointerId(0));
							PointF tempPos1 = new PointF(x1, y1);
							final float x2 = event.getX(event.getPointerId(1));
							final float y2 = event.getY(event.getPointerId(1));
							PointF tempPos2 = new PointF(x2,y2);
							if (activeBtn1 != null) {
								doMoveButton(activeBtn1, tempPos1);
							}
							if (activeBtn2 != null) {
								doMoveButton(activeBtn2, tempPos2);
							}
		                    break; 
		                 }
	             }  
        	}
        	break;
        }
		return true;
	}

	public void draw() {
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK);
			paint.setColor(0xFF000000);
			canvas.drawBitmap(backBmp, 0,0, paint);
			for(int i =0 ;i<bmBtns.size();i++){
				if(bmBtns.get(i).isVisible){
					if(bmBtns.get(i).isShow){
							canvas.drawBitmap(bmBtns.get(i).downBmp, bmBtns.get(i).rect.left,bmBtns.get(i).rect.top, paint);
					}else{
						if(bmBtns.get(i).upBmp != null)
							canvas.drawBitmap(bmBtns.get(i).upBmp, bmBtns.get(i).rect.left,bmBtns.get(i).rect.top, paint);
					}
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				if (canvas != null)
					sfh.unlockCanvasAndPost(canvas);
			} catch (Exception e2) {

			}
		}
	}

	public void run() {
		while (flag) {
			draw();
			try {
				Thread.sleep(20);
			} catch (Exception ex) {
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(Tag, "surfaceChanged");
		flag = true;
		th.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
		Log.v(Tag, "surfaceDestroyed");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.v(Tag, "onSizeChanged");
		try{
			doBeforeLoadImage(w, h);
			Bitmap tempB = BitmapFactory.decodeResource(this.getContext().getResources(), backImageID);
			backBmp = getResizeBitmap(tempB,new RectF(0,0,this.getWidth(),this.getHeight()));
			for(ImageButton bmBtn:bmBtns ){
				if(bmBtn.downBmpId>0){
					Bitmap tempB1 = BitmapFactory.decodeResource(this.getContext().getResources(), bmBtn.downBmpId);
					bmBtn.downBmp = getResizeBitmap(tempB1);
				}
				if(bmBtn.upBmpId>0){
					Bitmap tempB2 = BitmapFactory.decodeResource(this.getContext().getResources(), bmBtn.upBmpId);
					bmBtn.upBmp = getResizeBitmap(tempB2);
				}
				bmBtn.rect = bmBtn.setRect(w, h,scaleF);
				bmBtn.setOrgRect(bmBtn.rect);
			}
			doAfterLoadImage(w, h);
			Log.v(Tag, "onSizeChanged");
		}catch(Exception e){
			Log.i(Tag,""+e.getClass().getSimpleName()+e.getMessage()+e.getStackTrace());
		}
	}
	
/**********************************************************************************************************/
	public ImageButton getButton(float x,float y){//
		for(int i=bmBtns.size()-1;i>=0;i--){
			ImageButton bmBtn =bmBtns.get(i);
			if(bmBtn.isOnlyShow) continue;
			RectF btnRect = bmBtn.getRect(); 
			if(btnRect.contains(x,y)){
				return bmBtn;
			}
		}
		return null;
	}
	
	public ImageButton getButton(String name){
		for(int i=bmBtns.size()-1;i>=0;i--){
			ImageButton bmBtn =bmBtns.get(i);
			if(bmBtn.cmd.equals(name)){
				return bmBtn;
			}
		}
		return null;
	}
	
	public String getName(float x,float y){
		return getCMD(x,y);
	}
	
	public String getCMD(float x,float y){
		String cmd = "";
		ImageButton bmBtn = getButton(x, y);
		if(bmBtn!=null) {
			if(!bmBtn.isNotNeedSend){
				cmd = bmBtn.cmd;
			}else{
				cmd = "";
			}
		}
		return cmd;
	}
	
	public Bitmap getResizeBitmap(Bitmap orgBitmap,RectF newSize){
		Bitmap bm = null;
		try{
			scaleF = new ScaleSizeF(((float) newSize.width())/orgBitmap.getWidth(),  ((float) newSize.height())/orgBitmap.getHeight());
			Matrix matrix = new Matrix();
			matrix.postScale(scaleF.sx, scaleF.sy);
			bm = Bitmap.createBitmap(orgBitmap, 0, 0,orgBitmap.getWidth(),orgBitmap.getHeight(), matrix, true);
		}catch(Throwable e ){
		}
		return bm; 
	}

	public Bitmap getResizeBitmap(Bitmap orgBitmap){
		Bitmap bm = null;
		try{
			Matrix matrix = new Matrix();
			matrix.postScale(scaleF.sx, scaleF.sy);
			bm = Bitmap.createBitmap(orgBitmap, 0, 0,orgBitmap.getWidth(),orgBitmap.getHeight(), matrix, true);
		}catch(Throwable e ){
		}
		return bm; 
	}
	

	/***
	 *
	 */
	public float getRad(float px1, float py1, float px2, float py2) {	
		float x = px2 - px1;	
		float y = py1 - py2;
		float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));	
		float cosAngle = x / xie;	
		float rad = (float) Math.acos(cosAngle);
		if (py2 < py1) {
			rad = -rad;
		}
		return rad;
	}
	
	public void printSamples(MotionEvent ev) {
	     final int historySize = ev.getHistorySize();
	     final int pointerCount = ev.getPointerCount();
	     for (int h = 0; h < historySize; h++) {
	    	 Log.d("MutilTouch",String.format("EID:%d His:%d  At time %d:",h,ev.getHistoricalEventTime(h)));
	         for (int p = 0; p < pointerCount; p++) {
	        	 Log.i(Tag,String.format(  "	pointer %d: (%f,%f)",
	                 ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h)));
	         }
	     }
	     Log.d("MutilTouch",String.format(  "EID:%d  At time %d:", ev.getAction(), ev.getEventTime()));
	     for (int p = 0; p < pointerCount; p++) {
	    	 Log.i(Tag,String.format(  "  pointer %d: (%f,%f)",
	             ev.getPointerId(p), ev.getX(p), ev.getY(p)));
	     }
	 }
	
/**********************************************************************************************************/
	
	protected void doReleaseButton(ImageButton btn, PointF curPos){
		btn.isShow = false;
	}
	
	protected void doPressButton(ImageButton btn, PointF curPos){
		btn.isShow = true;
	}
	
	protected void doBeforeLoadImage(int w, int h) {
		bmBtns.clear();
		Log.i(Tag, "doBeforeLoadImage");
	}
	protected void doAfterLoadImage(int w, int h) {
		Log.i(Tag, "doAfterLoadImage");
	}
	protected void doMoveButton(ImageButton btn, PointF curPos) {
		
	}
/**********************************************************************************************************/
	//activity destroy
	public void recycleBitmap(){
		try{
			for(ImageButton bmBtn:bmBtns ){
				try {
					Thread.sleep(20);
				} catch (Exception ex) {
				}
				if(bmBtn.downBmp!=null && !bmBtn.downBmp.isRecycled()){
					bmBtn.downBmp.recycle();
				}
				if(bmBtn.upBmp!=null && !bmBtn.upBmp.isRecycled()){
					bmBtn.upBmp.recycle();
				}
			}
			
			if(backBmp!=null && !backBmp.isRecycled()){
				backBmp.recycle();
			}
			System.gc();
		}catch(Exception e ){
			Log.i(Tag,""+e.getClass().getSimpleName()+e.getMessage()+e.getStackTrace());
		}
	}
}