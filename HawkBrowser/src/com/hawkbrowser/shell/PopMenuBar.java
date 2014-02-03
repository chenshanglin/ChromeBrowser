package com.hawkbrowser.shell;

import com.hawkbrowser.R;
import com.hawkbrowser.util.CommonUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class PopMenuBar implements View.OnClickListener {

	private Context mContext;
	private ViewGroup mView;
	private PopupWindow mPopup;
	private EventListener mListener;
	
	public interface EventListener {
		void onQuit();
		void onRefresh();	
		void onAddBookmark();
		void onShowBookmark();
		void onShowDownloadMgr();
		void onShowSetting();
	}	
	
	public PopMenuBar(Context context) {
		mContext = context;
		
		init();
	}
	
	public PopMenuBar(Context context, EventListener listener) {
		this(context);
		
		mListener = listener;
	}
		
	public void setEventListener(EventListener listener) {
		mListener = listener;
	}
	
	private void init() {
		
		LayoutInflater li = (LayoutInflater) 
			mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mView = (ViewGroup) li.inflate(R.layout.pop_menu, null);
		
		View bmHis = mView.findViewById(R.id.popmenu_bokmarkhistory);
		bmHis.setOnClickListener(this);
		
		View addBm = mView.findViewById(R.id.popmenu_addbookmark);
		addBm.setOnClickListener(this);
		
		View refresh = mView.findViewById(R.id.popmenu_refresh);
		refresh.setOnClickListener(this);
		
		View quit = mView.findViewById(R.id.popmenu_exit);
		quit.setOnClickListener(this);
		
		View download = mView.findViewById(R.id.popmenu_downloadmanager);
		download.setOnClickListener(this);
		
		View setting = mView.findViewById(R.id.popmenu_systemsetting);
		setting.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		if(null == mListener) {
			return;
		}
		
		switch(v.getId()) {
			case R.id.popmenu_bokmarkhistory:
				mListener.onShowBookmark();
				break;
				
			case R.id.popmenu_addbookmark:
				mListener.onAddBookmark();
				break;
				
			case R.id.popmenu_refresh:
				mListener.onRefresh();
				break;
				
			case R.id.popmenu_exit:
				mListener.onQuit();
				break;
				
			case R.id.popmenu_downloadmanager:
				mListener.onShowDownloadMgr();
				break;
				
			case R.id.popmenu_systemsetting:
				mListener.onShowSetting();
				break;
		}
	}
	
    public void show(View anchor) {
    	if(null == mPopup) {
    		Point screenSize = CommonUtil.screenSize(mContext);
    		Resources rs = mContext.getResources();
    		int height = rs.getDimensionPixelSize(R.dimen.popmenu_height);
    		mPopup = new PopupWindow(mView, screenSize.x, height);
    		mPopup.showAsDropDown(anchor, 0, 0);
    	}
    }
    
    public boolean isShow() {
    	return mPopup.isShowing();
    }
    
    public void dismiss() {
    	if(null != mPopup) {
    		mPopup.dismiss();
    		mPopup = null;
    	}
    }
}
