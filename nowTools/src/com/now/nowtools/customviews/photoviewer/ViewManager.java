package com.now.nowtools.customviews.photoviewer;

import java.util.ArrayList;
import java.util.List;

import com.now.nowtools.customviews.photoviewer.models.IViewItem;

public class ViewManager {

	private List<IViewItem> viewItems;
	
	public ViewManager(){
		viewItems = new ArrayList<IViewItem>();
	}
	
	public void clear(){
		viewItems = new ArrayList<IViewItem>();		
	}
	
	public void addView(IViewItem item){
		viewItems.add(item);
	}

	public void doClickTask(float eventX, float eventY){
		for( int i = viewItems.size()-1; i >= 0; i--){
			IViewItem item = viewItems.get(i);
			if(item.isInViewArea(eventX, eventY)){
				if(item.doClick(eventX, eventY) == true){
					//Comment this out if we want to invoke onClick of underneath views too
					break;
				}
			}
		}
	}
	
	public IViewItem getView(String name){
		for(IViewItem item : viewItems){
			if(item.getName().equals(name)){
				return item;
			}
		}
		return null;
	}
	
	public List<IViewItem> getViews(){
		return viewItems;
	}
}
