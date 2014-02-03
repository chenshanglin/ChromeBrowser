package com.hawkbrowser.app;

import java.util.List;

public interface HistoryStorageListener {
	void onSaveComplete(History.Item item);
	void onGetComplete(List<History.Item> items);
}
