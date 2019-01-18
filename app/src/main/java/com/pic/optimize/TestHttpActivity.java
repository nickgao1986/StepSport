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
import com.pic.optimize.http.BookResponse;
import com.pic.optimize.http.BookService;
import com.pic.optimize.http.api.ApiListener;
import com.pic.optimize.http.api.ApiUtil;
import com.pic.optimize.http.test.TestGetBookApi;
import com.pic.optimize.view.TitleBarCommon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        useRetrofit();
    }



    private void useRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://139.199.89.89") //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .build();
        BookService service = retrofit.create(BookService.class);
        Call<BookResponse> call = service.getResult();

        //3.发送请求
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                Log.d(TAG,"<<<<<response="+response);
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {

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
