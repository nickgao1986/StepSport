package com.example.model;

import android.text.TextUtils;

import com.example.item.LinkInfo;
import com.example.item.NewsInfo;
import com.example.util.ParseNewsInfoUtil;

import java.util.ArrayList;

public class NewsModel  {
	
	public static final String NEWS_ALL = "all";
	public static final String NEWS_IMP = "imp";
	public static final String NEWS_STAR = "star";
	public ArrayList<NewsInfo> newsListAll = new ArrayList<NewsInfo>();
	public ArrayList<NewsInfo> newsListImp = new ArrayList<NewsInfo>();
	public ArrayList<NewsInfo> newsListStar = new ArrayList<NewsInfo>();
	
	
	
	private static NewsModel instance;
	private NewsModel(){	
    }
    public static synchronized NewsModel getInstance(){
    	if(instance == null){
    		instance = new NewsModel();
    	}
    	return instance;
    }
    
    private static NewsModel homeModel;

    public static synchronized NewsModel getHomeModel(){
    	if(homeModel == null){
    		homeModel = new NewsModel();
    	}
    	return homeModel;
    }
    
    private static NewsModel myHomeModel;

    public static synchronized NewsModel getMyHomeModel(){
    	if(myHomeModel == null){
    		myHomeModel = new NewsModel();
    	}
    	return myHomeModel;
    }
    
    
    public ArrayList<NewsInfo> getNewsList(String flag){
    	
    	if(TextUtils.isEmpty(flag) || flag.equals(NEWS_IMP)){
    		return newsListImp;
    	} else if(flag.equals(NEWS_ALL)){
    		return newsListAll;
    	} else if (flag.equals(NEWS_STAR)){
    		return newsListStar;
    	}
    	
    	return null;
    }
    
    
    
    

	private int n = 0;
	
	private int refreshNum = 0;
	private boolean isFirstRefresh = true;
	private int fPhotoNum = 0;
	private int fDiaryNum = 0;
	private int fRecordNum = 0;
	private int fRepostNum = 0;
	private int iTotalNum = 0;
	private int iImNum = 0;
	private int iPhotoNum = 0;
	private int iStateNum = 0;
	private int iStarNum = 0;
	private String logo = "";
	private String status = "";
	private String realname = "";
	private String statustime = "";
	private String online = "";
	private String indexprivacy = "";
	private String istar = "";
	private String ismyfriend = "";
	private String starintro = "";
	private String gender = "0";
	private String updateTime = "";
    private NewsInfo activeNewsInfo;
    private ArrayList<LinkInfo> stateList = null;
    
    public String getUpdateTime(){
    	return updateTime;
    }
    
    public void setUpdateTime(String updateTime){
    	this.updateTime = updateTime;
    }
	
    public int getRefreshNum() {
		return refreshNum;
	}
	public void setRefreshNum(int refreshNum) {
		this.refreshNum = refreshNum;
	}
    
    public boolean isFirstRefresh() {
		return isFirstRefresh;
	}
	public void setFirstRefresh(boolean refresh) {
		isFirstRefresh = refresh;
	}
    
	public int getAllPhotoNum() {
		return fPhotoNum;
	}
	public void setAllPhotoNum(int photoNum) {
		fPhotoNum = photoNum;
	}
	public int getDiaryNum() {
		return fDiaryNum;
	}
	public void setDiaryNum(int diaryNum) {
		fDiaryNum = diaryNum;
	}
	public int getRecordNum() {
		return fRecordNum;
	}
	public void setRecordNum(int recordNum) {
		fRecordNum = recordNum;
	}
	public int getRepostNum() {
		return fRepostNum;
	}
	public void setRepostNum(int repostNum) {
		fRepostNum = repostNum;
	}

    public NewsInfo getActiveItem(){
    	return activeNewsInfo;
    }

    public void setActiveItem(NewsInfo newsInfo){
    	this.activeNewsInfo = newsInfo;
    }
    
    public int getLastNum(){
    	return n;
    }
    
    public void setLastNum(int n){
    	this.n = n;
    }
    
    public String getLogo(){
    	return logo;
    }
    
    public void setLogo(String logo){
    	this.logo = logo;
    }
    
    public String getPrivacy(){
    	return indexprivacy;
    }
    
    public void setPrivacy(String indexprivacy){
    	this.indexprivacy = indexprivacy;
    }
    
    public String getIstar(){
    	return istar;
    }
    
    public void setIstar(String istar){
    	this.istar = istar;
    }
    
    public String getIsmyfriend(){
    	return ismyfriend;
    }
    
    public void setIsmyfriend(String ismyfriend){
    	this.ismyfriend = ismyfriend;
    }
    
    public String getStarintro(){
    	return starintro;
    }
    
    public void setStarintro(String starintro){
    	this.starintro = starintro;
    }
    
    public String getOnline(){
    	return online;
    }
    
    public void setOnline(String online){
    	this.online = online;
    }
    
    public String getStatus(){
    	return status;
    }
    
    public void setStatus(String status){
    	this.status = status;
    	if(!TextUtils.isEmpty(status)){
    		stateList = ParseNewsInfoUtil.parseStr(status);
    	}
    }
    
    public ArrayList<LinkInfo> getStateList(){
    	return stateList;
    }
    
    public String getRealname(){
    	return realname;
    }
    
    public void setRealname(String realname){
    	this.realname = realname;
    }
    
    public String getStatustime(){
    	return statustime;
    }
    
    public void setStatustime(String statustime){
    	this.statustime = statustime;
    }
    
    public String getGender(){
    	return gender;
    }
    
    public void setGender(String gender){
    	this.gender = gender;
    }
    
    public void clear(){
    	n = 0;
    	iTotalNum = 0;
    	iImNum = 0;
    	iPhotoNum = 0;
    	iStateNum = 0;
    	iStarNum = 0;
    	logo = "";
    	status = "";
    	realname = "";
    	statustime = "";
    	online = "";
    	indexprivacy = "";
    	istar = "";
    	ismyfriend = "";
    	starintro = "";
    	updateTime = "";
    	stateList = null;
    	isFirstRefresh = true;
    	refreshNum = 0;
    }
}
