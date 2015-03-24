package com.hualu.wifistart.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

public class ButtonDrawable extends ShapeDrawable
{
	//private static final int WIDTH = 5;
	private Paint mStrokePaint;
	private int mWidth = 2;

	public ButtonDrawable(int paramInt1, int paramInt2)
	{
		this(paramInt1, paramInt2, 2);
	}

	public ButtonDrawable(int paramInt1, int paramInt2, int paramInt3)
	{
		super(new RectShape());
		this.mWidth = paramInt3;
		Paint localPaint = getPaint();
		this.mStrokePaint = new Paint(localPaint);
		localPaint.setColor(paramInt1);
		this.mStrokePaint.setStyle(Paint.Style.STROKE);
		this.mStrokePaint.setStrokeWidth(this.mWidth);
		this.mStrokePaint.setColor(paramInt2);
	}

	protected void onDraw(Shape paramShape, Canvas paramCanvas, Paint paramPaint)
	{
		paramShape.draw(paramCanvas, paramPaint);
		paramShape.draw(paramCanvas, this.mStrokePaint);
	}
}