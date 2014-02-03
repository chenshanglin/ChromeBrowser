package com.hawkbrowser.core;

import android.graphics.Bitmap;
import com.hawkbrowser.webkit.WebViewClient;
import com.hawkbrowser.webkit.WebView;

public class HawkWebViewClient extends WebViewClient {

	private EventListener	mListener;
	
	public interface EventListener {
		
		void onPageFinished(WebView view, String url);
		void onPageStarted(WebView view, String url, Bitmap favicon);
	}

	
	public HawkWebViewClient(EventListener listener) {
		mListener = listener;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		
		if(null != mListener) {
			mListener.onPageFinished(view, url);
		}
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		
		if(null != mListener) {
			mListener.onPageStarted(view, url, favicon);
		}
	}
}
