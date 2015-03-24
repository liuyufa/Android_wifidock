package com.hualu.wifistart.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;

public final class CircleDrawable extends ShapeDrawable
{
	private int color;
	private boolean hasDot;

	public CircleDrawable(int paramInt, boolean paramBoolean)
	{
		super(new OvalShape());
		setIntrinsicHeight(48);
		setIntrinsicWidth(48);
		this.color = paramInt;
		this.hasDot = paramBoolean;
	}

	protected void onDraw(Shape paramShape, Canvas paramCanvas, Paint paramPaint)
	{
		float f = getIntrinsicHeight() / 2;
		paramPaint.setAntiAlias(true);
		paramPaint.setColor(this.color);
		paramShape.draw(paramCanvas, paramPaint);
		if (!this.hasDot)
		return;
		paramPaint.setAntiAlias(true);
		paramPaint.setColor(-16777216);
		paramCanvas.drawCircle(f, f, 0.5F * f, paramPaint);
	}
}