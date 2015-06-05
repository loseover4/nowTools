package com.now.nowtools.utils;

import android.util.Log;

public class CommonUtils {
	public static float getScaleRatio(float containerW, float containerH, float w2, float h2){
		float s1x = containerW/w2;
		float s1y = containerH/h2;			
		Log.d("TestHung", "s1x: "+ s1x);					
		Log.d("TestHung", "s1y: "+ s1y);						
		Log.d("TestHung", "s1: "+ ((s1x < s1y) ? s1x : s1y));	
		return (s1x < s1y) ? s1x : s1y;	
	}
}
