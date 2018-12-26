package com.pic.optimize.weibo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.pic.optimize.R;

public class EmotionView extends LinearLayout implements OnItemClickListener {

	private GridView mGridView;
	private static final ArrayList<Integer> emotionDisplayList = new ArrayList<Integer>();
	public static final LinkedHashMap<Integer, String> emotionsKeySrc = new LinkedHashMap<Integer, String>();
	public static final HashMap<String, Integer> emotionsKeyString = new HashMap<String, Integer>();
	private EmotionAdapter mEmotionAdapter;
	
	private class GridViewAdapter extends BaseAdapter {

		List<Integer> list;
		
		public GridViewAdapter(List<Integer> list) {
			super();
			this.list = list;
		}
		
		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			int resId = (Integer) getItem(position);
			ImageView iv = null;
			if (convertView == null) {
				iv = (ImageView) (convertView = new ImageView(getContext()
						.getApplicationContext()));
			}
			else {
				iv = (ImageView) convertView;
			}
			iv.setImageResource(resId);
			iv.setBackgroundResource(R.drawable.bg_face);
			int height = getResources().getDimensionPixelSize(
					R.dimen.emotion_item_view_height);
			iv.setPadding(0, height, 0, height);
			return iv;
		}
		
	}
	static {

		emotionsKeySrc.put(R.drawable.face1, "怒");
		emotionsKeySrc.put(R.drawable.face2, "蛋糕");
		emotionsKeySrc.put(R.drawable.face3, "蜡烛");
		emotionsKeySrc.put(R.drawable.face4, "干杯");
		emotionsKeySrc.put(R.drawable.face5, "抓狂");
		emotionsKeySrc.put(R.drawable.face6, "衰");
		emotionsKeySrc.put(R.drawable.face7, "晕");
		emotionsKeySrc.put(R.drawable.face8, "粽子");
		emotionsKeySrc.put(R.drawable.face9, "风扇");
		emotionsKeySrc.put(R.drawable.face10, "花");
		emotionsKeySrc.put(R.drawable.face11, "足球");
		emotionsKeySrc.put(R.drawable.face12, "绿丝带");
		emotionsKeySrc.put(R.drawable.face13, "哼");
		emotionsKeySrc.put(R.drawable.face14, "心");
		emotionsKeySrc.put(R.drawable.face15, "冰棍");
		emotionsKeySrc.put(R.drawable.face16, "哈哈");
		emotionsKeySrc.put(R.drawable.face17, "爱你");
		emotionsKeySrc.put(R.drawable.face18, "月亮");
		emotionsKeySrc.put(R.drawable.face19, "猪头");
		emotionsKeySrc.put(R.drawable.face20, "下雨");
		emotionsKeySrc.put(R.drawable.face21, "红牌");
		emotionsKeySrc.put(R.drawable.face22, "泪");
		emotionsKeySrc.put(R.drawable.face23, "哨子");
		emotionsKeySrc.put(R.drawable.face24, "困");
		emotionsKeySrc.put(R.drawable.face25, "呵呵");
		emotionsKeySrc.put(R.drawable.face26, "阳光");
		emotionsKeySrc.put(R.drawable.face27, "汗");
		emotionsKeySrc.put(R.drawable.face28, "黄牌");
		emotionsKeySrc.put(R.drawable.face29, "嘻嘻");
		emotionsKeySrc.put(R.drawable.face30, "伤心");
	

		Iterator<Integer> iterator = emotionsKeySrc.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			int temp = iterator.next();
			emotionsKeyString.put(emotionsKeySrc.get(temp), temp);
			emotionDisplayList.add(temp);
			i++;
		}
	}
	
	public void setEmotionAdapter(EmotionAdapter emotionAdapter) {
		mEmotionAdapter = emotionAdapter;
	}

	public interface EmotionAdapter {
		void doAction(int resId, String desc);
	}

	public EmotionView(Context context) {
		super(context);
		initViews();
	}

	public EmotionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	private void initViews() {
		Context context = getContext();
		LayoutInflater.from(context).inflate(R.layout.emotion_main, this);
		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setAdapter(new GridViewAdapter(emotionDisplayList));
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		int resid = emotionDisplayList.get(position);
		mEmotionAdapter.doAction(resid, emotionsKeySrc.get(resid));
	}
	



}
