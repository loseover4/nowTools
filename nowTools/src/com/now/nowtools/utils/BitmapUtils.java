package com.now.nowtools.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtils {
	public static Bitmap rotate(Bitmap source, float degree){
		Matrix matrix = new Matrix();
	    matrix.postRotate(degree);
	    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
}
