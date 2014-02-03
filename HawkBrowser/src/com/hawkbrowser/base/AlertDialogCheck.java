package com.hawkbrowser.base;

import com.hawkbrowser.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class AlertDialogCheck {

	private boolean mChecked;
	private int mMsgId;
	private int mPositiveBtnTextId;
	private int mNegativeBtnTextId;
	private Listener mListener;
	
	public static interface Listener {
		void onClick(boolean isConfirmed, boolean isChecked);
	}
	
	public AlertDialogCheck(int msgResId, int positiveBtnTextId, 
		int negativeBtnTextId, Listener listener) {
		
		mChecked = false;
		mMsgId = msgResId;
		mPositiveBtnTextId = positiveBtnTextId;
		mNegativeBtnTextId = negativeBtnTextId;
		mListener = listener;
	}
	
    public void show(Context context) {
        // Build the dialog and set up the button click handlers       
		class DialogListener implements DialogInterface.OnClickListener {
						
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// TODO Auto-generated method stub
				if(null != mListener) {
					mListener.onClick(DialogInterface.BUTTON_POSITIVE == id,
						mChecked);
				}
			}
		}
		
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
		DialogListener listener = new DialogListener();
		
		ViewGroup vg = (ViewGroup)
			LayoutInflater.from(context).inflate(R.layout.dialog_check, null);
		
		TextView msgView = (TextView)
			vg.findViewById(R.id.dialog_check_message);
		msgView.setText(mMsgId);
		
		CheckBox cb = (CheckBox)vg.findViewById(R.id.dialog_check_checkbox);
		cb.setText(R.string.delete_file_same_time);
		cb.setOnCheckedChangeListener(
			new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, 
				boolean isChecked) {
				mChecked = isChecked;				
			}
		});
		
		builder.setView(vg);
		builder.setPositiveButton(mPositiveBtnTextId, listener);
		builder.setNegativeButton(mNegativeBtnTextId, listener);
		
		AlertDialog dlg =  builder.create();
        dlg.show();
    }
}
