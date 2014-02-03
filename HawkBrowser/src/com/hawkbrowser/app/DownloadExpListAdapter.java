package com.hawkbrowser.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hawkbrowser.R;
import com.hawkbrowser.base.MimeManager;
import com.hawkbrowser.util.CommonUtil;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadExpListAdapter extends BaseExpandableListAdapter
	implements ExpandableListView.OnChildClickListener {

	private static final int[] mGroupNames = {
		R.string.download_ondownload,
		R.string.download_downloadfinished
	};
	
	private DownloadActivity mContext;
	private List< List<DownloadItem> > mGroupItems;
	private List<DownloadItem> mCheckedItems;
	private boolean mIsInEditMode;
	
	public DownloadExpListAdapter(DownloadActivity context, 
		List<DownloadItem> items) { 
		
		mIsInEditMode = false;
		mContext = context;
		setData(items);
		
	}
	
	public void setData(List<DownloadItem> items) {

		mCheckedItems = new ArrayList<DownloadItem>();
		mGroupItems = new ArrayList< List<DownloadItem> >();
		
		ArrayList<DownloadItem> onProgress = new ArrayList<DownloadItem>();
		ArrayList<DownloadItem> onFinished = new ArrayList<DownloadItem>();
 		
		if(null != items) {
			for(DownloadItem item : items) {
				if(item.status() == DownloadItem.Status.FINISHED) {
					onFinished.add(item);
				} else {
					onProgress.add(item);
				}
			}
		}
		
		mGroupItems.add(onProgress);
		mGroupItems.add(onFinished);
	}
	
	public void setEditable(boolean bEditable) {
		mIsInEditMode = bEditable;
	}
	
	public boolean getEditable() {
		return mIsInEditMode;
	}
	
	public List<DownloadItem> getCheckedItems() {
		return mCheckedItems;
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, 
		View v, int groupPosition, int childPosition, long id) {
		
		ViewGroup vg = (ViewGroup)v;
		
		if(mIsInEditMode) {
			CheckBox cb = (CheckBox)vg.findViewById(R.id.download_item_check);
			cb.setChecked(!cb.isChecked());

			DownloadItem item = 
					mGroupItems.get(groupPosition).get(childPosition); 
			
			if(cb.isChecked()) {
				mCheckedItems.add(item);
			} else {
				mCheckedItems.remove(item);
			}
			
			mContext.onDownloadItemChecked(item);
			
		} else {
			DownloadItem item = 
				mGroupItems.get(groupPosition).get(childPosition); 
			mContext.onDownloadItemClick(item);
		}
		
		return true;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return (groupPosition + 1) << 8 + childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, 
		boolean isLastChild, View convertView, ViewGroup parent) {
		
		ViewGroup vg = null;
		
		
		if(null != convertView) {
			vg = (ViewGroup) convertView;
		}
		
		if(null == vg) {
			vg = (ViewGroup) LayoutInflater.from(mContext).inflate(
					R.layout.download_list_item, null);
		}
		
		View check = vg.findViewById(R.id.download_item_check);
		check.setVisibility(mIsInEditMode ? View.VISIBLE : View.GONE);
		
		DownloadItem item = mGroupItems.get(groupPosition).get(childPosition);
		TextView name = (TextView) 
			vg.findViewById(R.id.download_listitem_name);
		name.setText(item.name());
		
		TextView size = (TextView)
			vg.findViewById(R.id.download_listitem_size);
		size.setText(String.format("%d KB/%d KB", 
			item.size() / 1024, item.progress() / 1024));
		
		TextView speed = (TextView)
			vg.findViewById(R.id.download_listitem_speed);
		if(item.status() == DownloadItem.Status.FINISHED) {
			speed.setVisibility(View.INVISIBLE);
		} else if(item.status() == DownloadItem.Status.PAUSED){
			speed.setText(R.string.paused);
		} else {
			speed.setText(String.format("%d KB/S", item.downloadSpeed()));
		}
		
		ProgressBar pb = (ProgressBar)
			vg.findViewById(R.id.download_progressbar);
		pb.setMax(100);
		pb.setProgress((int)item.progress() * 100 / (int)item.size());
			
		if(item.status() == DownloadItem.Status.PAUSED) {
			int color = mContext.getResources().getColor(
				R.color.progressbar_pause_color);
			pb.setBackgroundColor(color);
		} else if(item.status() == DownloadItem.Status.FINISHED) {
			
			ImageView iv = (ImageView) 
				vg.findViewById(R.id.download_list_item_icon);
			Drawable icon = MimeManager.instance().getDrawable(mContext, 
				item.localFilePath());
			
			if(null != icon) {
				iv.setImageDrawable(icon);
			}
		}
		
				
		return vg;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(null != mGroupItems) {
			if(groupPosition >= 0 && groupPosition < mGroupItems.size()) {
				return mGroupItems.get(groupPosition).size();
			}
		}
		
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub		
		return mGroupNames.length;
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, 
		View convertView, ViewGroup parent) {
						
		ViewGroup vg = null;
		
		if(null != convertView) {
			vg = (ViewGroup) convertView;
		}
		
		if(null == vg) {
			vg = (ViewGroup) LayoutInflater.from(mContext).inflate(
					R.layout.download_group_item, null);
		}
		
		TextView titleView = (TextView) 
			vg.findViewById(R.id.download_group_item_text);
		
		Resources rc = mContext.getResources();
		String title = String.format(
			rc.getString(mGroupNames[groupPosition], 
			mGroupItems.get(groupPosition).size()));
		titleView.setText(title);

		ImageView arrowView = (ImageView)
			vg.findViewById(R.id.download_group_item_arrow);
		
		arrowView.setImageResource(
			isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);
		
		return vg;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
