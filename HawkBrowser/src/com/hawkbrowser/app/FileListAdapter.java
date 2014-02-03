package com.hawkbrowser.app;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hawkbrowser.R;
import com.hawkbrowser.base.MimeManager;
import com.hawkbrowser.util.CommonUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter
	implements AdapterView.OnItemClickListener {
	
	private List<File> mFiles;
	private List<File> mCheckedItems;
	private Context mContext;
	private Listener mListener;
	private boolean mIsInEditMode;
	
	public static interface Listener {
		void onFileItemClick(File file);
		void onFileItemChecked(File file);
	}
	
	public FileListAdapter(Context context, File currentFolder) {
		
		mIsInEditMode = false;
		mContext = context;
		setData(currentFolder);
	}
	
	public void setEditable(boolean bEditable) {
		mIsInEditMode = bEditable;
	}
	
	public boolean getEditable() {
		return mIsInEditMode;
	}
	
	public List<File> getCheckedItems() {
		return mCheckedItems;
	}
	
	public List<File> getAllItems() {
		return mFiles;
	}
	
	public void setData(File currentFolder) {
		
		mCheckedItems = new ArrayList<File>();
		
		File[] files = currentFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.isHidden();
			}
		});
		
		if((null == files) || (files.length <= 0)) {
			mFiles = new ArrayList<File>();
		} else {
			mFiles = new ArrayList<File>(files.length);
			
			for(File file : files) {
				mFiles.add(file);
			}
			
			Collections.sort(mFiles, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
				    if(f1.isDirectory()) {
				    	if(f2.isDirectory()) {
				    		return f1.getName().compareTo(f2.getName());
				    	} else {
				    		return -1;
				    	}
				    } else {
				    	if(f2.isDirectory()) {
				    		return 1;
				    	} else {
				    		return f1.getName().compareTo(f2.getName());
				    	}
				    }
				}
			});
		}
		
		// In edit mode, don't need "go to up dir" item
		if(!mIsInEditMode) {
			File rootFile = Environment.getExternalStorageDirectory();
			if(!rootFile.equals(currentFolder)) {
				mFiles.add(0, null);
			}
		}
	}
		
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, 
			View view, int position, long id) {
		
		ViewGroup vg = (ViewGroup)view;
		
		if(mIsInEditMode) {
			CheckBox cb = (CheckBox)vg.findViewById(R.id.file_item_check);
			cb.setChecked(!cb.isChecked());
			
			if(cb.isChecked()) {
				mCheckedItems.add(mFiles.get(position));
			} else {
				mCheckedItems.remove(mFiles.get(position));
			}
			
			if(null != mListener) {
				mListener.onFileItemChecked(mFiles.get(position));
			}
			
		} else {
			if(null != mListener) {
				mListener.onFileItemClick(mFiles.get(position));
			}
		}		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub		
		return mFiles.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		File file = mFiles.get(position);
		return (null != file) ? file.hashCode() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
			
		LayoutInflater inflater = LayoutInflater.from(mContext);
		ViewGroup itemView = (ViewGroup) 
			inflater.inflate(R.layout.file_list_item, null);
				
		ImageView itemIcon = (ImageView) 
			itemView.findViewById(R.id.file_item_icon);
		TextView itemName = (TextView)
			itemView.findViewById(R.id.file_item_name);
		ImageView arrowIcon = (ImageView)
			itemView.findViewById(R.id.file_item_arrow);
		
		File file = mFiles.get(position);
		View check = itemView.findViewById(R.id.file_item_check);
		
		if(null == file) {
			itemIcon.setImageResource(R.drawable.icon_up_dir);
			itemName.setText(R.string.back_to_parent);
			arrowIcon.setVisibility(View.GONE);
			check.setVisibility(View.GONE);
		} else {
			itemName.setText(file.getName());
			check.setVisibility(mIsInEditMode ? View.VISIBLE : View.GONE);
			
			if(file.isDirectory()) {
				itemIcon.setImageResource(R.drawable.icon_folder);
				arrowIcon.setVisibility(View.VISIBLE);
			} else {
				
				Drawable icon = MimeManager.instance().getDrawable(mContext, 
					file.getPath());
				
				if(null != icon) {
					itemIcon.setImageDrawable(icon);
				} else {
					itemIcon.setImageResource(R.drawable.icon_file);
				}
				
				arrowIcon.setVisibility(View.GONE);
			}
		}
				
		return itemView;
	}
}
