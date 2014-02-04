package com.hawkbrowser.shell;

import com.hawkbrowser.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddressBar {

	private EventListener	mListener;
	private Context			mContext;
	private	EditText		mAddressEdit;
	private	View			mGoView;
	private	View			mClearView;
	
	public interface EventListener {
		void onGo(String url);
	}
	
	public AddressBar(Context ctx, ViewGroup view) {
		mContext = ctx;
		
		mAddressEdit = (EditText) view.findViewById(R.id.addressbar_edit);
		mGoView = view.findViewById(R.id.addressbar_gobtn);
		mClearView = view.findViewById(R.id.addressbar_clearbtn);
		
		init();
	}
	
	public AddressBar(Context ctx, ViewGroup view, EventListener listener) {
		this(ctx, view);
		mListener = listener;
	}
	
	public void setEventListener(EventListener listener) {
		mListener = listener;
	}
	
	public void setTitle(String title) {
		mAddressEdit.setText(title);
	}
	
	private void init() {
		
		mGoView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = mAddressEdit.getText().toString();
				
				if(url.isEmpty()) {
					Resources rs = mContext.getResources();
					url = rs.getString(R.string.homepageurl);
				}
				
				if(!url.startsWith("http")) {
					url = "http://" + url;
				}
				
				if(null != mListener) {
					mListener.onGo(url);
				}
			}
		});
		
		mClearView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAddressEdit.setText("");				
			}
		});
		
        mAddressEdit.setOnEditorActionListener(new OnEditorActionListener() {
        	// on ime go
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId != EditorInfo.IME_ACTION_GO) && (event == null ||
                        event.getKeyCode() != KeyEvent.KEYCODE_ENTER ||
                        event.getKeyCode() != KeyEvent.ACTION_DOWN)) {
                    return false;
                }

                mListener.onGo(mAddressEdit.getText().toString());
                
                mAddressEdit.clearFocus();
                setKeyboardVisibilityForUrl(false);
                return true;
            }
        });
        
        mAddressEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setKeyboardVisibilityForUrl(hasFocus);
            }
        });
	}
	
    private void setKeyboardVisibilityForUrl(boolean visible) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (visible) {
            imm.showSoftInput(mAddressEdit, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(mAddressEdit.getWindowToken(), 0);
        }
    }
}
