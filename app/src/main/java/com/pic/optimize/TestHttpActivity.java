package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pic.optimize.fresco.HttpUtil;
import com.pic.optimize.http.Book;
import com.pic.optimize.http.BookMainAdapter;
import com.pic.optimize.http.api.ApiBase;
import com.pic.optimize.http.api.ApiListener;
import com.pic.optimize.http.test.PedometerNoticeModuleApi;
import com.pic.optimize.view.TitleBarCommon;

import org.json.JSONArray;

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

        proceedNoticeModuleApi();
    }



    private void proceedNoticeModuleApi() {
        new PedometerNoticeModuleApi(true).get(this, null, false, false, new ApiListener(){

            @Override
            public void success(ApiBase api) {
                Log.d(TAG,"<<<<<<success");
            }

            @Override
            public void failure(ApiBase api) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new InitDataAsyncTask().execute();

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

    private class InitDataAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpUtil httpUtil = new HttpUtil();
            String url = "http://139.199.89.89/api/v1/books";
            String response = httpUtil.get(TestHttpActivity.this,url);
            Log.d(TAG,"response="+response);
            parseResponse(response);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG,"<<<<<mBookArrayList="+ mBookArrayList);
            mBookMainAdapter.setList(mBookArrayList);
        }
    }

    private void parseResponse(String response) {
        try{
            Gson gson = new Gson();
            JSONArray jsonArray = new JSONArray(response);
            ArrayList<Book> books;
            mBookArrayList = new Gson().fromJson(response, new TypeToken<List<Book>>(){}.getType());

        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = mBookArrayList.get(position);
        BookDetail.startActivity(TestHttpActivity.this,book);
    }
}
