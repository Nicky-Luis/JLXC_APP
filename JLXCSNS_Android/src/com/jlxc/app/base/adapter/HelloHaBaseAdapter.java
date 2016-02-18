package com.jlxc.app.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * convert() implementation.<br/>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 * 
 * @param <T>
 *            The type of the items in the list.
 */
public abstract class HelloHaBaseAdapter<T, H extends HelloHaBaseAdapterHelper>
		extends BaseAdapter {

	protected static final String TAG = HelloHaBaseAdapter.class
			.getSimpleName();
	protected final Context context;
	protected int layoutResId;
	protected final List<T> data;
	protected MultiItemTypeSupport<T> mMultiItemSupport;
	private boolean itemClickEnable = true;

	/**
	 * Create a QuickAdapter.
	 * 
	 * @param context
	 *            The context.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 */
	public HelloHaBaseAdapter(Context context, int layoutResId) {
		this(context, layoutResId, null);
	}

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * 
	 * @param context
	 *            The context.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public HelloHaBaseAdapter(Context context, int layoutResId, List<T> data) {
		this.data = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
		this.context = context;
		this.layoutResId = layoutResId;
	}

	// 设置items是否可点击
	public void setItemsClickEnable(boolean bool) {
		itemClickEnable = bool;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return itemClickEnable;
	}

	@Override
	public boolean isEnabled(int position) {
		// return position < data.size();
		return itemClickEnable;
	}

	public HelloHaBaseAdapter(Context context, List<T> data,
			MultiItemTypeSupport<T> multiItemSupport) {
		this.mMultiItemSupport = multiItemSupport;
		this.data = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		if (position >= data.size())
			return null;
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		if (mMultiItemSupport != null)
			return mMultiItemSupport.getViewTypeCount();
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		if (mMultiItemSupport != null)
			return mMultiItemSupport.getItemViewType(position,
					data.get(position));
		return position >= data.size() ? 0 : 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final H helper = getAdapterHelper(position, convertView, parent);
		T item = getItem(position);
		helper.setAssociatedObject(item);
		convert(helper, item);
		return helper.getView();

	}

	/**
	 * 添加元素到末尾
	 * */
	public void add(T elem) {
		data.add(elem);
		notifyDataSetChanged();
	}

	/**
	 * 添加元素到首部
	 * */
	public void addToFirst(T elem) {
		data.add(0, elem);
		notifyDataSetChanged();
	}

	/**
	 * 添加一系列元素
	 * */
	public void addAll(List<T> elem) {
		data.addAll(elem);
		notifyDataSetChanged();
	}

	/**
	 * 添加一系列元素到首部
	 * */
	public void addAllToFirst(List<T> elem) {
		data.addAll(0, elem);
		notifyDataSetChanged();
	}

	/**
	 * 替换元素
	 * */
	public void set(T oldElem, T newElem) {
		set(data.indexOf(oldElem), newElem);
	}

	/**
	 * 设置某一元素的值
	 * */
	public void set(int index, T elem) {
		data.set(index, elem);
		notifyDataSetChanged();
	}
	
	/**
	 * 插入某一元素
	 * */
	public void insert(int index, T elem) {
		data.add(index, elem);
		notifyDataSetChanged();
	}

	/**
	 * 移除某一元素
	 */
	public void remove(T elem) {
		data.remove(elem);
		notifyDataSetChanged();
	}

	/**
	 * 移除某一位置的元素
	 */
	public void remove(int index) {
		data.remove(index);
		notifyDataSetChanged();
	}

	/**
	 * 替换所有元素
	 * */
	public void replaceAll(List<T> elem) {
		data.clear();
		data.addAll(elem);
		notifyDataSetChanged();
	}

	public boolean contains(T elem) {
		return data.contains(elem);
	}

	/** Clear data list */
	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}

	/**
	 * Implement this method and use the helper to adapt the view to the given
	 * item.
	 * 
	 * @param helper
	 *            A fully initialized helper.
	 * @param item
	 *            The item that needs to be displayed.
	 */
	protected abstract void convert(H helper, T item);

	/**
	 * You can override this method to use a custom BaseAdapterHelper in order
	 * to fit your needs
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param convertView
	 *            The old view to reuse, if possible. Note: You should check
	 *            that this view is non-null and of an appropriate type before
	 *            using. If it is not possible to convert this view to display
	 *            the correct data, this method can create a new view.
	 *            Heterogeneous lists can specify their number of view types, so
	 *            that this View is always of the right type (see
	 *            {@link #getViewTypeCount()} and {@link #getItemViewType(int)}
	 *            ).
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return An instance of BaseAdapterHelper
	 */
	protected abstract H getAdapterHelper(int position, View convertView,
			ViewGroup parent);

}
