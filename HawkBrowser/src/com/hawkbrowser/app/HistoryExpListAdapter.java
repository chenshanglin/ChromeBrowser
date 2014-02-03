package com.hawkbrowser.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hawkbrowser.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryExpListAdapter extends BaseExpandableListAdapter
	implements ExpandableListView.OnChildClickListener {

	private List< List<History.Item> > mGroupItems;
	private History mHistory;
	private BookmarkActivity mContext;
	
	private static final int[] mGroupNames = {
		R.string.history_today,
		R.string.history_yestory,
		R.string.history_longago
	};
	
	public HistoryExpListAdapter(BookmarkActivity context, History history) { 
		
		mContext = context;
		mHistory = history;
		
		mGroupItems = new ArrayList< List<History.Item> >();
		ArrayList<History.Item> todayItems = new ArrayList<History.Item>();
		ArrayList<History.Item> yesterdayItems = new ArrayList<History.Item>();
		ArrayList<History.Item> longAgoItems = new ArrayList<History.Item>();
		
		List<History.Item> allItems = mHistory.getHistory(null, null);
				
		if(null != allItems) {
			int yearDayToday = 
				Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
						
			for(History.Item item : allItems) {
				
				int yearDay = item.time().get(Calendar.DAY_OF_YEAR);
				Log.d("History", String.format("today: %d; item day: %d", 
					yearDayToday, yearDay));
				
				if(yearDay == yearDayToday) {
					todayItems.add(item);
				} else if(yearDay == yearDayToday - 1) {
					yesterdayItems.add(item);
				} else {
					longAgoItems.add(item);
				}
			}
		}

		mGroupItems.add(todayItems);
		mGroupItems.add(yesterdayItems);
		mGroupItems.add(longAgoItems);
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, 
		View v, int groupPosition, int childPosition, long id) {
				
		History.Item item = mGroupItems.get(groupPosition).get(childPosition);
		mContext.onHistoryItemClicked(item);
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
		
		TextView v = null;
		
		
		if(null != convertView) {
			v = (TextView) convertView;
		}
		
		if(null == v) {
			v = (TextView) LayoutInflater.from(mContext).inflate(
					R.layout.history_list_item, null);
		}
		
		History.Item item = mGroupItems.get(groupPosition).get(childPosition);
		v.setText(item.title() + "\n" + item.url());
		
		return v;
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
					R.layout.history_group_item, null);
		}
		
		TextView titleView = (TextView) 
			vg.findViewById(R.id.history_group_item_date);
		
		Resources rc = mContext.getResources();
		String title = String.format(
			rc.getString(mGroupNames[groupPosition], 
			mGroupItems.get(groupPosition).size()));
		titleView.setText(title);

		ImageView arrowView = (ImageView)
			vg.findViewById(R.id.history_group_item_arrow);
		
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
