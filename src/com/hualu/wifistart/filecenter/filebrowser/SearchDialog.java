package com.hualu.wifistart.filecenter.filebrowser;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class SearchDialog extends Dialog { 
    private static int default_width = 175; //默认宽度
    private static int default_height = 55;//默认高度
    public SearchDialog(Context context, int layout, int style) { 
        this(context, default_width, default_height, layout, style); 
    }

    public SearchDialog(Context context, int width, int height, int layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        float density = getDensity(context);
        params.width = (int) (width*density);
        params.height = (int) (height*density);
        params.alpha = 0.7f;//透明度
        params.dimAmount =0f;//点击不变暗
        params.gravity = Gravity.CENTER;//居中显示
        window.setAttributes(params);
    }
    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
       return dm.density;
    }
}
