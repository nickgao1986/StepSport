package com.example.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.util.ParseNewsInfoUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StateFaceModel {

	private static StateFaceModel instance = null;

	public static synchronized StateFaceModel getInstance() {
		if (instance == null) {
			instance = new StateFaceModel();
		}
		return instance;
	}

	private boolean mInitialized = false;
	private HashMap<String, Bitmap> mFaceMap = new HashMap<String, Bitmap>();
	private ArrayList<String> mFaceStrings = new ArrayList<String>();
	private ArrayList<Bitmap> mFaceIcons = new ArrayList<Bitmap>();
	private HashMap<String, Bitmap> mSmallFaceMap = new HashMap<String, Bitmap>();
	private ArrayList<Bitmap> mSmallFaceIcons = new ArrayList<Bitmap>();

	private StateFaceModel() {
	}

	public void init(Context context) {
		if (mInitialized || context == null) {
			// initialize only once
			return;
		}

		mFaceMap.clear();
		mFaceStrings.clear();
		mFaceIcons.clear();
		mSmallFaceMap.clear();
		mSmallFaceIcons.clear();
		ArrayList<String> faces = new ArrayList<String>();
		try {
			//读取json文件里的内容
			String text = ParseNewsInfoUtil.getAssetJsonByName(context,"state_face/state_face.json");
			JSONObject jsonObject = new JSONObject(text);
			JSONObject data = jsonObject.optJSONObject("data");
			JSONArray list = data.optJSONArray("list");
			for (int i=0;i<list.length();i++) {
				JSONObject item = list.optJSONObject(i);
				String value = item.optString("face");
				faces.add(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < faces.size(); ++i) {
			int index = i + 1;
			int id = context.getResources().getIdentifier("msgface_" + index,
					"drawable", "com.pic.optimize");
			try {
				Bitmap bm = BitmapFactory.decodeResource(
						context.getResources(), id);
				mSmallFaceMap.put(faces.get(i), bm);
				mSmallFaceIcons.add(bm);


				mFaceStrings.add(faces.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mInitialized = true;
	}

	public ArrayList<String> getFaceStrings() {
		return mFaceStrings;
	}

	public Bitmap getSmallFaceIcon(String content) {
		if(mSmallFaceMap != null) {
			return mSmallFaceMap.get(content);
		}else{
			return null;
		}
	}

}
