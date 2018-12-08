package com.pic.optimize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pic.optimize.fresco.FeedsMainAdapter;

import java.util.ArrayList;

public class TestListActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_main);
        ListView listView = (ListView) findViewById(R.id.news_home_listview);
        listView.setAdapter(new FeedsMainAdapter(this,getList()));
        listView.setOnItemClickListener(this);
    }


    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("NewsSwitchActivity");
        list.add("SnapToScreenActivity");
        list.add("PicWallActivity");
        list.add("加载优化");
        list.add("第三方框架");
        list.add("图片压缩");
        list.add("超大图片加载");
        list.add("性能优化");
        list.add("图片优化");
        list.add("存储优化");
        list.add("加载优化");
        list.add("第三方框架");
        list.add("图片压缩");
        list.add("超大图片加载");
        list.add("性能优化");
        list.add("图片优化");
        list.add("存储优化");
        list.add("加载优化");
        list.add("第三方框架");
        list.add("图片压缩");
        list.add("超大图片加载");
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                NewsSwitchActivity.startActivity(TestListActivity.this);
                break;
            case 1:
                SnapToScreenActivity.startActivity(TestListActivity.this);
                break;
        }
    }
}

