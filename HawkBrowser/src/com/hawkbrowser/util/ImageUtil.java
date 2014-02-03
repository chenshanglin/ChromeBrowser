package com.hawkbrowser.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

public final class ImageUtil {
	public static Bitmap loadBitmapFromView(View v) {
		
		Bitmap b = Bitmap.createBitmap(v.getWidth(), 
				v.getHeight(), Bitmap.Config.ARGB_8888);                
	    Canvas c = new Canvas(b);
	    v.layout(0, 0, v.getWidth(), v.getHeight());
	    v.draw(c);
	    return b;
	}
}
