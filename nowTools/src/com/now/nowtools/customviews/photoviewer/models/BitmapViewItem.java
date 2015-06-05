package com.now.nowtools.customviews.photoviewer.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

public class BitmapViewItem extends RectFViewItem{
	private Bitmap bitmap;
	private float previousSscaleRatio = -1;
	private float scaleRatio;
	
	private BitmapViewItem(String mName, RectF mBound) {
		super(mName, mBound);
	}
	
	public BitmapViewItem(String mName, Bitmap mBitmap, float mScaleRatio) {
		super(mName);
		bitmap = mBitmap;
		scaleRatio = mScaleRatio;
		updateBoundToMatchBitmap();
	}
	
	public BitmapViewItem(String mName, Bitmap mBitmap, float mTopLeftCornerX, float mTopLeftCornerY, float mScaleRatio) {
		super(mName);
		bitmap = mBitmap;
		scaleRatio = mScaleRatio;
		left = mTopLeftCornerX;
		top = mTopLeftCornerY;
		updateBoundToMatchBitmap();
	}
	
	@Override 
	public void doDraw(Canvas canvas){
		canvas.drawBitmap(bitmap, null, getBoundFromBitmapWithScale(), null);
	}
	
	private void updateBoundToMatchBitmap(){
		RectF mBound = getBoundFromBitmapWithScale();
		setBound(mBound);
	}
	
	public RectF getBoundFromBitmapWithScale(){
//		float mOldLeft = left;
//		float mOldTop = top;
//		left = left - ((float)bitmap.getWidth()*scaleRatio)/2;
//		top = top - ((float)bitmap.getHeight()*scaleRatio)/2;
//		right = mOldLeft + ((float)bitmap.getWidth()*scaleRatio)/2;
//		bottom = mOldTop + ((float)bitmap.getHeight()*scaleRatio)/2;
//		return new RectF(left, top, right, bottom);
		
//		Log.d("TestHung", "Bitmap name: "+ name);		
//		Log.d("TestHung", "Left: "+ left);
//		Log.d("TestHung", "Top: "+ top);
//		Log.d("TestHung", "Right: "+ right);
//		Log.d("TestHung", "Bottom: "+ bottom);
		return new RectF(left, top, left + (float)bitmap.getWidth()*scaleRatio, top +(float)bitmap.getHeight()*scaleRatio);
		
//		return new RectF(left - ((float)bitmap.getWidth())* (scaleRatio - 1),
//						top - ((float)bitmap.getHeight())* (scaleRatio - 1), 
//						(float)bitmap.getWidth() + ((float)bitmap.getWidth())* (scaleRatio - 1), 
//						(float)bitmap.getHeight() +((float)bitmap.getHeight())* (scaleRatio - 1));
	}
	
	public void updateTopLeftToDrawBitmapAtCenter(float canvasW, float canvasH){
		left = canvasW/2 - ((float)bitmap.getWidth()*scaleRatio)/2;
		top = canvasH/2 - ((float)bitmap.getHeight()*scaleRatio)/2;
		updateBoundToMatchBitmap();
	}
	
	public void updateTopLeftToDrawBitmapAtAPoint(float canvasW, float canvasH, float x, float y){
		Point pixelPointWithPreviousScale = getPixelPoint(previousSscaleRatio, x, y);
		left = x - ((float)pixelPointWithPreviousScale.x)*scaleRatio;
		top = y - ((float)pixelPointWithPreviousScale.y)*scaleRatio;
		Log.d("TestHung", "top: "+ top);
		updateBoundToMatchBitmap();
	}
	
	public int getPixel(float x, float y){
		int photoWidth = bitmap.getWidth();
		int photoHeight = bitmap.getHeight();
		Point pixelPoint = getPixelPoint(scaleRatio, x, y);
		if(pixelPoint.x < 0 || pixelPoint.x >= photoWidth 
				|| pixelPoint.y < 0 || pixelPoint.y >= photoHeight){
			return -1;
		}		
		
		return bitmap.getPixel(pixelPoint.x, pixelPoint.y);
	}
	
	private Point getPixelPoint(float scale, float scaledX, float scaledY){
		Point result = new Point();
		int photoWidth = bitmap.getWidth();
		int photoHeight = bitmap.getHeight();
		result.x = (int) ((scaledX - left) / scale);
		result.y = (int) ((scaledY - top) / scale);
		if(result.x < 0 || result.x >= photoWidth 
				|| result.y < 0 || result.y >= photoHeight){
			return new Point (0, 0);
		}		
		return result;
	}
	
	public void rotateBitmap(float degree){
		Matrix mTransformMatrix = new Matrix();
		mTransformMatrix.postRotate(degree);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mTransformMatrix, true);
		updateBoundToMatchBitmap();
	}
	
	public void setTopLeft(int x, int y){
		left = x;
		top = y;
	}
	
	public void zoomAtPoint(int x, int y){
		
	}
	
	//Getter and Setter
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		updateBoundToMatchBitmap();
	}

	public float getScaleRatio() {
		return scaleRatio;
	}

	public void setScaleRatio(float scaleRatio) {
		previousSscaleRatio = this.scaleRatio;
		this.scaleRatio = scaleRatio;
		updateBoundToMatchBitmap();
	}
	
	public void setScaleRatioAtCenter(float scaleRatio, float canvasW, float canvasH) {
		previousSscaleRatio = this.scaleRatio;
		this.scaleRatio = scaleRatio;
		updateTopLeftToDrawBitmapAtCenter(canvasW, canvasH);
	}
	
	public void setScaleRatioAtPoint(float scaleRatio, float canvasW, float canvasH, float x, float y) {
		previousSscaleRatio = this.scaleRatio;
		this.scaleRatio = scaleRatio;
		updateTopLeftToDrawBitmapAtAPoint(canvasW, canvasH, x , y);
	}
	
	public void setLeft(float mLeft){
		left = mLeft;
		updateBoundToMatchBitmap();
	}
	
	public void setTop(float mTop){
		top = mTop;
		updateBoundToMatchBitmap();
	}
}
