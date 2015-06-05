package com.now.nowtools.customviews.photoviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.now.nowtools.R;
import com.now.nowtools.customviews.photoviewer.models.BitmapViewItem;
import com.now.nowtools.customviews.photoviewer.models.CircleViewItem;
import com.now.nowtools.customviews.photoviewer.models.IViewItem;
import com.now.nowtools.customviews.photoviewer.models.RectFViewItem;
import com.now.nowtools.customviews.photoviewer.models.ViewItemOnClickListener;
import com.now.nowtools.utils.CommonUtils;

public class PhotoViewer extends View{
	// Canvas
	private float canvasW;
	private float canvasH;
	
	// Property
	private boolean showText;
	private int textPos;
	private int imageResource;
	
	// Text
	private Paint mTextPaint;
	
	// Shapes
	private Paint mPaint;
	private float mCircleX;
	private float mCircleY;
	private float mCircleRadius;
	private String mCircleName = "circle";	
	
	// Photo
	private String mPhotoName = "photo";
	private Bitmap mPhoto;
	private float mPhotoScale;
		
	//Button
	private String mButtonName = "blueButton";
	private Bitmap mButton;
	private String mButtonLockName = "blueLockButton";
	
	//Color Viewer
	private Paint mColorViewerPaint;
	private float mColorViewerRadius;
	private String mColorViewerName = "colorviewer";
	
	//View Manager
	private ViewManager mViewManager;
	
	//Gesture Detector
	private ScaleGestureDetector mScaleDetector;	
	private GestureDetector mGestureDetector;
	
	//Lock
	private boolean onScrollLock;
	
	public PhotoViewer(Context mContext){
		super(mContext);
		initialize();
		updateResource();
	}
	
	public PhotoViewer(Context mContext, AttributeSet attrs) {
		super(mContext, attrs);
		TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
				R.styleable.PhotoViewer, 0, 0);

		try {
			showText = a.getBoolean(R.styleable.PhotoViewer_showText, false);
			textPos = a.getInteger(R.styleable.PhotoViewer_labelPosition, 0);
			imageResource = a.getResourceId(R.styleable.PhotoViewer_imageResource, R.drawable.default_image);
		} finally {
			//this is shared resource, need to recycle
			a.recycle();
		}
		initialize();
		updateResource();
	}

	
	
	@Override
	public void onDraw (Canvas canvas){
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		for( IViewItem item : mViewManager.getViews()){
			item.doDraw(canvas);
		}
		canvas.drawText("Text Here", 0, 0, mTextPaint);
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d("TestHung", "onMeasure");
	}
	
	//All Shapes should be initialized here, since their sizes will change
	@Override
	protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasH = h;
		canvasW = w;
		
		mViewManager.clear();
		
		//Buttons
		float buttonScaleRatio = CommonUtils.getScaleRatio(w, h, (float)mButton.getWidth(), (float)mButton.getHeight());	
		BitmapViewItem mButtonBoundViewItem = new BitmapViewItem(mButtonName , mButton, buttonScaleRatio/10);
		mButtonBoundViewItem.setViewItemOnClickListener(new ViewItemOnClickListener() {			
			@Override
			public void onClick(float eventX, float eventY) {		
				rotateImageClockwise90();
			}
		});

		//Lock Buttons
		BitmapViewItem mLockBoundViewItem = new BitmapViewItem(mButtonLockName , mButton, buttonScaleRatio/10);
		mLockBoundViewItem.setLeft(100);
		mLockBoundViewItem.setViewItemOnClickListener(new ViewItemOnClickListener() {			
			@Override
			public void onClick(float eventX, float eventY) {				
				lockScroll();				
			}
		});		
		
		//Resize the photo to fit in the custom view	
		if(mPhotoScale <= 0){
			mPhotoScale = CommonUtils.getScaleRatio(w, h, (float)mPhoto.getWidth(), (float)mPhoto.getHeight());
		}	
		mPhotoScale = CommonUtils.getScaleRatio(w, h, (float)mPhoto.getWidth(), (float)mPhoto.getHeight());
		BitmapViewItem mPhotoBoundViewItem = new BitmapViewItem(mPhotoName, mPhoto, mPhotoScale);
		mPhotoBoundViewItem.updateTopLeftToDrawBitmapAtCenter(w, h);
		mPhotoBoundViewItem.setViewItemOnClickListener(new ViewItemOnClickListener() {			
			@Override
			public void onClick(float eventX, float eventY) {			
				Log.d("TestHung", "Clicked: mPhoto");		
			}
		});
		
		// Circle
		CircleViewItem mCircleViewItem = new CircleViewItem(mCircleName, mCircleX, mCircleY, mCircleRadius);
		mCircleViewItem.setPaint(mPaint);
		
		// Color Viewer
		CircleViewItem mColorViewerItem = new CircleViewItem(mColorViewerName,  
				w - mColorViewerRadius, h - mColorViewerRadius, mColorViewerRadius);
		mColorViewerItem.setPaint(mColorViewerPaint);
		
		//Add View to the manager. The order matters. We add layers from bottom to top
		mViewManager.addView(mPhotoBoundViewItem);
		mViewManager.addView(mButtonBoundViewItem);
		mViewManager.addView(mLockBoundViewItem);
		mViewManager.addView(mColorViewerItem);
		//mViewManager.addView(mCircleViewItem);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Scale
		mScaleDetector.onTouchEvent(event);
		
		// Gesture
		boolean result = mGestureDetector.onTouchEvent(event);
		
		if (!result) {			
			// Touch Circle
			CircleViewItem cirleItem = (CircleViewItem) mViewManager.getView(mCircleName);
			
			// Color Viewer
			CircleViewItem colorViewerItem = (CircleViewItem) mViewManager.getView(mColorViewerName);
			BitmapViewItem photo = (BitmapViewItem)mViewManager.getView(mPhotoName);
			
			// Action
			switch (event.getAction() & MotionEvent.ACTION_MASK){
				case MotionEvent.ACTION_UP:			
					Log.d("TestHung", "Action up");	
					mViewManager.doClickTask(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_MOVE:		
					if(cirleItem != null){
						cirleItem.setCenterX(event.getX());
						cirleItem.setCenterY(event.getY());		
					}
					
					if(photo != null && colorViewerItem != null){
						int pixel = photo.getPixel(event.getX(), event.getY());
						mColorViewerPaint.setColor(pixel);
						colorViewerItem.setPaint(mColorViewerPaint);
					}
					break;
			}
			invalidate();
		}
		return true;
	}	
	
	public void rotateImageClockwise90(){
		BitmapViewItem photoItem = (BitmapViewItem) mViewManager.getView(mPhotoName);
		photoItem.rotateBitmap(90);
		photoItem.updateTopLeftToDrawBitmapAtCenter(canvasW, canvasH);
	}
	
	public void lockScroll(){
		onScrollLock = !onScrollLock;
		String msg = onScrollLock ? "Drag Photo Locked" : "Drag Photo Unlocked";
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	//Getter and Setter 
	public boolean isShowText() {
		return showText;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
		refreshView();
	}

	public int getTextPos() {
		return textPos;
	}

	public void setTextPos(int textPos) {
		this.textPos = textPos;
		refreshView();
	}
	
	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
		refreshViewAndUpdateResource();
	}
	
	public void setImageBitmap(Bitmap bitmap) {
		this.mPhoto = bitmap;
		BitmapViewItem photo = (BitmapViewItem)mViewManager.getView(mPhotoName);
		photo.setBitmap(mPhoto);
		refreshView();
	}

	// This method initialize all local variables.
	// All Paints objects should be initialized here
	private void initialize() {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.WHITE);
		
		mPaint = new Paint(0);
		mPaint.setColor(0xff101010);

		mCircleX = -100;
		mCircleY = -100;
		mCircleRadius = 50;

		mColorViewerPaint = new Paint(0);
		mColorViewerRadius = 50;

		mViewManager = new ViewManager();

		mScaleDetector = new ScaleGestureDetector(getContext(),
				new ScaleListener());

		mGestureDetector = new GestureDetector(getContext(),
				new GestureListener());

	}

	private void updateResource() {
		mPhoto = BitmapFactory.decodeResource(getResources(), imageResource);
		mButton = BitmapFactory.decodeResource(getResources(), R.drawable.button);
	}

	private void refreshViewAndUpdateResource(){
		updateResource();
		//Let the system know that the view need to be redrawn
		invalidate();
		//Size of lay out changed, need to get new layout
		requestLayout();
	}
	
	private void refreshView(){
		//Let the system know that the view need to be redrawn
		invalidate();
		//Size of lay out changed, need to get new layout
		requestLayout();
	}
		
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	if(detector.isInProgress()){
				BitmapViewItem photo = (BitmapViewItem)mViewManager.getView(mPhotoName);
				
		        mPhotoScale *= detector.getScaleFactor();
		        
		        // Don't let the object get too small or too large.
		        mPhotoScale = Math.max(0.1f, Math.min(mPhotoScale, 5.0f));

				Log.d("TestHung", "getCurrentSpanX: " + detector.getFocusX());
				Log.d("TestHung", "getCurrentSpanY: " + detector.getFocusY());
				Log.d("TestHung", "mPhotoScale: " + mPhotoScale);
				
				//photo.setScaleRatio(mPhotoScale);
				photo.setScaleRatioAtPoint(mPhotoScale, canvasW, canvasH, detector.getFocusX(), detector.getFocusY());	
				
				Log.d("TestHung", "photo.left: " + photo.left);
	    	}
	        return true;
	    }
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			Log.d("TestHung", "onDown X: " +e.getX());
			Log.d("TestHung", "onDown Y: " +e.getY());
		    return true;
		}		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
			if(onScrollLock == false){
				BitmapViewItem photo = (BitmapViewItem)mViewManager.getView(mPhotoName);
				photo.left -= distanceX;
				photo.top -= distanceY;
			}
            return false;
        }
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d("TestHung", "onDoubleTap");
			BitmapViewItem photo = (BitmapViewItem)mViewManager.getView(mPhotoName);			
			mPhotoScale = mPhotoScale + 0.3f;
			photo.setScaleRatioAtPoint(mPhotoScale, canvasW, canvasH, e.getX(), e.getY());
			return true;
	    }
	}
}