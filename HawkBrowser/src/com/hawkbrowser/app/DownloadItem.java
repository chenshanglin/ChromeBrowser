package com.hawkbrowser.app;

import java.io.Serializable;

import com.hawkbrowser.util.CommonUtil;

import android.text.format.Time;
import android.util.Log;

public class DownloadItem implements Serializable {
	
	enum Status {
		NOTSTARTED,
		ONPROGRESS,
		PAUSED,
		FINISHED,
		FAILED
	}
	
	private static final long serialVersionUID = 1L;
	private int mId;
	private long mProgress;
	private long mLength;
	private String mUrl;
	private Status mStatus;
	private String mName;
	private String mLocalFilePath;
	transient private long mLastSetProgressTime;
	transient private long mSpeed; 

	public DownloadItem(int id, long length, String url, String fileName) {
		mId = id;
		mProgress = 0;
		mLength = length;
		mUrl = url;
		mStatus = Status.NOTSTARTED;
		mSpeed = 0;
		mLastSetProgressTime = 0;
		mName = fileName;
		
		if(null == mName) {
			CommonUtil.fileNameFromUrl(url);
		}
	}
	
	public void setProgress(long progress) {
		mProgress += progress;
		
		if(mProgress >= mLength) {
			mProgress = mLength;
		}
		
		long now = System.nanoTime();
		
		if(0 != mLastSetProgressTime) {
			long timeDistance = now - mLastSetProgressTime;
					
			if(timeDistance > 0) {
				mSpeed = progress * 1000 * 1000 * 1000 / (timeDistance << 10);
			}
			
		} else {
			mLastSetProgressTime = now;
		}
	}
	
	public long progress() {
		return mProgress;
	}
	
	public void setStatus(Status status) {
		mStatus = status;
	}
	
	public Status status() {
		return mStatus;
	}
	
	public long downloadSpeed() {
		return mSpeed;
	}
		
	public String url() {
		return mUrl;
	}
	
	public int hashCode() {
		return mId;
	}
	
	public String name() {
		return mName;
	}
	
	public long size() {
		return mLength;
	}
	
	public void setLocalFilePath(String path) {
		mLocalFilePath = path;
	}
	
	public String localFilePath() {
		return mLocalFilePath;
	}
	
	@Override
	public String toString() {
		return String.format("DownloadItem: %s, Status: %s", 
			mName, mStatus);
	}
}
