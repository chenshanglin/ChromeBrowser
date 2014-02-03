package com.hawkbrowser.app;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class History implements HistoryStorageListener {

	Context mContext;
	HistoryStorage mStorage;
	
	public static class Item {
		String mTitle;
		String mUrl;
		Calendar mTime;
		
		Item(String title, String url, Calendar time) {
			mTitle = title;
			mUrl = url;
			mTime = time;
		}
		
		public String title() {
			return mTitle;
		}
		
		public String url() {
			return mUrl;
		}
		
		public Calendar time() {
			return mTime;
		}
		
		@Override
		public String toString() {
			return String.format("%s;%s;%s", mTime.toString(), mTitle, mUrl);
		}
	}
				
	public History(Context context) {
		mContext = context;
		mStorage = new HistorySQLStorage(context, this);
	}
	
	public void add(String title, String url) {
		Calendar now = Calendar.getInstance();
		add(new Item(title, url, now));
	}
	
	public void add(Item item) {
		mStorage.saveItem(item);
	}
		
	public List<Item> getHistory(Calendar from, Calendar to) {
		return mStorage.getItem(from, to);
	}
	
	@Override
	public void onSaveComplete(History.Item item) {
		// nothing to do now
	}
	
	@Override
	public void onGetComplete(List<History.Item> items) {
		// nothing to do now
	}
}
