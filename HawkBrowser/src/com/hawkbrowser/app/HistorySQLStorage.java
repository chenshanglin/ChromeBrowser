package com.hawkbrowser.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hawkbrowser.app.History.Item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

public class HistorySQLStorage implements HistoryStorage {

	HistoryDBHelper mDBHelper;
	SQLiteDatabase mDB;
	AtomicBoolean mIsDBOpened;
	HistoryStorageListener mListener;
	History.Item mPendingSaveItem;
	Calendar mPendingGetTimeFrom;
	Calendar mPendingGetTimeTo;
	
	class OpenDBAsyncTask extends 
		AsyncTask<HistorySQLStorage, Void, HistorySQLStorage> {
			
		protected void onPostExecute(HistorySQLStorage storage) {
			Log.d("History", "getWritableDatabase onPostExecute");
			mIsDBOpened.set(true);
			deleteExpiredItems();
			storage.runPendingTasks();
		}

		@Override
		protected HistorySQLStorage doInBackground(HistorySQLStorage... params) {
			
			Log.d("History", "getWritableDatabase");
			params[0].mDB = params[0].mDBHelper.getWritableDatabase();
			return params[0];
		}
		
		private void deleteExpiredItems() {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MONTH, -1);
			
			String where = HistoryDBHelper.COLUMN_NAME_DAY + " < ?";
			String[] whereArgs = { 
				String.format("%d", now.get(Calendar.DAY_OF_YEAR)) };
			
			mDB.delete(HistoryDBHelper.TABLE_NAME_HISTORY, where, whereArgs);
		}
	}
		
	public HistorySQLStorage(Context context, 
		HistoryStorageListener listener) {
		
		mIsDBOpened = new AtomicBoolean(false);
		mDBHelper = new HistoryDBHelper(context);
		mListener = listener;
		new OpenDBAsyncTask().execute(this);
	}
	
	@Override
	public void setListener(HistoryStorageListener listener) {
		mListener = listener;
	}
	
	@Override
	public void saveItem(History.Item item) {
		
		if(mIsDBOpened.get()) {
			internalSaveItem(item);
		} else {
			mPendingSaveItem = item;
		}
	}
	
	@Override
	public List<History.Item> getItem(Calendar from, Calendar to) {
		
		if(mIsDBOpened.get()) {
			return internalGetItem(from, to);
		} else {
			mPendingGetTimeFrom = from;
			mPendingGetTimeTo = to;
			return null;
		}
		
	}
	
	@Override
	public void close() {
		if(mIsDBOpened.get()) {
			mIsDBOpened.set(false);
			mDB.close();
			mDB = null;
		}
	}
	
	private void runPendingTasks() {
		
		if(null != mPendingSaveItem) {
			internalSaveItem(mPendingSaveItem);
			
			if(null != mListener) {
				mListener.onSaveComplete(mPendingSaveItem);
				mPendingSaveItem = null;
			}
		}
		
		if((null != mPendingGetTimeFrom) && (null != mPendingGetTimeTo)) {
			List<History.Item> items = 
				internalGetItem(mPendingGetTimeFrom, mPendingGetTimeTo);
			
			if(null != mListener) {
				mListener.onGetComplete(items);
			}
		}
	}
	
	private void internalSaveItem(History.Item item) {
		
		if(null == item) {
			return;
		}
		
		Log.d("History", String.format("SaveItem: %s", item.toString()));
		
		ContentValues values = new ContentValues();
		values.put(HistoryDBHelper.COLUMN_NAME_TIME, 
			item.time().getTimeInMillis());
		values.put(HistoryDBHelper.COLUMN_NAME_TITLE, item.title());
		values.put(HistoryDBHelper.COLUMN_NAME_URL, item.url());
		values.put(HistoryDBHelper.COLUMN_NAME_DAY, 
			item.time().get(Calendar.DAY_OF_YEAR));
		mDB.insert(HistoryDBHelper.TABLE_NAME_HISTORY, null, values);
	}
	
	private List<History.Item> internalGetItem(Calendar from, Calendar to) {
		
		if(null == from) {
			from = Calendar.getInstance();
			from.add(Calendar.MONTH, -1);
		}
		
		if(null == to) {
			to = Calendar.getInstance();
			to.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		Log.d("History", String.format("from: %s; to: %s", 
				from.toString(), to.toString()));
		
		String[] projection = {
			HistoryDBHelper.COLUMN_NAME_TIME,
			HistoryDBHelper.COLUMN_NAME_TITLE,
			HistoryDBHelper.COLUMN_NAME_URL
		};
		
		String where = HistoryDBHelper.COLUMN_NAME_DAY + " >= ? and " 
				+ HistoryDBHelper.COLUMN_NAME_DAY + " < ?";
		String[] whereArgs = { String.format("%d", from.get(Calendar.DAY_OF_YEAR)), 
				String.format("%d", to.get(Calendar.DAY_OF_YEAR)) };
		String sortOrder = HistoryDBHelper.COLUMN_NAME_TIME + " DESC";
		
		Cursor c = mDB.query(HistoryDBHelper.TABLE_NAME_HISTORY, projection, 
				where, whereArgs, null, null, sortOrder);	
		c.moveToFirst();
		
		ArrayList<History.Item> results = new ArrayList<History.Item>();
				
		Log.d("History", 
				String.format("row count: %d", c.getCount()));
		
		if(c.getCount() > 0) {
			do {
				long ms = c.getLong(
					c.getColumnIndex(HistoryDBHelper.COLUMN_NAME_TIME));
				String title = c.getString(
					c.getColumnIndex(HistoryDBHelper.COLUMN_NAME_TITLE));
				String url = c.getString(
					c.getColumnIndex(HistoryDBHelper.COLUMN_NAME_URL));
				
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(ms);
				
				History.Item item = new History.Item(title, url, time);
				results.add(new History.Item(title, url, time));
				
				Log.d("History", 
					String.format("ReadItem: %s", item.toString()));
				
			} while(c.moveToNext());
		}
		
		c.close();
		
		return results;
	}
}
