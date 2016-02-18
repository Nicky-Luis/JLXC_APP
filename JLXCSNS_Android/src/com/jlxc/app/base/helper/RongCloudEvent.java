package com.jlxc.app.base.helper;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.manager.ActivityManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BigImgLookActivity;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.login.ui.activity.LaunchActivity;
import com.jlxc.app.login.ui.activity.LoginActivity;
import com.jlxc.app.message.helper.PhotoCollectionsProvider;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.message.ui.activity.ConversationActivity;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.SentMessageErrorCode;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.notification.PushNotificationMessage;

/**
 * Created by zhjchen on 1/29/15.
 */

/**
 * 融云SDK事件监听处理。
 * 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有：
 * 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。
 * 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。
 * 5、群组信息提供者：GetGroupInfoProvider。
 * 蓉c
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。
 * 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 * 10、会话列表界面操作的监听器：ConversationListBehaviorListener。
 */
public final class RongCloudEvent implements RongIMClient.OnReceiveMessageListener, RongIM.OnSendMessageListener,
        RongIM.UserInfoProvider, RongIM.GroupInfoProvider, RongIM.ConversationBehaviorListener,
        RongIMClient.ConnectionStatusListener, RongIM.LocationProvider, RongIMClient.OnReceivePushMessageListener, RongIM.ConversationListBehaviorListener {

    private static final String TAG = "PushReceiver";

    private static RongCloudEvent mRongCloudInstance;

    private Context mContext;
//    private UserInfosDao mUserInfosDao;
//    private AbstractHttpRequest<User> getUserInfoByUserIdHttpRequest;

    public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	/**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContext = context;
        initDefaultListener();
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {
        RongIM.setUserInfoProvider(this, true);//设置用户信息提供者。
        RongIM.setGroupInfoProvider(this, true);//设置群组信息提供者。
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
        RongIM.setConversationListBehaviorListener(this);
        RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
        RongIM.setOnReceivePushMessageListener(this);
        RongIM.setOnReceiveMessageListener(this);
//        RongIM.setPushMessageBehaviorListener(this);//自定义 push 通知。
    }

    /*
     * 连接成功注册。
     * <p/>
     * 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener() {
//		RongIMClientWrapper.setOnReceiveMessageListener(this);//设置消息接收监听器。
//        RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.
//		RongIMClientWrapper.setConnectionStatusListener(this);//设置连接状态监听器。
//		RongIMClientWrapper.setOnReceivePushMessageListener(this);//设置通知监听器
    	RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.
//        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);//设置连接状态监听器。
        //扩展功能自定义
        InputProvider.ExtendProvider[] provider = {
        		new PhotoCollectionsProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
        };
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
//        RongIM.getInstance().setPrimaryInputProvider(new InputTestProvider((RongContext) mContext));
    }
    
    /**
     * 自定义 push 通知。
     *
     * @param msg
     * @return
     */
    private NotificationManager mNotificationManager;
    @Override
    public boolean onReceivePushMessage(PushNotificationMessage msg) {
        Log.i("MainTabActivity", "onReceived-onPushMessageArrive:" + msg.getContent());

//        PushNotificationManager.getInstance().onReceivePush(msg);
        
		long newWhen = System.currentTimeMillis();
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		int iconId = R.drawable.icon;
		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setClass(mContext.getApplicationContext(), LaunchActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification();
		notification.when = newWhen;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.defaults = Notification.DEFAULT_SOUND;
		notification.icon = iconId;
		notification.tickerText = "您有一条新消息";
		notification.setLatestEventInfo(mContext, msg.getTargetUserName(), "给您发了一条消息", contentIntent);
		// notification的id使用发送者的id
		mNotificationManager.notify(0, notification);
        
//        PushNotificationManager.getInstance().onReceivePush(msg);

//        Intent intent = new Intent();
//        Uri uri;
//
//        intent.setAction(Intent.ACTION_VIEW);
//
//        Conversation.ConversationType conversationType = msg.getConversationType();
//
//        uri = Uri.parse("rong://" + RongContext.getInstance().getPackageName()).buildUpon().appendPath("conversationlist").build();
//        intent.setData(uri);
//        Log.d(TAG, "onPushMessageArrive-url:" + uri.toString());
//
//        Notification notification=null;
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(RongContext.getInstance(), 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        if (android.os.Build.VERSION.SDK_INT < 11) {
//            notification = new Notification(RongContext.getInstance().getApplicationInfo().icon, "自定义 notification", System.currentTimeMillis());
//
//            notification.setLatestEventInfo(RongContext.getInstance(), "自定义 title", "这是 Content:"+msg.getObjectName(), pendingIntent);
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            notification.defaults = Notification.DEFAULT_SOUND;
//        } else {
//             notification = new Notification.Builder(RongContext.getInstance())
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setTicker("您收到一条消息")
//                    .setContentTitle("您收到一条消息")
//                    .setContentText("这是 Content:"+msg.getObjectName())
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true)
//                    .setDefaults(Notification.DEFAULT_ALL).build();
//        }
//
//        NotificationManager nm = (NotificationManager) RongContext.getInstance().getSystemService(RongContext.getInstance().NOTIFICATION_SERVICE);
//
//        nm.notify(0, notification);

        return true;
    }

//    private Bitmap getAppIcon() {
//        BitmapDrawable bitmapDrawable;
//        Bitmap appIcon;
//        bitmapDrawable = (BitmapDrawable) RongContext.getInstance().getApplicationInfo().loadIcon(RongContext.getInstance().getPackageManager());
//        appIcon = bitmapDrawable.getBitmap();
//        return appIcon;
//    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     *
     * @param message 接收到的消息的实体信息。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(Message message, int left) {
        
		//顶部更新
		Intent messageIntent = new Intent(JLXCConst.BROADCAST_MESSAGE_REFRESH);
		mContext.sendBroadcast(messageIntent);
		//底部更新
		Intent tabIntent = new Intent(JLXCConst.BROADCAST_TAB_BADGE);
		mContext.sendBroadcast(tabIntent);
		//如果当前页面是会话页面 不显示通知
		Activity currentActivity = ActivityManager.getInstence().currentActivity();
		if (null != currentActivity && currentActivity instanceof ConversationActivity) {
			return false;
		}
		
		long newWhen = System.currentTimeMillis();
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		int iconId = R.drawable.icon;
		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setClass(mContext.getApplicationContext(), LaunchActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification();
		notification.when = newWhen;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.defaults = Notification.DEFAULT_SOUND;
		notification.icon = iconId;
		notification.tickerText = "您有一条新消息";
		//名字
		String titleName = "某位同学";
		String targetId = message.getTargetId();
		IMModel imModel = IMModel.findByGroupId(targetId);
		if (null != imModel.getTitle() && imModel.getTitle().length()>0) {
			titleName = imModel.getTitle();
		}
		notification.setLatestEventInfo(mContext, titleName, "给您发了一条消息", contentIntent);
		// notification的id使用发送者的id
		mNotificationManager.notify(0, notification);		

//        if (messageContent instanceof TextMessage) {//文本消息
//            TextMessage textMessage = (TextMessage) messageContent;
//            Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
//        } else if (messageContent instanceof ImageMessage) {//图片消息
//            ImageMessage imageMessage = (ImageMessage) messageContent;
//            Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
//        } else if (messageContent instanceof VoiceMessage) {//语音消息
//            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
//            Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
//        } else if (messageContent instanceof RichContentMessage) {//图文消息
//            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
//            Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
//        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
//            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
//            Log.d(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
//        } else if (messageContent instanceof DeAgreedFriendRequestMessage) {//好友添加成功消息
//            DeAgreedFriendRequestMessage deAgreedFriendRequestMessage = (DeAgreedFriendRequestMessage) messageContent;
//            Log.d(TAG, "onReceived-deAgreedFriendRequestMessage:" + deAgreedFriendRequestMessage.getMessage());
//            receiveAgreeSuccess(deAgreedFriendRequestMessage);
//        } else if (messageContent instanceof ContactNotificationMessage) {//好友添加消息
//            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
//            Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
//            Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());
////            RongIM.getInstance().getRongIMClient().deleteMessages(new int[]{message.getMessageId()});
////            if(DemoContext.getInstance()!=null) {
////                RongIM.getInstance().getRongIMClient().removeConversation(Conversation.ConversationType.SYSTEM, "10000");
////                String targetname = DemoContext.getInstance().getUserNameByUserId(contactContentMessage.getSourceUserId());
////                RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.SYSTEM, "10000", contactContentMessage.getSourceUserId(), contactContentMessage, null);
////
////            }
//            Intent in = new Intent();
//            in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
//            in.putExtra("rongCloud", contactContentMessage);
//            in.putExtra("has_message", true);
//            mContext.sendBroadcast(in);
//        } else {
//            Log.d(TAG, "onReceived-其他消息，自己来判断处理");
//        }

        return false;

    }

//    /**
//     * @param deAgreedFriendRequestMessage
//     */
//    private void receiveAgreeSuccess(DeAgreedFriendRequestMessage deAgreedFriendRequestMessage) {
//        ArrayList<UserInfo> friendreslist = new ArrayList<UserInfo>();
//        if (DemoContext.getInstance() != null) {
//            friendreslist = DemoContext.getInstance().getFriendList();
//            friendreslist.add(deAgreedFriendRequestMessage.getUserInfo());
//
////            DemoContext.getInstance().setFriends(friendreslist);
//        }
//        Intent in = new Intent();
//        in.setAction(MainActivity.ACTION_DMEO_AGREE_REQUEST);
//        in.putExtra("AGREE_REQUEST", true);
//        mContext.sendBroadcast(in);
//
//    }


    @Override
    public Message onSend(Message message) {
        return message;
    }

    /**
     * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message 消息。
     */
//    @Override
//    public void onSent(Message message) {
//
//        MessageContent messageContent = message.getContent();
//
//        if (messageContent instanceof TextMessage) {//文本消息
//            TextMessage textMessage = (TextMessage) messageContent;
//            Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
//        } else if (messageContent instanceof ImageMessage) {//图片消息
//            ImageMessage imageMessage = (ImageMessage) messageContent;
//            Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
//        } else if (messageContent instanceof VoiceMessage) {//语音消息
//            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
//            Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
//        } else if (messageContent instanceof RichContentMessage) {//图文消息
//            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
//            Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
//        } else {
//            Log.d(TAG, "onSent-其他消息，自己来判断处理");
//        }
//    }

    /**
     * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
     *
     * @param userId 用户 Id。
     * @return 用户信息，（注：由开发者提供用户信息）。
     */
    @Override
    public UserInfo getUserInfo(final String userId) {

//        /**
//         * demo 代码  开发者需替换成自己的代码。
//         */
//        mUserInfosDao = DBManager.getInstance(mContext).getDaoSession().getUserInfosDao();
//
//        QueryBuilder qb = mUserInfosDao.queryBuilder();
//        qb.where(UserInfosDao.Properties.Userid.eq(userId));
//        UserInfos userInfo = mUserInfosDao.queryBuilder().where(UserInfosDao.Properties.Userid.eq(userId)).unique();
//
//        if (userInfo == null && DemoContext.getInstance() != null) {
//            getUserInfoByUserIdHttpRequest = DemoContext.getInstance().getDemoApi().getUserInfoByUserId(userId, (ApiCallback<User>) this);
//        }
//
//        return DemoContext.getInstance().getUserInfoById(userId);
    
    	//这个不好使
    	Resources r = mContext.getResources();
    	Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
    		    + r.getResourcePackageName(R.drawable.ic_launcher) + "/"
    		    + r.getResourceTypeName(R.drawable.ic_launcher) + "/"
    		    + r.getResourceEntryName(R.drawable.ic_launcher));
    	
		UserModel userModel = UserManager.getInstance().getUser();
		String uidStr = JLXCConst.JLXC+userModel.getUid();
	
		Uri headuUri = uri;
		UserInfo userInfo = null; 
		//自己
		if (uidStr.equals(userId)) {
			//如果有头像
			headuUri = Uri.parse(JLXCConst.ATTACHMENT_ADDR+userModel.getHead_image());
			userInfo = new UserInfo(userId, userModel.getName(), headuUri);
			return userInfo;
		}
		//如果是好友
		final IMModel imModel = IMModel.findByGroupId(userId);
		Log.i("MainTabActivity", userId+" "+imModel);
		if (null != imModel) {
			headuUri = Uri.parse(JLXCConst.ATTACHMENT_ADDR+imModel.getAvatarPath());
			userInfo = new UserInfo(userId, imModel.getTitle(), headuUri);
		}
		
		//不管是谁都重新请求一次
		String path = JLXCConst.GET_IMAGE_AND_NAME+"?user_id="+userId.replace(JLXCConst.JLXC, "");
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				// TODO Auto-generated method stub
				super.onSuccess(jsonResponse, flag);
				if (jsonResponse.getInteger(JLXCConst.HTTP_STATUS) == JLXCConst.STATUS_SUCCESS) {
					JSONObject resultObject = jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT);
					//获取信息
					String name = resultObject.getString("name");
					String headImage = resultObject.getString("head_image");
					UserInfo userInfo = new UserInfo(userId, name, Uri.parse(JLXCConst.ATTACHMENT_ADDR+headImage));
					if (null != imModel) {
						imModel.setTitle(name);
						imModel.setAvatarPath(headImage);
						imModel.update();
					}else {
						IMModel newModel = new IMModel();
						newModel.setType(IMModel.ConversationType_PRIVATE);
						newModel.setTargetId(userId);
						newModel.setTitle(name);
						newModel.setAvatarPath(headImage);
						newModel.setIsNew(0);
						newModel.setIsRead(1);
						newModel.setCurrentState(IMModel.GroupNotAdd);
						newModel.setOwner(UserManager.getInstance().getUser().getUid());
						newModel.save();
					}
					//刷新信息
					RongIM.getInstance().refreshUserInfoCache(userInfo);
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, flag);
			}
		}, null));
			
//		Log.i("MainTabActivity", userInfo.getName());
		if (userInfo == null) {
			userInfo = new UserInfo(userId,"学僧",headuUri);
		}
		Log.i("MainTabActivity", userInfo.getName());
    	return userInfo;
    }


    /**
     * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
     *
     * @param groupId 群组 Id.
     * @return 群组信息，（注：由开发者提供群组信息）。
     */
    @Override
    public Group getGroupInfo(String groupId) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
//        if (DemoContext.getInstance().getGroupMap() == null)
//            return null;
//
//        return DemoContext.getInstance().getGroupMap().get(groupId);
    	return null;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user) {
    	
    	Intent intent = new Intent(context, OtherPersonalActivity.class);
    	intent.putExtra(OtherPersonalActivity.INTENT_KEY, JLXCUtils.stringToInt(user.getUserId().replace(JLXCConst.JLXC, "")));
    	context.startActivity(intent);
    	((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
//        if (user != null) {
//            Log.d("Begavior", conversationType.getName() + ":" + user.getName());
//            Intent in = new Intent(context, DePersonalDetailActivity.class);
//            in.putExtra("USER", user);
//            in.putExtra("SEARCH_USERID", user.getUserId());
//            context.startActivity(in);
//        }
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        //图片点击
        if (message.getContent() instanceof ImageMessage) {
        	ImageMessage imageMessage = (ImageMessage) message.getContent();
        	 Uri imageUri = (imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
        	 Intent intent = new Intent(context, BigImgLookActivity.class);
        	 intent.putExtra(BigImgLookActivity.INTENT_KEY, imageUri.toString());
        	 context.startActivity(intent);
        }
//        /**
//         * demo 代码  开发者需替换成自己的代码。
//         */
//        if (message.getContent() instanceof LocationMessage) {
//            Intent intent = new Intent(context, SOSOLocationActivity.class);
//            intent.putExtra("location", message.getContent());
//            context.startActivity(intent);
//        } else if (message.getContent() instanceof RichContentMessage) {
//            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();
//            Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());
//
//        } else if (message.getContent() instanceof ImageMessage) {
//            ImageMessage imageMessage = (ImageMessage) message.getContent();
//            Intent intent = new Intent(context, PhotoActivity.class);
//
//            intent.putExtra("photo", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
//            if (imageMessage.getThumUri() != null)
//                intent.putExtra("thumbnail", imageMessage.getThumUri());
//
//            context.startActivity(intent);
//        }
//
//        Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param status 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus status) {
        Log.d(TAG, "onChanged:" + status);
        if (status.getMessage().equals(ConnectionStatus.DISCONNECTED.getMessage())) {
        }
    }


    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context  上下文
     * @param callback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback callback) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
//        DemoContext.getInstance().setLastLocationCallback(callback);
//        context.startActivity(new Intent(context, SOSOLocationActivity.class));//SOSO地图
    }

    /**
     * 点击会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation conversation) {
    	
    	//启动聊天
    	RongIM.getInstance().startConversation(context, conversation.getConversationType(), conversation.getConversationTargetId(), conversation.getUIConversationTitle());
    	((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        return true;
    }
    
    /**
     * 长按会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 长按会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation conversation) {
        return false;
    }


//    @Override
//    public void onComplete(AbstractHttpRequest abstractHttpRequest, Object obj) {
////        if (getUserInfoByUserIdHttpRequest != null && getUserInfoByUserIdHttpRequest.equals(abstractHttpRequest)) {
////            if (obj instanceof User) {
////                final User user = (User) obj;
////                if (user.getCode() == 200) {
////                    UserInfos addFriend = new UserInfos();
////                    addFriend.setUsername(user.getResult().getUsername());
////                    addFriend.setUserid(user.getResult().getId());
////                    addFriend.setPortrait(user.getResult().getPortrait());
////                    addFriend.setStatus("0");
////                    mUserInfosDao.insertOrReplace(addFriend);
////                }
////            }
////        }
//    }
//
//    @Override
//    public void onFailure(AbstractHttpRequest abstractHttpRequest, BaseException e) {
//
//    }

	@Override
	public boolean onSent(Message arg0, SentMessageErrorCode arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMessageLinkClick(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
