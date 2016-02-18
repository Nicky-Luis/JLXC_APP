package com.jlxc.app.base.utils;

public interface JLXCConst {

	// 正式环境 112.74.202.59 192.168.1.107 www.90newtec.com
	// 测试环境
	public static final String DOMIN = "http://www.90newtec.com/jlxc_php/index.php/Home/MobileApi";
	public static final String ATTACHMENT_ADDR = "http://www.90newtec.com/jlxc_php/Uploads/";
	public static final String ROOT_PATH = "http://www.90newtec.com/jlxc_php/";

	public static final int STATUS_SUCCESS = 1;// 接口返回成功
	public static final int STATUS_FAIL = 0;// 接口返回失败
	public static final String HTTP_MESSAGE = "message"; // 返回值信息
	public static final String HTTP_RESULT = "result";// 返回值结果
	public static final String HTTP_STATUS = "status";// 返回值状态
	public static final String HTTP_LIST = "list";// 返回值列表

	public static final int PAGE_SIZE = 10;

	// IM和推送 公用前缀
	public static final String JLXC = "jlxc";// 返回值列表

	// broadCast
	// 状态回复消息或点赞或者新好友
	public static final String BROADCAST_NEW_MESSAGE_PUSH = "com.jlxc.broadcastreceiver.newsPush";
	// tab栏徽标更新通知
	public static final String BROADCAST_TAB_BADGE = "com.jlxc.broadcastreceiver.tabBadge";
	// 消息顶部更新
	public static final String BROADCAST_MESSAGE_REFRESH = "com.jlxc.broadcastreceiver.messageRefresh";
	// 动态详细更新后上一页面也进行更新
	public static final String BROADCAST_NEWS_LIST_REFRESH = "com.jlxc.broadcastreceiver.newsDetailRefresh";
	// 新圈子发布
	public static final String BROADCAST_NEW_TOPIC_REFRESH = "com.jlxc.broadcastreceiver.newTopicPublish";
	
	// 匹配网页
	public static final String URL_PATTERN = "[http|https]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*";
	// 匹配手机号
	public static final String PHONENUMBER_PATTERN = "1[3|4|5|7|8|][0-9]{9}";
	// 用户名匹配
	public static final String USER_ACCOUNT_PATTERN = "^[a-zA-Z0-9]{6,20}+$";
	// 匹配身份证号:15位 18位
	public static final String ID_CARD = "^\\d{15}|^\\d{17}([0-9]|X|x)$";
	// 姓名正则
	public static final String NAME_PATTERN = "([\u4e00-\u9fa5]{2,5})(&middot;[\u4e00-\u9fa5]{2,5})*";

	// //////////////////////////////////////////////登录注册部分////////////////////////////////////////////////
	// 是否有该用户
	public static final String IS_USER = DOMIN + "/isUser";
	// 获取验证码
	public static final String GET_MOBILE_VERIFY = DOMIN + "/getMobileVerify";
	// 用户注册
	public static final String REGISTER_USER = DOMIN + "/registerUser";
	// 找回密码
	public static final String FIND_PWD = DOMIN + "/findPwd";
	// 用户登录
	public static final String LOGIN_USER = DOMIN + "/loginUser";
	// 注册时填写个人信息 
	public static final String SAVE_PERSONAL_INFO = DOMIN + "/savePersonalInfo";
	// //////////////////////////////////////////////首页'说说'部分////////////////////////////////////////////////
	// 发布状态
	public static final String PUBLISH_NEWS = DOMIN + "/publishNews";
	// 状态新闻列表
	public static final String NEWS_LIST = DOMIN + "/newsList";
	// 校园广场新闻列表
	public static final String SCHOOL_NEWS_LIST = DOMIN + "/schoolNewsList";
	// 校园广场新闻主页
	public static final String SCHOOL_HOME_DATA = DOMIN + "/schoolHomeData";	
	// 状态点赞列表
	public static final String GET_NEWS_LIKE_LIST = DOMIN + "/getNewsLikeList";
	// 发送评论
	public static final String SEND_COMMENT = DOMIN + "/sendComment";
	// 删除评论
	public static final String DELETE_COMMENT = DOMIN + "/deleteComment";
	// 删除二级评论
	public static final String DELETE_SECOND_COMMENT = DOMIN
			+ "/deleteSecondComment";
	// 发送二级评论
	public static final String SEND_SECOND_COMMENT = DOMIN
			+ "/sendSecondComment";
	// 点赞或者取消赞
	public static final String LIKE_OR_CANCEL = DOMIN + "/likeOrCancel";
	// 新闻详情
	public static final String NEWS_DETAIL = DOMIN + "/newsDetail";
	// 浏览过该新闻的用户列表
	public static final String GET_NEWS_VISIT_LIST = DOMIN
			+ "/getNewsVisitList";
	// //////////////////////////////////////////////个人信息////////////////////////////////////////////////
	// 修改个人信息
	public static final String CHANGE_PERSONAL_INFORMATION = DOMIN
			+ "/changePersonalInformation";
	// 获取学校列表
	public static final String GET_SCHOOL_LIST = DOMIN + "/getSchoolList";
	// 获取学校学生列表
	public static final String GET_SCHOOL_STUDENT_LIST = DOMIN
			+ "/getSchoolStudentList";
	// 修改学校
	public static final String CHANGE_SCHOOL = DOMIN + "/changeSchool";
	// 设置HelloHaID
	public static final String SET_HELLOHAID = DOMIN + "/setHelloHaId";
	// 获取用户二维码
	public static final String GET_USER_QRCODE = DOMIN + "/getUserQRCode";
	// 修改个人信息中的图片 如背景图 头像
	public static final String CHANGE_INFORMATION_IMAGE = DOMIN
			+ "/changeInformationImage";
	// 个人信息中 获取最新动态的三张图片 旧版
	public static final String GET_NEWS_IMAGES = DOMIN + "/getNewsImages";
	// 个人信息中 获取最新动态的十张图片
	public static final String GET_NEWS_COVER_LIST = DOMIN + "/getNewsCoverList";	
	// 个人信息中 获取来访三张头像 弃用
	public static final String GET_VISIT_IMAGES = DOMIN + "/getVisitImages";
	// 个人信息中 获取粉丝数量
	public static final String GET_FANS_COUNT = DOMIN + "/getFansCount";	
	// 个人信息中 获取好友三张头像
	public static final String GET_FRIENDS_IMAGE = DOMIN + "/getFriendsImage";
	// 个人信息中 用户发布过的状态列表
	public static final String USER_NEWS_LIST = DOMIN + "/userNewsList";
	// 个人信息 删除状态
	public static final String DELETE_NEWS = DOMIN + "/deleteNews";
	// 个人信息 查看别人的信息 旧版
	public static final String PERSONAL_INFORMATION = DOMIN
			+ "/personalInformation";
	// 个人信息 查看别人的信息
	public static final String PERSONAL_INFO = DOMIN
			+ "/personalInfo";	
	// 最近来访列表
	public static final String GET_VISIT_LIST = DOMIN + "/getVisitList";
	// 删除来访
	public static final String DELETE_VISIT = DOMIN + "/deleteVisit";
	// 别人的好友列表
	public static final String GET_OTHER_FRIENDS_LIST = DOMIN
			+ "/getOtherFriendsList";
	// 共同的好友列表
	public static final String GET_COMMON_FRIENDS_LIST = DOMIN
			+ "/getCommonFriendsList";
	// 举报用户
	public static final String REPORT_OFFENCE = DOMIN + "/reportOffence";
	// 版本更新
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getLastestVersion?sys=2
	public static final String GET_LASTEST_VERSION = DOMIN
			+ "/getLastestVersion";
	// ////////////////////////////////////////IM模块//////////////////////////////////////////
	// 添加好友
	// http://localhost/jlxc_php/index.php/Home/MobileApi/addFriend
	public static final String Add_FRIEND = DOMIN + "/addFriend";
	// 删除好友
	// http://localhost/jlxc_php/index.php/Home/MobileApi/deleteFriend
	public static final String DELETE_FRIEND = DOMIN + "/deleteFriend";
	// 添加好友备注
	// http://localhost/jlxc_php/index.php/Home/MobileApi/addRemark
	public static final String ADD_REMARK = DOMIN + "/addRemark";
	// 获取图片和名字
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getImageAndName
	public static final String GET_IMAGE_AND_NAME = DOMIN + "/getImageAndName";
	// 是否同步好友
	// http://localhost/jlxc_php/index.php/Home/MobileApi/NeedSyncFriends
	public static final String NEED_SYNC_FRIENDS = DOMIN + "/needSyncFriends";
	// 获取好友列表
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getFriendsList
	public static final String GET_FRIENDS_LIST = DOMIN + "/getFriendsList";
	// 获取关注列表
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getAttentList
	public static final String GET_ATTENT_LIST = DOMIN + "/getAttentList";	
	// 获取粉丝列表
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getFansList
	public static final String GET_FANS_LIST = DOMIN + "/getFansList";
	// 获取其他人的关注列表
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getAttentList
	public static final String GET_OTHER_ATTENT_LIST = DOMIN + "/getOtherAttentList";	
	// 获取其他人的粉丝列表
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getFansList
	public static final String GET_OTHER_FANS_LIST = DOMIN + "/getOtherFansList";	
	// 获取全部好友列表
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getAllFriendsList
	public static final String GET_ALL_FRIENDS_LIST = DOMIN
			+ "/getAllFriendsList";
	////////////////////////////////////////发现模块 圈子//////////////////////////////////////////
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getTopicCategory
	// 获取圈子类型
	public static final String GET_TOPIC_CATEGORY = DOMIN + "/getTopicCategory";
	// http://localhost/jlxc_php/index.php/Home/MobileApi/postNewTopic
	// 创建一个圈子
	public static final String POST_NEW_TOPIC = DOMIN + "/postNewTopic";
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getTopicDetail
	// 获取圈子详情
	public static final String GET_TOPIC_DETAIL = DOMIN + "/getTopicDetail";
	// 加入一个圈子详情
	public static final String JOIN_TOPIC = DOMIN + "/joinTopic";
	// 退出一个圈子详情
	public static final String QUIT_TOPIC = DOMIN + "/quitTopic";	
	// 获取我的圈子列表
	//http://localhost/jlxc_php/index.php/Home/MobileApi/getMyTopicList
	public static final String GET_MY_TOPIC_LIST = DOMIN + "/getMyTopicList";
	// 获取我的圈子列表
	public static final String GET_TOPIC_NEWS_LIST = DOMIN + "/getTopicNewsList";
	// 获取圈子成员列表
	public static final String GET_TOPIC_MEMBER_LIST = DOMIN + "/getTopicMemberList";
	// 获取话题主页列表
	public static final String GET_TOPIC_HOME_LIST = DOMIN + "/getTopicHomeList";
	// 获取分类话题列表
	public static final String GET_CATEGORY_TOPIC_LIST = DOMIN + "/getCategoryTopicList";
	////////////////////////////////////////发现模块//////////////////////////////////////////
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getContactUser
	// 获取联系人用户
	public static final String GET_CONTACT_USER = DOMIN + "/getContactUser";
	// http://localhost/jlxc_php/index.php/Home/MobileApi/getSameSchoolList
	// 获取同校的人列表
	public static final String GET_SAME_SCHOOL_LIST = DOMIN
			+ "/getSameSchoolList";
	// http://localhost/jlxc_php/index.php/Home/MobileApi/findUserList
	// 搜索用户列表
	public static final String FIND_USER_LIST = DOMIN + "/findUserList";
	// http://localhost/jlxc_php/index.php/Home/MobileApi/helloHaIdExists
	// 判断该哈哈号是否存在
	public static final String HELLOHA_ID_EXISTS = DOMIN + "/helloHaIdExists";
	// http://localhost/jlxc_php/index.php/Home/MobileApi/recommendFriendsList
	// 推荐的人列表
	public static final String RECOMMEND_FRIENDS_LIST = DOMIN
			+ "/recommendFriendsList";

}
