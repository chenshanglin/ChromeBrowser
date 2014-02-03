package com.hawkbrowser.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hawkbrowser.R;
import com.hawkbrowser.util.CommonUtil;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public final class DownloadManager {
	
	public static interface Listener {
		void onProgressUpdate(DownloadItem item);
		void onDownloadFinished(DownloadItem item);
		void onDownloadStoped(DownloadItem item);
	}
	
	public static final String FILE_NAME = "download.dat";
	
	private int mItemId = 0;
	private Context mContext;
	private List<DownloadItem> mItems;
	private Listener mListener;
	private List<DownloadAsyncTask> mTasks;
		
	public DownloadManager(Context context) {
		
		mContext = context;
		mTasks = new ArrayList<DownloadAsyncTask>();
		load();
	}
	
	public void Download(String url, String userAgent, String fileName, 
		String contentDisposition, String mimetype, long contentLength) {
		
		if(CommonUtil.checkDiskSpace(mContext, contentLength)) {
			
			DownloadItem item = new DownloadItem(++mItemId, 
				contentLength, url, fileName);
			mItems.add(0, item);
			internalDownload(item);
		}
	}
	
	public void stop(DownloadItem item) {
		for(DownloadAsyncTask task : mTasks) {
			if(task.item() == item) {
				task.cancel(true);
				mTasks.remove(task);
				return;
			}
		}
		
		// if there is no task currently, we should set item status here
		item.setStatus(DownloadItem.Status.PAUSED);
	}
	
	public void continueDownload(DownloadItem item) {
		
		if(CommonUtil.checkDiskSpace(mContext, item.size())) {
			internalDownload(item);
		}
	}
	
	public void onProgressUpdate(DownloadItem item) {
		
		if(null != mListener) {
			mListener.onProgressUpdate(item);
		}
	}
	
	public void onDownloadFinished(DownloadItem item) {
		
		item.setStatus(DownloadItem.Status.FINISHED);
		
		if(null != mListener) {
			mListener.onDownloadFinished(item);
		}
		
		deleteDownloadTask(item);
	}
	
	public void onDownloadStoped(DownloadItem item) {
		
		item.setStatus(DownloadItem.Status.PAUSED);
		
		if(null != mListener) {
			mListener.onDownloadStoped(item);
		}
		
		deleteDownloadTask(item);
	}
	
	public void setEventListener(Listener listener) {
		mListener = listener;
	}
	
	public List<DownloadItem> getItems() {
		return mItems;
	}
	
	public List<DownloadItem> getNonActiveItems() {
		List<DownloadItem> items = new ArrayList<DownloadItem>();
		
		for(DownloadItem item : mItems) {
			if(item.status() != DownloadItem.Status.ONPROGRESS) {
				items.add(item);
			}
		}
		
		return items;
	}
	
	public void deleteDownloadItem(DownloadItem item) {
		
		deleteDownloadTask(item);
		mItems.remove(item);
	}
	
	private void internalDownload(DownloadItem item) {
		DownloadAsyncTask task = new DownloadAsyncTask(this);
		task.execute(item);
		mTasks.add(task);
	}
	
	private void deleteDownloadTask(DownloadItem item) {
		
		for(DownloadAsyncTask task : mTasks) {
			if(task.item() == item) {
				mTasks.remove(task);
				break;
			}
		}
	}
	
	private void load() {
		
		mItems = new ArrayList<DownloadItem>();
		File file = new File(mContext.getFilesDir(), FILE_NAME);
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try {
			
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			
			do {
				DownloadItem item = (DownloadItem) ois.readObject();
				
				if(null != item) {
					Log.d("download", 
						String.format("load: %s", item.toString()));
					mItems.add(item);
				} else {
					break;
				}
				
			} while(true);
						
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				
				if(null != ois) {
					ois.close();
					ois = null;
					fis = null;
				} else if(null != fis) {
					fis.close();
					fis = null;
				}
				
			} catch(Exception e) {
				
			}
		}
	}
	
	public void save() {
		
		File file = new File(mContext.getFilesDir(), FILE_NAME);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			
			if(null != mItems) {
				for(DownloadItem item : mItems) {
					Log.d("download", 
							String.format("save: %s", item.toString()));
					oos.writeObject(item);
				}
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {
				if(null != oos) {
					oos.close();
					oos = null;
					fos = null;
				} else if(null != fos) {
					fos.close();
					fos = null;
				}
			} catch(Exception e) {
				
			}
		}
	}
}
