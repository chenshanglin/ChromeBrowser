package com.hawkbrowser.base;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Tree<T> {
	
	private Node<T> mRoot;
	
	public interface FindMatcher {
		boolean isMatch(Object l, Object r);
	}
	
	public interface Iterator {
		void iterate(Object l, Object r);
	}
	
	public Tree(T rootData) {
		mRoot = new Node<T>(rootData);
	}
	
	public static class Node<T> {
		
		private T mData;
		private Node<T> mParent;
		private List<Node<T>> mChildren;
		
		public Node(T data) {
			mData = data;
			mParent = null;
		}
		
		public Node(T data, Node<T> parent) {
			mData = data;
			mParent = parent;
			mChildren = null;
		}
		
		public List<Node<T>> children() {
			return mChildren;
		}
		
		public T data() {
			return mData;
		}
	}
	
	public Node<T> root() {
		return mRoot;
	}
	
	public Node<T> find(T data) {
		return find(mRoot, data);
	}
	
	public void iterate(Object arg, Iterator iter) {
		if(null != iter) {
			iterate(mRoot, arg, iter);
		}
	}
	
	public void iterate(Node<T> node, Object arg, Iterator iter) {	
		iter.iterate(node.mData, arg);
				
		if(null != node.mChildren) {
			for(Node<T> child : node.mChildren) {
				iterate(child, arg, iter);
			}
		}
	}
	
	public Node<T> find(T data, FindMatcher matcher) {
		return find(mRoot, data, matcher);
	}
	
	public boolean add(Node<T> parent, T data) {
		
		Log.d("Tree", String.format("parent is %s; data is %s", 
			null == parent ? "null" : parent.toString(), 
			null == data ? "null" : data.toString()));
		
		if(null == data || null != find(data)) {
			return false;
		}
		
		if(null == parent) {
			parent = mRoot;
		}
		
		if(null == parent.mChildren) {
			parent.mChildren = new ArrayList<Node<T>>();
		}
		
		Log.d("Tree", String.format("add data: %s", data.toString()));
		
		parent.mChildren.add(new Node<T>(data));
		
		return true;
	}
	
	public boolean remove(Node<T> node) {
		if(null == node.mParent) {
			return false;
		}
		
		node.mParent.mChildren.remove(node);
		return true;
	}
	
	private Node<T> find(Node<T> node, T data, FindMatcher matcher) {
		
		if(matcher.isMatch(node.mData, data)) {
			return node;
		}
		
		if(null != node.mChildren) {
			for(Node<T> child : node.mChildren) {
				if(null != find(child, data, matcher)) {
					return child;
				}
			}
		}
		
		return null;
	}
		
	private Node<T> find(Node<T> node, T data) {
				
		if((null != node.mData) && node.mData.equals(data)) {
			return node;
		}
		
		if(null != node.mChildren) {
			for(Node<T> child : node.mChildren) {
				if(null != find(child, data)) {
					return child;
				}
			}
		}
		
		return null;
	}
	
}
