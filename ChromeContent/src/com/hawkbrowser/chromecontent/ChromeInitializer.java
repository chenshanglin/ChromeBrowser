package com.hawkbrowser.chromecontent;

import org.chromium.base.BaseSwitches;
import org.chromium.base.CommandLine;
import org.chromium.base.PathUtils;
import org.chromium.content.app.LibraryLoader;
import org.chromium.content.browser.BrowserStartupController;
import org.chromium.content.browser.ResourceExtractor;
import org.chromium.content.common.ProcessInitException;

import android.content.Context;
import android.util.Log;

public class ChromeInitializer {
	
	public interface InitializeCallback {
		void onSuccess(boolean alreadyStarted);
		void onFailure();
	}
		
    public static final String COMMAND_LINE_FILE = "/data/local/tmp/content-shell-command-line";
    private static final String TAG = "ChromeInitializer";
    private static final String[] MANDATORY_PAK_FILES = new String[] {"content_shell.pak"};
    private static final String PRIVATE_DATA_DIRECTORY_SUFFIX = "content_shell";
	
	private static Object mLock = new Object();
	private static ChromeInitializer mInstance = null;
	private boolean mIsInitialized = false;
	private boolean mIsStart = false;
	private boolean mIsLibraryLoaded = false;
	private boolean mIsStartFinished = false;
	private InitializeCallback mCallback; 
	
	public static ChromeInitializer get() {
		
		if(null == mInstance) {
			synchronized(mLock) {
				if(null == mInstance) {
					mInstance = new ChromeInitializer();
				}
			}
		}
		
		return mInstance;
	}
	
	private ChromeInitializer() {
		
	}
	
	public boolean isInitialized() {
		return mIsInitialized;
	}
	
	public void initialize() throws ProcessInitException {
		
		if(mIsInitialized)
			return;
		
		mIsInitialized = true;
		
        ResourceExtractor.setMandatoryPaksToExtract(MANDATORY_PAK_FILES);
        PathUtils.setPrivateDataDirectorySuffix(PRIVATE_DATA_DIRECTORY_SUFFIX);
		        
        if (!CommandLine.isInitialized()) 
            CommandLine.initFromFile(COMMAND_LINE_FILE);
        
        waitForDebuggerIfNeeded();
	}
	
	// package internal only
	public void	ensureLibraryLoaded() throws ProcessInitException {
		
		if(mIsLibraryLoaded)
			return;
		
		mIsLibraryLoaded = true;
		
        try {
            LibraryLoader.ensureInitialized();
        } catch (ProcessInitException e) {
            Log.e(TAG, "ContentView initialization failed.", e);
            throw e;
        }
		
	}
	
	public void setCallback(InitializeCallback callback) {
		mCallback = callback;
	}
	
	public boolean isChromeStartFinished() {
		return mIsStartFinished;
	}
	
	public void startChrome(Context context) throws ProcessInitException {
				
		if(mIsStart)
			return;
		
		mIsStart = true;
		
        try {
            BrowserStartupController.get(context).startBrowserProcessesAsync(
                new BrowserStartupController.StartupCallback() {
                    @Override
                    public void onSuccess(boolean alreadyStarted) {
                    	mIsStartFinished = true;
                        if(null != mCallback)
                        	mCallback.onSuccess(alreadyStarted);
                    }

                    @Override
                    public void onFailure() {
                    	mIsStartFinished = true;
                        if(null != mCallback)
                        	mCallback.onFailure();
                    }
                });
        } catch (ProcessInitException e) {
            Log.e(TAG, "Unable to load native library.", e);
            throw e;
        }
	}
	// 
	
    private void waitForDebuggerIfNeeded() {
        if (CommandLine.getInstance().hasSwitch(BaseSwitches.WAIT_FOR_JAVA_DEBUGGER)) {
            Log.e(TAG, "Waiting for Java debugger to connect...");
            android.os.Debug.waitForDebugger();
            Log.e(TAG, "Java debugger connected. Resuming execution.");
        }
    }
}
