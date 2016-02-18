package com.jlxc.app.news.utils;

import java.util.LinkedList;
import java.util.List;

import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.CampusPersonModel;
import com.jlxc.app.news.model.CommentModel;
import com.jlxc.app.news.model.ItemModel;
import com.jlxc.app.news.model.ItemModel.BodyItem;
import com.jlxc.app.news.model.ItemModel.CampusHeadItem;
import com.jlxc.app.news.model.ItemModel.CommentItem;
import com.jlxc.app.news.model.ItemModel.CommentListItem;
import com.jlxc.app.news.model.ItemModel.LikeListItem;
import com.jlxc.app.news.model.ItemModel.OperateItem;
import com.jlxc.app.news.model.ItemModel.SubCommentItem;
import com.jlxc.app.news.model.ItemModel.TitleItem;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.news.model.SubCommentModel;

/**
 * 将新闻的数据进行转换
 * */
public class DataToItem {
	// 将数据转换为动态主页item形式的数据
	public static List<ItemModel> newsDataToItems(List<NewsModel> orgDataList) {
		LinkedList<ItemModel> itemList = new LinkedList<ItemModel>();
		for (NewsModel newsMd : orgDataList) {
			itemList.add(createNewsTitle(newsMd, ItemModel.NEWS_TITLE));
			itemList.add(createBody(newsMd, ItemModel.NEWS_BODY));
			itemList.add(createOperate(newsMd, ItemModel.NEWS_OPERATE));
			itemList.add(createLikeList(newsMd, ItemModel.NEWS_LIKELIST));
			itemList.add(createCommentList(newsMd, ItemModel.NEWS_COMMENT));
		}
		return itemList;
	}

	// 将数据转换为校园item形式的数据
	public static List<ItemModel> campusDataToItems(List<NewsModel> newsData,
			List<CampusPersonModel> personData) {
		LinkedList<ItemModel> itemList = new LinkedList<ItemModel>();
		if (personData.size() > 0) {
			itemList.addFirst(createCampusHead(personData,
					ItemModel.CAMPUS_HEAD));
		}

		for (NewsModel newsMd : newsData) {
			itemList.add(createNewsTitle(newsMd, ItemModel.CAMPUS_TITLE));
			itemList.add(createBody(newsMd, ItemModel.CAMPUS_BODY));
			itemList.add(createOperate(newsMd, ItemModel.CAMPUS_OPERATE));
			itemList.add(createLikeList(newsMd, ItemModel.CAMPUS_LIKELIST));
		}
		return itemList;
	}

	/**
	 * 将动态详细数据转换为item类型数据
	 * */
	public static List<ItemModel> newsDetailToItems(NewsModel newsData) {
		LinkedList<ItemModel> itemList = new LinkedList<ItemModel>();
		itemList.add(createNewsTitle(newsData, ItemModel.NEWS_DETAIL_TITLE));
		itemList.add(createBody(newsData, ItemModel.NEWS_DETAIL_BODY));
		itemList.add(createLikeList(newsData, ItemModel.NEWS_DETAIL_LIKELIST));
		List<CommentModel> cmtList = newsData.getCommentList();
		for (CommentModel cmtMd : cmtList) {
			itemList.add(createComment(cmtMd, ItemModel.NEWS_DETAIL_COMMENT));
			List<SubCommentModel> subCmtList = cmtMd.getSubCommentList();
			for (SubCommentModel subCmtMd : subCmtList) {
				itemList.add(createSubComment(subCmtMd, newsData.getNewsID(),
						ItemModel.NEWS_DETAIL_SUB_COMMENT));
			}
		}
		return itemList;
	}

	// 提取新闻中的头部信息
	private static ItemModel createNewsTitle(NewsModel news, int Type) {
		TitleItem item = new TitleItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setHeadImage(news.getUserHeadImage());
			item.setHeadSubImage(news.getUserHeadSubImage());
			item.setUserName(news.getUserName());
			item.setSendTime(news.getSendTime());
			item.setUserSchool(news.getUserSchool());
			item.setSchoolCode(news.getSchoolCode());
			item.setIsLike(news.getIsLike());
			item.setUserID(news.getUid());
			item.setLikeCount(news.getLikeQuantity());
			item.setTagContent(news.getTypeContent());
		} catch (Exception e) {
			LogUtils.e("createNewsTitle error.");
		}
		return (ItemModel) item;
	}

	// 提取新闻中的操作信息
	private static ItemModel createOperate(NewsModel news, int Type) {
		OperateItem item = new OperateItem();
		try {
			item.setItemType(Type);
			item.setLikeCount(news.getLikeQuantity());
			item.setNewsID(news.getNewsID());
			item.setSendTime(news.getSendTime());
			item.setIsLike(news.getIsLike());
			item.setTopicID(news.getTopicID());
			item.setTopicName(news.getTopicName());
		} catch (Exception e) {
			LogUtils.e("createOperate error.");
		}
		return (ItemModel) item;
	}

	// 提取新闻中的主体信息
	private static ItemModel createBody(NewsModel news, int Type) {
		BodyItem item = new BodyItem();
		try {
			item.setItemType(Type);

			item.setNewsID(news.getNewsID());
			item.setNewsContent(news.getNewsContent());
			item.setImageNewsList(news.getImageNewsList());
			item.setSendTime(news.getSendTime());
			item.setLocation(news.getLocation());
			item.setTopicID(news.getTopicID());
			item.setTopicName(news.getTopicName());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (ItemModel) item;
	}

	// 提取新闻中的点赞信息
	private static ItemModel createLikeList(NewsModel news, int Type) {
		LikeListItem item = new LikeListItem();
		try {
			item.setItemType(Type);
			item.setNewsID(news.getNewsID());
			item.setLikeCount(news.getLikeQuantity());
			item.setLikeHeadListimage(news.getLikeHeadListimage());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (ItemModel) item;
	}

	// 提取新闻中的评论列表信息
	private static ItemModel createCommentList(NewsModel news, int Type) {
		CommentListItem item = new CommentListItem();

		try {
			item.setNewsID(news.getNewsID());
			item.setItemType(Type);
			item.setReplyCount(news.getCommentQuantity());
			item.setCommentList(news.getCommentList());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (ItemModel) item;
	}

	// 提取新闻中的评论信息
	public static ItemModel createComment(CommentModel cmt, int Type) {
		CommentItem item = new CommentItem();
		try {
			item.setItemType(Type);
			item.setComment(cmt);
		} catch (Exception e) {
			LogUtils.e("create comment error.");
		}
		return (ItemModel) item;
	}

	// 提取校园头部
	private static ItemModel createCampusHead(
			List<CampusPersonModel> personData, int Type) {
		CampusHeadItem item = new CampusHeadItem();
		try {
			item.setItemType(Type);
			// 校园头部动态ID
			item.setPersonList(personData);
		} catch (Exception e) {
			LogUtils.e("create Campus Head error.");
		}
		return (ItemModel) item;
	}

	// 提取动态数据中的子评论信息
	public static ItemModel createSubComment(SubCommentModel cmt,
			String newsID, int Type) {
		SubCommentItem item = new SubCommentItem();
		try {
			item.setItemType(Type);
			item.setNewsID(newsID);
			item.setSubCommentModel(cmt);
		} catch (Exception e) {
			LogUtils.e("create subcomment item error.");
		}
		return (ItemModel) item;
	}
}
