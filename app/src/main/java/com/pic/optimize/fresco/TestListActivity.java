package com.pic.optimize.fresco;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.pic.optimize.R;

import java.util.ArrayList;

public class TestListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_main);
        ListView listView = (ListView) findViewById(R.id.news_home_listview);
        listView.setAdapter(new FeedsMainAdapter(this,getList()));
    }


    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
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
        list.add("性能优化");
        list.add("图片优化");
        list.add("存储优化");
        list.add("加载优化");
        list.add("第三方框架");
        list.add("图片压缩");
        list.add("超大图片加载");
        return list;
    }
}

