package com.hawkbrowser.chromelib;

import org.chromium.content.browser.ActivityContentVideoViewClient;
import org.chromium.content.browser.ContentVideoViewClient;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewClient;
import org.chromium.content.common.ProcessInitException;
import org.chromium.ui.base.ActivityWindowAndroid;
import org.chromium.ui.base.WindowAndroid;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class WebView extends FrameLayout {
	
	private TabManager mTabManager;
    private WindowAndroid mWindowAndroid;
    private String mPendingLoadUrl;
    
    public WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    public void loadUrl(String url) throws ProcessInitException {
    	
		if(ChromeInitializer.get().isChromeStartFinished())
			loadUrlAfterChromeStart(url);
		else {
			
			mPendingLoadUrl = url;
			
        	ChromeInitializer.get().setCallback(new ChromeInitializer.InitializeCallback() {
				
				@Override
				public void onSuccess(boolean alreadyStarted) {
					// TODO Auto-generated method stub
					initAfterChromeStart();
					loadUrlAfterChromeStart(mPendingLoadUrl);
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
				}
			});
        	
        	ChromeInitializer.get().startChrome(getContext());
		}
    }
	
	private void initAfterChromeStart() {
		    	
    	mTabManager = new TabManager(getContext(), null);
    	mTabManager.setStartupUrl(mPendingLoadUrl);
    	mTabManager.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    	
        mWindowAndroid = getContext() instanceof Activity ?
        		new ActivityWindowAndroid((Activity)getContext()) : 
        			new WindowAndroid(getContext());
        mTabManager.setWindow(mWindowAndroid);
        
        addView(mTabManager, new FrameLayout.LayoutParams(
        	FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}
	
	private void loadUrlAfterChromeStart(String url) {
		
		if(mTabManager.getCurrentTab() != null) 
			mTabManager.getCurrentTab().loadUrlWithSanitization(url);
		else {
			mTabManager.createTab(url);
			
			final Activity hostActivity = getContext() instanceof Activity ? 
					(Activity) getContext() : null;
			
			if(null != hostActivity) {
				getContentView().setContentViewClient(new ContentViewClient() {
		            @Override
		            public ContentVideoViewClient getContentVideoViewClient() {
		                return new ActivityContentVideoViewClient(hostActivity);
		            }
		        });	
			}
		}
	}
		
	public String getUrl() {
		if(null == mTabManager.getCurrentTab())
			return "";
		else
			return mTabManager.getCurrentTab().getContentView().getUrl();
	}
	
	public boolean canGoBack() {
		if(null == mTabManager.getCurrentTab())
			return false;
		else
			return mTabManager.getCurrentTab().getContentView().canGoBack();
	}
	
	public boolean canGoForward() {
		if(null == mTabManager.getCurrentTab())
			return false;
		else
			return mTabManager.getCurrentTab().getContentView().canGoForward();
	}
	
	public void goBack() {
		if(null != mTabManager.getCurrentTab())
			mTabManager.getCurrentTab().getContentView().goBack();
	}

	public void goForward() {
		if(null != mTabManager.getCurrentTab())
			mTabManager.getCurrentTab().getContentView().goForward();
	}

	public ContentView getContentView() {
		if(null != mTabManager.getCurrentTab())
			return mTabManager.getCurrentTab().getContentView();
		else
			return null;
	}
}
