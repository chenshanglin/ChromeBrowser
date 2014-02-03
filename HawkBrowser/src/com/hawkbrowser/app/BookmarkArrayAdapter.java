package com.hawkbrowser.app;

import java.util.List;

import com.hawkbrowser.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookmarkArrayAdapter extends BaseAdapter
	implements AdapterView.OnItemClickListener {

	private List<Bookmark.Item> mItems;
	private LayoutInflater mInflater;
	private BookmarkActivity mContext;
	
	public BookmarkArrayAdapter(BookmarkActivity context, 
			List<Bookmark.Item> items) {
				
		mItems = items;
		mContext = context;
		mInflater = (LayoutInflater) 
			mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, 
			View view, int position, long id) {
		mContext.onBookmarkItemClicked(mItems.get(position));
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}
	
	@Override
	public Object getItem(int position) {
		if(position > 0 && position < mItems.size()) {
			return mItems.get(position);
		}
		
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		if(position > 0 && position < mItems.size()) {
			return mItems.get(position).id();
		}
		
		return 0;
	}
	
	@Override
	public View getView(int position, 
			View convertView, ViewGroup parent) {
		
		Log.d("Bookmark", String.format("getView: %d", position));
		
		if(null != convertView) {
			return convertView;
		}
				
		ViewGroup itemView = (ViewGroup) 
			mInflater.inflate(R.layout.bookmark_list_item, null);
		
		Bookmark.Item bi = mItems.get(position);
		String name = null;
		
		if(bi.type() == Bookmark.Type.Folder) {
			ImageView icon = (ImageView)
				itemView.findViewById(R.id.bookmark_list_item_icon);
			icon.setImageResource(R.drawable.icon_folder);
			name = bi.title();
		} else {
			name = bi.title() + "\n" + bi.url();
		}
		
		Log.d("Bookmark", 
			String.format("getView, item name: %s", name));
		
		TextView titleView = (TextView) 
				itemView.findViewById(R.id.bookmark_list_item_title);
		titleView.setText(name);
		
		return itemView;
	}	
}
