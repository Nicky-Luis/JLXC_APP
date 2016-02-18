package com.jlxc.app.group.utils;

import java.util.LinkedList;
import java.util.List;

import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.group.model.GroupNewsItemModel;
import com.jlxc.app.group.model.GroupNewsItemModel.GroupNewsBodyItem;
import com.jlxc.app.group.model.GroupNewsItemModel.GroupNewsOperateItem;
import com.jlxc.app.group.model.GroupNewsItemModel.GroupNewsTitleItem;
import com.jlxc.app.news.model.NewsModel;
/**
 * 将数据转换成items类型的数据
 * */
public class NewsToGroupItem {

	// 将数据转换为动态item形式的数据
	public static List<GroupNewsItemModel> newsToItem(
			List<NewsModel> orgDataList) {
		LinkedList<GroupNewsItemModel> itemList = new LinkedList<GroupNewsItemModel>();
		for (NewsModel newsMd : orgDataList) {
			itemList.add(createTitle(newsMd, GroupNewsItemModel.GROUP_TITLE));
			itemList.add(createBody(newsMd, GroupNewsItemModel.GROUP_BODY));
			itemList.add(createOperate(newsMd, GroupNewsItemModel.GROUP_OPERATE));
		}
		return itemList;
	}

	// 提取新闻中的头部信息
	public static GroupNewsItemModel createTitle(NewsModel news, int Type) {
		GroupNewsTitleItem item = new GroupNewsTitleItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setUserName(news.getUserName());
			item.setUserID(news.getUid());
			item.setSchool(news.getUserSchool());
			item.setSchoolCode(news.getSchoolCode());
			item.setUserHeadImage(news.getUserHeadImage());
			item.setUserSubHeadImage(news.getUserHeadSubImage());
		} catch (Exception e) {
			LogUtils.e("createNewsTitle error.");
		}
		return item;
	}

	// 提取新闻中的主体信息
	public static GroupNewsItemModel createBody(NewsModel news, int Type) {
		GroupNewsBodyItem item = new GroupNewsBodyItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setNewsContent(news.getNewsContent());
			item.setImageNewsList(news.getImageNewsList());
			item.setLocation(news.getLocation());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (GroupNewsItemModel) item;
	}

	// 提取新闻中的操作信息
	public static GroupNewsItemModel createOperate(NewsModel news, int Type) {
		GroupNewsOperateItem item = new GroupNewsOperateItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setIsLike(news.getIsLike());
			item.setReplyCount(news.getCommentQuantity());
			item.setLikeCount(news.getLikeQuantity());
			item.setTime(news.getSendTime());
		} catch (Exception e) {
			LogUtils.e("createOperate error.");
		}
		return (GroupNewsItemModel) item;
	}

}
