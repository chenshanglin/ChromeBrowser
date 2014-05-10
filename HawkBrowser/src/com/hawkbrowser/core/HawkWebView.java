package com.hawkbrowser.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.hawkbrowser.webkit.WebSettings;
import com.hawkbrowser.webkit.WebView;
import com.hawkbrowser.webkit.WebViewClient;

public class HawkWebView extends WebView {

	public HawkWebView(Context context) {
		super(context);
	}

	public HawkWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	
	public HawkWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	
	public void init(WebViewClient viewClient, 
			HawkWebChromeClient chromeClient) {
		
		WebSettings setting = getSettings();
		
		String rawUserAgent = setting.getUserAgentString();
		rawUserAgent = getUserAgent(rawUserAgent);
		setting.setUserAgentString(rawUserAgent);
		
		setting.setJavaScriptEnabled(true);
		setWebViewClient(viewClient);
		setWebChromeClient(chromeClient);
		// requestFocusFromTouch();
		requestFocus();
	}
	
	private String getUserAgent(String rawUserAgent) {
		int pos = rawUserAgent.lastIndexOf("Mobile Safari");
		
		if(-1 != pos) {
			return rawUserAgent.substring(0, pos) + "HawkBrowser/1.0";
		} else {
			return rawUserAgent;
		}
	}
}
