package com.hawkbrowser.webkit;

import org.chromium.android_webview.AwBrowserProcess;
import org.chromium.android_webview.AwResource;
import org.chromium.content.browser.ResourceExtractor;
import org.chromium.content.common.CommandLine;

import com.hawkbrowser.webview.R;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

public class ChromeInitializer {

	private static final String TAG = "WebView";
	
    private static final String[] MANDATORY_PAKS = {
    	"webviewchromium.pak", "en-US.pak"
    };
    
    private static boolean sInitialized = false;
    
    public static void initialize(Context context) {
    	
    	if(sInitialized) {
    		return;
    	}
    	
    	sInitialized = true;
    	
        AwResource.setResources(context.getResources());

        AwResource.RAW_LOAD_ERROR = R.raw.blank_html;
        AwResource.RAW_NO_DOMAIN = R.raw.blank_html;

        AwResource.STRING_DEFAULT_TEXT_ENCODING = R.string.app_name;

        CommandLine.initFromFile("/data/local/tmp/android-webview-command-line");

        if (CommandLine.getInstance().hasSwitch(CommandLine.WAIT_FOR_JAVA_DEBUGGER)) {
           Log.e(TAG, "Waiting for Java debugger to connect...");
           Debug.waitForDebugger();
           Log.e(TAG, "Java debugger connected. Resuming execution.");
        }

        ResourceExtractor.setMandatoryPaksToExtract(MANDATORY_PAKS);
        ResourceExtractor.setExtractImplicitLocaleForTesting(false);
        AwBrowserProcess.loadLibrary();
        AwBrowserProcess.start(context);
    }
}
