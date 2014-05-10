package com.hawkbrowser.shell;

import org.chromium.base.PathUtils;
import org.chromium.content.browser.ResourceExtractor;
import org.chromium.content.common.ProcessInitException;

import com.hawkbrowser.chromecontent.ChromeInitializer;

import android.app.Application;
import android.util.Log;

public class TestApplication extends Application {
	
	private static final String TAG = "ContentShellTest";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		try {
			ChromeInitializer.get().initialize();
		} catch(ProcessInitException e) {
			Log.e(TAG, e.getMessage());
			System.exit(0);
		}
	}
}
