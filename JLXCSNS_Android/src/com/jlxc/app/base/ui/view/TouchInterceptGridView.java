package com.jlxc.app.base.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * @ClassName TouchInterceptGridView
 * @Description 保证gridview的外层view可以处理事件，禁止事件传递进gridview的child view
 * @author devlei
 * @date Dec 2, 2014 5:23:23 PM
 *
 */
public class TouchInterceptGridView extends GridView {

        public TouchInterceptGridView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
                // TODO Auto-generated constructor stub
        }

        public TouchInterceptGridView(Context context, AttributeSet attrs) {
                super(context, attrs);
                // TODO Auto-generated constructor stub
        }

        public TouchInterceptGridView(Context context) {
                super(context);
                // TODO Auto-generated constructor stub
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;// true 拦截事件自己处理，禁止向下传递
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
                return false;// false 自己不处理此次事件以及后续的事件，那么向上传递给外层view
        }

}