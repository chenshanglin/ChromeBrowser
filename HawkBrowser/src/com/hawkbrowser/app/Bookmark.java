package com.hawkbrowser.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.hawkbrowser.base.Tree;
import com.hawkbrowser.util.CommonUtil;


public class Bookmark {

	final private String BOOKMARK_FILE_NAME = "bookmark.dat";
	
	private Tree<Item> mTree;
	private int mCurrentId;
	private Context mContext;
	
	public static enum Type {
		Folder, Link
	}
	
	
	public static class Item implements Serializable  {
		transient private int mId;
		private String mName;
		private String mFolderName;
		private String mUrl;
		private Type mType;
		
		public Item(String name, String url, Type type) {
			mName = name;
			mUrl = url;
			mType = type;
			mId = 0;
		}
		
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			
			if(!(obj instanceof Item)) {
				return false;
			}
			
			Item r = (Item) obj;
			
			Log.d("Bookmark", String.format("this: %s; right: %s",
					toString(), obj.toString()));
			
			return mUrl.equals(r.mUrl);
		}
		
		public int hashCode() {
			return mId;
		}
	
		public String title() {
			return mName;
		}
		
		public String url() {
			return mUrl;
		}
		
		public Type type() {
			return mType;
		}
		
		public int id() {
			return mId;
		}
		
		@Override
		public String toString() {
			return String.format("id: %d, type: %s, name: %s, " +
				"folderName: %s, url %s", mId, mType, mName, mFolderName, mUrl
				);
		}
	}
	
	public Bookmark(Context context) {
		mCurrentId = 0;
		mContext = context;
	
		Load();
	}
	
	public List<Item> getChildren(Item parent) {
		
		Tree.Node<Item> node = null;
		
		if(null == parent) {
			node = mTree.root();
		} else {
			node = mTree.find(parent);
		}
			
		ArrayList<Item> items = new ArrayList<Item>();
		
		if((null != node) && (null != node.children())) {
			for(Tree.Node<Item> child : node.children()) {
				items.add(child.data());
			}
		}
		
		return items;
	}
	
	public boolean Add(Item item) {
		Tree.Node<Item> parent = mTree.find(item, 
				new Tree.FindMatcher() {
					@Override
					public boolean isMatch(Object l, Object r) {
						if((null == l) && (null == r)) {
							return true;
						}
						
						if((null != l) && (null != r)) {
							Item li = (Item)l;
							Item ri = (Item)r;
							return (Type.Folder == li.mType) 
								&& (li.mName == ri.mFolderName);
						}
						
						return false;
					}
				});
		
		return mTree.add(parent, item);
	}
	
	private void Load() {
		
		mTree = new Tree<Item>(null);
		
		FileInputStream fileInputStream = null;
		ObjectInputStream objInputStream = null;
		
		try {
			File file = new File(mContext.getFilesDir(), BOOKMARK_FILE_NAME);
			
			if(file.isFile()) {
				fileInputStream = new FileInputStream(file);
				objInputStream = new ObjectInputStream(fileInputStream);
				
				do {
					Item item = (Item) objInputStream.readObject();
					
					if(null != item) {
						item.mId = ++mCurrentId;
						Log.d("Bookmark", 
								String.format("Read Bookmark: %s", item.toString()));
						Add(item);
					} else {
						break;
					}
				} while(true);
			}
		} catch(Exception e) {

			Log.e("Bookmark", CommonUtil.getExceptionStackTrace(e));
			
			try {
				if(null != objInputStream) {
					objInputStream.close();
					objInputStream = null;
					fileInputStream = null;
				} else if(null != fileInputStream) {
					fileInputStream.close();
					fileInputStream = null;
				}
			} catch(Exception eInner) {
				
			}
		} 
	}
	
	class BookmarkTreeSerializer implements Tree.Iterator {
		
		private ObjectOutputStream mOutStream = null;
		private FileOutputStream mFileOutStream = null;
		
		public BookmarkTreeSerializer() {
			try {
				File file = new File(mContext.getFilesDir(), 
						BOOKMARK_FILE_NAME);
				mFileOutStream = new FileOutputStream(file);
				mOutStream = new ObjectOutputStream(mFileOutStream);
			} catch(Exception e) {
				Log.e("Bookmark", CommonUtil.getExceptionStackTrace(e));
			} 
		}
		
		public void finalize() {
			try {
				if(null != mOutStream) {
					mOutStream.close();
					mOutStream = null;
					mFileOutStream = null;
				} else if(null != mFileOutStream) {
					mFileOutStream.close();
					mFileOutStream = null;
				}
			} catch(Exception e) {
				
			}
		}
		
		public ObjectOutputStream getStream() {
			return mOutStream;
		}
		
		@Override
		public void iterate(Object l, Object r) {
			
			ObjectOutputStream outStream = (ObjectOutputStream) r;
			
			try {
				Item item = (Item)l;
				
				if(null != item) {
					outStream.writeObject(item);
					Log.d("Bookmark", 
						String.format("Serialize Bookmark: %s", item.toString()));
				}
			} catch(Exception e) {
				Log.e("Bookmark", CommonUtil.getExceptionStackTrace(e));
			} 
		}
	}
	
	public void Flush() {
		BookmarkTreeSerializer ts = new BookmarkTreeSerializer();
		ObjectOutputStream outStream = ts.getStream();
		
		if(null != outStream) {
			mTree.iterate(outStream, ts);
		}
	}
}
