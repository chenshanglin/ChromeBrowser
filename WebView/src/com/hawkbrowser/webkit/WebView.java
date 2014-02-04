package com.hawkbrowser.webkit;

import java.util.concurrent.Callable;

import org.chromium.android_webview.AwBrowserContext;
import org.chromium.android_webview.AwContents;
import org.chromium.android_webview.AwContentsClient;
import org.chromium.android_webview.AwLayoutSizer;
import org.chromium.android_webview.AwSettings;
import org.chromium.base.ThreadUtils;
import org.chromium.content.browser.LoadUrlParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.DownloadListener;
import android.widget.FrameLayout;


public class WebView extends FrameLayout {
	
	private final static String PREFERENCES_NAME = "WebViewPrefs";
	
	private AwContents mAwContents;
	private AwBrowserContext mBrowserContext;
	private ContentSettingsAdapter mWebSettings;
	private WebViewContentsClientAdapter mContentsClientAdapter;
	private AwContents.InternalAccessDelegate mInternalAccessDelegate;
	
    /**
     * Interface to listen for find results.
     */
    public interface FindListener {
        /**
         * Notifies the listener about progress made by a find operation.
         *
         * @param activeMatchOrdinal the zero-based ordinal of the currently selected match
         * @param numberOfMatches how many matches have been found
         * @param isDoneCounting whether the find operation has actually completed. The listener
         *                       may be notified multiple times while the
         *                       operation is underway, and the numberOfMatches
         *                       value should not be considered final unless
         *                       isDoneCounting is true.
         */
        public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches,
            boolean isDoneCounting);
    }

    /**
     * Interface to listen for new pictures as they change.
     *
     * @deprecated This interface is now obsolete.
     */
    @Deprecated
    public interface PictureListener {
        /**
         * Used to provide notification that the WebView's picture has changed.
         * See {@link WebView#capturePicture} for details of the picture.
         *
         * @param view the WebView that owns the picture
         * @param picture the new picture. Applications targeting
         *     {@link android.os.Build.VERSION_CODES#JELLY_BEAN_MR2} or above
         *     will always receive a null Picture.
         * @deprecated Deprecated due to internal changes.
         */
        @Deprecated
        public void onNewPicture(WebView view, Picture picture);
    }
    
    /**
     *  Transportation object for returning WebView across thread boundaries.
     */
    public class WebViewTransport {
        private WebView mWebview;

        /**
         * Sets the WebView to the transportation object.
         *
         * @param webview the WebView to transport
         */
        public synchronized void setWebView(WebView webview) {
            mWebview = webview;
        }

        /**
         * Gets the WebView object.
         *
         * @return the transported WebView object
         */
        public synchronized WebView getWebView() {
            return mWebview;
        }
    }

	
    public WebView(Context context) {
        this(context, null);
    }

	public WebView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.webViewStyle);
	}
	
	public WebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
		
	private void init() {
		
	    SharedPreferences sharedPreferences = getContext().getSharedPreferences(
	    		PREFERENCES_NAME, Context.MODE_PRIVATE);
	    
        if (mBrowserContext == null) {
            mBrowserContext = new AwBrowserContext(sharedPreferences);
        }
        
        if(null == mInternalAccessDelegate) {
        	mInternalAccessDelegate = new InternalAccessAdapter();
        }
        
        mWebSettings = new ContentSettingsAdapter(new AwSettings(
                getContext(), false, true));
        mContentsClientAdapter = new WebViewContentsClientAdapter(this);
               
        mAwContents = new AwContents(mBrowserContext, this,
        		mInternalAccessDelegate, mContentsClientAdapter,
                new AwLayoutSizer(), mWebSettings.getAwSettings());
	}
	
	public void loadUrl(String url) {
		mAwContents.loadUrl(new LoadUrlParams(url));
	}
	
	public boolean canGoBack() {
		return mAwContents.canGoBack();
	}
	
	public void goBack() {
		mAwContents.goBack();
	}
	
	public boolean canGoForward() {
		return mAwContents.canGoForward();
	}
	
	public void goForward() {
		mAwContents.goForward();
	}
	
	public String getUrl() {
		return mAwContents.getUrl();
	}
	
    public void destroy() {
        mAwContents.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mAwContents.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAwContents.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAwContents.onDetachedFromWindow();
    }

    @Override
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        mAwContents.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return mAwContents.onCreateInputConnection(outAttrs);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mAwContents.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mAwContents.dispatchKeyEvent(event);
    }

	@SuppressLint("WrongCall")
	@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mAwContents.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mAwContents.onSizeChanged(w, h, ow, oh);
    }

    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        mAwContents.onContainerViewOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mAwContents != null) {
            mAwContents.onContainerViewScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    public void computeScroll() {
        mAwContents.computeScroll();
    }

    @Override
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mAwContents.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mAwContents.onWindowVisibilityChanged(visibility);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return mAwContents.onTouchEvent(ev);
    }

    @SuppressLint("WrongCall")
	@Override
    public void onDraw(Canvas canvas) {
        mAwContents.onDraw(canvas);
        super.onDraw(canvas);
    }

    @SuppressLint("NewApi")
	@Override
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityNodeProvider provider =
            mAwContents.getAccessibilityNodeProvider();
        return provider == null ? super.getAccessibilityNodeProvider() : provider;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AwContents.class.getName());
        mAwContents.onInitializeAccessibilityNodeInfo(info);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AwContents.class.getName());
        mAwContents.onInitializeAccessibilityEvent(event);
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return mAwContents.performAccessibilityAction(action, arguments);
    }
    
    public WebSettings getSettings() {
        return mWebSettings;
    }
    
    public Bitmap getFavicon() {
        return mAwContents.getFavicon();
    }
    
    private boolean checkNeedsPost() {
        boolean needsPost = !ThreadUtils.runningOnUiThread();
        if (!needsPost && mAwContents == null) {
            throw new IllegalStateException(
                    "AwContents must be created if we are not posting!");
        }
        return needsPost;
    }
    
    public WebBackForwardList copyBackForwardList() {
        return new WebBackForwardListChromium(
                mAwContents.getNavigationHistory());
    }
    
    static void completeWindowCreation(WebView parent, WebView child) {
        AwContents parentContents = parent.mAwContents;
        AwContents childContents =
                child == null ? null : child.mAwContents;
        parentContents.supplyContentsForPopup(childContents);
    }
    
    public void setWebViewClient(WebViewClient client) {
        mContentsClientAdapter.setWebViewClient(client);
    }

    public void setWebChromeClient(WebChromeClient client) {
        mContentsClientAdapter.setWebChromeClient(client);
    }

    public String getTitle() {
        return mAwContents.getTitle();
    }

    public int getProgress() {
        if (mAwContents == null) return 100;
        // No checkThread() because the value is cached java side (workaround for b/10533304).
        return mAwContents.getMostRecentProgress();
    }
    
    public void reload() {
        mAwContents.reload();
    }
    
    public void setDownloadListener(DownloadListener listener) {
    	mContentsClientAdapter.setDownloadListener(listener);
    }
	
    private class InternalAccessAdapter implements AwContents.InternalAccessDelegate {

        @Override
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return WebView.super.drawChild(canvas, child, drawingTime);
        }

        @Override
        public boolean super_onKeyUp(int keyCode, KeyEvent event) {
            return WebView.super.onKeyUp(keyCode, event);
        }

        @Override
        public boolean super_dispatchKeyEventPreIme(KeyEvent event) {
            return WebView.super.dispatchKeyEventPreIme(event);
        }

        @Override
        public boolean super_dispatchKeyEvent(KeyEvent event) {
            return WebView.super.dispatchKeyEvent(event);
        }

        @Override
        public boolean super_onGenericMotionEvent(MotionEvent event) {
            return WebView.super.onGenericMotionEvent(event);
        }

        @Override
        public void super_onConfigurationChanged(Configuration newConfig) {
        	WebView.super.onConfigurationChanged(newConfig);
        }

        @Override
        public void super_scrollTo(int scrollX, int scrollY) {
            // We're intentionally not calling super.scrollTo here to make testing easier.
        	WebView.this.scrollTo(scrollX, scrollY);
        }

        @Override
        public void overScrollBy(int deltaX, int deltaY,
                int scrollX, int scrollY,
                int scrollRangeX, int scrollRangeY,
                int maxOverScrollX, int maxOverScrollY,
                boolean isTouchEvent) {
            // We're intentionally not calling super.scrollTo here to make testing easier.
        	WebView.this.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                     scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }

        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
        	WebView.super.onScrollChanged(l, t, oldl, oldt);
        }

        @Override
        public boolean awakenScrollBars() {
            return WebView.super.awakenScrollBars();
        }

        @Override
        public boolean super_awakenScrollBars(int startDelay, boolean invalidate) {
            return WebView.super.awakenScrollBars(startDelay, invalidate);
        }

        @Override
        public void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        	WebView.super.setMeasuredDimension(measuredWidth, measuredHeight);
        }

        @Override
        public int super_getScrollBarStyle() {
            return WebView.super.getScrollBarStyle();
        }

        @Override
        public boolean requestDrawGL(Canvas canvas) {
            return false;
        }
    }
}