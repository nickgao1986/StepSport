package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pic.optimize.http.Book;
import com.pic.optimize.http.BookMainAdapter;
import com.pic.optimize.http.api.ApiListener;
import com.pic.optimize.http.api.ApiUtil;
import com.pic.optimize.http.test.TestGetBookApi;
import com.pic.optimize.view.TitleBarCommon;

import java.util.ArrayList;
import java.util.List;

public class TestHttpActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = TestHttpActivity.class.getSimpleName();
    private ArrayList<Book> mBookArrayList;
    private BookMainAdapter mBookMainAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TestHttpActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_main);

        initTitle();
        ListView listView = (ListView) findViewById(R.id.news_home_listview);
        mBookMainAdapter = new BookMainAdapter(this);
        listView.setAdapter(mBookMainAdapter);
        listView.setOnItemClickListener(this);
    }




    private void proceedBookApi() {
        new TestGetBookApi().get(this, "loading", true, false, new ApiListener(){

            @Override
            public void success(ApiUtil api) {
                Log.d(TAG,"<<<<<<success");
                TestGetBookApi api1 = (TestGetBookApi)api;
                if(api1.mData != null) {
                    mBookArrayList = new Gson().fromJson(api1.mData, new TypeToken<List<Book>>(){}.getType());
                    mBookMainAdapter.setList(mBookArrayList);
                }
            }

            @Override
            public void failure(ApiUtil api) {
                Log.d(TAG,"<<<<<<failure");

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //new InitDataAsyncTask().execute();
        proceedBookApi();

    }

    private void initTitle() {
        TitleBarCommon titleBarCommon = (TitleBarCommon)findViewById(R.id.head_common_layout);
        titleBarCommon.setRightTextViewString("增加");
        titleBarCommon.setRightTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDetail.startActivity(TestHttpActivity.this,null);

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = mBookArrayList.get(position);
        BookDetail.startActivity(TestHttpActivity.this,book);
    }

}
