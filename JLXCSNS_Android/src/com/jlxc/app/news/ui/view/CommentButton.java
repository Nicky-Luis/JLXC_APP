package com.jlxc.app.news.ui.view;

import com.jlxc.app.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentButton extends FrameLayout {

	// 背景图片
	private ImageView imageView;
	// 文字
	private TextView textView;

	public CommentButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CommentButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 将自定义组合控件的布局渲染成View
		View view = View.inflate(context,
				R.layout.custom_view_comment_btn_layout, this);
		// imageView = (ImageView)
		// view.findViewById(R.id.iv_comment_background);
		textView = (TextView) view.findViewById(R.id.tv_comment_content);
	}

	/** 设置点赞的状态 */
	public void setContent(int count) {
		//设置显示格式
		if (count >= 10000) {
			count = count / 10000;
			textView.setText("" + count + "W+");
			return;
		} else if (count >= 1000) {
			count = count / 1000;
			textView.setText("" + count + "K+");
			return;
		}else {
			textView.setText("" + count);
		}
		
		//设置数字显示的位置
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textView
				.getLayoutParams();
		if (count >= 1000) {
			params.rightMargin = 18;
		} else if (count >= 100) {
			params.rightMargin = 20;
		} else if (count >= 10) {
			params.rightMargin = 25;
		} else {
			params.rightMargin = 30;
		}
		textView.setLayoutParams(params);
	}
}
