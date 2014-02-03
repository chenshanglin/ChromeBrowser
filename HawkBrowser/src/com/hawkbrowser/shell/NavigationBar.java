package com.hawkbrowser.shell;

import com.hawkbrowser.R;
import com.hawkbrowser.base.ImageButtonText;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationBar {

	public interface EventListener {
		void onGoBack();
		void onGoForward();
		void onNewWebView();
		void onSelectWebView();
		void onMenu();
	}
	
	ViewGroup		mNavigationBar;
	EventListener	mEventListener;
	Context			mContext;
	
	public NavigationBar(Context context, 
			ViewGroup layout, EventListener listener) {
		
		mContext = context;
		mEventListener = listener;
		mNavigationBar = layout;
		
		setupEventListener();
	}
	
	public void setCanGoBack(boolean canGoBack) {
		
		View goBack = (View) 
				mNavigationBar.findViewById(R.id.navigationbar_back);
		goBack.setEnabled(canGoBack);
	}
	
	public void setCanGoForward(boolean canGoForward) {
		
		View goForward = 
				mNavigationBar.findViewById(R.id.navigationbar_forward);
		goForward.setEnabled(canGoForward);
	}
	
	public void setSelectWindowText(String text) {
		
		ViewGroup selectView = (ViewGroup) 
			mNavigationBar.findViewById(R.id.navigationbar_selectwindow);
		TextView tv = (TextView) 
			selectView.findViewById(R.id.navigationbar_selectwindow_text);
		tv.setText(text);
		
		/*
		ImageButtonText selectView = (ImageButtonText) 
			mNavigationBar.findViewById(R.id.navigationbar_selectwindow);
		selectView.setText(text);
		*/
	}
	
	private void setupEventListener() {
		
		View goBack = 
				mNavigationBar.findViewById(R.id.navigationbar_back);
		goBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mEventListener) {
					mEventListener.onGoBack();
				}
			}
		});
		
		View goForward = 
			mNavigationBar.findViewById(R.id.navigationbar_forward);
		goForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mEventListener) {
					mEventListener.onGoForward();
				}
			}
		});
		
		View menu = mNavigationBar.findViewById(R.id.navigationbar_menu);
		menu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mEventListener) {
					mEventListener.onMenu();
				}
			}
		});
		
		View newView = 
				mNavigationBar.findViewById(R.id.navigationbar_home);
		newView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mEventListener) {
					mEventListener.onNewWebView();
				}
			}
		});
		
		View selectView = 
			mNavigationBar.findViewById(R.id.navigationbar_selectwindow);
		selectView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mEventListener) {
					mEventListener.onSelectWebView();
				}
			}
		});
	}
}
