package com.hawkbrowser.shell;

import com.hawkbrowser.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShortCut extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent shortcutIntent = new Intent(this, HawkBrowser.class);
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, 
				shortcutIntent);
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "HawkBrowser");
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
			Intent.ShortcutIconResource.fromContext(this, 
				R.drawable.ic_launcher));
		
		setResult(RESULT_OK, resultIntent);
		
		finish();
	}

}
