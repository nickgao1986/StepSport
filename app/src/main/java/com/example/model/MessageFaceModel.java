/**
 * Copyright (C)  2007 - 2010 kaixin001  
 */
package com.example.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MessageFaceModel{

	/** single instance of this class */
	private static MessageFaceModel instance = null;
	
	/** context */
	private boolean mInitialized = false;
	
	private HashMap<String,Bitmap> mFaceMap = new HashMap<String,Bitmap>();
	
	private ArrayList<String> mFaceStrings = new ArrayList<String>();
	
	private ArrayList<Bitmap> mFaceIcons = new ArrayList<Bitmap>();
	
	/**
	 * constructor
	 */
	private MessageFaceModel(){
		
	}
	
	/**
	 * Factory method
	 */
	public static synchronized MessageFaceModel getInstance(){
		if(instance == null){
			instance = new MessageFaceModel();
		}
		return instance;
	}
	
	/**
	 * initialize face data
	 */
	public void init(Context context){
		if(mInitialized){
			//initialize only once
			return;
		}
		
		mFaceMap.clear();
		mFaceStrings.clear();
		mFaceIcons.clear();
		
		AssetManager assetManager = context.getAssets();
		ArrayList<String> faces = new ArrayList<String>();
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(assetManager.open("message_face/MessageFace.xml"));
			Element root = doc.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName("string");
			for(int i =0;i< nodeList.getLength();i++)
			{
				Node node = nodeList.item(i);
				String s = "";
				NodeList list = node.getChildNodes();
				if(list != null){
					for(int j = 0; j < list.getLength(); j++){
						s += list.item(j).getNodeValue();
					}
				}
				faces.add(s);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;

		}
		
		int i;
		for(i = 0; i < faces.size(); ++i){
			int index = i + 1;
			int id = context.getResources().getIdentifier(   
                    "msgface_" + index,    
                    "drawable", "com.pic.optimize");
			try {
				Bitmap bm =  BitmapFactory.decodeResource(context.getResources(),id);
				mFaceMap.put(faces.get(i), bm);
				mFaceStrings.add(faces.get(i));
				mFaceIcons.add(bm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mInitialized = true;
		
	}
	
	public ArrayList<Bitmap> getFaceIcons(){
		return mFaceIcons;
	}
	
	public ArrayList<String> getFaceStrings(){
		return mFaceStrings;
	}
	
	public Bitmap getFaceIcon(String face){
		if(mFaceMap != null){
			return mFaceMap.get(face);
		}else{
			return null;
		}
	}
	
	/**
	 * find face string in TextView content and replace then with face icon
	 */
	public SpannableString ProcessTextForFace(String textContent){
		
		if(textContent.length() == 0){
			return null;
		}
		
		int searchStartPos = 0;
		SpannableString spannableString = new SpannableString(textContent);
		while(true){
			int sPos = textContent.indexOf("(#", searchStartPos);
			if(sPos < 0){
				break;
			}
			
			int ePos = textContent.indexOf(")", searchStartPos);
			if(ePos < 0){
				break;
			}
			
			if (sPos >= ePos) {
				break;
			}
			
			//one symbol pair is found
			String face = textContent.substring(sPos, ePos + 1);
			Bitmap bm = getFaceIcon(face);
			if(bm != null){
				spannableString.setSpan(new ImageSpan(bm),
							 			sPos, 
							 			ePos + 1,
							 			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			
			searchStartPos = ePos + 1;
		}
		
		return spannableString;
	
	}
	
	public void clear() {
		// TODO Auto-generated method stub
		mInitialized = false;
		mFaceMap.clear();
		mFaceStrings.clear();
		mFaceIcons.clear();
	}

}
