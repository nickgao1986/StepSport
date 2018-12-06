package com.example.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.item.LinkInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParseNewsInfoUtil {

	private static Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
	private static Pattern PHONE_PATTERN = Patterns.PHONE;
	private static Pattern WEBURL_PATTERN = Patterns.WEB_URL;
	
	public static ArrayList<LinkInfo> parseStr(String strLink) {
		if(TextUtils.isEmpty(strLink)){
    		return null;
    	}
		ArrayList<LinkInfo> resultList = new ArrayList<LinkInfo>();
    	ArrayList<LinkInfo> infoList = null;
    	try{
    		infoList = new ArrayList<LinkInfo>();
    		Matcher matcher = EMAIL_PATTERN.matcher(strLink);
    		int begin = 0;
    		while(matcher.find()) {
    			int start = matcher.start();
    			int end = matcher.end();
        		LinkInfo info = new LinkInfo();
        		info.setStartIndex(start);
        		info.setEndIndex(end);
            	info.setContent(matcher.group());
            	info.setType(LinkInfo.EMAIL);
        		infoList.add(info);
    		}
    		
    		Matcher matcher1 = PHONE_PATTERN.matcher(strLink);
    		while(matcher1.find()) {
    			int start = matcher1.start();
    			int end = matcher1.end();
        		LinkInfo info = new LinkInfo();
        		info.setStartIndex(start);
        		info.setEndIndex(end);
            	info.setContent(matcher1.group());
            	info.setType(LinkInfo.PHONENUMBER);
        		infoList.add(info);
    		}
    		
    		Pattern pattern = Pattern.compile("(\\(#\\S{1,2}\\))");
    	    Matcher matcher2 = pattern.matcher(strLink);
    		 while(matcher2.find()) {
    			int start = matcher2.start();
    			int end = matcher2.end();
        		LinkInfo info = new LinkInfo();
        		info.setStartIndex(start);
        		info.setEndIndex(end);
            	info.setContent(matcher2.group());
            	info.setFace(true);
        		infoList.add(info);
    		}
    		
    		Collections.sort(infoList);
    		int last = 0;
    		for(int i=0;i<infoList.size();i++) {
    			LinkInfo info = infoList.get(i);
    			if(begin != info.getStartIndex()){
    				LinkInfo infoBefore = new LinkInfo();
        			infoBefore.setContent(strLink.substring(begin,info.getStartIndex()));
        			resultList.add(infoBefore);
    			}
    			resultList.add(info);
    			begin = info.getEndIndex();
    			last = info.getEndIndex();
    		}
    		if(last < strLink.length()) {
    			LinkInfo info = new LinkInfo();
        		info.setContent(strLink.substring(last,strLink.length()));
        		resultList.add(info);
    		}
    		
    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return resultList;
	}


	public static String getAssetJsonByName(Context context, String fileName) {
		StringBuilder sb = new StringBuilder();
		BufferedReader bf = null;
		try {
			AssetManager am = context.getAssets();
			bf = new BufferedReader(new InputStreamReader(am.open(fileName)));
			String line;
			while ((line = bf.readLine()) != null) {
				sb.append(line);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (bf != null) {
					bf.close();
					bf = null;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return sb.toString().length() > 0 ? sb.toString() : null;
	}

}
