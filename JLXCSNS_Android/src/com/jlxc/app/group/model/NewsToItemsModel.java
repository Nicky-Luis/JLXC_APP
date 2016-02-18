package com.jlxc.app.group.model;

import java.util.LinkedList;
import java.util.List;

import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.personal.model.MyNewsListItemModel;
import com.jlxc.app.personal.model.MyNewsListItemModel.MyNewsBodyItem;
import com.jlxc.app.personal.model.MyNewsListItemModel.MyNewsOperateItem;
import com.jlxc.app.personal.model.MyNewsListItemModel.MyNewsTitleItem;

public class NewsToItemsModel {

	// 将数据转换为动态item形式的数据
	public static List<MyNewsListItemModel> newsToItem(
			List<NewsModel> orgDataList) {
		LinkedList<MyNewsListItemModel> itemList = new LinkedList<MyNewsListItemModel>();
		for (NewsModel newsMd : orgDataList) {
			itemList.add(createNewsTitle(newsMd, MyNewsListItemModel.NEWS_TITLE));
			itemList.add(createBody(newsMd, MyNewsListItemModel.NEWS_BODY));
			itemList.add(createOperate(newsMd, MyNewsListItemModel.NEWS_OPERATE));
		}
		return itemList;
	}

	// 提取新闻中的头部信息
	private static MyNewsListItemModel createNewsTitle(NewsModel news, int Type) {
		MyNewsTitleItem item = new MyNewsTitleItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setUserName(news.getUserName());
			item.setSendTime(news.getSendTime());
			item.setUserTag(news.getUserSchool());
			item.setUserID(news.getUid());
			item.setUserHeadImage(news.getUserHeadImage());
			item.setUserSubHeadImage(news.getUserHeadSubImage());
		} catch (Exception e) {
			LogUtils.e("createNewsTitle error.");
		}
		return item;
	}

	// 提取新闻中的主体信息
	private static MyNewsListItemModel createBody(NewsModel news, int Type) {
		MyNewsBodyItem item = new MyNewsBodyItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setNewsContent(news.getNewsContent());
			item.setImageNewsList(news.getImageNewsList());
			item.setLocation(news.getLocation());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (MyNewsListItemModel) item;
	}

	// 提取新闻中的操作信息
	private static MyNewsListItemModel createOperate(NewsModel news, int Type) {
		MyNewsOperateItem item = new MyNewsOperateItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setIsLike(news.getIsLike());
			item.setReplyCount(news.getCommentQuantity());
			item.setLikeCount(news.getLikeQuantity());
		} catch (Exception e) {
			LogUtils.e("createOperate error.");
		}
		return (MyNewsListItemModel) item;
	}


}
