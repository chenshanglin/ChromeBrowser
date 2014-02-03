package com.hawkbrowser.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.hawkbrowser.R;
import com.hawkbrowser.util.CommonUtil;

public class DownloadDialog {

	private EditText mEditText;
	private Listener mListener;
	private Param mParam;
	
	public static class Param {
		public String mUrl;
		public String mUserAgent;
		public String mContentDisposition;
		public String mMimeType;
		public long mSize;
		public Context mContext;
		
		public Param(String url, String userAgent, 
				String contentDisposition, String mimeType, 
				long contentLength, Context context) {
			
			mUrl = url;
			mUserAgent = userAgent;
			mContentDisposition = contentDisposition;
			mMimeType = mimeType;
			mSize = contentLength;
			mContext = context;
		}
	}
	
	public static interface Listener {
		void onClick(boolean isConfirmed, String newName, Param param);
	}
	
	public DownloadDialog(Param param, Listener listener) {
		
		mParam = param;
		mListener = listener;
	}
	
    public void show() {
        // Build the dialog and set up the button click handlers       
		class DialogListener implements DialogInterface.OnClickListener {
						
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// TODO Auto-generated method stub
				if(null != mListener) {
					mListener.onClick(
						DialogInterface.BUTTON_POSITIVE == id,
						mEditText.getText().toString(), mParam);
				}
			}
		}
		
        AlertDialog.Builder builder = new AlertDialog.Builder(mParam.mContext);
		DialogListener listener = new DialogListener();
		
		ViewGroup vg = (ViewGroup) LayoutInflater.from(mParam.mContext).
			inflate(R.layout.dialog_download, null);
		
		TextView sizeView = (TextView) 
			vg.findViewById(R.id.dialog_download_filesize);
		String format = 
			mParam.mContext.getResources().getString(R.string.file_size_kb);
		String sizeValue = String.format(format, mParam.mSize / 1024);
		sizeView.setText(sizeValue);
		
		mEditText = (EditText) vg.findViewById(R.id.dialog_download_filename);
		mEditText.setText(CommonUtil.fileNameFromUrl(mParam.mUrl));
		
		vg.setBackgroundResource(android.R.color.white);
		
		builder.setPositiveButton(R.string.download, listener);
		builder.setNegativeButton(R.string.cancel, listener);
		
		AlertDialog dlg =  builder.create();
		
		dlg.setView(vg, 0, 0, 0, 0);
		dlg.show();
		
		Window window = dlg.getWindow();
		WindowManager.LayoutParams layoutParam = window.getAttributes();
		layoutParam.width = CommonUtil.screenSize(mParam.mContext).x;
        window.setAttributes(layoutParam);
    }
}
