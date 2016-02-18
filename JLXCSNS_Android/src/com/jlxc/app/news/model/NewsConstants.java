package com.jlxc.app.news.model;

public class NewsConstants {
	// 小分辨率手机
	public final static int SMALL_PIX = 540;
	// 中等分辨率
	public final static int MIDDLE_PIX = 720;
	// 最多点赞数量
	public final static int MAX_LIKE_COUNT = 10;
	// 动态显示的评论数量
	public int NEWS_COMMENT_NUM = 3;
	// 意图（键盘的状态）
	public final static String INTENT_KEY_COMMENT_STATE = "comment_state";
	// 键盘关闭状态
	public final static int KEY_BOARD_CLOSE = 0;
	// 键盘打开，等待评论状态
	public final static int KEY_BOARD_COMMENT = 1;
	// 键盘打开，等待回复状态
	public final static int KEY_BOARD_REPLY = 2;
	// 意图（需要回复的评论id，有时直接进行回复）
	public final static String INTENT_KEY_COMMENT_ID = "comment_id";
	// 意图（当前的news对象）
	public final static String INTENT_KEY_NEWS_OBJ = "current_news_obj";
	// 意图（当前的news的id）
	public final static String INTENT_KEY_NEWS_ID = "current_news_id";
	// 更新操作（包括评论与点赞的操作）
	public final static String OPERATE_UPDATE = "news_update";
	// 删除动态操作
	public final static String OPERATE_DELETET = "news_delete";
	// 未对动态进行操作
	public final static String OPERATE_NO_ACTION = "no_action";
	// 发布动态操作完成
	public final static String PUBLISH_FINISH = "pulish_finished";
	// 刷新动态页面
	public final static String NEWS_LISTVIEW_REFRESH = "news_list_refresh";
	// 评论框输入类型为评论
	public final static int Input_Type_Comment = 0;
	// 评论框输入类型为子评论
	public final static int Input_Type_SubComment = 1;
	// 评论框输入类型为回复
	public final static int Input_Type_SubReply = 2;
}
