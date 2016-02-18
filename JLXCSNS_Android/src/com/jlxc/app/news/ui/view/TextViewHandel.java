package com.jlxc.app.news.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.jlxc.app.base.ui.view.CustomListViewDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.jlxc.app.base.utils.ToastUtil;
import com.lidroid.xutils.view.annotation.event.OnTouch;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.widget.TextView;

public class TextViewHandel {

	private static Context mContext;
	private static String mContent;

	public TextViewHandel(Context context, String str) {
		mContext = context;
		mContent = str;
	}

	private class NoLineClickSpan extends ClickableSpan {

		private String mUrl;

		public NoLineClickSpan(String text) {
			super();
			this.mUrl = text;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			// 设置链接的文本颜色
			ds.setColor(ds.linkColor);
			// 去掉下划线
			ds.setUnderlineText(false);
		}

		@Override
		public void onClick(View widget) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(mUrl);
			intent.setData(content_url);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}
	}

	//
	public static OnLongClickListener getLongClickListener(
			final Context context, final String str) {
		return new OnLongClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			@Override
			public boolean onLongClick(View arg0) {
				List<String> menuList = new ArrayList<String>();
				menuList.add("复制内容");
				final CustomListViewDialog downDialog = new CustomListViewDialog(
						context, menuList);
				downDialog.setClickCallBack(new ClickCallBack() {

					@Override
					public void Onclick(View view, int which) {
						//
						ClipboardManager clipboard = (ClipboardManager) context
								.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setPrimaryClip(ClipData.newPlainText(null,
								str));
						downDialog.dismiss();
						ToastUtil.show(context, "已复制");
					}
				});
				if (null != downDialog && !downDialog.isShowing()) {
					downDialog.show();
				}

				return false;
			}
		};
	}

	public void setTextContent(TextView tv, final String content) {
		SpannableString spStr = new SpannableString(content);
		int end = content.length();
		URLSpan[] urls = spStr.getSpans(0, end, URLSpan.class);
		// 循环把链接发过去
		for (URLSpan url : urls) {
			NoLineClickSpan myURLSpan = new NoLineClickSpan(url.getURL());
			spStr.setSpan(myURLSpan, spStr.getSpanStart(url),
					spStr.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		tv.append(spStr);
		tv.setMovementMethod(LinkMovementMethod.getInstance());

		//
		tv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				CharSequence text = content;
				if (text != null && text instanceof Spannable) {
					return true;
				}
				return false;
			}
		});
	}
}