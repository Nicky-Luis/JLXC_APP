package com.jlxc.app.message.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jlxc.app.base.app.JLXCApplication;
import com.jlxc.app.base.manager.ActivityManager;
import com.jlxc.app.base.ui.view.gallery.imageloader.GalleyActivity;
import com.jlxc.app.base.utils.FileUtil;
import com.jlxc.app.base.utils.JLXCUtils;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

public class PhotoCollectionsProvider extends InputProvider.ExtendProvider {
    HandlerThread mWorkThread;

    Handler mUploadHandler;

    private RongContext mContext;

    public PhotoCollectionsProvider(RongContext context) {
        super(context);
        this.mContext = context;
        mWorkThread = new HandlerThread("JLXC");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());

    }

    @Override
    public Drawable obtainPluginDrawable(Context arg0) {
        // TODO Auto-generated method stub
        return arg0.getResources().getDrawable(R.drawable.rc_ic_picture);
    }

    @Override
    public CharSequence obtainPluginTitle(Context arg0) {
        return "相册";
    }

    @Override
    public void onPluginClick(View arg0) {
        // TODO Auto-generated method stub
        // 点击跳转至图片选择界面

        Intent intent = new Intent(mContext, GalleyActivity.class);
        intent.putExtra(GalleyActivity.INTENT_KEY_SELECTED_COUNT,0);
        intent.putExtra(GalleyActivity.INTENT_KEY_ONE, true);
        startActivityForResult(intent, 86);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 根据选择完毕的图片返回值，直接上传文件
        if (requestCode == 86 && data != null) {

        	DisplayMetrics dm = new DisplayMetrics();
    		ActivityManager.getInstence().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    		int[] screenSize = { dm.widthPixels, dm.heightPixels };
        	String tmpImageName = JLXCUtils.getPhotoFileName() + "";
        	@SuppressWarnings("unchecked")
			List<String> resultList = (List<String>) data.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
			// 循环处理图片
			for (String fileRealPath : resultList) {
				// 用户id+时间戳
				if (fileRealPath != null&& FileUtil.tempToLocalPath(fileRealPath, tmpImageName,screenSize[0], screenSize[1])) {
					tmpImageName = "file://" + FileUtil.BIG_IMAGE_PATH + tmpImageName;
					Uri pathUri = Uri.parse(tmpImageName);
                    mUploadHandler.post(new MyRunnable(pathUri));
					break;
				}
			}
        	
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 用于显示文件的异步线程
     *
     * @ClassName: MyRunnable
     * @Description: 用于显示文件的异步线程
     *
     */
    class MyRunnable implements Runnable {

        Uri mUri;

        public MyRunnable(Uri uri) {
            mUri = uri;
        }

        @Override
        public void run() {

            // 封装image类型的IM消息
            final ImageMessage content = ImageMessage.obtain(mUri, mUri);

            if (RongIM.getInstance() != null&& RongIM.getInstance().getRongIMClient() != null)
                RongIM.getInstance().getRongIMClient().sendImageMessage(getCurrentConversation().getConversationType(),getCurrentConversation().getTargetId(),content,null,null,new RongIMClient.SendImageMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode code) {

                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onProgress(Message message, int progress) {

                    }
                });

        }
    }


}
