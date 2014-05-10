package com.hawkbrowser.webviewtest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewTestActivity extends Activity {

    private final static String INITIAL_URL = "http://www.baidu.com";
    private WebView mAwTestContainerView;
    private EditText mUrlTextView;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

//        mAwTestContainerView = createAwTestContainerView();
//
//        LinearLayout contentContainer = (LinearLayout) findViewById(R.id.content_container);
//        mAwTestContainerView.setLayoutParams(new LinearLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
//        contentContainer.addView(mAwTestContainerView);
        
        mAwTestContainerView = (WebView) findViewById(R.id.content_container);
        initWebView(mAwTestContainerView);
        mAwTestContainerView.requestFocus();

        initializeUrlField();
        initializeNavigationButtons();

        String startupUrl = getUrlFromIntent(getIntent());
        if (TextUtils.isEmpty(startupUrl)) {
            startupUrl = INITIAL_URL;
        }

        mAwTestContainerView.loadUrl(startupUrl);
        mUrlTextView.setText(startupUrl);
    }
    
    private void initWebView(WebView view) {
    	view.getSettings().setJavaScriptEnabled(true);
        
    	view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    private WebView createAwTestContainerView() {
    	
        WebView testContainerView = new WebView(this);        
        initWebView(testContainerView);
        return testContainerView;
    }

    private static String getUrlFromIntent(Intent intent) {
        return intent != null ? intent.getDataString() : null;
    }

    private void setKeyboardVisibilityForUrl(boolean visible) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (visible) {
            imm.showSoftInput(mUrlTextView, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(mUrlTextView.getWindowToken(), 0);
        }
    }

    private void initializeUrlField() {
        mUrlTextView = (EditText) findViewById(R.id.url);
        mUrlTextView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId != EditorInfo.IME_ACTION_GO) && (event == null ||
                        event.getKeyCode() != KeyEvent.KEYCODE_ENTER ||
                        event.getKeyCode() != KeyEvent.ACTION_DOWN)) {
                    return false;
                }

                String url = mUrlTextView.getText().toString();
                if(!url.startsWith("http")) {
                	url = "http://" + url;
                }
                
                mAwTestContainerView.loadUrl(url);
                mUrlTextView.clearFocus();
                setKeyboardVisibilityForUrl(false);
                mAwTestContainerView.requestFocus();
                return true;
            }
        });
        mUrlTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setKeyboardVisibilityForUrl(hasFocus);
                mNextButton.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
                mPrevButton.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
                if (!hasFocus) {
                    mUrlTextView.setText(mAwTestContainerView.getUrl());
                }
            }
        });
    }

    private void initializeNavigationButtons() {
        mPrevButton = (ImageButton) findViewById(R.id.prev);
        mPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwTestContainerView.canGoBack()) {
                    mAwTestContainerView.goBack();
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next);
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwTestContainerView.canGoForward()) {
                        mAwTestContainerView.goForward();
                }
            }
        });
    }

}
