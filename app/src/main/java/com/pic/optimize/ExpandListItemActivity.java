package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpandListItemActivity extends Activity {

	private ArrayList<MyData> myList = new ArrayList<MyData>();
	private myAdapter myAdapter;
	private LayoutInflater inflater = null;
	private int oldPostion = -1;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,ExpandListItemActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expand_list_item_main);
		final ListView list = (ListView) findViewById(R.id.draggable_list);
		MyData data = new MyData();
		data.name = "11";
		myList.add(data);
		
		data = new MyData();
		data.name = "22";
		myList.add(data);
		
		data = new MyData();
		data.name = "33";
		myList.add(data);
		
		data = new MyData();
		data.name = "44";
		myList.add(data);
		
		data = new MyData();
		data.name = "55";
		myList.add(data);
		
		data = new MyData();
		data.name = "66";
		myList.add(data);
		
		data = new MyData();
		data.name = "77";
		myList.add(data);
		
	
		inflater = LayoutInflater.from(this);
		TextView header = (TextView)inflater.inflate(R.layout.expand_header, null);
		TextView footer = (TextView)inflater.inflate(R.layout.expand_footer, null);

		//list.addFooterView(footer);
		//list.addHeaderView(header);
		myAdapter = new myAdapter();
		list.setAdapter(myAdapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MyData data = myList.get(position);
				if (oldPostion == position) {
					if (data.expand)  {
						oldPostion = -1;
					}
					data.expand = !data.expand;
				}else{
					oldPostion = position;
					data.expand = true;
				}
				
				int totalHeight = 0;
				for(int i=0;i<myAdapter.getCount();i++) {
					View viewItem = myAdapter.getView(i, null, list);
					viewItem.measure(0, 0);
					totalHeight += viewItem.getMeasuredHeight();
				}
				
				ViewGroup.LayoutParams params = list.getLayoutParams();
				params.height = totalHeight
						+ (list.getDividerHeight() * (list.getCount() - 1));
				list.setLayoutParams(params);
				myAdapter.notifyDataSetChanged();
			}
		});
		Button btn = (Button)findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
	}

	
	private class myAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return myList.size();
		}

		@Override
		public Object getItem(int position) {
			return myList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			MyTag tag = new MyTag();
			MyData data = myList.get(position);
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.expand_item_layout, null);
				tag.item1 = (TextView)convertView.findViewById(R.id.item1);
				tag.item2 = (RelativeLayout)convertView.findViewById(R.id.item2);
				convertView.setTag(tag);
			}else{
				tag = (MyTag)convertView.getTag();
			}
			if(data.expand) {
				tag.item2.setVisibility(View.VISIBLE);
			}else{
				tag.item2.setVisibility(View.GONE);
			}
			
			tag.item1.setText(data.name);
			return convertView;
		}
		
	}
	
	private class MyTag{
		private TextView item1;
		private RelativeLayout item2;
	}
	
	private class MyData{
		boolean expand;
		String name;
	}
	
}
