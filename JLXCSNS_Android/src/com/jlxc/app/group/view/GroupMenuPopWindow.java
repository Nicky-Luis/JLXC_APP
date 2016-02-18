package com.jlxc.app.group.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.group.model.GroupCategoryModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 左侧group菜单popupwindow
 * */
@SuppressLint("InflateParams")
public class GroupMenuPopWindow extends PopupWindow {

	// 加载图片
	private DisplayImageOptions options;
	// 布局
	private View conentView;
	// 频道类别list
	private ListView groupTypeListView;
	private HelloHaAdapter<GroupCategoryModel> categoryAdapter;
	// 类型list
	private List<GroupCategoryModel> categoryModels = new ArrayList<GroupCategoryModel>();
	// onclick
	private CategorySelectListener listener;
	// 记录上一次可见的第一条item
	private int lastFirstVisibleItem = 0;

	public GroupMenuPopWindow(final Context context) {

		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.main_clear)
				.showImageOnFail(R.color.main_clear).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater.inflate(R.layout.popup_window_group_menu, null);
		// listview
		groupTypeListView = (ListView) conentView
				.findViewById(R.id.category_list_view);
		// adapter
		categoryAdapter = new HelloHaAdapter<GroupCategoryModel>(context,
				R.layout.category_list_adapter) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					GroupCategoryModel item) {
				helper.setText(R.id.category_text_view, item.getCategory_name());
				helper.setText(R.id.category_detail_text_view,
						item.getCategory_desc());
				// 设置背景颜色
				helper.setBackgroundColor(R.id.layout_item_background,
						item.getBackgroundValue());
				// 显示背景图片
				// if (item.getCategory_cover().equals("NULL")) {
				// helper.setImageResource(R.id.category_cover_image_view,
				// R.drawable.group_main_background);
				// } else {
				// ImageView imageView = helper
				// .getView(R.id.category_cover_image_view);
				// ImageLoader.getInstance().displayImage(
				// JLXCConst.ROOT_PATH + item.getCategory_cover(),
				// imageView, options);
				// }
			}
		};
		groupTypeListView.setAdapter(categoryAdapter);
		// 点击
		groupTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (null != listener) {
					listener.select(categoryModels.get(position));
				}
			}
		});
		// 滚动事件监听
		groupTypeListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (lastFirstVisibleItem != firstVisibleItem
						&& null != listener) {
					listener.onFirstVisibleItemChange(firstVisibleItem);
					lastFirstVisibleItem = firstVisibleItem;
				}
			}
		});

		// 设置PopupWindow的View0
		this.setContentView(conentView);
		// 设置窗体的尺寸
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 背景变暗
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.anim_group_menu);
	}

	// 设置内容
	public void setCategoryList(List<GroupCategoryModel> categoryModels) {
		this.categoryModels = categoryModels;
		categoryAdapter.replaceAll(this.categoryModels);
	}

	/**
	 * 显示窗体
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
			if (null != listener) {
				// 第一次显示时修改title背景
				listener.onFirstVisibleItemChange(0);
			}
		} else {
			this.dismiss();
		}
	}

	// 监听器
	public interface CategorySelectListener {
		// 点击item选择
		public void select(GroupCategoryModel model);

		// 滚动listview时
		public void onFirstVisibleItemChange(int index);
	}

	public CategorySelectListener getListener() {
		return listener;
	}

	public void setListener(CategorySelectListener listener) {
		this.listener = listener;
	}
}
