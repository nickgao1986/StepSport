package com.example.item;

import android.graphics.RectF;
import android.text.TextUtils;

import java.util.ArrayList;

public class LinkInfo implements Comparable<LinkInfo>{
    private String content;
    private String type;
    private String id;
    private boolean bIsFace = false;
    private boolean bSelected = false;
    public static final String EMAIL = "Email";
    public static final String WEBURL = "WebUrl";
    public static final String PHONENUMBER = "PhoneNumber";
    private int startIndex;
    private int endIndex;
    
	private ArrayList<RectF> rectFList = new ArrayList<RectF>();
    
    public LinkInfo(){	
    }
    
    public LinkInfo(String content, String type, String id){	
    	this.content = content;
    	this.type = type;
    	this.id = id;
    }
    
    public boolean isSelected(){
    	return bSelected;
    }
    
    public void setSelected(boolean bSelected){
    	this.bSelected = bSelected;
    }
    
    public ArrayList<RectF> getRectFList(){
    	return rectFList;
    }
    
    public String getContent(){
    	return content;
    }
    
    public void setContent(String content){
    	this.content = content;
    }
    
    public String getType(){
    	return type;
    }
    
    public void setType(String type){
    	this.type = type;
    }
    
    public String getId(){
    	return id;
    }
    
    public void setId(String id){
    	this.id = id;
    }
    
    public void setFace(boolean bIsFace){
    	this.bIsFace = bIsFace;
    }
    
    public boolean isFace(){
    	return bIsFace;
    }
    
    public int getStartIndex() {
  		return startIndex;
  	}

  	public void setStartIndex(int startIndex) {
  		this.startIndex = startIndex;
  	}
  	
  	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
    
    public boolean isEmail() {
    	if(TextUtils.isEmpty(type)){
    		return false;
    	}
    	if(EMAIL.equals(type)) {
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public boolean isWebUrl() {
    	if(TextUtils.isEmpty(type)){
    		return false;
    	}
    	if(WEBURL.equals(type)) {
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public boolean isPhoneNumber() {
    	if(TextUtils.isEmpty(type)){
    		return false;
    	}
    	if(PHONENUMBER.equals(type)) {
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public boolean isCommonString(){
    	if(TextUtils.isEmpty(type)){
    		return true;
    	}
    	return false;
    }
    
    public void addRectF(RectF rectF){
    	rectFList.add(rectF);
    }
    
    public boolean contains(float x, float y){
    	boolean ret = false;
    	int len = rectFList.size();
    	for(int i = 0; i < len; i++){
    		RectF rectF = rectFList.get(i);
    		if(rectF.contains(x, y)){
    			return true;
    		}
    	}
    	return ret;
    }

	@Override
	public int compareTo(LinkInfo another) {
		if(this.startIndex < another.startIndex) {
			return -1;
		}else if(this.startIndex > another.startIndex){
			return 1;
		}
		return 0;
	}
}
