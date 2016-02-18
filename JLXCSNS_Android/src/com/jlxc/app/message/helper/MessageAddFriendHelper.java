package com.jlxc.app.message.helper;

import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.message.model.IMModel;

//发现部分的 添加好友帮助类
public class MessageAddFriendHelper {

	//添加好友
	public static void addFriend(IMModel imModel) {
		//本地数据持久化
		IMModel newModel = IMModel.findByGroupId(imModel.getTargetId());
		//如果存在更新
		if (null != newModel) {
			newModel.setTitle(imModel.getTitle());
			newModel.setAvatarPath(imModel.getAvatarPath());
//			newModel.setIsNew(0);
//			newModel.setIsRead(1);
			newModel.setCurrentState(IMModel.GroupHasAdd);
//			newModel.setAddDate(TimeHandle.getCurrentDataStr());
			newModel.update();
		}else {
			newModel = new IMModel();
			newModel.setType(IMModel.ConversationType_PRIVATE);
			newModel.setTargetId(imModel.getTargetId());
			newModel.setTitle(imModel.getTitle());
			newModel.setAvatarPath(imModel.getAvatarPath());
//			newModel.setAddDate(TimeHandle.getCurrentDataStr());
			newModel.setIsNew(0);
			newModel.setIsRead(1);
			newModel.setCurrentState(IMModel.GroupHasAdd);
			newModel.setOwner(UserManager.getInstance().getUser().getUid());
			newModel.save();
		}
	}
}
