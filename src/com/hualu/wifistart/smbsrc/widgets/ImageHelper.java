package com.hualu.wifistart.smbsrc.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class ImageHelper {
	static public BitmapDrawable GeneralScaleBmpByRID(Context context,int rid,RectF rectF){
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(),rid);     
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();        

        float newWidth = rectF.width();
        float newHeight = rectF.height();        
    
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        
    
        Matrix matrix = new Matrix();
        
      
        matrix.postScale(scaleWidth, scaleHeight);
   
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);        

        @SuppressWarnings("deprecation")
		BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
        return bmd;
	}
}
