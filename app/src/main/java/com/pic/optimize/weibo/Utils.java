package com.pic.optimize.weibo;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.os.Environment;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.pic.optimize.weibo.view.EmotionView;

public class Utils {

	public static final String CAMERA_FILE_CACHE_PREX = "tmp_bmp_";
	public static String getSDPath(){
	       File sdDir = null;
	       boolean sdCardExist = Environment.getExternalStorageState()  
	                           .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
	       if(sdCardExist)  
	       {               
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录
	      }  
	       if(sdDir!=null){
	    	   ensureFold(sdDir.getAbsolutePath());
	    	   return sdDir.toString();
	       }else
	    	   return null;
	      
	}
	
	private static void ensureFold(String sdir) {
		String s1 = sdir + Constants.PORTRAIT_DIR_SUFFIX;
		String s2 = sdir + Constants.PREVIEW_BMP_DIR_SUFFIX;
		File file = new File(s1);
		System.out.println("====s1="+s1);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		File file2 = new File(s2);
		if(!file.exists()) {
			file.mkdirs();
		}
		
	}
	
	public static void highlightContent(Context ctx, Spannable str) {
		Matcher matcher;
		int start = 0;
		int end = 0;
		Pattern pattern = null;
		pattern = Pattern.compile("\\[(\\S+?)\\]");
		matcher = pattern.matcher(str);
		Integer drawableSrc = null;
		while (matcher.find()) {
			drawableSrc = EmotionView.emotionsKeyString.get(matcher.group(1));
			if (drawableSrc != null && drawableSrc > 0) {
				start = matcher.start();
				end = matcher.end();
				str.setSpan(new ImageSpan(ctx, drawableSrc), start, end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

}
