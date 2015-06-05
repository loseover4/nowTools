package com.now.nowtools.customviews.photoviewer.models;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleViewItem implements IViewItem{
	private String name;
	private float centerX;
	private float centerY;
	private float radius;
	private Paint paint;
	private ViewItemOnClickListener clickListener;
	
	public CircleViewItem(String mName, float mCenterX, float mCenterY, float mRadius){
		name = mName;
		centerX = mCenterX;
		centerY = mCenterY;
		radius = mRadius;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isInViewArea(float eventX, float eventY) {
		double distance = Math.sqrt((eventX - centerX)*(eventX - centerX) + (eventY - centerY)*(eventY - centerY));
		return distance < radius;
	}

	@Override
	public void setViewItemOnClickListener(ViewItemOnClickListener listener) {
		clickListener = listener;		
	}

	@Override
	public boolean doClick(float eventX, float eventY) {
		if(clickListener != null){
			clickListener.onClick(eventX, eventY);
			return true;
		}
		return false;
	}

	@Override 
	public void doDraw(Canvas canvas){
		canvas.drawCircle(centerX, centerY, radius, paint);	
	}
	
	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public float getCenterX() {
		return centerX;
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public ViewItemOnClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(ViewItemOnClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
}
