package com.hawkbrowser.base;

import com.hawkbrowser.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class ImageButtonText extends FrameLayout {
	
	private ImageButton mImage;
	private TextView	mText;

	public ImageButtonText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		initLayout(context, null);
	}
	
	public ImageButtonText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout(context, attrs);
	}

	public ImageButtonText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initLayout(context, attrs);
	}
	
	public void setText(String text) {
		mText.setText(text);
	}
	
	private void initLayout(Context context, AttributeSet attrs) {
		
		this.setClickable(true);
		
		mText = new TextView(context);
		addView(mText, 0);
		mText.setClickable(false);
		
		LayoutParams textViewLayout = 
			(FrameLayout.LayoutParams) mText.getLayoutParams();
		textViewLayout.gravity = Gravity.CENTER;
		textViewLayout.height = LayoutParams.WRAP_CONTENT;
		textViewLayout.width = LayoutParams.WRAP_CONTENT;
		textViewLayout.topMargin = 5;
		textViewLayout.rightMargin = 2;
		mText.setLayoutParams(textViewLayout);
		
		mImage = new ImageButton(context);
		addView(mImage, 1);
		mImage.setClickable(false);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, 
			R.styleable.ImageButtonText);
		
		CharSequence text = ta.getText(
			R.styleable.ImageButtonText_android_text);
		mText.setText(text);
		
		int textSize = ta.getDimensionPixelSize(
			R.styleable.ImageButtonText_android_textSize, 5);
		mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			
		Drawable image = ta.getDrawable(
			R.styleable.ImageButtonText_android_src);
		mImage.setImageDrawable(image);
		
		Drawable bg = ta.getDrawable(
			R.styleable.ImageButtonText_android_background);
		mImage.setBackgroundDrawable(bg);
		
		ta.recycle();
	}
}
