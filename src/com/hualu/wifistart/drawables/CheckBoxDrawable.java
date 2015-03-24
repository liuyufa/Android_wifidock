package com.hualu.wifistart.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

public class CheckBoxDrawable extends ShapeDrawable
{
/*	private static final float mBigBottom = 0.8F;
	private static final float mBigLeft = 0.45F;
	private static final float mBigRight = 0.8F;
	private static final float mBigTop = 0.225F;
	private static final float mSmallBottom = 0.8F;
	private static final float mSmallLeft = 0.2F;
	private static final float mSmallRight = 0.5F;
	private static final float mSmallTop = 0.45F;*/
	private boolean checked;
	private int color;

	public CheckBoxDrawable(Context paramContext, int paramInt, boolean paramBoolean)
	{
		super(new RectShape());
		float f = paramContext.getResources().getDisplayMetrics().density;
		setIntrinsicHeight((int)(24.0F * f));
		setIntrinsicWidth((int)(24.0F * f));
		this.color = paramInt;
		this.checked = paramBoolean;
	}

	protected void onDraw(Shape paramShape, Canvas paramCanvas, Paint paramPaint)
	{
		paramPaint.setAntiAlias(true);
		paramPaint.setColor(this.color);
		paramShape.draw(paramCanvas, paramPaint);
		if (!this.checked)
			return;
		int i = getIntrinsicWidth();
		paramPaint.setAntiAlias(true);
		paramPaint.setColor(-16777216);
		paramPaint.setStrokeWidth(i / 8);
		paramCanvas.drawLine(0.2F * i, 0.45F * i, 0.5F * i, 0.8F * i, paramPaint);
		paramCanvas.drawLine(0.45F * i, 0.8F * i, 0.8F * i, 0.225F * i, paramPaint);
	}
}