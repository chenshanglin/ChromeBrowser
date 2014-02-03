package com.hawkbrowser.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.hawkbrowser.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class CommonUtil {
	
	public final static String ROOT_DATA_DIR = "hawkbrowser";
	public final static String DOWNLOAD_DIR = "download";
	public final static String BROWSER_CACHE_DIR = "cache";

	public static void showTips(Context ctx, String msg) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(ctx, msg, duration);
		toast.show();
	}
	
	public static void showTips(Context ctx, int msgStringId) {
		
		Resources rc = ctx.getResources();
		String msg = rc.getString(msgStringId);
		showTips(ctx, msg);
	}
	
	public static String getExceptionStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	@SuppressLint("NewApi")
	public static Point screenSize(Context ctx) {
		WindowManager wm = (WindowManager) 
				ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			Point size = new Point();
			display.getSize(size);
			return size;
		}
		else {
			return new Point(display.getWidth(), display.getHeight());
		}
		
	}
	
	public static long getExternalStorageFreeSpace() {
		
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		
		return stat.getAvailableBlocks() * stat.getBlockSize();
	}
	
	public static File getUniqueFile(String dir, String fileName) {
		
		int fileNameSuffix = 0;
		File file = new File(dir, fileName);
		
		while(file.exists()) {
			
			String newFileName;
			int dotIndex = fileName.lastIndexOf('.');
			
			if(-1 == dotIndex) {
				newFileName = String.format("%s%d", fileName, ++fileNameSuffix);
			} else {
				String name = fileName.substring(0, dotIndex);
				String ext = fileName.substring(dotIndex + 1);
				newFileName = String.format("%s%d.%s", 
					name, ++fileNameSuffix, ext);
			}
			
			file = new File(dir, newFileName);
		} 
		
		return file;
	}
	
	public static String fileNameFromUrl(String url) {
		
		String name = null;
		
		int lastDot = url.lastIndexOf('/');
		if(-1 == lastDot) {
			name = url;
		} else { 
			name = url.substring(lastDot + 1);
		}
		
		return name;
	}
	
	public static String getOurExtDataDir() {
		File rootDir = Environment.getExternalStorageDirectory();
		File ourDataDir = new File(rootDir, ROOT_DATA_DIR);
		
		if(!ourDataDir.exists()) {
			ourDataDir.mkdir();
		}
		
		return ourDataDir.getPath();
	}
	
	public static String getDownloadDataDir() {
		File downloadDir = new File(getOurExtDataDir(), DOWNLOAD_DIR);
		
		if(!downloadDir.exists()) {
			downloadDir.mkdir();
		}
		
		return downloadDir.getPath();
	}
	
	public static File getCacheDir() {
		File cacheDir = new File(getOurExtDataDir(), BROWSER_CACHE_DIR);
		
		if(!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		
		return cacheDir;
	}
	
	public static boolean checkDiskSpace(Context context, long contentLength) {
		
		String externalStorageState = Environment.getExternalStorageState(); 
		if(!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			CommonUtil.showTips(context, R.string.nosdcard);
			return false;
		}
		
		/*
		if(CommonUtil.getExternalStorageFreeSpace() < contentLength) {
			CommonUtil.showTips(mContext, R.string.diskspacenotengouth);
			return;
		}
		*/
		
		return true;
	}
	
	public static void showDialog(Context context, int msgResId, 
		int positiveBtnTextId, int negativeBtnTextId, 
		DialogInterface.OnClickListener listener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setMessage(msgResId);
		builder.setPositiveButton(positiveBtnTextId, listener);
		builder.setNegativeButton(negativeBtnTextId, listener);
		
		AlertDialog dlg = builder.create();
		dlg.show();
	}
	
	public static boolean deleteFile(File f) {
		if(null == f) {
			return true;
		}
				
		boolean bSuccess = true;
		
		if(f.isDirectory()) {
			File[] children = f.listFiles();
			
			if((null != children) && children.length > 0) {
				for(File child : children) {
					bSuccess = bSuccess && deleteFile(child);
				}
			}
		}
		
		return bSuccess && f.delete();
	}
	
	public static Drawable getIconFromApk(Context context, String apkPath) {
		
		if((null == apkPath) || !apkPath.endsWith(".apk")) {
			return null;
		}
		
		Drawable icon = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = pm.getPackageArchiveInfo(apkPath, 
			PackageManager.GET_ACTIVITIES);
		
		if(null != pi) {
			if(Build.VERSION.SDK_INT >= 8) {
				pi.applicationInfo.sourceDir = apkPath;
				pi.applicationInfo.publicSourceDir = apkPath;
			}
			
			icon = pi.applicationInfo.loadIcon(pm);
		}
		
		return icon;
	}
	
	public static String getFileExt(String path) {
		int pos = path.lastIndexOf('.');
		return (-1 != pos) ? path.substring(pos + 1) : "";
	}
}
