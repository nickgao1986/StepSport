package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pic.optimize.fresco.HttpUtil;
import com.pic.optimize.http.Book;
import com.pic.optimize.http.api.ApiUtil;
import com.pic.optimize.http.api.ApiListener;
import com.pic.optimize.http.test.TestBookApi;
import com.pic.optimize.view.TitleBarCommon;

public class BookDetail extends Activity {

    public static final String TAG = TestHttpActivity.class.getSimpleName();
    private TextView mBookName;
    private EditText mBookNameEdit;
    private boolean isAddOrEdit;
    private LinearLayout description_layout;
    private EditText mDescriptionEdit;
    private Book mBook;
    private TitleBarCommon mTitleBarCommon;

    public static void startActivity(Context context, Book book) {
        Intent intent = new Intent();
        intent.putExtra("book",book);
        intent.setClass(context,BookDetail.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        initTitle();
        Intent intent = getIntent();
        mBook = (Book)intent.getSerializableExtra("book");
        mBookName = (TextView)findViewById(R.id.bookName);
        mBookNameEdit = (EditText)findViewById(R.id.bookname_edit);
        mDescriptionEdit = (EditText) findViewById(R.id.bookdescription_edit);
        TextView mSaveBtn = (TextView)findViewById(R.id.save_tv);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // new InitDataAsyncTask(mBook).execute();
                testApi();
            }
        });

        description_layout = (LinearLayout)findViewById(R.id.description_layout);
        if(mBook == null) {
            description_layout.setVisibility(View.VISIBLE);
            isAddOrEdit = true;
            mTitleBarCommon.setRightTextViewString("");
        }else{
            mBookNameEdit.setText(mBook.bookName);
            isAddOrEdit = false;
        }

    }

    private void testApi() {
        if(!isAddOrEdit) {
            new TestBookApi(mBook.bookid, mBookNameEdit.getText().toString()).post(this, null, true, false, new ApiListener() {
                @Override
                public void success(ApiUtil api) {
                    Log.d(TAG, "<<<<<success=" + api);
                    finish();
                }

                @Override
                public void failure(ApiUtil api) {
                    Log.d(TAG, "<<<<<failure=" + api);
                    finish();
                }
            });
        }else{
            new TestBookApi( mBookNameEdit.getText().toString(), mDescriptionEdit.getText().toString()).post(this, null, true, false, new ApiListener() {
                @Override
                public void success(ApiUtil api) {
                    Log.d(TAG, "<<<<<success=" + api);
                    finish();
                }

                @Override
                public void failure(ApiUtil api) {
                    Log.d(TAG, "<<<<<failure=" + api);
                    finish();
                }
            });
        }

    }





//    private class InitDataAsyncTask extends AsyncTask<Void,Void,Void> {
//
//        private Book mBook;
//        public InitDataAsyncTask(Book book) {
//            mBook = book;
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            if(!isAddOrEdit) {
//                String para = new String("bookid="+mBook.bookid+"&bookName="+mBookNameEdit.getText().toString());
//                String url = "http://139.199.89.89/api/v1/books";
//                HttpUtil httpUtil = new HttpUtil();
//                String response = httpUtil.post(BookDetail.this,url,para);
//                Log.d(TAG,"<<<<<response="+response);
//            }else{
//                String para = new String("bookDescription="+mBookNameEdit.getText().toString()+"&bookName="+mBookNameEdit.getText().toString());
//                String url = "http://139.199.89.89/api/v1/books";
//                HttpUtil httpUtil = new HttpUtil();
//                String response = httpUtil.post(BookDetail.this,url,para);
//                Log.d(TAG,"<<<<<response="+response);
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            finish();
//        }
//    }


    private void initTitle() {
        mTitleBarCommon = (TitleBarCommon)findViewById(R.id.head_common_layout);
        mTitleBarCommon.setRightTextViewString("删除");
        mTitleBarCommon.setRightTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAddOrEdit) {
                    new DeleteAsyncTask().execute();
                }

            }
        });
        mTitleBarCommon.setLeftViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private class DeleteAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String para = new String("bookid="+mBook.bookid);
            String url = "http://139.199.89.89/api/v1/books";
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.doDelete(url,para);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
