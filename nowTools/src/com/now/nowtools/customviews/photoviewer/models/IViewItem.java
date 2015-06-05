package com.now.nowtools.customviews.photoviewer.models;

import android.graphics.Canvas;

public interface IViewItem {
	public String getName();
	public boolean isInViewArea(float eventX, float eventY);
	public void setViewItemOnClickListener(ViewItemOnClickListener listener);
	public boolean doClick(float eventX, float eventY);
	public void doDraw(Canvas canvas);
}
