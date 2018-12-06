package com.example.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

		AssetManager assetManager = context.getAssets();
		ArrayList<String> faces = new ArrayList<String>();
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(assetManager
					.open("state_face/state_face.xml"));
			Element root = doc.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName("string");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String s = "";
				NodeList list = node.getChildNodes();
				if (list != null) {
					for (int j = 0; j < list.getLength(); j++) {
						s += list.item(j).getNodeValue();
					}
				}
				faces.add(s);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;

		}

		int i;
		for (i = 0; i < faces.size(); ++i) {
			int index = i + 1;
			int id = context.getResources().getIdentifier("stateface_" + index,
					"drawable", "com.pic.optimize");
			try {
				Bitmap bm = BitmapFactory.decodeResource(
						context.getResources(), id);
				mSmallFaceMap.put(faces.get(i), bm);
				mSmallFaceIcons.add(bm);


				mFaceStrings.add(faces.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mInitialized = true;
//		Bitmap bitmap = mSmallFaceMap.get(s);
//		System.out.println("===bitmap=" + bitmap);
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
