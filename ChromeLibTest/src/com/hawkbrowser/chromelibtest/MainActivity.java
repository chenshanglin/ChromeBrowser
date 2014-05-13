package com.hawkbrowser.chromelibtest;

import org.chromium.content.common.ProcessInitException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.hawkbrowser.chromelib.WebView;

public class MainActivity extends Activity {

	private static final String TAG = "ContentShellTest";
	
    private final static String INITIAL_URL = "http://www.baidu.com";
    private WebView mAwTestContainerView;
    private EditText mUrlTextView;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private String mStartupUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        initializeUrlField();
        initializeNavigationButtons();

        mStartupUrl = getUrlFromIntent(getIntent());
        if (TextUtils.isEmpty(mStartupUrl)) {
        	mStartupUrl = INITIAL_URL;
        }
                                           
    	mAwTestContainerView = (WebView) findViewById(R.id.web_view);     
    	
    	try {
    		mAwTestContainerView.loadUrl(mStartupUrl);
    	} catch (ProcessInitException e) {
            Log.e(TAG, "Failed to load native library.", e);
            System.exit(-1);
        }
    	   	
//    	mAwTestContainerView.loadUrl(mStartupUrl);
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
                
            	try {
            		mAwTestContainerView.loadUrl(url);
            	} catch (ProcessInitException e) {
                    Log.e(TAG, "Failed to load native library.", e);
                    System.exit(-1);
                }
            	
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
