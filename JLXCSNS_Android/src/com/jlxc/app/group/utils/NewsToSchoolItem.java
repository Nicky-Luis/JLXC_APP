package com.jlxc.app.group.utils;

import java.util.LinkedList;
import java.util.List;

import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.group.model.SchoolItemModel;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsTitleItem;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsBodyItem;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsLikeListItem;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsOperateItem;
import com.jlxc.app.news.model.NewsModel;

/**
 * 将数据转换成items类型的数据
 * */
public class NewsToSchoolItem {

	// 将数据转换为动态item形式的数据
	public static List<SchoolItemModel> newsToItem(List<NewsModel> orgDataList) {
		LinkedList<SchoolItemModel> itemList = new LinkedList<SchoolItemModel>();
		for (NewsModel newsMd : orgDataList) {
			itemList.add(createTitle(newsMd, SchoolItemModel.SCHOOL_NEWS_TITLE));
			itemList.add(createBody(newsMd, SchoolItemModel.SCHOOL_NEWS_BODY));
			itemList.add(createOperate(newsMd,
					SchoolItemModel.SCHOOL_NEWS_OPERATE));
			itemList.add(createLikeList(newsMd,
					SchoolItemModel.SCHOOL_NEWS_LIKELIST));
		}
		return itemList;
	}

	// 提取新闻中的头部信息
	public static SchoolItemModel createTitle(NewsModel news, int Type) {
		SchoolNewsTitleItem item = new SchoolNewsTitleItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setUserName(news.getUserName());
			item.setUserID(news.getUid());
			item.setHeadImage(news.getUserHeadImage());
			item.setHeadSubImage(news.getUserHeadSubImage());
			item.setSendTime(news.getSendTime());
		} catch (Exception e) {
			LogUtils.e("createNewsTitle error.");
		}
		return item;
	}

	// 提取新闻中的主体信息
	public static SchoolItemModel createBody(NewsModel news, int Type) {
		SchoolNewsBodyItem item = new SchoolNewsBodyItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setNewsContent(news.getNewsContent());
			item.setImageNewsList(news.getImageNewsList());
			item.setLocation(news.getLocation());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (SchoolItemModel) item;
	}

	// 提取新闻中的操作信息
	public static SchoolItemModel createOperate(NewsModel news, int Type) {
		SchoolNewsOperateItem item = new SchoolNewsOperateItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setIsLike(news.getIsLike());
			item.setLikeCount(news.getLikeQuantity());
			item.setCommentCount(JLXCUtils.stringToInt(news
					.getCommentQuantity()));
		} catch (Exception e) {
			LogUtils.e("createOperate error.");
		}
		return (SchoolItemModel) item;
	}

	// 提取新闻中的点赞信息
	private static SchoolItemModel createLikeList(NewsModel news, int Type) {
		SchoolNewsLikeListItem item = new SchoolNewsLikeListItem();
		try {
			item.setItemType(Type);
			
			item.setNewsID(news.getNewsID());
			item.setLikeCount(news.getLikeQuantity());
			item.setLikeHeadListimage(news.getLikeHeadListimage());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (SchoolNewsLikeListItem) item;
	}
}
