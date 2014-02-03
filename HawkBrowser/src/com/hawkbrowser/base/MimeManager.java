package com.hawkbrowser.base;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.hawkbrowser.R;
import com.hawkbrowser.util.CommonUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.webkit.MimeTypeMap;

public class MimeManager {

	public static final String MIME_TEXT = "text/plain";
	
	private static MimeManager	mInstance;
	private static final ReentrantLock mLock = new ReentrantLock();

	private HashMap	mExtToDrawable;
	private HashMap mExtToMimeType;
	
	private MimeManager() {
		
		mExtToMimeType = new HashMap();		
		mExtToMimeType.put("txt", MIME_TEXT);
		mExtToMimeType.put("log", MIME_TEXT);
		
		mExtToDrawable = new HashMap();
	}
	
	public static MimeManager instance() {
		
		if(null == mInstance) {
			mLock.lock();
			
			if(null == mInstance) {
				mInstance = new MimeManager();
			}

			mLock.unlock();
		}
		
		return mInstance;
	}
	
	public String getMimeType(String path) {
		
		String type = null;
		String ext = getFileExt(path);
		
		if((null != ext) && !ext.isEmpty()) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
	        type = mime.getMimeTypeFromExtension(ext);
	        
	        if(null == type) {
	        	type = (String)mExtToMimeType.get(ext);
	        }
		}
		
		if((null == type) || type.isEmpty()) {
			type = "*/*";
		}
			
		return type;
	}
	
	public Drawable getDrawable(Context context, String path) {
		
		// Try load cache icon for apk
		Drawable icon = (Drawable) mExtToDrawable.get(path);
		
		if(null == icon) {
			if(path.endsWith(".apk")) {
				icon = CommonUtil.getIconFromApk(context, path);
				mExtToDrawable.put(path, icon);
				
			} else {
				
				// Try load cache icon for ext
				String ext = getFileExt(path);
				icon = (Drawable) mExtToDrawable.get(ext);
				
				if(null == icon) {
					icon = loadDefaultDrawable(context, path);
					mExtToDrawable.put(ext, icon);
				}
			}
		}
		
		return icon;
	}
	
	private Drawable loadDefaultDrawable(Context context, String path) {
		
		Drawable icon = null;
		String type = getMimeType(path);
		
		if(type.indexOf("image") != -1) {
			icon = context.getResources().getDrawable(R.drawable.icon_photo);
		} else if(type.indexOf("video") != -1) {
			icon = context.getResources().getDrawable(R.drawable.icon_movie);
		} else if(type.indexOf("audio") != -1) {
			icon = context.getResources().getDrawable(R.drawable.icon_music);
		}
		
		return icon;
	}
	
	
	
	private String getFileExt(String path) {
		int pos = path.lastIndexOf('.');
		return (-1 != pos) ? path.substring(pos + 1) : "";
	}
}
