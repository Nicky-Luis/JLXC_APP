package com.jlxc.app.news.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class CampusModel {

	// 校园动态列表
	private List<NewsModel> campusNewsList;
	// 校园的人
	private List<CampusPersonModel> personList;

	@SuppressWarnings("unchecked")
	public void setContentWithJson(JSONObject object) {
		// 校园动态的转换
		List<JSONObject> JNewsObj = (List<JSONObject>) object.get("list");
		List<NewsModel> newsList = new ArrayList<NewsModel>();
		for (JSONObject newsObject : JNewsObj) {
			NewsModel newsTemp = new NewsModel();
			newsTemp.setContentWithJson(newsObject);
			newsList.add(newsTemp);
		}
		setCampusNewsList(newsList);

		// 校园的人转换
		List<JSONObject> JPersonObj = (List<JSONObject>) object.get("info");
		List<CampusPersonModel> personList = new ArrayList<CampusPersonModel>();
		for (JSONObject personObject : JPersonObj) {
			CampusPersonModel personTemp = new CampusPersonModel();
			personTemp.setContentWithJson(personObject);
			personList.add(personTemp);
		}
		setPersonList(personList);
	}

	public List<NewsModel> getCampusNewsList() {
		return campusNewsList;
	}

	public void setCampusNewsList(List<NewsModel> campusNewsList) {
		this.campusNewsList = campusNewsList;
	}

	public List<CampusPersonModel> getPersonList() {
		return personList;
	}

	public void setPersonList(List<CampusPersonModel> personList) {
		this.personList = personList;
	}

}
