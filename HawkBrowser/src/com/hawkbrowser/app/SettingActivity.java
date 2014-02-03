package com.hawkbrowser.app;

import com.hawkbrowser.R;
import com.hawkbrowser.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		setupListener();
	}
	
	private void setupListener() {
		View back = findViewById(R.id.setting_back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		View fontSize = findViewById(R.id.setting_fontsize);
		fontSize.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}
}
