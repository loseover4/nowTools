package com.now.nowtools.customviews.photoviewer.models;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

public class RectFViewItem implements IViewItem{	
	private ViewItemOnClickListener clickListener;
	public String name;
	public float top;
	public float left;
	public float bottom;
	public float right;
	
	public RectFViewItem(String mName){
		name = mName;
	}
	public RectFViewItem(String mName, RectF bound){
		name = mName;
		setBound(bound);
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public boolean isInViewArea(float eventX, float eventY) {		
		return (eventX>= left && eventX <= right) && (eventY >= top && eventY <= bottom);
	}

	@Override
	public void setViewItemOnClickListener(ViewItemOnClickListener listener) {
		clickListener = listener;		
	}
	
	@Override
	public boolean doClick(float eventX, float eventY){
		if(clickListener != null){
			clickListener.onClick(eventX, eventY);
			return true;
		}
		return false;
	}
	
	@Override 
	public void doDraw(Canvas canvas){
		canvas.drawRect(left, top, right, bottom, null);
	}
	
	public void setBound(RectF bound){
		top = bound.top;
		left = bound.left;
		bottom = bound.bottom;
		right = bound.right;
	}
}
