// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.content_shell;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewRenderView;
import org.chromium.content.browser.LoadUrlParams;
import org.chromium.ui.base.WindowAndroid;

/**
 * Container for the various UI components that make up a shell window.
 */
@JNINamespace("content")
public class Shell extends FrameLayout {

    private static final long COMPLETED_PROGRESS_TIMEOUT_MS = 200;

    // TODO(jrg): a mContentView.destroy() call is needed, both upstream and downstream.
    private ContentView mContentView;
    private ContentViewRenderView mContentViewRenderView;
    private WindowAndroid mWindow;

    private boolean mLoading = false;

    /**
     * Constructor for inflating via XML.
     */
    public Shell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set the SurfaceView being renderered to as soon as it is available.
     */
    public void setContentViewRenderView(ContentViewRenderView contentViewRenderView) {
        FrameLayout contentViewHolder = this;
        if (contentViewRenderView == null) {
            if (mContentViewRenderView != null) {
                contentViewHolder.removeView(mContentViewRenderView);
            }
        } else {
            contentViewHolder.addView(contentViewRenderView,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
        }
        mContentViewRenderView = contentViewRenderView;
    }

    /**
     * @param window The owning window for this shell.
     */
    public void setWindow(WindowAndroid window) {
        mWindow = window;
    }

    /**
     * @return Whether or not the Shell is loading content.
     */
    public boolean isLoading() {
        return mLoading;
    }

    /**
     * Loads an URL.  This will perform minimal amounts of sanitizing of the URL to attempt to
     * make it valid.
     *
     * @param url The URL to be loaded by the shell.
     */
    public void loadUrl(String url) {
        if (url == null) return;

        if (TextUtils.equals(url, mContentView.getUrl())) {
            mContentView.getContentViewCore().reload(true);
        } else {
            mContentView.loadUrl(new LoadUrlParams(sanitizeUrl(url)));
        }

        // TODO(aurimas): Remove this when crbug.com/174541 is fixed.
        mContentView.clearFocus();
        mContentView.requestFocus();
    }

    /**
     * Given an URL, this performs minimal sanitizing to ensure it will be valid.
     * @param url The url to be sanitized.
     * @return The sanitized URL.
     */
    public static String sanitizeUrl(String url) {
        if (url == null) return url;
        if (url.startsWith("www.") || url.indexOf(":") == -1) url = "http://" + url;
        return url;
    }

    @SuppressWarnings("unused")
    @CalledByNative
    private void onUpdateUrl(String url) {

    }

    @SuppressWarnings("unused")
    @CalledByNative
    private void onLoadProgressChanged(double progress) {
    }

    @CalledByNative
    private void toggleFullscreenModeForTab(boolean enterFullscreen) {
    }

    @CalledByNative
    private boolean isFullscreenForTabOrPending() {
        return false;
    }

    @SuppressWarnings("unused")
    @CalledByNative
    private void setIsLoading(boolean loading) {
        mLoading = loading;
    }

    /**
     * Initializes the ContentView based on the native tab contents pointer passed in.
     * @param nativeTabContents The pointer to the native tab contents object.
     */
    @SuppressWarnings("unused")
    @CalledByNative
    private void initFromNativeTabContents(long nativeTabContents) {
        mContentView = ContentView.newInstance(getContext(), nativeTabContents, mWindow);

          this.addView(mContentView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        mContentView.requestFocus();
        mContentViewRenderView.setCurrentContentView(mContentView);
    }

    /**
     * @return The {@link ContentView} currently shown by this Shell.
     */
    public ContentView getContentView() {
        return mContentView;
    }
}
