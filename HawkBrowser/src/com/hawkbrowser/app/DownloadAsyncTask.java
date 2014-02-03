package com.hawkbrowser.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import com.hawkbrowser.util.CommonUtil;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadAsyncTask extends 
	AsyncTask<DownloadItem, Integer, DownloadItem> {
	
	private static int CONNECT_TIMEOUT = 10000; // ms
	private static int READ_TIMEOUT = 10000; // ms

	private DownloadManager mDownloadMgr;
	private DownloadItem mItem;
	
	public DownloadAsyncTask(DownloadManager downloadMgr) {
		mDownloadMgr = downloadMgr;
	}
	
	public DownloadItem item() {
		return mItem;
	}
	
	protected DownloadItem doInBackground(DownloadItem... params) {
		
		InputStream httpStream = null;
		
		try {
			
			mItem = params[0];
			mItem.setStatus(DownloadItem.Status.ONPROGRESS);
			
			URL url = new URL(mItem.url());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod("GET");
			
			if(mItem.progress() > 0) {
				String range = String.format("bytes=%d-", mItem.progress());
				conn.setRequestProperty("Range", range);
			}
			
			conn.setDoInput(true);
			
			conn.connect();
			int responseCode = conn.getResponseCode();
			Log.d("Download", String.format("Response code: %d, url %s", 
					responseCode, mItem.url()));
			
			if(HttpStatus.SC_OK == responseCode || 
				HttpStatus.SC_PARTIAL_CONTENT == responseCode) {
				
				httpStream = conn.getInputStream();
				saveToFile(httpStream);
				
			} else {
				mItem.setStatus(DownloadItem.Status.FAILED);
			}
			
			conn.disconnect();
			
		} catch(Exception e) {
			
			Log.e("Download", CommonUtil.getExceptionStackTrace(e));
			
		} finally {
			
			if(null != httpStream) {
				
				try {
					httpStream.close();
					httpStream = null;
				} catch(Exception e) { 
					
				}
			}
		}
		
		return params[0];
	}
	
	protected void onPostExecute(DownloadItem item) {
		Log.d("download", "onPoseExecute: " + item.name());
		mDownloadMgr.onDownloadFinished(item);
	}
	
	protected void onProgressUpdate(Integer... progress) {
				
		mItem.setProgress(progress[0].longValue());
		mDownloadMgr.onProgressUpdate(mItem);
	}
	
	// This function is not called.......
	@Override
	protected void onCancelled(DownloadItem item) {
		Log.d("download", "onCancelled: " + item.name());
		mDownloadMgr.onDownloadStoped(item);
	}
	
	
	@Override
	protected void onCancelled() {
		mDownloadMgr.onDownloadStoped(mItem);
	}
	
	
	private void saveToFile(InputStream inputStream) {
		
		Log.d("Download", String.format("save to file: %s", mItem.name()));
		String externalStorageState = Environment.getExternalStorageState();
		if(!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			mItem.setLocalFilePath(null);
			return;
		}
		
		FileOutputStream fos = null;
		File file = null;
		
		if(null != mItem.localFilePath()) {
			file = new File(mItem.localFilePath());
		} else {
			file = CommonUtil.getUniqueFile(
				CommonUtil.getDownloadDataDir(), mItem.name());
		}
		Log.d("Download", String.format("Output File: %s", file.getPath()));
		
		try {
			fos = new FileOutputStream(file, mItem.progress() > 0);
			mItem.setLocalFilePath(file.getPath());
			byte[] buffer = new byte[4096];
			int readLen = 0;
			int totalReadLen = 0;
			long timeLastProgressUpdate = System.nanoTime();
			long progressUpdateDistance = 300 * 1000 * 1000; // 200ms
			
			while((readLen = inputStream.read(buffer)) != -1) {
				
				if(isCancelled()) {
					Log.d("download", "download cancel: " + mItem.name());
					break;
				}
				
				fos.write(buffer, 0, readLen);
				totalReadLen += readLen;
								
				if(System.nanoTime() - timeLastProgressUpdate 
						> progressUpdateDistance) {
					publishProgress(totalReadLen);
					totalReadLen = 0;
					timeLastProgressUpdate = System.nanoTime();
				}
			}
			
			if(totalReadLen > 0) {
				publishProgress(totalReadLen);
				totalReadLen = 0;
			}
			
			fos.flush();
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			if(null != fos) {
				
				try {
					fos.close();
					fos = null;
				} catch(Exception e) {
					
				}
			}
		}
	}
}
