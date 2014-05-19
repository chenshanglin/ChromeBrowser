package com.hawkbrowser.shell;

import java.util.ArrayList;
import com.hawkbrowser.R;
import com.hawkbrowser.util.CommonUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class WebViewSelecter implements View.OnTouchListener {
	
	public interface EventListener {
		void onItemSelect(int i);
		void onItemClose(int i);
	}
	
	private static final int ITEM_COUNT_PER_SCREEN = 3;
	private	EventListener mListener;
	private Context mContext;
	private ArrayList<String> mTitles;
	private ArrayList<Bitmap> mBitmaps;
	private ViewGroup mView;
	private	int	mCurrentScreen;
	private PopupWindow mPopup;
	private float mStartPosX = 0;
	
	public WebViewSelecter(Context ctx, ArrayList<String> titles, 
			ArrayList<Bitmap> bitmaps) {
		
		mContext = ctx;
		mTitles = titles;
		mBitmaps = bitmaps;
		mCurrentScreen = 0;
				
		mView = createView();
		updateItems();
	}
		
	@Override
	public boolean onTouch(View v, MotionEvent event) {
			
		if(mTitles.size() <= ITEM_COUNT_PER_SCREEN) {
			return false;
		}
		
		if(MotionEvent.ACTION_DOWN == event.getAction()) {
			mStartPosX = event.getX();
			return false;
		} 
		
		if(MotionEvent.ACTION_UP == event.getAction()) {
			
			boolean isMove = false;
			final int moveLen = 50;
			
			if(event.getX() - mStartPosX > moveLen) {
				--mCurrentScreen;
				isMove = true;
			} else if(event.getX() - mStartPosX < -moveLen) {
				++mCurrentScreen;
				isMove = true;
			}
							
			if(isMove) {
				int maxScreen = 1;
				
				if(mTitles.size() > 0) {
					maxScreen = (mTitles.size() - 1) / ITEM_COUNT_PER_SCREEN;
				}
				
				if(mCurrentScreen > maxScreen) {
					mCurrentScreen = maxScreen;
				}
				
				if(mCurrentScreen < 0) {
					mCurrentScreen = 0;
				}
				
				mStartPosX = 0;
				updateItems();
				return true;
			}
		}
		
		return false;
	}
	
	public void setEventListener(EventListener listener) {
		mListener = listener;
	}
	
	public View getView() {
		return mView;
	}
	
    private ViewGroup createView() {
    	
    	LayoutInflater inflater = (LayoutInflater)
				mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
		ViewGroup webThumbContainer = (ViewGroup) 
				inflater.inflate(R.layout.select_webview, null);
		webThumbContainer.getBackground().setAlpha(175);
				
		for(int i = 0; i < webThumbContainer.getChildCount(); ++i) {

			ViewGroup item = (ViewGroup)
				webThumbContainer.getChildAt(i);
								
			ImageView webImage = (ImageView)
					item.findViewById(R.id.selectwebviewitem_image);
						
			webImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(null != mListener) {
						mListener.onItemSelect((Integer)v.getTag());
					}
				}
			});
			
			webImage.setOnTouchListener(this);
			
			View closeView = item.findViewById(R.id.selectwebviewitem_title);
			closeView.setTag(i);
			closeView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(null != mListener) {
						int i = (Integer)v.getTag();
						ViewGroup item = (ViewGroup) mView.getChildAt(i);
						item.setVisibility(View.INVISIBLE);
						mTitles.remove(i);
						mBitmaps.remove(i);
						mListener.onItemClose(i);
					}
				}
			});
		}
		
		webThumbContainer.setOnTouchListener(this);
			
		return webThumbContainer;
    }
    
    private void setItemsVisibility(int visibility) {
    
		for(int i = 0; i < mView.getChildCount(); ++i) {

			ViewGroup item = (ViewGroup) mView.getChildAt(i);
			item.setVisibility(visibility);
		}
    }
    
    public void updateItems() {
    	  	
    	setItemsVisibility(View.INVISIBLE);
    	
		int startPos = mCurrentScreen * ITEM_COUNT_PER_SCREEN;
    	int endPos = Math.min(mTitles.size(), startPos + ITEM_COUNT_PER_SCREEN);
    	    	
		for(int i = startPos; i < endPos; ++i) {

			ViewGroup item = (ViewGroup)
				mView.getChildAt(i - startPos);
			
			item.setVisibility(View.VISIBLE);
			
			TextView titleView = (TextView) 
					item.findViewById(R.id.selectwebviewitem_title);
			titleView.setText(mTitles.get(i));
			
			ImageView webImage = (ImageView)
					item.findViewById(R.id.selectwebviewitem_image);
			webImage.setImageBitmap(mBitmaps.get(i));
			webImage.setTag(i);
		}
    }
    
    public boolean isShow() {
    	return null != mPopup;
    }
    
    public void show(View anchor) {
    	if(null == mPopup) {
    		Point screenSize = CommonUtil.screenSize(mContext);
    		mPopup = new PopupWindow(mView, screenSize.x, screenSize.y / 4);
    		mPopup.showAsDropDown(anchor, 0, 0);
    	}
    }
    
    public void dismiss() {
    	if(null != mPopup) {
    		mPopup.dismiss();
    		
    		for(Bitmap bitmap : mBitmaps) {
    			bitmap.recycle();
    		}
    		
    		mBitmaps.clear();
    		mPopup = null;
    	}
    }
}
