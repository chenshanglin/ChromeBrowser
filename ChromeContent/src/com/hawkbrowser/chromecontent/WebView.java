package com.hawkbrowser.chromecontent;

import org.chromium.content.browser.ActivityContentVideoViewClient;
import org.chromium.content.browser.ContentVideoViewClient;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewClient;
import org.chromium.content.common.ProcessInitException;
import org.chromium.content_shell.ShellManager;
import org.chromium.ui.base.ActivityWindowAndroid;
import org.chromium.ui.base.WindowAndroid;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class WebView extends FrameLayout {
	
	private ShellManager mShellManager;
    private WindowAndroid mWindowAndroid;
    private Activity mHostActivity;
    private String mPendingLoadUrl;
    
    public WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    public void init(Activity activity) throws ProcessInitException {
    	
    	if(null != mShellManager)
    		return;
    	
    	ChromeInitializer.get().ensureLibraryLoaded();
    	
    	mHostActivity = activity;
    	mShellManager = new ShellManager(getContext(), null);
    	mShellManager.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mWindowAndroid = new ActivityWindowAndroid(activity);
        mShellManager.setWindow(mWindowAndroid);
        
        addView(mShellManager, new FrameLayout.LayoutParams(
        	FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        
        if(!ChromeInitializer.get().isChromeStartFinished()) {
        	
        	ChromeInitializer.get().setCallback(new ChromeInitializer.InitializeCallback() {
				
				@Override
				public void onSuccess(boolean alreadyStarted) {
					// TODO Auto-generated method stub
					if(null != mPendingLoadUrl)
						loadUrlAfterChromeStart(mPendingLoadUrl);
					mPendingLoadUrl = null;
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
				}
			});
        	
        	ChromeInitializer.get().startChrome(activity);
        }
    }
	
	public void loadUrl(String url) {
		if(ChromeInitializer.get().isChromeStartFinished())
			loadUrlAfterChromeStart(url);
		else
			mPendingLoadUrl = url;
	}
	
	private void loadUrlAfterChromeStart(String url) {
		
		if(mShellManager.getActiveShell() != null) 
			mShellManager.getActiveShell().loadUrl(url);
		else {
			mShellManager.launchShell(url);
			
			getContentView().setContentViewClient(new ContentViewClient() {
	            @Override
	            public ContentVideoViewClient getContentVideoViewClient() {
	                return new ActivityContentVideoViewClient(mHostActivity);
	            }
	        });
		}
	}
		
	public String getUrl() {
		if(null == mShellManager.getActiveShell())
			return "";
		else
			return mShellManager.getActiveShell().getContentView().getUrl();
	}
	
	public boolean canGoBack() {
		if(null == mShellManager.getActiveShell())
			return false;
		else
			return mShellManager.getActiveShell().getContentView().canGoBack();
	}
	
	public boolean canGoForward() {
		if(null == mShellManager.getActiveShell())
			return false;
		else
			return mShellManager.getActiveShell().getContentView().canGoForward();
	}
	
	public void goBack() {
		if(null != mShellManager.getActiveShell())
			mShellManager.getActiveShell().getContentView().goBack();
	}

	public void goForward() {
		if(null != mShellManager.getActiveShell())
			mShellManager.getActiveShell().getContentView().goForward();
	}

	public ContentView getContentView() {
		if(null != mShellManager.getActiveShell())
			return mShellManager.getActiveShell().getContentView();
		else
			return null;
	}
}
