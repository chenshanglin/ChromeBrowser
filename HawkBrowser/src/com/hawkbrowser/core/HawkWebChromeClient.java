package com.hawkbrowser.core;

import com.hawkbrowser.webkit.WebChromeClient;
import com.hawkbrowser.webkit.WebView;

public class HawkWebChromeClient extends WebChromeClient {

	private EventListener	mListener;
	
	public interface EventListener {
		void onReceivedTitle(WebView view, String title);
		void onProgressChanged(WebView view, int newProgress);
	}
	
	public HawkWebChromeClient(EventListener listener) {
		mListener = listener;
	}
	
	public void setEventListener(EventListener listener) {
		mListener = listener;
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
		
		if(null != mListener) {
			mListener.onReceivedTitle(view, title);
		}
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		
		if(null != mListener) {
			mListener.onProgressChanged(view, newProgress);
		}
	}
}
