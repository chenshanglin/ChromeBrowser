package com.hawkbrowser.app;

import java.util.Calendar;
import java.util.List;

import android.text.format.Time;

public interface HistoryStorage {
	void setListener(HistoryStorageListener listener);
	void saveItem(History.Item item);
	void close();
	List<History.Item> getItem(Calendar from, Calendar to);
}
